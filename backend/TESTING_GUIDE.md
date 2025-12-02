# API Testing Guide

## Prerequisites
- Postman installed (or any API testing tool)
- Backend server running
- MySQL database setup complete

## Quick Start

### 1. Import Postman Collection

1. Open Postman
2. Click **Import**
3. Select `postman_collection.json`
4. Collection will be imported with all endpoints

### 2. Set Environment Variables

In Postman, create environment variables:
- `base_url`: `http://localhost:3000` (or your server URL)
- `token`: (will be set after login)
- `admin_token`: (will be set after admin login)

---

## Test Scenarios

### Scenario 1: User Registration & Login

#### Test 1.1: Register New User
```
POST {{base_url}}/api/auth/register

Body:
{
  "name": "Test User",
  "email": "test@example.com",
  "phone": "9876543210",
  "password": "Test@123"
}

Expected Response (201):
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "userId": 1,
    "name": "Test User",
    "email": "test@example.com",
    "referralCode": "PAYC12345678",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Save the token** to environment variable `token`

#### Test 1.2: Login User
```
POST {{base_url}}/api/auth/login

Body:
{
  "email": "test@example.com",
  "password": "Test@123"
}

Expected Response (200):
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "name": "Test User",
    "email": "test@example.com",
    "walletBalance": 0,
    "referralCode": "PAYC12345678",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### Test 1.3: Verify Token
```
POST {{base_url}}/api/auth/verify-token

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
{
  "success": true,
  "message": "Token is valid",
  "data": {
    "userId": 1,
    "name": "Test User",
    "email": "test@example.com",
    "walletBalance": 0,
    "referralCode": "PAYC12345678"
  }
}
```

---

### Scenario 2: Payment Flow

#### Test 2.1: Create Payment Order
```
POST {{base_url}}/api/payment/create-order

Headers:
Authorization: Bearer {{token}}

Body:
{
  "amount": 1000
}

Expected Response (200):
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "order_xxxxxxxxxxxxx",
    "amount": 1000,
    "currency": "INR",
    "key": "rzp_test_xxxxxxxxxxxxx"
  }
}
```

**Save orderId for next step**

#### Test 2.2: Verify Payment (After Razorpay Payment)
```
POST {{base_url}}/api/payment/verify

Headers:
Authorization: Bearer {{token}}

Body:
{
  "razorpay_order_id": "order_xxxxxxxxxxxxx",
  "razorpay_payment_id": "pay_xxxxxxxxxxxxx",
  "razorpay_signature": "generated_signature"
}

Expected Response (200):
{
  "success": true,
  "message": "Payment verified successfully",
  "data": {
    "amount": 1000,
    "newBalance": 1000,
    "transactionId": 1
  }
}
```

---

### Scenario 3: Task Completion Flow

#### Test 3.1: Get Available Tasks
```
GET {{base_url}}/api/tasks/list

Headers:
Authorization: Bearer {{token}}

Expected Response (200):
{
  "success": true,
  "data": {
    "tasks": [
      {
        "id": 1,
        "title": "Watch Product Video",
        "description": "Watch a 2-minute product demonstration video",
        "task_type": "video",
        "reward": 10,
        "duration_minutes": 2,
        "company_name": "TechCorp",
        "isCompleted": false
      }
    ]
  }
}
```

#### Test 3.2: Start Task
```
POST {{base_url}}/api/tasks/start

Headers:
Authorization: Bearer {{token}}

Body:
{
  "taskId": 1
}

Expected Response (200):
{
  "success": true,
  "message": "Task started successfully",
  "data": {
    "taskHistoryId": 1,
    "taskId": 1,
    "title": "Watch Product Video",
    "reward": 10
  }
}
```

**Save taskHistoryId for next step**

#### Test 3.3: Complete Task
```
POST {{base_url}}/api/tasks/complete

Headers:
Authorization: Bearer {{token}}

Body:
{
  "taskHistoryId": 1
}

Expected Response (200):
{
  "success": true,
  "message": "Task completed successfully",
  "data": {
    "reward": 10,
    "newBalance": 1010
  }
}
```

---

### Scenario 4: Plan Purchase

#### Test 4.1: Get All Plans
```
GET {{base_url}}/api/plans/list

Expected Response (200):
{
  "success": true,
  "data": {
    "plans": [
      {
        "id": 1,
        "name": "Economy",
        "description": "Perfect for beginners",
        "price": 15000,
        "dailyProfit": 333,
        "durationDays": 30,
        "totalReturn": 10000,
        "minWithdrawal": 100,
        "status": "active"
      }
    ]
  }
}
```

