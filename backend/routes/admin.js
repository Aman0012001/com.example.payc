const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../config/database');
const { verifyAdmin } = require('../middleware/auth');

// Admin Login
router.post('/login', async (req, res) => {
    try {
        const { username, password } = req.body;

        const [admins] = await db.query(
            'SELECT id, username, email, password, role, status FROM admin_users WHERE username = ?',
            [username]
        );

        if (admins.length === 0) {
            return res.status(401).json({
                success: false,
                message: 'Invalid credentials'
            });
        }

        const admin = admins[0];

        if (admin.status !== 'active') {
            return res.status(403).json({
                success: false,
                message: 'Account is inactive'
            });
        }

        const isPasswordValid = await bcrypt.compare(password, admin.password);

        if (!isPasswordValid) {
            return res.status(401).json({
                success: false,
                message: 'Invalid credentials'
            });
        }

        await db.query('UPDATE admin_users SET last_login = NOW() WHERE id = ?', [admin.id]);

        const token = jwt.sign(
            { adminId: admin.id, username: admin.username, role: admin.role },
            process.env.JWT_SECRET,
            { expiresIn: '24h' }
        );

        res.json({
            success: true,
            message: 'Login successful',
            data: {
                adminId: admin.id,
                username: admin.username,
                role: admin.role,
                token: token
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Login failed',
            error: error.message
        });
    }
});

// Dashboard Statistics
router.get('/stats', verifyAdmin, async (req, res) => {
    try {
        const [userStats] = await db.query(
            'SELECT COUNT(*) as totalUsers, SUM(wallet_balance) as totalBalance FROM users WHERE status = "active"'
        );

        const [transactionStats] = await db.query(
            `SELECT 
                SUM(CASE WHEN transaction_type = 'deposit' AND status = 'success' THEN amount ELSE 0 END) as totalDeposits,
                SUM(CASE WHEN transaction_type = 'withdrawal' AND status = 'success' THEN amount ELSE 0 END) as totalWithdrawals,
                COUNT(*) as totalTransactions
             FROM transactions
             WHERE DATE(created_at) = CURDATE()`
        );

        const [pendingWithdrawals] = await db.query(
            'SELECT COUNT(*) as count, SUM(amount) as total FROM withdrawals WHERE status = "pending"'
        );

        const [activePlans] = await db.query(
            'SELECT COUNT(*) as count FROM user_plans WHERE status = "active"'
        );

        res.json({
            success: true,
            data: {
                users: {
                    total: userStats[0].totalUsers,
                    totalBalance: parseFloat(userStats[0].totalBalance || 0)
                },
                todayTransactions: {
                    deposits: parseFloat(transactionStats[0].totalDeposits || 0),
                    withdrawals: parseFloat(transactionStats[0].totalWithdrawals || 0),
                    count: transactionStats[0].totalTransactions
                },
                pendingWithdrawals: {
                    count: pendingWithdrawals[0].count,
                    total: parseFloat(pendingWithdrawals[0].total || 0)
                },
                activePlans: activePlans[0].count
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch statistics',
            error: error.message
        });
    }
});

// Get All Users
router.get('/users', verifyAdmin, async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 50;
        const offset = (page - 1) * limit;

        const [users] = await db.query(
            `SELECT id, name, email, phone, wallet_balance, total_earned, 
                    total_withdrawn, status, created_at, last_login
             FROM users
             ORDER BY created_at DESC
             LIMIT ? OFFSET ?`,
            [limit, offset]
        );

        const [countResult] = await db.query('SELECT COUNT(*) as total FROM users');

        res.json({
            success: true,
            data: {
                users: users,
                pagination: {
                    currentPage: page,
                    totalPages: Math.ceil(countResult[0].total / limit),
                    totalRecords: countResult[0].total
                }
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch users',
            error: error.message
        });
    }
});

// Get Pending Withdrawals
router.get('/withdrawals', verifyAdmin, async (req, res) => {
    try {
        const [withdrawals] = await db.query(
            `SELECT w.id, w.user_id, u.name, u.email, w.amount, w.payment_method,
                    w.upi_id, w.account_number, w.account_holder_name, w.status, w.created_at
             FROM withdrawals w
             JOIN users u ON w.user_id = u.id
             WHERE w.status = 'pending'
             ORDER BY w.created_at ASC`
        );

        res.json({
            success: true,
            data: { withdrawals }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch withdrawals',
            error: error.message
        });
    }
});

// Approve Withdrawal
router.post('/approve-withdrawal', verifyAdmin, async (req, res) => {
    const connection = await db.getConnection();

    try {
        await connection.beginTransaction();

        const { withdrawalId, action, adminNote } = req.body;

        if (!withdrawalId || !action) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Withdrawal ID and action are required'
            });
        }

        const [withdrawals] = await connection.query(
            'SELECT id, user_id, amount, transaction_id, status FROM withdrawals WHERE id = ? FOR UPDATE',
            [withdrawalId]
        );

        if (withdrawals.length === 0) {
            await connection.rollback();
            return res.status(404).json({
                success: false,
                message: 'Withdrawal request not found'
            });
        }

        const withdrawal = withdrawals[0];

        if (withdrawal.status !== 'pending') {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Withdrawal already processed'
            });
        }

        if (action === 'approve') {
            await connection.query(
                `UPDATE withdrawals 
                 SET status = 'approved', approved_by = ?, approved_at = NOW(), admin_note = ? 
                 WHERE id = ?`,
                [req.admin.id, adminNote || null, withdrawalId]
            );

            await connection.query(
                'UPDATE transactions SET status = "success" WHERE id = ?',
                [withdrawal.transaction_id]
            );

            await connection.query(
                `INSERT INTO notifications (user_id, title, message, notification_type) 
                 VALUES (?, ?, ?, ?)`,
                [withdrawal.user_id, 'Withdrawal Approved',
                `Your withdrawal request of ₹${withdrawal.amount} has been approved.`, 'success']
            );

        } else if (action === 'reject') {
            // Refund amount to wallet
            await connection.query(
                'UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?',
                [withdrawal.amount, withdrawal.user_id]
            );

            await connection.query(
                `UPDATE withdrawals 
                 SET status = 'rejected', approved_by = ?, approved_at = NOW(), admin_note = ? 
                 WHERE id = ?`,
                [req.admin.id, adminNote || 'Rejected by admin', withdrawalId]
            );

            await connection.query(
                'UPDATE transactions SET status = "failed" WHERE id = ?',
                [withdrawal.transaction_id]
            );

            await connection.query(
                `INSERT INTO notifications (user_id, title, message, notification_type) 
                 VALUES (?, ?, ?, ?)`,
                [withdrawal.user_id, 'Withdrawal Rejected',
                    `Your withdrawal request has been rejected. Amount refunded to wallet.`, 'warning']
            );
        }

        await connection.commit();

        res.json({
            success: true,
            message: `Withdrawal ${action}d successfully`
        });

    } catch (error) {
        await connection.rollback();
        console.error('Approve withdrawal error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to process withdrawal',
            error: error.message
        });
    } finally {
        connection.release();
    }
});

