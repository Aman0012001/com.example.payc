const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const { body, validationResult } = require('express-validator');
const db = require('../config/database');
const { verifyToken } = require('../middleware/auth');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

// Configure multer for profile image uploads
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        const uploadDir = path.join(__dirname, '../uploads/profiles');
        // Create directory if it doesn't exist
        if (!fs.existsSync(uploadDir)) {
            fs.mkdirSync(uploadDir, { recursive: true });
        }
        cb(null, uploadDir);
    },
    filename: function (req, file, cb) {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, 'profile-' + req.user.id + '-' + uniqueSuffix + path.extname(file.originalname));
    }
});

const fileFilter = (req, file, cb) => {
    // Accept images only
    if (file.mimetype.startsWith('image/')) {
        cb(null, true);
    } else {
        cb(new Error('Only image files are allowed!'), false);
    }
};

const upload = multer({
    storage: storage,
    fileFilter: fileFilter,
    limits: {
        fileSize: 5 * 1024 * 1024 // 5MB limit
    }
});

// Get User Profile
router.get('/', verifyToken, async (req, res) => {
    try {
        const [users] = await db.query(
            `SELECT id, name, email, phone, referral_code, wallet_balance, 
                    total_earned, total_withdrawn, manager_level, is_super_manager,
                    profile_image, created_at, last_login
             FROM users WHERE id = ?`,
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        // Get bank account details from withdrawals table (most recent)
        const [bankDetails] = await db.query(
            `SELECT bank_name, account_number, ifsc_code, account_holder_name, upi_id
             FROM withdrawals 
             WHERE user_id = ? AND (bank_name IS NOT NULL OR upi_id IS NOT NULL)
             ORDER BY created_at DESC 
             LIMIT 1`,
            [req.user.id]
        );

        res.json({
            success: true,
            data: {
                ...users[0],
                bankDetails: bankDetails.length > 0 ? bankDetails[0] : null
            }
        });

    } catch (error) {
        console.error('Get profile error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to fetch profile',
            error: error.message
        });
    }
});

// Update User Profile
router.put('/update', [
    verifyToken,
    body('name').optional().trim().isLength({ min: 3 }).withMessage('Name must be at least 3 characters'),
    body('email').optional().isEmail().normalizeEmail().withMessage('Invalid email address'),
    body('phone').optional().isMobilePhone().withMessage('Invalid phone number')
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

        const { name, email, phone } = req.body;
        const updates = [];
        const values = [];

        if (name) {
            updates.push('name = ?');
            values.push(name);
        }

        if (email) {
            // Check if email is already taken by another user
            const [existingEmail] = await db.query(
                'SELECT id FROM users WHERE email = ? AND id != ?',
                [email, req.user.id]
            );

            if (existingEmail.length > 0) {
                return res.status(400).json({
                    success: false,
                    message: 'Email already in use'
                });
            }

            updates.push('email = ?');
            values.push(email);
        }

        if (phone) {
            // Check if phone is already taken by another user
            const [existingPhone] = await db.query(
                'SELECT id FROM users WHERE phone = ? AND id != ?',
                [phone, req.user.id]
            );

            if (existingPhone.length > 0) {
                return res.status(400).json({
                    success: false,
                    message: 'Phone number already in use'
                });
            }

            updates.push('phone = ?');
            values.push(phone);
        }

        if (updates.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'No fields to update'
            });
        }

        values.push(req.user.id);

        await db.query(
            `UPDATE users SET ${updates.join(', ')} WHERE id = ?`,
            values
        );

        res.json({
            success: true,
            message: 'Profile updated successfully'
        });

    } catch (error) {
        console.error('Update profile error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to update profile',
            error: error.message
        });
    }
});

// Update Bank Account Details
router.put('/bank-details', [
    verifyToken,
    body('bankName').optional().trim().notEmpty().withMessage('Bank name is required'),
    body('accountNumber').optional().trim().notEmpty().withMessage('Account number is required'),
    body('ifscCode').optional().trim().notEmpty().withMessage('IFSC code is required'),
    body('accountHolderName').optional().trim().notEmpty().withMessage('Account holder name is required'),
    body('upiId').optional().trim().notEmpty().withMessage('UPI ID is required')
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

        const { bankName, accountNumber, ifscCode, accountHolderName, upiId } = req.body;

        // Store bank details in a metadata table or update user profile
        // For now, we'll create a dummy withdrawal record to store the details
        // In production, you should create a separate bank_accounts table

        await db.query(
            `INSERT INTO withdrawals 
             (user_id, amount, bank_name, account_number, ifsc_code, account_holder_name, upi_id, payment_method, status) 
             VALUES (?, 0, ?, ?, ?, ?, ?, 'bank_transfer', 'cancelled')
             ON DUPLICATE KEY UPDATE 
             bank_name = VALUES(bank_name),
             account_number = VALUES(account_number),
             ifsc_code = VALUES(ifsc_code),
             account_holder_name = VALUES(account_holder_name),
             upi_id = VALUES(upi_id)`,
            [req.user.id, bankName || null, accountNumber || null, ifscCode || null, accountHolderName || null, upiId || null]
        );

        res.json({
            success: true,
            message: 'Bank details updated successfully'
        });

    } catch (error) {
        console.error('Update bank details error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to update bank details',
            error: error.message
        });
    }
});

