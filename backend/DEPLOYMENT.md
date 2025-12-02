# Deployment Guide

## Table of Contents
1. [cPanel Deployment](#cpanel-deployment)
2. [VPS Deployment (Ubuntu)](#vps-deployment)
3. [Database Setup](#database-setup)
4. [SSL Certificate](#ssl-certificate)
5. [Environment Variables](#environment-variables)

---

## cPanel Deployment

### Prerequisites
- cPanel hosting with Node.js support
- MySQL database access
- SSH access (optional but recommended)

### Step 1: Upload Files

1. Compress your backend folder:
```bash
zip -r backend.zip backend/
```

2. Upload via cPanel File Manager or FTP
3. Extract the files in your desired directory (e.g., `/home/username/backend`)

### Step 2: Setup Node.js Application

1. Go to **cPanel → Setup Node.js App**
2. Click **Create Application**
3. Configure:
   - **Node.js version**: 18.x or higher
   - **Application mode**: Production
   - **Application root**: `/home/username/backend`
   - **Application URL**: `api.yourdomain.com`
   - **Application startup file**: `server.js`

### Step 3: Install Dependencies

1. Click **Run NPM Install** in cPanel
   OR use terminal:
```bash
cd /home/username/backend
npm install --production
```

### Step 4: Setup Database

1. Go to **cPanel → MySQL Databases**
2. Create a new database: `username_payc_db`
3. Create a database user and assign to database
4. Import schema:
```bash
mysql -u username_payc -p username_payc_db < database/schema.sql
```

### Step 5: Configure Environment

1. Create `.env` file in backend directory
2. Copy from `.env.example` and update values:
```env
PORT=3000
NODE_ENV=production
DB_HOST=localhost
DB_USER=username_payc
DB_PASSWORD=your_password
DB_NAME=username_payc_db
JWT_SECRET=your_secure_secret_key
RAZORPAY_KEY_ID=your_razorpay_key
RAZORPAY_KEY_SECRET=your_razorpay_secret
```

### Step 6: Start Application

1. In cPanel Node.js App interface, click **Restart**
2. Check application status
3. Test API: `https://api.yourdomain.com/health`

---

## VPS Deployment (Ubuntu 20.04/22.04)

### Step 1: Initial Server Setup

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Node.js 18.x
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify installation
node --version
npm --version

# Install MySQL
sudo apt install mysql-server -y

# Secure MySQL installation
sudo mysql_secure_installation
```

### Step 2: Setup MySQL Database

```bash
# Login to MySQL
sudo mysql -u root -p

# Create database and user
CREATE DATABASE payc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'payc_user'@'localhost' IDENTIFIED BY 'strong_password_here';
GRANT ALL PRIVILEGES ON payc_db.* TO 'payc_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Import schema
mysql -u payc_user -p payc_db < /path/to/backend/database/schema.sql
```

### Step 3: Deploy Application

```bash
# Create app directory
sudo mkdir -p /var/www/payc-api
cd /var/www/payc-api

# Clone or upload your code
git clone https://github.com/yourusername/payc-backend.git .
# OR upload via SCP/SFTP

# Install dependencies
npm install --production

# Create .env file
nano .env
# Paste your environment variables

# Test the application
npm start
```

### Step 4: Setup PM2 (Process Manager)

```bash
# Install PM2 globally
sudo npm install -g pm2

# Start application with PM2
pm2 start server.js --name payc-api

# Setup PM2 to start on boot
pm2 startup systemd
pm2 save

# Useful PM2 commands
pm2 status          # Check status
pm2 logs payc-api   # View logs
pm2 restart payc-api # Restart app
pm2 stop payc-api   # Stop app
```

### Step 5: Setup Nginx Reverse Proxy

```bash
# Install Nginx
sudo apt install nginx -y

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/payc-api

# Add this configuration:
```

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

```bash
# Enable the site
sudo ln -s /etc/nginx/sites-available/payc-api /etc/nginx/sites-enabled/

# Test Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

### Step 6: Setup SSL with Let's Encrypt

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d api.yourdomain.com

# Certbot will automatically configure Nginx for HTTPS

# Test auto-renewal
sudo certbot renew --dry-run
```

### Step 7: Setup Firewall

```bash
# Allow SSH, HTTP, and HTTPS
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw enable

# Check status
sudo ufw status
```

---

## Database Backup

### Automated Daily Backup Script

Create `/home/username/backup-db.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/home/username/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="payc_db"
DB_USER="payc_user"
DB_PASS="your_password"

mkdir -p $BACKUP_DIR

mysqldump -u $DB_USER -p$DB_PASS $DB_NAME | gzip > $BACKUP_DIR/payc_db_$DATE.sql.gz

# Delete backups older than 7 days
find $BACKUP_DIR -name "payc_db_*.sql.gz" -mtime +7 -delete
```

```bash
# Make executable
chmod +x /home/username/backup-db.sh

# Add to crontab (daily at 2 AM)
crontab -e
# Add this line:
0 2 * * * /home/username/backup-db.sh
```

---

## Monitoring

### Setup PM2 Monitoring

```bash
# Monitor CPU and Memory
pm2 monit

# Setup PM2 Plus (optional - cloud monitoring)
pm2 link <secret_key> <public_key>
```

### Setup Log Rotation

```bash
# Install PM2 log rotate
pm2 install pm2-logrotate

# Configure
pm2 set pm2-logrotate:max_size 10M
pm2 set pm2-logrotate:retain 7
```

---

## Troubleshooting

### Check Application Logs
```bash
# PM2 logs
pm2 logs payc-api --lines 100

# Nginx logs
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/nginx/access.log
```

### Restart Services
```bash
# Restart application
pm2 restart payc-api

# Restart Nginx
sudo systemctl restart nginx

# Restart MySQL
sudo systemctl restart mysql
```

### Check Service Status
```bash
# Check PM2
pm2 status

# Check Nginx
sudo systemctl status nginx

# Check MySQL
sudo systemctl status mysql
```

---

## Security Checklist

- [ ] Change default MySQL root password
- [ ] Use strong JWT secret (min 32 characters)
- [ ] Enable firewall (UFW)
- [ ] Setup SSL certificate
- [ ] Disable root SSH login
- [ ] Setup automated backups
- [ ] Keep system updated
- [ ] Monitor logs regularly
- [ ] Use environment variables for secrets
- [ ] Enable rate limiting
- [ ] Setup fail2ban (optional)

---

## Performance Optimization

### Enable Gzip in Nginx

Add to Nginx config:
```nginx
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
```

### Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_transaction_user ON transactions(user_id, created_at);

-- Optimize tables
OPTIMIZE TABLE users;
OPTIMIZE TABLE transactions;
```

---

## Support

For issues:
1. Check logs first
2. Verify environment variables
3. Test database connection
4. Check firewall rules
5. Contact support: support@payc.com
