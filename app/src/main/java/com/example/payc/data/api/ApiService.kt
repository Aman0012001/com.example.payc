package com.example.payc.data.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @GET("auth/verify-token")
    suspend fun verifyToken(@Header("Authorization") token: String): Response<VerifyTokenResponse>
    
    // Wallet
    @GET("wallet/balance")
    suspend fun getWalletBalance(@Header("Authorization") token: String): Response<WalletBalanceResponse>
    
    @GET("wallet/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<TransactionsResponse>
    
    @POST("wallet/withdraw")
    suspend fun requestWithdrawal(
        @Header("Authorization") token: String,
        @Body request: WithdrawalRequest
    ): Response<WithdrawalResponse>
    
    // Tasks
    @GET("tasks/list")
    suspend fun getTasks(@Header("Authorization") token: String): Response<TasksResponse>
    
    @POST("tasks/start")
    suspend fun startTask(
        @Header("Authorization") token: String,
        @Body request: StartTaskRequest
    ): Response<StartTaskResponse>
    
    @POST("tasks/complete")
    suspend fun completeTask(
        @Header("Authorization") token: String,
        @Body request: CompleteTaskRequest
    ): Response<CompleteTaskResponse>
    
    @GET("tasks/history")
    suspend fun getTaskHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Response<TaskHistoryResponse>
    
    // Plans
    @GET("plans/list")
    suspend fun getPlans(@Header("Authorization") token: String): Response<PlansResponse>
    
    @POST("plans/purchase")
    suspend fun purchasePlan(
        @Header("Authorization") token: String,
        @Body request: PurchasePlanRequest
    ): Response<PurchasePlanResponse>
    
    @GET("plans/user-plans")
    suspend fun getUserPlans(@Header("Authorization") token: String): Response<UserPlansResponse>
    
    // Payment
    @POST("payment/create-order")
    suspend fun createPaymentOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Response<CreateOrderResponse>
    
    @POST("payment/verify")
    suspend fun verifyPayment(
        @Header("Authorization") token: String,
        @Body request: VerifyPaymentRequest
    ): Response<VerifyPaymentResponse>
    
    // Profile
    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfileResponse>
    
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>
}

// Request Models
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val referralCode: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class WithdrawalRequest(
    val amount: Double,
    val paymentMethod: String,
    val accountDetails: Map<String, String>
)

data class StartTaskRequest(val taskId: Int)
data class CompleteTaskRequest(val taskHistoryId: Int)
data class PurchasePlanRequest(val planId: Int)
data class CreateOrderRequest(val amount: Double)
data class VerifyPaymentRequest(
    val orderId: String,
    val paymentId: String,
    val signature: String
)

data class UpdateProfileRequest(
    val name: String?,
    val email: String?,
    val phone: String?,
    val bankAccount: String?,
    val ifscCode: String?,
    val upiId: String?
)

// Response Models
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)

data class VerifyTokenResponse(
    val success: Boolean,
    val user: User?
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val walletBalance: Double,
    val referralCode: String,
    val createdAt: String
)

data class WalletBalanceResponse(
    val success: Boolean,
    val balance: Double
)

data class TransactionsResponse(
    val success: Boolean,
    val transactions: List<Transaction>,
    val pagination: Pagination
)

data class Transaction(
    val id: Int,
    val type: String,
    val amount: Double,
    val description: String,
    val status: String,
    val createdAt: String
)

data class Pagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int
)

data class WithdrawalResponse(
    val success: Boolean,
    val message: String,
    val withdrawalId: Int?
)

data class TasksResponse(
    val success: Boolean,
    val tasks: List<Task>
)

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val reward: Double,
    val duration: Int,
    val category: String,
    val imageUrl: String?
)

data class StartTaskResponse(
    val success: Boolean,
    val message: String,
    val taskHistoryId: Int?
)

data class CompleteTaskResponse(
    val success: Boolean,
    val message: String,
    val reward: Double?,
    val newBalance: Double?
)

data class TaskHistoryResponse(
    val success: Boolean,
    val history: List<TaskHistory>
)

data class TaskHistory(
    val id: Int,
    val taskId: Int,
    val taskTitle: String,
    val reward: Double,
    val status: String,
    val startedAt: String,
    val completedAt: String?
)

data class PlansResponse(
    val success: Boolean,
    val plans: List<Plan>
)

data class Plan(
    val id: Int,
    val name: String,
    val price: Double,
    val dailyProfit: Double,
    val duration: Int,
    val description: String,
    val features: List<String>
)

data class PurchasePlanResponse(
    val success: Boolean,
    val message: String,
    val userPlanId: Int?
)

data class UserPlansResponse(
    val success: Boolean,
    val plans: List<UserPlan>
)

data class UserPlan(
    val id: Int,
    val planId: Int,
    val planName: String,
    val price: Double,
    val dailyProfit: Double,
    val duration: Int,
    val startDate: String,
    val endDate: String,
    val status: String,
    val totalEarned: Double
)

data class CreateOrderResponse(
    val success: Boolean,
    val orderId: String?,
    val amount: Double?,
    val currency: String?,
    val key: String?
)

data class VerifyPaymentResponse(
    val success: Boolean,
    val message: String,
    val newBalance: Double?
)

data class UserProfileResponse(
    val success: Boolean,
    val user: User
)

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)
