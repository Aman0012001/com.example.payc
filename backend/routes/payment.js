const express = require('express');
const router = express.Router();
const Razorpay = require('razorpay');
const crypto = require('crypto');
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// Initialize Razorpay
const razorpay = new Razorpay({
    key_id: process.env.RAZORPAY_KEY_ID,
    key_secret: process.env.RAZORPAY_KEY_SECRET
});

// Create Order
router.post('/create-order', verifyToken, async (req, res) => {
    try {
        const { amount } = req.body;

        if (!amount || amount < 100) {
            return res.status(400).json({
                success: false,
                message: 'Minimum deposit amount is ₹100'
            });
        }

        // Create Razorpay order
        const options = {
            amount: amount * 100, // amount in paise
            currency: 'INR',
            receipt: `receipt_${req.user.id}_${Date.now()}`,
            notes: {
                userId: req.user.id,
                email: req.user.email
            }
        };

        const order = await razorpay.orders.create(options);

        // Save payment record
        await db.query(
            `INSERT INTO payments (user_id, razorpay_order_id, amount, currency, status) 
             VALUES (?, ?, ?, ?, 'created')`,
            [req.user.id, order.id, amount, 'INR']
        );

        res.json({
            success: true,
            message: 'Order created successfully',
            data: {
                orderId: order.id,
                amount: amount,
                currency: 'INR',
                key: process.env.RAZORPAY_KEY_ID
            }
        });

    } catch (error) {
        console.error('Create order error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to create order',
            error: error.message
        });
    }
});

// Verify Payment
router.post('/verify', verifyToken, async (req, res) => {
    const connection = await db.getConnection();

    try {
        await connection.beginTransaction();

        const { razorpay_order_id, razorpay_payment_id, razorpay_signature } = req.body;

        if (!razorpay_order_id || !razorpay_payment_id || !razorpay_signature) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Missing payment verification details'
            });
        }

        // Verify signature
        const sign = razorpay_order_id + '|' + razorpay_payment_id;
        const expectedSign = crypto
            .createHmac('sha256', process.env.RAZORPAY_KEY_SECRET)
            .update(sign.toString())
            .digest('hex');

        if (razorpay_signature !== expectedSign) {
            await connection.rollback();
            return res.status(400).json({
                success: false,
                message: 'Invalid payment signature'
            });
        }

        // Get payment record
        const [payments] = await connection.query(
            'SELECT id, user_id, amount FROM payments WHERE razorpay_order_id = ? FOR UPDATE',
            [razorpay_order_id]
        );

        if (payments.length === 0) {
            await connection.rollback();
            return res.status(404).json({
                success: false,
                message: 'Payment record not found'
            });
        }

        const payment = payments[0];
        const amount = parseFloat(payment.amount);

        // Get current balance
        const [users] = await connection.query(
            'SELECT wallet_balance FROM users WHERE id = ? FOR UPDATE',
            [payment.user_id]
        );

        const currentBalance = parseFloat(users[0].wallet_balance);
        const newBalance = currentBalance + amount;

        // Update wallet balance
        await connection.query(
            'UPDATE users SET wallet_balance = ? WHERE id = ?',
            [newBalance, payment.user_id]
        );

        // Create transaction record
        const [transactionResult] = await connection.query(
            `INSERT INTO transactions 
             (user_id, transaction_type, amount, balance_before, balance_after, status, description, reference_id) 
             VALUES (?, 'deposit', ?, ?, ?, 'success', 'Wallet recharge', ?)`,
            [payment.user_id, amount, currentBalance, newBalance, razorpay_payment_id]
        );

        // Update payment record
        await connection.query(
            `UPDATE payments 
             SET razorpay_payment_id = ?, razorpay_signature = ?, 
                 status = 'captured', transaction_id = ? 
             WHERE id = ?`,
            [razorpay_payment_id, razorpay_signature, transactionResult.insertId, payment.id]
        );

        // Create notification
        await connection.query(
            `INSERT INTO notifications (user_id, title, message, notification_type) 
             VALUES (?, ?, ?, ?)`,
            [payment.user_id, 'Payment Successful!',
            `₹${amount} has been added to your wallet.`, 'success']
        );

        await connection.commit();

        res.json({
            success: true,
            message: 'Payment verified successfully',
            data: {
                amount: amount,
                newBalance: newBalance,
                transactionId: transactionResult.insertId
            }
        });

    } catch (error) {
        await connection.rollback();
        console.error('Verify payment error:', error);
        res.status(500).json({
            success: false,
            message: 'Payment verification failed',
            error: error.message
        });
    } finally {
        connection.release();
    }
});

// Razorpay Webhook
router.post('/webhook', async (req, res) => {
    const connection = await db.getConnection();

    try {
        const secret = process.env.RAZORPAY_WEBHOOK_SECRET;
        const signature = req.headers['x-razorpay-signature'];

        // Verify webhook signature
        const expectedSignature = crypto
            .createHmac('sha256', secret)
            .update(JSON.stringify(req.body))
            .digest('hex');

        if (signature !== expectedSignature) {
            return res.status(400).json({ success: false, message: 'Invalid signature' });
        }

        const event = req.body.event;
        const payload = req.body.payload.payment.entity;

        if (event === 'payment.captured') {
            // Payment was captured successfully
            await connection.query(
                `UPDATE payments 
                 SET status = 'captured', payment_method = ? 
                 WHERE razorpay_payment_id = ?`,
                [payload.method, payload.id]
            );
        } else if (event === 'payment.failed') {
            // Payment failed
            await connection.query(
                `UPDATE payments 
                 SET status = 'failed', error_code = ?, error_description = ? 
                 WHERE razorpay_payment_id = ?`,
                [payload.error_code, payload.error_description, payload.id]
            );
        }

        res.json({ success: true });

    } catch (error) {
        console.error('Webhook error:', error);
        res.status(500).json({ success: false });
    } finally {
        connection.release();
    }
});

module.exports = router;
