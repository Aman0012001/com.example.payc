# GitHub Secrets Quick Reference

## Copy these values to GitHub Secrets

### 1. FIREBASE_APP_ID
```
Go to: https://console.firebase.google.com/project/payc-d7a77/settings/general
Find: Your Android app (com.example.payc)
Copy: App ID (format: 1:XXXXXXXXX:android:XXXXXXXXXXXXXXXX)
```

### 2. FIREBASE_SERVICE_ACCOUNT_JSON
```
File: payc-d7a77-firebase-adminsdk-xxxxx.json
Action: Copy ENTIRE file content
Paste: As-is into GitHub secret (JSON format)
```

### 3. GOOGLE_SERVICES_JSON_BASE64
```
File: app/google-services-base64.txt
Action: Copy entire content
Note: ⚠️ Must use REAL google-services.json, not placeholder!
```

### 4. KEYSTORE_FILE_BASE64
```
File: keystore-base64.txt
Action: Copy entire content
Status: ✅ Already generated
```

### 5. KEYSTORE_PASSWORD
```
Value: payc2025
```

### 6. KEY_PASSWORD
```
Value: payc2025
```

### 7. KEY_ALIAS
```
Value: payc
```

---

## Quick Commands

### Re-generate google-services.json Base64 (after downloading real file):
```powershell
cd app
$content = Get-Content "google-services.json" -Raw
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($content)) | Out-File -FilePath "google-services-base64.txt" -NoNewline
Write-Host "✅ Saved to google-services-base64.txt"
```

### Get SHA-1 and SHA-256:
```powershell
keytool -list -v -keystore "payc-release-key.jks" -alias payc -storepass payc2025 -keypass payc2025 | Select-String "SHA1|SHA256"
```

### Test workflow locally (dry run):
```powershell
./gradlew assembleDebug assembleRelease --dry-run
```

---

## Files Generated
- ✅ `app/google-services-base64.txt` - Base64 encoded google-services.json
- ✅ `keystore-base64.txt` - Base64 encoded keystore
- ✅ `.github/workflows/android.yml` - CI/CD workflow
- ✅ `FIREBASE_CICD_SETUP.md` - Complete setup guide
