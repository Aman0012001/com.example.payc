const express = require('express');
const router = express.Router();
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// Manager Levels and Salaries
const MANAGER_LEVELS = {
    supreme: { required_plan: 'Supreme', required_count: 20, salary: 70000 },
    silver: { required_plan: 'Silver', required_count: 20, salary: 100000 },
    platinum: { required_plan: 'Platinum', required_count: 20, salary: 200000 },
    gold: { required_plan: 'Gold', required_count: 20, salary: 300000 },
    diamond: { required_plan: 'Diamond', required_count: 20, salary: 400000 },
    master: { required_plan: 'Master', required_count: 20, salary: 500000 }
};

const SUPER_MANAGER_MULTIPLIERS = {
    economy: 250000,
    supreme: 375000,
    silver: 500000,
    platinum: 1000000,
    gold: 1500000,
    diamond: 2000000,
    master: 2500000
};

// Get Manager Status
router.get('/status', verifyToken, async (req, res) => {
    try {
        // Get user's current level
        const [user] = await db.query('SELECT manager_level, is_super_manager FROM users WHERE id = ?', [req.user.id]);

        // Get referral counts by plan
        const [referralStats] = await db.query(`
            SELECT p.name as plan_name, COUNT(DISTINCT r.referred_id) as count
            FROM referrals r
            JOIN user_plans up ON r.referred_id = up.user_id
            JOIN plans p ON up.plan_id = p.id
            WHERE r.referrer_id = ? AND up.status = 'active'
            GROUP BY p.name
        `, [req.user.id]);

        const stats = {};
        referralStats.forEach(row => {
            stats[row.plan_name] = row.count;
        });

        // Check for potential upgrades
        let potentialLevel = 'none';
        let nextTarget = null;

        // Check in order of value
        const levels = ['master', 'diamond', 'gold', 'platinum', 'silver', 'supreme'];

        for (const level of levels) {
            const config = MANAGER_LEVELS[level];
            const count = stats[config.required_plan] || 0;

            if (count >= config.required_count) {
                if (potentialLevel === 'none') potentialLevel = level;
            } else if (!nextTarget) {
                nextTarget = {
                    level,
                    current: count,
                    required: config.required_count,
                    plan: config.required_plan
                };
            }
        }

        // Check Super Manager Status
        // Logic: "If every team member of a manager add 5 members then manager becomes super manager"
        // This is a bit complex to query efficiently. 
        // We need to check if ALL direct referrals (who count towards the manager level) have at least 5 referrals themselves.
        // For now, let's just return the basic stats.

        res.json({
            success: true,
            data: {
                currentLevel: user[0].manager_level,
                isSuperManager: user[0].is_super_manager,
                stats,
                nextTarget,
                salary: user[0].manager_level !== 'none' ? MANAGER_LEVELS[user[0].manager_level].salary : 0
            }
        });

    } catch (error) {
        console.error('Manager status error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch manager status',
            error: error.message
        });
    }
});

// Claim Salary
router.post('/claim-salary', verifyToken, async (req, res) => {
    const connection = await db.getConnection();
    try {
        await connection.beginTransaction();

        const [user] = await connection.query('SELECT manager_level, is_super_manager FROM users WHERE id = ?', [req.user.id]);

        if (user[0].manager_level === 'none') {
            await connection.rollback();
            return res.status(400).json({ success: false, message: 'You are not a manager yet' });
        }

        const currentMonth = new Date().toISOString().slice(0, 7); // YYYY-MM

        // Check if already claimed
        const [existingClaim] = await connection.query(
            'SELECT id FROM salary_claims WHERE user_id = ? AND month = ?',
            [req.user.id, currentMonth]
        );

        if (existingClaim.length > 0) {
            await connection.rollback();
            return res.status(400).json({ success: false, message: 'Salary already claimed for this month' });
        }

        let salary = MANAGER_LEVELS[user[0].manager_level].salary;

        // If super manager, use super manager salary
        if (user[0].is_super_manager) {
            // Logic to determine super manager salary based on level
            // *Economy manager will get 250k salary after becoming economy super manager
            // This implies the level is linked.
            // If I am a Supreme Manager, and I become Super, I get 375k.
            if (SUPER_MANAGER_MULTIPLIERS[user[0].manager_level]) {
                salary = SUPER_MANAGER_MULTIPLIERS[user[0].manager_level];
            }
        }

        // Create claim
        await connection.query(
            'INSERT INTO salary_claims (user_id, amount, month, status) VALUES (?, ?, ?, "paid")',
            [req.user.id, salary, currentMonth]
        );

        // Add to wallet
        await connection.query(
            'UPDATE users SET wallet_balance = wallet_balance + ?, total_earned = total_earned + ? WHERE id = ?',
            [salary, salary, req.user.id]
        );

        // Transaction record
        await connection.query(
            `INSERT INTO transactions 
             (user_id, transaction_type, amount, balance_before, balance_after, status, description) 
             VALUES (?, 'referral_bonus', ?, 0, 0, 'success', ?)`, // Note: balance tracking here is simplified, ideally fetch before/after
            [req.user.id, salary, `Manager Salary for ${currentMonth}`]
        );

        await connection.commit();

        res.json({
            success: true,
            message: `Salary of ${salary} claimed successfully for ${currentMonth}`
        });

    } catch (error) {
        await connection.rollback();
        console.error('Claim salary error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to claim salary',
            error: error.message
        });
    } finally {
        connection.release();
    }
});

module.exports = router;
