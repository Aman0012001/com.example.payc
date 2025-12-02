const express = require('express');
const router = express.Router();
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// Get Available Tasks
router.get('/list', verifyToken, async (req, res) => {
    try {
        const [tasks] = await db.query(
            `SELECT id, title, description, task_type, reward, duration_minutes, 
                    company_name, company_logo, status 
             FROM tasks 
             WHERE status = 'active' 
               AND (expires_at IS NULL OR expires_at > NOW())
               AND current_completions < max_completions
             ORDER BY priority DESC, reward DESC`
        );

        // Get user's completed tasks
        const [completedTasks] = await db.query(
            `SELECT task_id FROM task_history 
             WHERE user_id = ? AND status = 'completed'`,
            [req.user.id]
        );

        const completedTaskIds = completedTasks.map(t => t.task_id);

        res.json({
            success: true,
            data: {
                tasks: tasks.map(task => ({
                    ...task,
                    reward: parseFloat(task.reward),
                    isCompleted: completedTaskIds.includes(task.id)
                }))
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch tasks',
            error: error.message
        });
    }
});

// Start Task
router.post('/start', verifyToken, async (req, res) => {
    try {
        const { taskId } = req.body;

        if (!taskId) {
            return res.status(400).json({
                success: false,
                message: 'Task ID is required'
            });
        }

        // Check if task exists and is active
        const [tasks] = await db.query(
            'SELECT id, title, reward, status FROM tasks WHERE id = ? AND status = "active"',
            [taskId]
        );

        if (tasks.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Task not found or inactive'
            });
        }

        // Check if user already completed this task
        const [existing] = await db.query(
            'SELECT id FROM task_history WHERE user_id = ? AND task_id = ? AND status = "completed"',
            [req.user.id, taskId]
        );

        if (existing.length > 0) {
            return res.status(400).json({
                success: false,
                message: 'Task already completed'
            });
        }

        // Create task history record
        const [result] = await db.query(
            `INSERT INTO task_history (user_id, task_id, reward_earned, status) 
             VALUES (?, ?, ?, 'started')`,
            [req.user.id, taskId, tasks[0].reward]
        );

        res.json({
            success: true,
            message: 'Task started successfully',
            data: {
                taskHistoryId: result.insertId,
                taskId: taskId,
                title: tasks[0].title,
                reward: parseFloat(tasks[0].reward)
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to start task',
            error: error.message
        });
    }
});

// Complete Task
router.post('/complete', verifyToken, async (req, res) => {
    const connection = await db.getConnection();

    try {
        await connection.beginTransaction();

        const { taskHistoryId } = req.body;

        if (!taskHistoryId) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Task history ID is required'
            });
        }

        // Get task history
        const [taskHistory] = await connection.query(
            `SELECT th.id, th.user_id, th.task_id, th.reward_earned, th.status, t.title 
             FROM task_history th
             JOIN tasks t ON th.task_id = t.id
             WHERE th.id = ? AND th.user_id = ?`,
            [taskHistoryId, req.user.id]
        );

        if (taskHistory.length === 0) {
            await connection.rollback();
            return res.status(404).json({
                success: false,
                message: 'Task history not found'
            });
        }

        if (taskHistory[0].status === 'completed') {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Task already completed'
            });
        }

        const reward = parseFloat(taskHistory[0].reward_earned);

        // Get current balance
        const [users] = await connection.query(
            'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
            [req.user.id]
        );

        const currentBalance = parseFloat(users[0].wallet_balance);
        const newBalance = currentBalance + reward;

        // Update wallet balance
        await connection.query(
            'UPDATE users SET wallet_balance = ?, total_earned = total_earned + ? WHERE id = ?',
            [newBalance, reward, req.user.id]
        );

        // Update task history
        await connection.query(
            'UPDATE task_history SET status = "completed", completed_at = NOW() WHERE id = ?',
            [taskHistoryId]
        );

        // Update task completion count
        await connection.query(
            'UPDATE tasks SET current_completions = current_completions + 1 WHERE id = ?',
            [taskHistory[0].task_id]
        );

        // Create transaction record
        await connection.query(
            `INSERT INTO transactions 
             (user_id, transaction_type, amount, balance_before, balance_after, status, description) 
             VALUES (?, 'task_reward', ?, ?, ?, 'success', ?)`,
            [req.user.id, reward, currentBalance, newBalance, `Reward for: ${taskHistory[0].title}`]
        );

        // Create notification
        await connection.query(
            `INSERT INTO notifications (user_id, title, message, notification_type) 
             VALUES (?, ?, ?, ?)`,
            [req.user.id, 'Task Completed!',
            `You earned ₹${reward} for completing "${taskHistory[0].title}"`, 'success']
        );

        await connection.commit();

        res.json({
            success: true,
            message: 'Task completed successfully',
            data: {
                reward: reward,
                newBalance: newBalance
            }
        });

    } catch (error) {
        await connection.rollback();
        console.error('Complete task error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to complete task',
            error: error.message
        });
    } finally {
        connection.release();
    }
});

// Get Task History
router.get('/history', verifyToken, async (req, res) => {
    try {
        const [history] = await db.query(
            `SELECT th.id, th.task_id, t.title, t.task_type, th.reward_earned, 
                    th.status, th.started_at, th.completed_at
             FROM task_history th
             JOIN tasks t ON th.task_id = t.id
             WHERE th.user_id = ?
             ORDER BY th.started_at DESC
             LIMIT 50`,
            [req.user.id]
        );

        res.json({
            success: true,
            data: {
                history: history.map(h => ({
                    ...h,
                    rewardEarned: parseFloat(h.reward_earned)
                }))
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Failed to fetch task history',
            error: error.message
        });
    }
});

module.exports = router;