#### Test 4.2: Purchase Plan
```
POST {{base_url}}/api/plans/purchase

Headers:
Authorization: Bearer {{token}}

Body:
{
  "planId": 1
}

Expected Response (200):
{
  "success": true,
  "message": "Plan purchased successfully",
  "data": {
    "userPlanId": 1,
    "planName": "Economy",
    "dailyProfit": 333,
    "durationDays": 30,
    "newBalance": -14000
  }
}
```

---

### Scenario 5: Withdrawal Flow

#### Test 5.1: Request Withdrawal
```
POST {{base_url}}/api/wallet/withdraw

Headers:
Authorization: Bearer {{token}}

Body:
{
  "amount": 500,
  "paymentMethod": "upi",
  "accountDetails": "test@upi"
}

Expected Response (200):
{
  "success": true,
  "message": "Withdrawal request submitted successfully",
  "data": {
    "withdrawalId": 1,
    "amount": 500,
    "newBalance": 510,
    "status": "pending"
  }
}
```

---

### Scenario 6: Admin Operations

#### Test 6.1: Admin Login
```
POST {{base_url}}/api/admin/login

Body:
{
  "username": "admin",
  "password": "Admin@123"
}

Expected Response (200):
{
  "success": true,
  "message": "Login successful",
  "data": {
    "adminId": 1,
    "username": "admin",
    "role": "super_admin",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Save token to `admin_token`**

#### Test 6.2: Get Dashboard Stats
```
GET {{base_url}}/api/admin/stats

Headers:
Authorization: Bearer {{admin_token}}

Expected Response (200):
{
  "success": true,
  "data": {
    "users": {
      "total": 10,
      "totalBalance": 50000
    },
    "todayTransactions": {
      "deposits": 10000,
      "withdrawals": 5000,
      "count": 25
    },
    "pendingWithdrawals": {
      "count": 3,
      "total": 1500
    },
    "activePlans": 5
  }
}
```

#### Test 6.3: Approve Withdrawal
```
POST {{base_url}}/api/admin/approve-withdrawal

Headers:
Authorization: Bearer {{admin_token}}

Body:
{
  "withdrawalId": 1,
  "action": "approve",
  "adminNote": "Verified and approved"
}

Expected Response (200):
{
  "success": true,
  "message": "Withdrawal approved successfully"
}
```

---

## Error Testing

### Test Invalid Email Format
```
POST {{base_url}}/api/auth/register

Body:
{
  "name": "Test",
  "email": "invalid-email",
  "password": "Test@123"
}

Expected Response (400):
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "msg": "Invalid email address",
      "param": "email"
    }
  ]
}
```

### Test Insufficient Balance
```
POST {{base_url}}/api/wallet/withdraw

Headers:
Authorization: Bearer {{token}}

Body:
{
  "amount": 999999,
  "paymentMethod": "upi",
  "accountDetails": "test@upi"
}

Expected Response (400):
{
  "success": false,
  "message": "Insufficient balance"
}
```

### Test Unauthorized Access
```
GET {{base_url}}/api/wallet/balance

Headers:
Authorization: Bearer invalid_token

Expected Response (401):
{
  "success": false,
  "message": "Invalid token."
}
```

---

## Load Testing

Use Apache Bench or Artillery for load testing:

```bash
# Install Artillery
npm install -g artillery

# Create test scenario (test.yml)
config:
  target: 'http://localhost:3000'
  phases:
    - duration: 60
      arrivalRate: 10

scenarios:
  - name: "Get Plans"
    flow:
      - get:
          url: "/api/plans/list"

# Run test
artillery run test.yml
```

---

## Automated Testing Script

Create `test.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:3000"

echo "Testing Health Endpoint..."
curl -X GET $BASE_URL/health

echo "\nTesting User Registration..."
curl -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"Test@123"}'

echo "\nTesting Login..."
curl -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@123"}'

echo "\nAll tests completed!"
```

---

## Checklist

- [ ] Health endpoint returns 200
- [ ] User registration works
- [ ] User login returns JWT token
- [ ] Token verification works
- [ ] Payment order creation works
- [ ] Task listing works
- [ ] Task start/complete flow works
- [ ] Plan listing works
- [ ] Plan purchase works
- [ ] Withdrawal request works
- [ ] Admin login works
- [ ] Admin can approve withdrawals
- [ ] Error handling works correctly
- [ ] Rate limiting is active
- [ ] CORS is configured properly

---

## Common Issues

### Issue: "Database connection failed"
**Solution**: Check MySQL credentials in `.env` file

### Issue: "Token expired"
**Solution**: Login again to get a new token

### Issue: "Payment verification failed"
**Solution**: Ensure Razorpay keys are correct and signature is valid

### Issue: "CORS error"
**Solution**: Add your frontend URL to `FRONTEND_URL` in `.env`

---

## Support

For testing issues, check:
1. Server logs: `pm2 logs payc-api`
2. Database connectivity
3. Environment variables
4. API endpoint URLs
