# Useful Commands Summary

Complete command reference for PayC Backend development and deployment.

---

## 📋 Table of Contents
1. [Local Development](#local-development)
2. [Production Deployment](#production-deployment)
3. [Database Management](#database-management)
4. [PM2 Process Manager](#pm2-process-manager)
5. [Nginx Web Server](#nginx-web-server)
6. [SSL Certificate Management](#ssl-certificate-management)
7. [Server Maintenance](#server-maintenance)
8. [Debugging & Monitoring](#debugging--monitoring)

---

## 🖥️ LOCAL DEVELOPMENT

### Install Dependencies
```bash
# Navigate to backend directory
cd c:/Users/amanj/OneDrive/Desktop/payc/backend

# Install all dependencies
npm install

# Install specific dependency
npm install express

# Install dev dependencies
npm install --save-dev nodemon
```

### Setup MySQL Database (Windows)
```bash
# Start MySQL service (Windows)
net start MySQL80

# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE payc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create user
CREATE USER 'payc_user'@'localhost' IDENTIFIED BY 'your_password';

# Grant permissions
GRANT ALL PRIVILEGES ON payc_db.* TO 'payc_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Import Database Schema
```bash
# Import schema.sql
mysql -u payc_user -p payc_db < c:/Users/amanj/OneDrive/Desktop/payc/backend/database/schema.sql

# Verify import
mysql -u payc_user -p payc_db -e "SHOW TABLES;"
```

### Run Backend Locally
```bash
# Navigate to backend
cd c:/Users/amanj/OneDrive/Desktop/payc/backend

# Start server (production mode)
npm start

# Start server (development mode with auto-reload)
npm run dev

# Test if server is running
curl http://localhost:3000/health
```

### Environment Setup
```bash
# Copy environment template
cp .env.example .env

# Edit environment file (Windows)
notepad .env

# Edit environment file (Linux/Mac)
nano .env
```

---

## 🚀 PRODUCTION DEPLOYMENT

### Initial Server Setup (Ubuntu 20.04/22.04)
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Node.js 18.x
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Verify installation
node --version
npm --version

# Install build essentials
sudo apt-get install -y build-essential
```

### Copy Backend to VPS via SCP
```bash
# From Windows (using PowerShell or Git Bash)
# Compress backend folder first
cd c:/Users/amanj/OneDrive/Desktop/payc
tar -czf backend.tar.gz backend/

# Copy to VPS
scp backend.tar.gz username@your-server-ip:/home/username/

# OR copy entire directory
scp -r c:/Users/amanj/OneDrive/Desktop/payc/backend username@your-server-ip:/var/www/

# Connect to VPS
ssh username@your-server-ip

# Extract on server
cd /home/username
tar -xzf backend.tar.gz
```

### Install Dependencies on Server
```bash
# Navigate to backend directory
cd /var/www/payc-api

# Install production dependencies only
npm install --production

# OR install all dependencies
npm install
```

---

## 🗄️ DATABASE MANAGEMENT

### MySQL Installation (Ubuntu)
```bash
# Install MySQL Server
sudo apt install mysql-server -y

# Start MySQL service
sudo systemctl start mysql

# Enable MySQL on boot
sudo systemctl enable mysql

# Check MySQL status
sudo systemctl status mysql

# Secure MySQL installation
sudo mysql_secure_installation
```

### Create Database and User
```bash
# Login to MySQL
sudo mysql -u root -p

# Create database
CREATE DATABASE payc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create user with password
CREATE USER 'payc_user'@'localhost' IDENTIFIED BY 'StrongPassword123!';

# Grant all privileges
GRANT ALL PRIVILEGES ON payc_db.* TO 'payc_user'@'localhost';

# Apply changes
FLUSH PRIVILEGES;

# Verify user
SELECT User, Host FROM mysql.user WHERE User = 'payc_user';

# Exit MySQL
EXIT;
```

### Import Schema on Production
```bash
# Import schema
mysql -u payc_user -p payc_db < /var/www/payc-api/database/schema.sql

# Verify tables
mysql -u payc_user -p payc_db -e "SHOW TABLES;"

# Check specific table
mysql -u payc_user -p payc_db -e "DESCRIBE users;"
```

### MySQL Backup Commands
```bash
# Backup entire database
mysqldump -u payc_user -p payc_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup with compression
mysqldump -u payc_user -p payc_db | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz

# Restore from backup
mysql -u payc_user -p payc_db < backup_20250126_120000.sql

# Restore from compressed backup
gunzip < backup_20250126_120000.sql.gz | mysql -u payc_user -p payc_db
```

### MySQL Hardening
```bash
# Login to MySQL
sudo mysql -u root -p

# Remove anonymous users
DELETE FROM mysql.user WHERE User='';

# Disallow root login remotely
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');

# Remove test database
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';

# Reload privileges
FLUSH PRIVILEGES;
EXIT;

# Bind MySQL to localhost only (edit config)
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
# Set: bind-address = 127.0.0.1

# Restart MySQL
sudo systemctl restart mysql
```

---

## ⚙️ PM2 PROCESS MANAGER

### Install PM2
```bash
# Install PM2 globally
sudo npm install -g pm2

# Verify installation
pm2 --version
```

### Start Application with PM2
```bash
# Navigate to backend directory
cd /var/www/payc-api

# Start application
pm2 start server.js --name payc-api

# Start with environment
pm2 start server.js --name payc-api --env production

# Start with specific Node version
pm2 start server.js --name payc-api --interpreter node@18
```

### PM2 Management Commands
```bash
# List all processes
pm2 list

# Show process details
pm2 show payc-api

# Monitor processes (real-time)
pm2 monit

# Stop application
pm2 stop payc-api

# Restart application
pm2 restart payc-api

# Reload application (zero-downtime)
pm2 reload payc-api

# Delete from PM2
pm2 delete payc-api

# Stop all processes
pm2 stop all

# Restart all processes
pm2 restart all
```

### PM2 Startup & Auto-Restart
```bash
# Generate startup script
pm2 startup systemd

# Copy and run the generated command (example):
sudo env PATH=$PATH:/usr/bin /usr/lib/node_modules/pm2/bin/pm2 startup systemd -u username --hp /home/username

# Save current process list
pm2 save

# Verify startup
sudo systemctl status pm2-username

# Disable startup
pm2 unstartup systemd
```

### PM2 Log Management
```bash
# View logs (real-time)
pm2 logs payc-api

# View last 100 lines
pm2 logs payc-api --lines 100

# View only error logs
pm2 logs payc-api --err

# Clear all logs
pm2 flush

# Install log rotation
pm2 install pm2-logrotate

# Configure log rotation
pm2 set pm2-logrotate:max_size 10M
pm2 set pm2-logrotate:retain 7
pm2 set pm2-logrotate:compress true
```

### PM2 Update & Maintenance
```bash
# Update PM2
npm install -g pm2@latest

# Update PM2 in-memory
pm2 update

# Reset PM2 metadata
pm2 reset payc-api

# Dump process list
pm2 dump
```

---

## 🌐 NGINX WEB SERVER

### Install Nginx
```bash
# Install Nginx
sudo apt install nginx -y

# Start Nginx
sudo systemctl start nginx

# Enable on boot
sudo systemctl enable nginx

# Check status
sudo systemctl status nginx
```

### Configure Nginx Reverse Proxy
```bash
# Create configuration file
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

# Remove default site (optional)
sudo rm /etc/nginx/sites-enabled/default

# Test configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx

# Restart Nginx
sudo systemctl restart nginx
```

### Nginx Management Commands
```bash
# Test configuration
sudo nginx -t

# Reload configuration (no downtime)
sudo systemctl reload nginx

# Restart Nginx
sudo systemctl restart nginx

# Stop Nginx
sudo systemctl stop nginx

# Start Nginx
sudo systemctl start nginx

# Check status
sudo systemctl status nginx

# View error logs
sudo tail -f /var/log/nginx/error.log

# View access logs
sudo tail -f /var/log/nginx/access.log

# Check Nginx version
nginx -v
```

---

## 🔒 SSL CERTIFICATE MANAGEMENT

### Install Certbot
```bash
# Install Certbot and Nginx plugin
sudo apt install certbot python3-certbot-nginx -y

# Verify installation
certbot --version
```

### Obtain SSL Certificate
```bash
# Get certificate for domain
sudo certbot --nginx -d api.yourdomain.com

# Get certificate for multiple domains
sudo certbot --nginx -d api.yourdomain.com -d www.api.yourdomain.com

# Certificate only (manual configuration)
sudo certbot certonly --nginx -d api.yourdomain.com
```

### SSL Certificate Management
```bash
# List all certificates
sudo certbot certificates

# Renew all certificates
sudo certbot renew

# Test renewal (dry run)
sudo certbot renew --dry-run

# Renew specific certificate
sudo certbot renew --cert-name api.yourdomain.com

# Force renewal
sudo certbot renew --force-renewal

# Delete certificate
sudo certbot delete --cert-name api.yourdomain.com
```

### Automate SSL Renewal
```bash
# Certbot auto-renewal is already setup via systemd timer
# Check renewal timer status
sudo systemctl status certbot.timer

# Enable timer
sudo systemctl enable certbot.timer

# Test renewal
sudo certbot renew --dry-run

# Manual cron job (alternative)
sudo crontab -e
# Add: 0 3 * * * certbot renew --quiet --post-hook "systemctl reload nginx"
```

---

## 🔧 SERVER MAINTENANCE

### Firewall (UFW) Commands
```bash
# Install UFW
sudo apt install ufw -y

# Check status
sudo ufw status

# Enable UFW
sudo ufw enable

# Disable UFW
sudo ufw disable

# Allow SSH (IMPORTANT: Do this first!)
sudo ufw allow OpenSSH
sudo ufw allow 22/tcp

# Allow HTTP
sudo ufw allow 80/tcp
sudo ufw allow http

# Allow HTTPS
sudo ufw allow 443/tcp
sudo ufw allow https

# Allow Nginx Full (HTTP + HTTPS)
sudo ufw allow 'Nginx Full'

# Allow specific port
sudo ufw allow 3000/tcp

# Deny specific port
sudo ufw deny 3000/tcp

# Delete rule
sudo ufw delete allow 3000/tcp

# Reset firewall
sudo ufw reset

# Show numbered rules
sudo ufw status numbered

# Delete rule by number
sudo ufw delete 2
```

### Systemctl Service Commands
```bash
# Start service
sudo systemctl start mysql
sudo systemctl start nginx
sudo systemctl start pm2-username

# Stop service
sudo systemctl stop mysql
sudo systemctl stop nginx

# Restart service
sudo systemctl restart mysql
sudo systemctl restart nginx

# Reload service
sudo systemctl reload nginx

# Enable on boot
sudo systemctl enable mysql
sudo systemctl enable nginx

# Disable on boot
sudo systemctl disable mysql

# Check status
sudo systemctl status mysql
sudo systemctl status nginx

# View service logs
sudo journalctl -u nginx -f
sudo journalctl -u mysql -f
```

### Cron Jobs for Backups
```bash
# Edit crontab
crontab -e

# Daily database backup at 2 AM
0 2 * * * mysqldump -u payc_user -pYourPassword payc_db | gzip > /home/username/backups/payc_db_$(date +\%Y\%m\%d).sql.gz

# Weekly backup at Sunday 3 AM
0 3 * * 0 mysqldump -u payc_user -pYourPassword payc_db | gzip > /home/username/backups/weekly_payc_db_$(date +\%Y\%m\%d).sql.gz

# Delete backups older than 7 days
0 4 * * * find /home/username/backups -name "payc_db_*.sql.gz" -mtime +7 -delete

# PM2 log cleanup daily
0 1 * * * pm2 flush

# List cron jobs
crontab -l

# Remove all cron jobs
crontab -r
```

---

## 🐛 DEBUGGING & MONITORING

### Server Debugging Commands
```bash
# Check disk space
df -h

# Check memory usage
free -h

# Check CPU usage
top
htop

# Check running processes
ps aux | grep node

# Check listening ports
sudo netstat -tulpn | grep LISTEN
sudo ss -tulpn | grep LISTEN

# Check specific port
sudo lsof -i :3000
sudo netstat -tulpn | grep :3000

# Kill process by port
sudo kill -9 $(sudo lsof -t -i:3000)

# Check system logs
sudo journalctl -xe

# Check last 100 system logs
sudo journalctl -n 100

# Monitor logs in real-time
sudo journalctl -f

# Check failed services
sudo systemctl --failed
```

### Application Logs
```bash
# PM2 logs
pm2 logs payc-api
pm2 logs payc-api --lines 200
pm2 logs payc-api --err

# Nginx logs
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
sudo tail -n 100 /var/log/nginx/error.log

# MySQL logs
sudo tail -f /var/log/mysql/error.log

# System logs
sudo tail -f /var/log/syslog
```

### Network Debugging
```bash
# Test API endpoint
curl http://localhost:3000/health
curl https://api.yourdomain.com/health

# Test with headers
curl -H "Authorization: Bearer token" http://localhost:3000/api/wallet/balance

# Test POST request
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@123"}'

# Check DNS
nslookup api.yourdomain.com
dig api.yourdomain.com

# Ping server
ping api.yourdomain.com

# Trace route
traceroute api.yourdomain.com

# Check SSL certificate
openssl s_client -connect api.yourdomain.com:443 -servername api.yourdomain.com
```

---

## 🔄 GIT DEPLOYMENT & UPDATES

### Initial Git Setup
```bash
# Install Git
sudo apt install git -y

# Configure Git
git config --global user.name "Your Name"
git config --global user.email "your@email.com"

# Clone repository
cd /var/www
sudo git clone https://github.com/yourusername/payc-backend.git payc-api

# Set permissions
sudo chown -R username:username /var/www/payc-api
```

### Update & Redeploy
```bash
# Navigate to project
cd /var/www/payc-api

# Pull latest changes
git pull origin main

# Install new dependencies
npm install --production

# Restart application
pm2 restart payc-api

# OR reload with zero downtime
pm2 reload payc-api

# Check status
pm2 status
pm2 logs payc-api --lines 50
```

### Git Commands
```bash
# Check status
git status

# View changes
git diff

# Discard local changes
git reset --hard

# Pull specific branch
git pull origin develop

# View commit history
git log --oneline -10

# Create new branch
git checkout -b feature-name

# Switch branch
git checkout main

# Stash changes
git stash
git stash pop
```

---

## 🐳 DOCKER (OPTIONAL)

### Install Docker
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo apt install docker-compose -y

# Verify installation
docker --version
docker-compose --version
```

### Docker Commands
```bash
# Build image
docker build -t payc-api .

# Run container
docker run -d -p 3000:3000 --name payc-api payc-api

# List containers
docker ps
docker ps -a

# Stop container
docker stop payc-api

# Start container
docker start payc-api

# Remove container
docker rm payc-api

# View logs
docker logs payc-api
docker logs -f payc-api

# Execute command in container
docker exec -it payc-api bash

# Remove all stopped containers
docker container prune
```

### Docker Compose
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild and start
docker-compose up -d --build

# Scale service
docker-compose up -d --scale api=3
```

---

## 📝 QUICK REFERENCE

### Most Used Commands

**Local Development:**
```bash
cd c:/Users/amanj/OneDrive/Desktop/payc/backend
npm install
npm start
```

**Production Deployment:**
```bash
cd /var/www/payc-api
npm install --production
pm2 start server.js --name payc-api
pm2 save
sudo systemctl reload nginx
```

**Daily Maintenance:**
```bash
pm2 status
pm2 logs payc-api --lines 50
sudo systemctl status nginx
df -h
```

**Emergency Restart:**
```bash
pm2 restart payc-api
sudo systemctl restart nginx
sudo systemctl restart mysql
```

---

## 🆘 TROUBLESHOOTING

### Common Issues

**Port already in use:**
```bash
sudo lsof -i :3000
sudo kill -9 $(sudo lsof -t -i:3000)
pm2 restart payc-api
```

**Database connection failed:**
```bash
sudo systemctl status mysql
sudo systemctl restart mysql
mysql -u payc_user -p payc_db -e "SELECT 1;"
```

**Nginx not starting:**
```bash
sudo nginx -t
sudo systemctl status nginx
sudo tail -f /var/log/nginx/error.log
```

**PM2 not starting on boot:**
```bash
pm2 startup systemd
pm2 save
sudo systemctl status pm2-username
```

---

**📚 For more details, see:**
- `README.md` - Setup instructions
- `DEPLOYMENT.md` - Deployment guide
- `TESTING_GUIDE.md` - API testing

**Built for PayC Backend v1.0**
