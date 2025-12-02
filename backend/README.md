# PayC Backend API Documentation

## Technology Stack
- **Runtime**: Node.js v18+
- **Framework**: Express.js
- **Database**: MySQL 8.0
- **Authentication**: JWT (JSON Web Tokens)
- **Payment Gateway**: Razorpay
- **Security**: bcrypt, helmet, express-rate-limit

## Quick Start

### Prerequisites
- Node.js 18+ installed
- MySQL 8.0+ installed
- Razorpay account (for payment integration)

### Installation

```bash
cd backend
npm install
```

### Environment Setup

Create a `.env` file in the backend directory:

```env
# Server Configuration
PORT=3000
NODE_ENV=production

# Database Configuration
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=payc_db
DB_PORT=3306

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_change_this_in_production
JWT_EXPIRES_IN=7d

# Razorpay Configuration
RAZORPAY_KEY_ID=rzp_test_xxxxxxxxxxxxx
RAZORPAY_KEY_SECRET=your_razorpay_secret_key

# App Configuration
APP_URL=http://localhost:3000
FRONTEND_URL=http://localhost:8080

# Email Configuration (Optional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your_email@gmail.com
SMTP_PASS=your_app_password
```

### Database Setup

```bash
# Create database
mysql -u root -p < database/schema.sql

# Run migrations (optional)
npm run migrate
```

### Run Server

```bash
# Development
npm run dev

# Production
npm start
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/verify-token` - Verify JWT token
- `POST /api/auth/refresh-token` - Refresh JWT token

### Wallet
- `GET /api/wallet/balance` - Get wallet balance
- `POST /api/wallet/deposit` - Initiate deposit
- `POST /api/wallet/withdraw` - Request withdrawal
- `GET /api/wallet/transactions` - Get transaction history

### Tasks
- `GET /api/tasks/list` - Get available tasks
- `POST /api/tasks/start` - Start a task
- `POST /api/tasks/complete` - Complete a task
- `GET /api/tasks/history` - Get task history

### Plans
- `GET /api/plans/list` - Get all active plans
- `GET /api/plans/:id` - Get plan details
- `POST /api/plans/purchase` - Purchase a plan

### Admin
- `POST /api/admin/login` - Admin login
- `GET /api/admin/stats` - Dashboard statistics
- `POST /api/admin/approve-withdrawal` - Approve withdrawal
- `POST /api/admin/add-plan` - Add new plan
- `PUT /api/admin/update-plan/:id` - Update plan
- `DELETE /api/admin/delete-plan/:id` - Delete plan
- `GET /api/admin/users` - Get all users
- `GET /api/admin/withdrawals` - Get pending withdrawals

### Payment
- `POST /api/payment/create-order` - Create Razorpay order
- `POST /api/payment/verify` - Verify payment
- `POST /api/payment/webhook` - Razorpay webhook

## Security Features

1. **JWT Authentication** - Secure token-based auth
2. **Password Hashing** - bcrypt with salt rounds
3. **SQL Injection Prevention** - Parameterized queries
4. **CORS Protection** - Configured for specific origins
5. **Rate Limiting** - Prevent brute force attacks
6. **Input Validation** - Express-validator
7. **Helmet.js** - Security headers
8. **HTTPS Only** - Force SSL in production

## Deployment

### cPanel Deployment
1. Upload files via FTP/File Manager
2. Create MySQL database
3. Import schema.sql
4. Configure .env file
5. Install Node.js via cPanel
6. Run `npm install --production`
7. Setup Node.js app in cPanel

### VPS Deployment (Ubuntu)
```bash
# Install Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install MySQL
sudo apt-get install mysql-server

# Clone and setup
git clone your-repo
cd backend
npm install --production

# Setup PM2
npm install -g pm2
pm2 start server.js --name payc-api
pm2 startup
pm2 save

# Setup Nginx reverse proxy
sudo nano /etc/nginx/sites-available/payc
```

## Testing

```bash
# Run tests
npm test

# Test specific endpoint
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## Support

For issues and questions, contact: support@payc.com
