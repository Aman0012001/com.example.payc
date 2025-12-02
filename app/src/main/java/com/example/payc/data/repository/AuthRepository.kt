package com.example.payc.data.repository

import android.content.Context
import com.example.payc.data.SecureTokenManager
import com.example.payc.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val tokenManager = SecureTokenManager(context)
    
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        referralCode: String? = null
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(name, email, phone, password, referralCode)
            val response = apiService.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.success && authResponse.token != null && authResponse.user != null) {
                    // Save token and user data
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserData(
                        authResponse.user.id,
                        authResponse.user.email,
                        authResponse.user.name,
                        authResponse.user.walletBalance
                    )
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(authResponse.message))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.success && authResponse.token != null && authResponse.user != null) {
                    // Save token and user data
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUserData(
                        authResponse.user.id,
                        authResponse.user.email,
                        authResponse.user.name,
                        authResponse.user.walletBalance
                    )
                    Result.success(authResponse)
                } else {
                    Result.failure(Exception(authResponse.message))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyToken(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                return@withContext Result.success(false)
            }
            
            val response = apiService.verifyToken("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val verifyResponse = response.body()!!
                Result.success(verifyResponse.success)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.success(false)
        }
    }
    
    fun logout() {
        tokenManager.clearSession()
    }
    
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
    
    fun getToken(): String? {
        return tokenManager.getToken()
    }
}
