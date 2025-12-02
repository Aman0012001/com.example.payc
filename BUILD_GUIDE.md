# 🚀 Quick Build Guide - Production APK

## Build Production APK

### Option 1: Using Gradle Wrapper (Recommended)

```powershell
# Navigate to project directory
cd c:\Users\amanj\OneDrive\Desktop\payc

# Clean previous builds
.\gradlew clean

# Build release APK
.\gradlew assembleRelease

# APK location:
# app\build\outputs\apk\release\app-release.apk
```

### Option 2: Build AAB for Play Store

```powershell
# Build Android App Bundle (AAB)
.\gradlew bundleRelease

# AAB location:
# app\build\outputs\bundle\release\app-release.aab
```

## First Time Setup - Generate Signing Key

```powershell
# Generate release keystore (do this ONCE)
keytool -genkey -v -keystore payc-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias payc

# Follow prompts to set password and details
# IMPORTANT: Save the password securely!
```

## Configure Signing (After Generating Key)

1. Create `keystore.properties` in project root:

```properties
storePassword=YOUR_KEYSTORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=payc
storeFile=../payc-release-key.jks
```

2. Update `app/build.gradle.kts` to use signing config (uncomment the signing sections)

## Build Status

✅ **ProGuard**: Enabled  
✅ **Minification**: Enabled  
✅ **Resource Shrinking**: Enabled  
✅ **Network Security**: HTTPS Only  
✅ **Code Obfuscation**: Enabled  

## Expected APK Size

- **Before optimization**: ~15-20 MB
- **After optimization**: ~6-10 MB (40-60% reduction)

## Verify Build

```powershell
# Check APK details
.\gradlew assembleRelease --info

# Analyze APK size
.\gradlew assembleRelease --scan
```

## Common Issues

### Issue: "SDK location not found"
**Solution**: Create `local.properties` with:
```properties
sdk.dir=C\:\\Users\\amanj\\AppData\\Local\\Android\\Sdk
```

### Issue: "Keystore not found"
**Solution**: Generate keystore first (see above) or build debug APK:
```powershell
.\gradlew assembleDebug
```

### Issue: "Build failed"
**Solution**: Clean and rebuild:
```powershell
.\gradlew clean
.\gradlew assembleRelease --stacktrace
```

## Quick Test

```powershell
# Install on connected device
.\gradlew installRelease

# Or manually install APK
adb install app\build\outputs\apk\release\app-release.apk
```

---

**Ready to build!** 🚀
