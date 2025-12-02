const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { body, validationResult } = require('express-validator');
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');

// Generate Referral Code
function generateReferralCode() {
    return 'PAYC' + Math.random().toString(36).substring(2, 10).toUpperCase();
}

// Register User
router.post('/register', [
    body('name').trim().isLength({ min: 3 }).withMessage('Name must be at least 3 characters'),
    body('email').isEmail().normalizeEmail().withMessage('Invalid email address'),
    body('phone').optional().isMobilePhone('en-IN').withMessage('Invalid phone number'),
    body('password').isLength({ min: 8 }).withMessage('Password must be at least 8 characters')
        .matches(/^(?=.*[A-Z])(?=.*\d)/).withMessage('Password must contain at least one uppercase letter and one number'),
    body('referralCode').optional().trim()
], async (req, res) => {
    try {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                message: 'Validation failed',
                errors: errors.array()
            });
        }

        const { name, email, phone, password, referralCode } = req.body;

        // Check if email already exists
        const [existingUsers] = await db.query(
            'SELECT id FROM users WHERE email = ?',
            [email]
        );

        if (existingUsers.length > 0) {
            return res.status(400).json({
                success: false,
                message: 'Email already registered'
            });
        }

        // Check if phone already exists (if provided)
        if (phone) {
            const [existingPhone] = await db.query(
                'SELECT id FROM users WHERE phone = ?',
                [phone]
            );

            if (existingPhone.length > 0) {
                return res.status(400).json({
                    success: false,
                    message: 'Phone number already registered'
                });
            }
        }

        // Verify referral code if provided
        let referrerId = null;
        if (referralCode) {
            const [referrer] = await db.query(
                'SELECT id FROM users WHERE referral_code = ?',
                [referralCode]
            );

            if (referrer.length > 0) {
                referrerId = referrer[0].id;
            }
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Generate unique referral code
        let userReferralCode = generateReferralCode();
        let codeExists = true;

        while (codeExists) {
            const [existing] = await db.query(
                'SELECT id FROM users WHERE referral_code = ?',
                [userReferralCode]
            );
            if (existing.length === 0) {
                codeExists = false;
            } else {
                userReferralCode = generateReferralCode();
            }
        }

        // Insert user
        const [result] = await db.query(
            `INSERT INTO users (name, email, phone, password, referral_code, referred_by) 
             VALUES (?, ?, ?, ?, ?, ?)`,
            [name, email, phone || null, hashedPassword, userReferralCode, referrerId]
        );

        const userId = result.insertId;

        // Create referral record if referred
        if (referrerId) {
            await db.query(
                'INSERT INTO referrals (referrer_id, referred_id) VALUES (?, ?)',
                [referrerId, userId]
            );

            // Send notification to referrer
            await db.query(
                `INSERT INTO notifications (user_id, title, message, notification_type) 
                 VALUES (?, ?, ?, ?)`,
                [referrerId, 'New Referral!', `${name} joined using your referral code!`, 'success']
            );
        }

        // Generate JWT token
        const token = jwt.sign(
            { userId: userId, email: email },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
        );

        res.status(201).json({
            success: true,
            message: 'Registration successful',
            data: {
                userId: userId,
                name: name,
                email: email,
                referralCode: userReferralCode,
                token: token
            }
        });

    } catch (error) {
        console.error('Registration error:', error);
        res.status(500).json({
            success: false,
            message: 'Registration failed',
            error: error.message
        });
    }
});

// Login User
router.post('/login', [
    body('email').isEmail().normalizeEmail().withMessage('Invalid email address'),
    body('password').notEmpty().withMessage('Password is required')
], async (req, res) => {
    try {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                message: 'Validation failed',
                errors: errors.array()
            });
        }

        const { email, password } = req.body;

        // Find user
        const [users] = await db.query(
            'SELECT id, name, email, password, status, wallet_balance, referral_code FROM users WHERE email = ?',
            [email]
        );

        if (users.length === 0) {
            return res.status(401).json({
                success: false,
                message: 'Invalid email or password'
            });
        }

        const user = users[0];

        // Check if user is active
        if (user.status !== 'active') {
            return res.status(403).json({
                success: false,
                message: 'Account is suspended or banned'
            });
        }

        // Verify password
        const isPasswordValid = await bcrypt.compare(password, user.password);

        if (!isPasswordValid) {
            return res.status(401).json({
                success: false,
                message: 'Invalid email or password'
            });
        }

        // Update last login
        await db.query(
            'UPDATE users SET last_login = NOW() WHERE id = ?',
            [user.id]
        );

        // Generate JWT token
        const token = jwt.sign(
            { userId: user.id, email: user.email },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
        );

        res.json({
            success: true,
            message: 'Login successful',
            data: {
                userId: user.id,
                name: user.name,
                email: user.email,
                walletBalance: parseFloat(user.wallet_balance),
                referralCode: user.referral_code,
                token: token
            }
        });

    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({
            success: false,
            message: 'Login failed',
            error: error.message
        });
    }
});

// Verify Token
router.post('/verify-token', verifyToken, async (req, res) => {
    try {
        const [users] = await db.query(
            'SELECT id, name, email, wallet_balance, referral_code FROM users WHERE id = ?',
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
            message: 'Token is valid',
            data: {
                userId: users[0].id,
                name: users[0].name,
                email: users[0].email,
                walletBalance: parseFloat(users[0].wallet_balance),
                referralCode: users[0].referral_code
            }
        });

    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Verification failed',
            error: error.message
        });
    }
});

module.exports = router;