// Change Password
router.put('/change-password', [
    verifyToken,
    body('currentPassword').notEmpty().withMessage('Current password is required'),
    body('newPassword').isLength({ min: 8 }).withMessage('New password must be at least 8 characters')
        .matches(/^(?=.*[A-Z])(?=.*\d)/).withMessage('Password must contain at least one uppercase letter and one number'),
    body('confirmPassword').custom((value, { req }) => {
        if (value !== req.body.newPassword) {
            throw new Error('Passwords do not match');
        }
        return true;
    })
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

        const { currentPassword, newPassword } = req.body;

        // Get current password hash
        const [users] = await db.query(
            'SELECT password FROM users WHERE id = ?',
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        // Verify current password
        const isPasswordValid = await bcrypt.compare(currentPassword, users[0].password);

        if (!isPasswordValid) {
            return res.status(401).json({
                success: false,
                message: 'Current password is incorrect'
            });
        }

        // Hash new password
        const hashedPassword = await bcrypt.hash(newPassword, 10);

        // Update password
        await db.query(
            'UPDATE users SET password = ? WHERE id = ?',
            [hashedPassword, req.user.id]
        );

        // Create notification
        await db.query(
            `INSERT INTO notifications (user_id, title, message, notification_type) 
             VALUES (?, ?, ?, ?)`,
            [req.user.id, 'Password Changed', 'Your password has been changed successfully.', 'success']
        );

        res.json({
            success: true,
            message: 'Password changed successfully'
        });

    } catch (error) {
        console.error('Change password error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to change password',
            error: error.message
        });
    }
});

// Upload Profile Image
router.post('/upload-image', verifyToken, upload.single('profileImage'), async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({
                success: false,
                message: 'No image file uploaded'
            });
        }

        // Get old profile image to delete it
        const [users] = await db.query(
            'SELECT profile_image FROM users WHERE id = ?',
            [req.user.id]
        );

        const oldImage = users[0]?.profile_image;

        // Update database with new image path
        const imagePath = `/uploads/profiles/${req.file.filename}`;
        await db.query(
            'UPDATE users SET profile_image = ? WHERE id = ?',
            [imagePath, req.user.id]
        );

        // Delete old image file if it exists
        if (oldImage) {
            const oldImagePath = path.join(__dirname, '..', oldImage);
            if (fs.existsSync(oldImagePath)) {
                fs.unlinkSync(oldImagePath);
            }
        }

        res.json({
            success: true,
            message: 'Profile image uploaded successfully',
            data: {
                profileImage: imagePath
            }
        });

    } catch (error) {
        // Delete uploaded file if database update fails
        if (req.file) {
            const filePath = req.file.path;
            if (fs.existsSync(filePath)) {
                fs.unlinkSync(filePath);
            }
        }

        console.error('Upload profile image error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to upload profile image',
            error: error.message
        });
    }
});

// Delete Profile Image
router.delete('/delete-image', verifyToken, async (req, res) => {
    try {
        // Get current profile image
        const [users] = await db.query(
            'SELECT profile_image FROM users WHERE id = ?',
            [req.user.id]
        );

        const profileImage = users[0]?.profile_image;

        if (!profileImage) {
            return res.status(404).json({
                success: false,
                message: 'No profile image to delete'
            });
        }

        // Delete from database
        await db.query(
            'UPDATE users SET profile_image = NULL WHERE id = ?',
            [req.user.id]
        );

        // Delete file
        const imagePath = path.join(__dirname, '..', profileImage);
        if (fs.existsSync(imagePath)) {
            fs.unlinkSync(imagePath);
        }

        res.json({
            success: true,
            message: 'Profile image deleted successfully'
        });

    } catch (error) {
        console.error('Delete profile image error:', error);
        res.status(500).json({
            success: false,
            message: 'Failed to delete profile image',
            error: error.message
        });
    }
});

module.exports = router;
