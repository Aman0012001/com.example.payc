package com.example.payc.data.repository

import android.content.Context
import com.example.payc.data.SecureTokenManager
import com.example.payc.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepository(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val tokenManager = SecureTokenManager(context)
    
    private fun getAuthHeader(): String {
        return "Bearer ${tokenManager.getToken()}"
    }
    
    suspend fun getBalance(): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWalletBalance(getAuthHeader())
            if (response.isSuccessful && response.body() != null) {
                val balanceResponse = response.body()!!
                if (balanceResponse.success) {
                    // Update local balance
                    tokenManager.updateWalletBalance(balanceResponse.balance)
                    Result.success(balanceResponse.balance)
                } else {
                    Result.failure(Exception("Failed to fetch balance"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTransactions(page: Int = 1): Result<TransactionsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTransactions(getAuthHeader(), page)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch transactions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun requestWithdrawal(
        amount: Double,
        paymentMethod: String,
        accountDetails: Map<String, String>
    ): Result<WithdrawalResponse> = withContext(Dispatchers.IO) {
        try {
            val request = WithdrawalRequest(amount, paymentMethod, accountDetails)
            val response = apiService.requestWithdrawal(getAuthHeader(), request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Withdrawal request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createPaymentOrder(amount: Double): Result<CreateOrderResponse> = withContext(Dispatchers.IO) {
        try {
            val request = CreateOrderRequest(amount)
            val response = apiService.createPaymentOrder(getAuthHeader(), request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create payment order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyPayment(
        orderId: String,
        paymentId: String,
        signature: String
    ): Result<VerifyPaymentResponse> = withContext(Dispatchers.IO) {
        try {
            val request = VerifyPaymentRequest(orderId, paymentId, signature)
            val response = apiService.verifyPayment(getAuthHeader(), request)
            if (response.isSuccessful && response.body() != null) {
                val verifyResponse = response.body()!!
                if (verifyResponse.success && verifyResponse.newBalance != null) {
                    // Update local balance
                    tokenManager.updateWalletBalance(verifyResponse.newBalance)
                }
                Result.success(verifyResponse)
            } else {
                Result.failure(Exception("Payment verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getLocalBalance(): Double {
        return tokenManager.getWalletBalance()
    }
}
