const db = require('../config/database');

/**
 * Atomic wallet operation handler
 * Ensures ACID properties for all wallet transactions
 */
class TransactionHandler {

    /**
     * Add funds to wallet (deposit)
     */
    static async addFunds(userId, amount, transactionType, description, referenceId = null) {
        const connection = await db.getConnection();

        try {
            await connection.beginTransaction();

            // Validate amount
            if (amount <= 0) {
                throw new Error('Amount must be positive');
            }

            // Lock user row for update
            const [users] = await connection.query(
                'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
                [userId]
            );

            if (users.length === 0) {
                throw new Error('User not found');
            }

            const currentBalance = parseFloat(users[0].wallet_balance);
            const newBalance = currentBalance + amount;

            // Update wallet balance
            await connection.query(
                'UPDATE users SET wallet_balance = ?, total_earned = total_earned + ? WHERE id = ?',
                [newBalance, amount, userId]
            );

            // Create transaction record
            const [result] = await connection.query(
                `INSERT INTO transactions 
                 (user_id, transaction_type, amount, balance_before, balance_after, status, description, reference_id) 
                 VALUES (?, ?, ?, ?, ?, 'success', ?, ?)`,
                [userId, transactionType, amount, currentBalance, newBalance, description, referenceId]
            );

            await connection.commit();

            return {
                success: true,
                transactionId: result.insertId,
                previousBalance: currentBalance,
                newBalance: newBalance,
                amount: amount
            };

        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    /**
     * Deduct funds from wallet (withdrawal, plan purchase)
     */
    static async deductFunds(userId, amount, transactionType, description, referenceId = null) {
        const connection = await db.getConnection();

        try {
            await connection.beginTransaction();

            // Validate amount
            if (amount <= 0) {
                throw new Error('Amount must be positive');
            }

            // Lock user row for update
            const [users] = await connection.query(
                'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
                [userId]
            );

            if (users.length === 0) {
                throw new Error('User not found');
            }

            const currentBalance = parseFloat(users[0].wallet_balance);

            // Check sufficient balance
            if (currentBalance < amount) {
                throw new Error('Insufficient balance');
            }

            const newBalance = currentBalance - amount;

            // Prevent negative balance (extra safety check)
            if (newBalance < 0) {
                throw new Error('Operation would result in negative balance');
            }

            // Update wallet balance
            await connection.query(
                'UPDATE users SET wallet_balance = ? WHERE id = ?',
                [newBalance, userId]
            );

            // Update total withdrawn if it's a withdrawal
            if (transactionType === 'withdrawal') {
                await connection.query(
                    'UPDATE users SET total_withdrawn = total_withdrawn + ? WHERE id = ?',
                    [amount, userId]
                );
            }

            // Create transaction record
            const [result] = await connection.query(
                `INSERT INTO transactions 
                 (user_id, transaction_type, amount, balance_before, balance_after, status, description, reference_id) 
                 VALUES (?, ?, ?, ?, ?, 'pending', ?, ?)`,
                [userId, transactionType, amount, currentBalance, newBalance, description, referenceId]
            );

            await connection.commit();

            return {
                success: true,
                transactionId: result.insertId,
                previousBalance: currentBalance,
                newBalance: newBalance,
                amount: amount
            };

        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    /**
     * Refund funds (cancelled withdrawal, failed transaction)
     */
    static async refundFunds(userId, amount, transactionId, reason) {
        const connection = await db.getConnection();

        try {
            await connection.beginTransaction();

            // Lock user row for update
            const [users] = await connection.query(
                'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
                [userId]
            );

            if (users.length === 0) {
                throw new Error('User not found');
            }

            const currentBalance = parseFloat(users[0].wallet_balance);
            const newBalance = currentBalance + amount;

            // Update wallet balance
            await connection.query(
                'UPDATE users SET wallet_balance = ? WHERE id = ?',
                [newBalance, userId]
            );

            // Update original transaction status
            await connection.query(
                'UPDATE transactions SET status = "failed" WHERE id = ?',
                [transactionId]
            );

            // Create refund transaction record
            await connection.query(
                `INSERT INTO transactions 
                 (user_id, transaction_type, amount, balance_before, balance_after, status, description, reference_id) 
                 VALUES (?, 'refund', ?, ?, ?, 'success', ?, ?)`,
                [userId, amount, currentBalance, newBalance, reason, transactionId]
            );

            await connection.commit();

            return {
                success: true,
                previousBalance: currentBalance,
                newBalance: newBalance,
                amount: amount
            };

        } catch (error) {
            await connection.rollback();
            throw error;
        } finally {
            connection.release();
        }
    }

    /**
     * Get user balance safely
     */
    static async getBalance(userId) {
        const [users] = await db.query(
            'SELECT wallet_balance, total_earned, total_withdrawn FROM users WHERE id = ?',
            [userId]
        );

        if (users.length === 0) {
            throw new Error('User not found');
        }

        return {
            balance: parseFloat(users[0].wallet_balance),
            totalEarned: parseFloat(users[0].total_earned),
            totalWithdrawn: parseFloat(users[0].total_withdrawn)
        };
    }

    /**
     * Verify transaction exists and belongs to user
     */
    static async verifyTransaction(transactionId, userId) {
        const [transactions] = await db.query(
            'SELECT * FROM transactions WHERE id = ? AND user_id = ?',
            [transactionId, userId]
        );

        return transactions.length > 0 ? transactions[0] : null;
    }
}

module.exports = TransactionHandler;
