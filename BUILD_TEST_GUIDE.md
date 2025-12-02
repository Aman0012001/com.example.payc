# Quick Build & Test Guide

## Building the APK

### Option 1: Debug Build (Fastest for Testing)
```bash
cd c:/Users/amanj/OneDrive/Desktop/payc
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Option 2: Release Build (For Production)
```bash
cd c:/Users/amanj/OneDrive/Desktop/payc
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

## Testing Checklist

### 1. Currency Symbol Changes
- [ ] Login screen shows "P" logo (not ₹)
- [ ] Home screen balance shows "RS" (not PKR)
- [ ] Wallet screen shows "RS" in all amounts
- [ ] Deposit screen placeholder shows "RS 0.00"
- [ ] Withdraw screen shows "RS" in amounts
- [ ] Task screen shows "RS" for deposits and profits
- [ ] Profile screen shows "RS 12,450" in Total Earned

### 2. ProfileScreen Functionality
- [ ] Edit Profile button is visible at bottom
- [ ] Logout button is visible at bottom
- [ ] Edit Profile button navigates correctly
- [ ] Logout button works correctly
- [ ] All animations play smoothly
- [ ] Stats cards display correctly

### 3. Design Consistency
- [ ] All screens have aurora background
- [ ] All screens use gold/dark theme
- [ ] All cards have glassmorphism effect
- [ ] Animations are smooth on all screens
- [ ] Typography is consistent

### 4. TaskScreen Design
- [ ] Matches premium design of other screens
- [ ] Cards have proper shadows and effects
- [ ] Layout is clean and professional
- [ ] Animations work properly

## Quick Test Commands

### Clean Build
```bash
./gradlew clean
./gradlew assembleDebug
```

### Install on Connected Device
```bash
./gradlew installDebug
```

### View Build Logs
```bash
./gradlew assembleDebug --info
```

## Common Issues & Solutions

### Issue: Build fails
**Solution**: Run clean build
```bash
./gradlew clean
./gradlew assembleDebug
```

### Issue: Gradle daemon issues
**Solution**: Stop and restart daemon
```bash
./gradlew --stop
./gradlew assembleDebug
```

### Issue: Out of memory
**Solution**: Increase heap size in `gradle.properties`
```
org.gradle.jvmargs=-Xmx2048m
```

## Backend Setup (If Needed)

### Start Backend Server
```bash
cd backend
npm install
npm start
```

Default: `http://localhost:3000`

### Environment Variables
Make sure `.env` file exists in backend folder with:
```
PORT=3000
DB_HOST=localhost
DB_USER=your_db_user
DB_PASSWORD=your_db_password
DB_NAME=payc
JWT_SECRET=your_secret_key
```

## Final Deployment Checklist

- [ ] All tests pass
- [ ] APK installs successfully
- [ ] All features work on physical device
- [ ] Backend is connected and working
- [ ] No console errors
- [ ] All animations are smooth
- [ ] App doesn't crash on any screen
- [ ] Currency displays are correct everywhere
- [ ] Profile buttons are visible and functional

## Support

If you encounter any issues:
1. Check the build logs for specific errors
2. Ensure all dependencies are installed
3. Verify Android SDK is properly configured
4. Make sure Java/Kotlin versions are compatible
5. Check that all imports are resolved

---

**Status**: Ready for testing! 🚀
