const express = require('express');
const router = express.Router();
const { body, validationResult } = require('express-validator');
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// Get Wallet Balance
router.get('/balance', verifyToken, async (req, res) => {
    try {
        const [users] = await db.query(
            `SELECT wallet_balance, total_earned, total_withdrawn 
             FROM users WHERE id = ?`,
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        res.json({
            success: true,
            data: {
                balance: parseFloat(users[0].wallet_balance),
                totalEarned: parseFloat(users[0].total_earned),
                totalWithdrawn: parseFloat(users[0].total_withdrawn)
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch wallet balance',
            error: error.message
        });
    }
});

// Get Transaction History
router.get('/transactions', verifyToken, async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 20;
        const offset = (page - 1) * limit;

        const [transactions] = await db.query(
            `SELECT id, transaction_type, amount, balance_before, balance_after, 
                    status, description, created_at 
             FROM transactions 
             WHERE user_id = ? 
             ORDER BY created_at DESC 
             LIMIT ? OFFSET ?`,
            [req.user.id, limit, offset]
        );

        const [countResult] = await db.query(
            'SELECT COUNT(*) as total FROM transactions WHERE user_id = ?',
            [req.user.id]
        );

        res.json({
            success: true,
            data: {
                transactions: transactions.map(t => ({
                    ...t,
                    amount: parseFloat(t.amount),
                    balanceBefore: parseFloat(t.balance_before),
                    balanceAfter: parseFloat(t.balance_after)
                })),
                pagination: {
                    currentPage: page,
                    totalPages: Math.ceil(countResult[0].total / limit),
                    totalRecords: countResult[0].total,
                    limit: limit
                }
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch transactions',
            error: error.message
        });
    }
});

// Request Withdrawal
router.post('/withdraw', [
    verifyToken,
    body('amount').isFloat({ min: 100 }).withMessage('Minimum withdrawal amount is ₹100'),
    body('paymentMethod').isIn(['bank_transfer', 'upi', 'paytm', 'phonepe', 'easypaisa', 'jazzcash']).withMessage('Invalid payment method'),
    body('accountDetails').notEmpty().withMessage('Account details are required')
], async (req, res) => {
    const connection = await db.getConnection();

    try {
        await connection.beginTransaction();

        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Validation failed',
                errors: errors.array()
            });
        }

        const { amount, paymentMethod, accountDetails } = req.body;

        // Get current balance
        const [users] = await connection.query(
            'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
            [req.user.id]
        );

        const currentBalance = parseFloat(users[0].wallet_balance);

        // Check if sufficient balance
        if (currentBalance < amount) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Insufficient balance'
            });
        }

        // Deduct amount from wallet
        const newBalance = currentBalance - amount;
        await connection.query(
            'UPDATE users SET wallet_balance = ?, total_withdrawn = total_withdrawn + ? WHERE id = ?',
            [newBalance, amount, req.user.id]
        );

        // Create transaction record
        const [transactionResult] = await connection.query(
            `INSERT INTO transactions 
             (user_id, transaction_type, amount, balance_before, balance_after, status, description) 
             VALUES (?, 'withdrawal', ?, ?, ?, 'pending', 'Withdrawal request')`,
            [req.user.id, amount, currentBalance, newBalance]
        );

        // Create withdrawal request
        const [withdrawalResult] = await connection.query(
            `INSERT INTO withdrawals 
             (user_id, transaction_id, amount, payment_method, 
              ${paymentMethod === 'upi' ? 'upi_id' : 'account_number'}, status) 
             VALUES (?, ?, ?, ?, ?, 'pending')`,
            [req.user.id, transactionResult.insertId, amount, paymentMethod, accountDetails]
        );

        // Create notification
        await connection.query(
            `INSERT INTO notifications (user_id, title, message, notification_type) 
             VALUES (?, ?, ?, ?)`,
            [req.user.id, 'Withdrawal Request Submitted',
            `Your withdrawal request of ₹${amount} is being processed.`, 'info']
        );

        await connection.commit();

        res.json({
            success: true,
            message: 'Withdrawal request submitted successfully',
            data: {
                withdrawalId: withdrawalResult.insertId,
                amount: amount,
                newBalance: newBalance,
                status: 'pending'
            }
        });

    } catch (error) {
        await connection.rollback();
        console.error('Withdrawal error:', error);
        res.status(500).json({
            success: false,
            message: 'Withdrawal request failed',
            error: error.message
        });
    } finally {
        connection.release();
    }
});

module.exports = router;
