const { body, param, query, validationResult } = require('express-validator');

// Validation error handler
const handleValidationErrors = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({
            success: false,
            message: 'Validation failed',
            errors: errors.array().map(err => ({
                field: err.param,
                message: err.msg
            }))
        });
    }
    next();
};

// Sanitize input to prevent XSS and injection
const sanitizeInput = (value) => {
    if (typeof value === 'string') {
        return value.trim().replace(/[<>]/g, '');
    }
    return value;
};

// Registration validation
const validateRegistration = [
    body('name')
        .trim()
        .notEmpty().withMessage('Name is required')
        .isLength({ min: 3, max: 50 }).withMessage('Name must be 3-50 characters')
        .matches(/^[a-zA-Z\s]+$/).withMessage('Name can only contain letters and spaces')
        .customSanitizer(sanitizeInput),

    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format')
        .normalizeEmail()
        .isLength({ max: 100 }).withMessage('Email too long')
        .customSanitizer(sanitizeInput),

    body('phone')
        .optional()
        .trim()
        .matches(/^[0-9]{10,15}$/).withMessage('Phone must be 10-15 digits')
        .customSanitizer(sanitizeInput),

    body('password')
        .notEmpty().withMessage('Password is required')
        .isLength({ min: 8, max: 128 }).withMessage('Password must be 8-128 characters')
        .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/).withMessage('Password must contain uppercase, lowercase, and number')
        .customSanitizer(sanitizeInput),

    body('referralCode')
        .optional()
        .trim()
        .isLength({ min: 8, max: 20 }).withMessage('Invalid referral code')
        .matches(/^[A-Z0-9]+$/).withMessage('Referral code must be alphanumeric uppercase')
        .customSanitizer(sanitizeInput),

    handleValidationErrors
];

// Login validation
const validateLogin = [
    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format')
        .normalizeEmail()
        .customSanitizer(sanitizeInput),

    body('password')
        .notEmpty().withMessage('Password is required')
        .isLength({ min: 1, max: 128 }).withMessage('Invalid password')
        .customSanitizer(sanitizeInput),

    handleValidationErrors
];

// Withdrawal validation
const validateWithdrawal = [
    body('amount')
        .notEmpty().withMessage('Amount is required')
        .isFloat({ min: 100, max: 100000 }).withMessage('Amount must be between ₹100 and ₹100,000')
        .custom((value) => {
            if (value <= 0) throw new Error('Amount must be positive');
            if (value % 1 !== 0) throw new Error('Amount must be a whole number');
            return true;
        }),

    body('paymentMethod')
        .notEmpty().withMessage('Payment method is required')
        .isIn(['bank_transfer', 'upi', 'paytm', 'phonepe']).withMessage('Invalid payment method'),

    body('accountDetails')
        .notEmpty().withMessage('Account details are required')
        .isLength({ min: 3, max: 100 }).withMessage('Account details must be 3-100 characters')
        .customSanitizer(sanitizeInput),

    handleValidationErrors
];

// Payment order validation
const validatePaymentOrder = [
    body('amount')
        .notEmpty().withMessage('Amount is required')
        .isFloat({ min: 100, max: 100000 }).withMessage('Amount must be between ₹100 and ₹100,000')
        .custom((value) => {
            if (value <= 0) throw new Error('Amount must be positive');
            return true;
        }),

    handleValidationErrors
];

// Payment verification validation
const validatePaymentVerification = [
    body('razorpay_order_id')
        .notEmpty().withMessage('Order ID is required')
        .isString().withMessage('Invalid order ID')
        .matches(/^order_[a-zA-Z0-9]+$/).withMessage('Invalid order ID format')
        .customSanitizer(sanitizeInput),

    body('razorpay_payment_id')
        .notEmpty().withMessage('Payment ID is required')
        .isString().withMessage('Invalid payment ID')
        .matches(/^pay_[a-zA-Z0-9]+$/).withMessage('Invalid payment ID format')
        .customSanitizer(sanitizeInput),

    body('razorpay_signature')
        .notEmpty().withMessage('Signature is required')
        .isString().withMessage('Invalid signature')
        .isLength({ min: 64, max: 256 }).withMessage('Invalid signature length')
        .customSanitizer(sanitizeInput),

    handleValidationErrors
];

// Task start validation
const validateTaskStart = [
    body('taskId')
        .notEmpty().withMessage('Task ID is required')
        .isInt({ min: 1 }).withMessage('Invalid task ID'),

    handleValidationErrors
];

// Task complete validation
const validateTaskComplete = [
    body('taskHistoryId')
        .notEmpty().withMessage('Task history ID is required')
        .isInt({ min: 1 }).withMessage('Invalid task history ID'),

    handleValidationErrors
];

// Plan purchase validation
const validatePlanPurchase = [
    body('planId')
        .notEmpty().withMessage('Plan ID is required')
        .isInt({ min: 1 }).withMessage('Invalid plan ID'),

    handleValidationErrors
];

// Admin withdrawal approval validation
const validateWithdrawalApproval = [
    body('withdrawalId')
        .notEmpty().withMessage('Withdrawal ID is required')
        .isInt({ min: 1 }).withMessage('Invalid withdrawal ID'),

    body('action')
        .notEmpty().withMessage('Action is required')
        .isIn(['approve', 'reject']).withMessage('Action must be approve or reject'),

    body('adminNote')
        .optional()
        .trim()
        .isLength({ max: 500 }).withMessage('Admin note too long')
        .customSanitizer(sanitizeInput),

    handleValidationErrors
];

// Add plan validation (admin)
const validateAddPlan = [
    body('name')
        .trim()
        .notEmpty().withMessage('Plan name is required')
        .isLength({ min: 3, max: 50 }).withMessage('Plan name must be 3-50 characters')
        .customSanitizer(sanitizeInput),

    body('description')
        .optional()
        .trim()
        .isLength({ max: 500 }).withMessage('Description too long')
        .customSanitizer(sanitizeInput),

    body('price')
        .notEmpty().withMessage('Price is required')
        .isFloat({ min: 100, max: 10000000 }).withMessage('Invalid price'),

    body('dailyProfit')
        .notEmpty().withMessage('Daily profit is required')
        .isFloat({ min: 1, max: 1000000 }).withMessage('Invalid daily profit'),

    body('durationDays')
        .notEmpty().withMessage('Duration is required')
        .isInt({ min: 1, max: 365 }).withMessage('Duration must be 1-365 days'),

    body('minWithdrawal')
        .optional()
        .isFloat({ min: 100 }).withMessage('Minimum withdrawal must be at least ₹100'),

    handleValidationErrors
];

// Pagination validation
const validatePagination = [
    query('page')
        .optional()
        .isInt({ min: 1 }).withMessage('Page must be a positive integer'),

    query('limit')
        .optional()
        .isInt({ min: 1, max: 100 }).withMessage('Limit must be 1-100'),

    handleValidationErrors
];

module.exports = {
    validateRegistration,
    validateLogin,
    validateWithdrawal,
    validatePaymentOrder,
    validatePaymentVerification,
    validateTaskStart,
    validateTaskComplete,
    validatePlanPurchase,
    validateWithdrawalApproval,
    validateAddPlan,
    validatePagination,
    handleValidationErrors
};