// Add Plan
router.post('/add-plan', verifyAdmin, async (req, res) => {
    try {
        const { name, description, price, dailyProfit, durationDays, minWithdrawal } = req.body;

        const totalReturn = dailyProfit * durationDays;

        const [result] = await db.query(
            `INSERT INTO plans (name, description, price, daily_profit, duration_days, total_return, min_withdrawal) 
             VALUES (?, ?, ?, ?, ?, ?, ?)`,
            [name, description, price, dailyProfit, durationDays, totalReturn, minWithdrawal || 100]
        );

        res.json({
            success: true,
            message: 'Plan added successfully',
            data: { planId: result.insertId }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to add plan',
            error: error.message
        });
    }
});

// Update Plan
router.put('/update-plan/:id', verifyAdmin, async (req, res) => {
    try {
        const { name, description, price, dailyProfit, durationDays, status } = req.body;

        const totalReturn = dailyProfit * durationDays;

        await db.query(
            `UPDATE plans 
             SET name = ?, description = ?, price = ?, daily_profit = ?, 
                 duration_days = ?, total_return = ?, status = ?
             WHERE id = ?`,
            [name, description, price, dailyProfit, durationDays, totalReturn, status, req.params.id]
        );

        res.json({
            success: true,
            message: 'Plan updated successfully'
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to update plan',
            error: error.message
        });
    }
});

// Delete Plan
router.delete('/delete-plan/:id', verifyAdmin, async (req, res) => {
    try {
        await db.query('UPDATE plans SET status = "inactive" WHERE id = ?', [req.params.id]);

        res.json({
            success: true,
            message: 'Plan deleted successfully'
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to delete plan',
            error: error.message
        });
    }
});

module.exports = router;
