const express = require('express');
const router = express.Router();
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// Get All Plans
router.get('/list', async (req, res) => {
    try {
        const [plans] = await db.query(
            `SELECT id, name, description, price, daily_profit, duration_days, 
                    total_return, min_withdrawal, status 
             FROM plans 
             WHERE status = 'active' 
             ORDER BY sort_order ASC, price ASC`
        );

        res.json({
            success: true,
            data: {
                plans: plans.map(plan => ({
                    ...plan,
                    price: parseFloat(plan.price),
                    dailyProfit: parseFloat(plan.daily_profit),
                    totalReturn: parseFloat(plan.total_return),
                    minWithdrawal: parseFloat(plan.min_withdrawal)
                }))
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch plans',
            error: error.message
        });
    }
});

// Get Plan Details
router.get('/:id', async (req, res) => {
    try {
        const [plans] = await db.query(
            `SELECT id, name, description, price, daily_profit, duration_days, 
                    total_return, min_withdrawal, status 
             FROM plans 
             WHERE id = ? AND status = 'active'`,
            [req.params.id]
        );

        if (plans.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Plan not found'
            });
        }

        res.json({
            success: true,
            data: {
                ...plans[0],
                price: parseFloat(plans[0].price),
                dailyProfit: parseFloat(plans[0].daily_profit),
                totalReturn: parseFloat(plans[0].total_return),
                minWithdrawal: parseFloat(plans[0].min_withdrawal)
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch plan details',
            error: error.message
        });
    }
});

// Purchase Plan
router.post('/purchase', verifyToken, async (req, res) => {
    const connection = await db.getConnection();

    try {
        await connection.beginTransaction();

        const { planId } = req.body;

        if (!planId) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Plan ID is required'
            });
        }

        // Get plan details
        const [plans] = await connection.query(
            'SELECT id, name, price, daily_profit, duration_days FROM plans WHERE id = ? AND status = "active"',
            [planId]
        );

        if (plans.length === 0) {
            await connection.rollback();
            return res.status(404).json({
                success: false,
                message: 'Plan not found or inactive'
            });
        }

        const plan = plans[0];
        const price = parseFloat(plan.price);

        // Get current balance
        const [users] = await connection.query(
            'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
            [req.user.id]
        );

        const currentBalance = parseFloat(users[0].wallet_balance);

        // Check if sufficient balance
        if (currentBalance < price) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Insufficient balance. Please add funds to your wallet.'
            });
        }

        // Deduct amount from wallet
        const newBalance = currentBalance - price;
        await connection.query(
            'UPDATE users SET wallet_balance = ? WHERE id = ?',
            [newBalance, req.user.id]
        );

        // Create user plan
        const [userPlanResult] = await connection.query(
            `INSERT INTO user_plans 
             (user_id, plan_id, purchase_price, daily_profit, total_days) 
             VALUES (?, ?, ?, ?, ?)`,
            [req.user.id, planId, price, plan.daily_profit, plan.duration_days]
        );

        // Create transaction record
        await connection.query(
            `INSERT INTO transactions 
             (user_id, transaction_type, amount, balance_before, balance_after, status, description) 
             VALUES (?, 'plan_purchase', ?, ?, ?, 'success', ?)`,
            [req.user.id, price, currentBalance, newBalance, `Purchased ${plan.name} Plan`]
        );

        // Create notification
        await connection.query(
            `INSERT INTO notifications (user_id, title, message, notification_type) 
             VALUES (?, ?, ?, ?)`,
            [req.user.id, 'Plan Purchased!',
            `You successfully purchased the ${plan.name} Plan. Daily profit: ₹${plan.daily_profit}`, 'success']
        );

        await connection.commit();

        res.json({
            success: true,
            message: 'Plan purchased successfully',
            data: {
                userPlanId: userPlanResult.insertId,
                planName: plan.name,
                dailyProfit: parseFloat(plan.daily_profit),
                durationDays: plan.duration_days,
                newBalance: newBalance
            }
        });

    } catch (error) {
        await connection.rollback();
        console.error('Purchase plan error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to purchase plan',
            error: error.message
        });
    } finally {
        connection.release();
    }
});

module.exports = router;
