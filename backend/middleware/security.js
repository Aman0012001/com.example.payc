const rateLimit = require('express-rate-limit');
const crypto = require('crypto');

// Strict rate limiter for authentication endpoints
const authLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 5, // 5 requests per window
    message: {
        success: false,
        message: 'Too many authentication attempts. Please try again after 15 minutes.'
    },
    standardHeaders: true,
    legacyHeaders: false,
    skipSuccessfulRequests: false
});

// General API rate limiter
const apiLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100, // 100 requests per window
    message: {
        success: false,
        message: 'Too many requests. Please try again later.'
    },
    standardHeaders: true,
    legacyHeaders: false
});

// Strict rate limiter for payment endpoints
const paymentLimiter = rateLimit({
    windowMs: 60 * 60 * 1000, // 1 hour
    max: 10, // 10 payment attempts per hour
    message: {
        success: false,
        message: 'Too many payment attempts. Please try again after 1 hour.'
    },
    standardHeaders: true,
    legacyHeaders: false
});

// Withdrawal rate limiter
const withdrawalLimiter = rateLimit({
    windowMs: 24 * 60 * 60 * 1000, // 24 hours
    max: 5, // 5 withdrawal requests per day
    message: {
        success: false,
        message: 'Daily withdrawal limit reached. Please try again tomorrow.'
    },
    standardHeaders: true,
    legacyHeaders: false
});

// Verify Razorpay webhook signature
const verifyRazorpayWebhook = (req, res, next) => {
    try {
        const webhookSecret = process.env.RAZORPAY_WEBHOOK_SECRET;
        const signature = req.headers['x-razorpay-signature'];

        if (!signature) {
            return res.status(400).json({
                success: false,
                message: 'Missing webhook signature'
            });
        }

        const body = JSON.stringify(req.body);
        const expectedSignature = crypto
            .createHmac('sha256', webhookSecret)
            .update(body)
            .digest('hex');

        if (signature !== expectedSignature) {
            console.error('Webhook signature verification failed');
            return res.status(400).json({
                success: false,
                message: 'Invalid webhook signature'
            });
        }

        next();
    } catch (error) {
        console.error('Webhook verification error:', error);
        return res.status(500).json({
            success: false,
            message: 'Webhook verification failed'
        });
    }
};

// Sanitize request body to prevent injection
const sanitizeBody = (req, res, next) => {
    if (req.body) {
        Object.keys(req.body).forEach(key => {
            if (typeof req.body[key] === 'string') {
                // Remove potential SQL injection characters
                req.body[key] = req.body[key]
                    .replace(/['"`;\\]/g, '')
                    .trim();
            }
        });
    }
    next();
};

// Prevent negative amounts in financial operations
const validatePositiveAmount = (req, res, next) => {
    const amount = req.body.amount;

    if (amount !== undefined) {
        const numAmount = parseFloat(amount);

        if (isNaN(numAmount) || numAmount <= 0) {
            return res.status(400).json({
                success: false,
                message: 'Amount must be a positive number'
            });
        }

        // Ensure amount is not too large (prevent overflow)
        if (numAmount > 10000000) {
            return res.status(400).json({
                success: false,
                message: 'Amount exceeds maximum limit'
            });
        }

        // Round to 2 decimal places
        req.body.amount = Math.round(numAmount * 100) / 100;
    }

    next();
};

// Check for duplicate request (idempotency)
const requestCache = new Map();

const preventDuplicateRequests = (req, res, next) => {
    const userId = req.user?.id;
    const endpoint = req.path;
    const key = `${userId}_${endpoint}_${JSON.stringify(req.body)}`;

    const now = Date.now();
    const cached = requestCache.get(key);

    // If same request within 5 seconds, reject
    if (cached && (now - cached) < 5000) {
        return res.status(429).json({
            success: false,
            message: 'Duplicate request detected. Please wait before retrying.'
        });
    }

    requestCache.set(key, now);

    // Clean up old entries (older than 10 seconds)
    for (const [k, timestamp] of requestCache.entries()) {
        if (now - timestamp > 10000) {
            requestCache.delete(k);
        }
    }

    next();
};

// HTTPS enforcement in production
const enforceHTTPS = (req, res, next) => {
    if (process.env.NODE_ENV === 'production' && !req.secure && req.get('x-forwarded-proto') !== 'https') {
        return res.redirect(301, `https://${req.get('host')}${req.url}`);
    }
    next();
};

// Security headers
const securityHeaders = (req, res, next) => {
    // Prevent clickjacking
    res.setHeader('X-Frame-Options', 'DENY');

    // Prevent MIME sniffing
    res.setHeader('X-Content-Type-Options', 'nosniff');

    // Enable XSS protection
    res.setHeader('X-XSS-Protection', '1; mode=block');

    // Strict transport security
    if (process.env.NODE_ENV === 'production') {
        res.setHeader('Strict-Transport-Security', 'max-age=31536000; includeSubDomains');
    }

    next();
};

module.exports = {
    authLimiter,
    apiLimiter,
    paymentLimiter,
    withdrawalLimiter,
    verifyRazorpayWebhook,
    sanitizeBody,
    validatePositiveAmount,
    preventDuplicateRequests,
    enforceHTTPS,
    securityHeaders
};
