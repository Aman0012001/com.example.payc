# 🔐 SHA Fingerprints for Firebase

## Release Keystore Fingerprints

### SHA-1
```
03:FC:D6:47:0C:AB:F2:DD:51:D9:05:C8:5E:84:E7:A6:99:6B:3E:19:10:97:80:45:2C:D8:7C:8A:31:7F:17:56:C7
```

### SHA-256
```
79:F3:8A:69:66:08:2E:62:EB:1E:8E:4F:90:9E
```

## How to Add to Firebase

1. Go to [Firebase Console - Project Settings](https://console.firebase.google.com/project/payc-d7a77/settings/general)
2. Scroll to **Your apps** section
3. Find your Android app (`com.example.payc`)
4. Click **Add fingerprint**
5. Paste the SHA-1 value above
6. Click **Add fingerprint** again
7. Paste the SHA-256 value above
8. Click **Save**

## Why Add SHA Fingerprints?

Required for:
- ✅ Firebase Authentication
- ✅ Firebase Dynamic Links
- ✅ Google Sign-In
- ✅ Firebase Cloud Messaging (FCM)
- ✅ Firebase App Check

---

**Keystore**: `payc-release-key.jks`
**Alias**: `payc`
**Generated**: 2025-12-02
