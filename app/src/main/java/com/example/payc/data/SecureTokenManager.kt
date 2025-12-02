package com.example.payc.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureTokenManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_WALLET_BALANCE = "wallet_balance"
    }
    
    // Save JWT token securely
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_JWT_TOKEN, token).apply()
    }
    
    // Get JWT token
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null)
    }
    
    // Save user data
    fun saveUserData(userId: Int, email: String, name: String, walletBalance: Double) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putFloat(KEY_WALLET_BALANCE, walletBalance.toFloat())
            apply()
        }
    }
    
    // Get user ID
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
    
    // Get user email
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }
    
    // Get user name
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }
    
    // Get wallet balance
    fun getWalletBalance(): Double {
        return sharedPreferences.getFloat(KEY_WALLET_BALANCE, 0f).toDouble()
    }
    
    // Update wallet balance
    fun updateWalletBalance(balance: Double) {
        sharedPreferences.edit().putFloat(KEY_WALLET_BALANCE, balance.toFloat()).apply()
    }
    
    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
    
    // Clear all session data (logout)
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
