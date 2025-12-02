const jwt = require('jsonwebtoken');
const db = require('../config/database');

// Verify JWT Token
const verifyToken = async (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];

        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Access denied. No token provided.'
            });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // Check if user exists and is active
        const [users] = await db.query(
            'SELECT id, email, name, status FROM users WHERE id = ? AND status = "active"',
            [decoded.userId]
        );

        if (users.length === 0) {
            return res.status(401).json({
                success: false,
                message: 'Invalid token or user not found.'
            });
        }

        req.user = users[0];
        next();
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({
                success: false,
                message: 'Token expired. Please login again.'
            });
        }
        return res.status(401).json({
            success: false,
            message: 'Invalid token.'
        });
    }
};

// Verify Admin Token
const verifyAdmin = async (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];

        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Access denied. No token provided.'
            });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // Check if admin exists and is active
        const [admins] = await db.query(
            'SELECT id, username, email, role FROM admin_users WHERE id = ? AND status = "active"',
            [decoded.adminId]
        );

        if (admins.length === 0) {
            return res.status(403).json({
                success: false,
                message: 'Access denied. Admin privileges required.'
            });
        }

        req.admin = admins[0];
        next();
    } catch (error) {
        return res.status(401).json({
            success: false,
            message: 'Invalid admin token.'
        });
    }
};

module.exports = { verifyToken, verifyAdmin };
