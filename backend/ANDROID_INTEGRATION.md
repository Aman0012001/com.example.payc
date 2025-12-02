# Android Integration Guide

## Prerequisites
- Android Studio
- Retrofit library for API calls
- Razorpay Android SDK

## Step 1: Add Dependencies

Add to your `app/build.gradle`:

```gradle
dependencies {
    // Retrofit for API calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Razorpay Payment Gateway
    implementation 'com.razorpay:checkout:1.6.33'
    
    // Coroutines for async operations
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // DataStore for token storage
    implementation 'androidx.datastore:datastore-preferences:1.0.0'
}
```

## Step 2: Create API Service

Create `ApiService.kt`:

```kotlin
package com.example.payc.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/auth/verify-token")
    suspend fun verifyToken(): Response<VerifyTokenResponse>
    
    // Wallet
    @GET("api/wallet/balance")
    suspend fun getWalletBalance(): Response<WalletBalanceResponse>
    
    @GET("api/wallet/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<TransactionsResponse>
    
    @POST("api/wallet/withdraw")
    suspend fun requestWithdrawal(@Body request: WithdrawalRequest): Response<WithdrawalResponse>
    
    // Tasks
    @GET("api/tasks/list")
    suspend fun getTasks(): Response<TasksResponse>
    
    @POST("api/tasks/start")
    suspend fun startTask(@Body request: StartTaskRequest): Response<StartTaskResponse>
    
    @POST("api/tasks/complete")
    suspend fun completeTask(@Body request: CompleteTaskRequest): Response<CompleteTaskResponse>
    
    @GET("api/tasks/history")
    suspend fun getTaskHistory(): Response<TaskHistoryResponse>
    
    // Plans
    @GET("api/plans/list")
    suspend fun getPlans(): Response<PlansResponse>
    
    @GET("api/plans/{id}")
    suspend fun getPlanDetails(@Path("id") planId: Int): Response<PlanDetailsResponse>
    
    @POST("api/plans/purchase")
    suspend fun purchasePlan(@Body request: PurchasePlanRequest): Response<PurchasePlanResponse>
    
    // Payment
    @POST("api/payment/create-order")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<CreateOrderResponse>
    
    @POST("api/payment/verify")
    suspend fun verifyPayment(@Body request: VerifyPaymentRequest): Response<VerifyPaymentResponse>
}
```

## Step 3: Create Data Models

Create `ApiModels.kt`:

```kotlin
package com.example.payc.api

import com.google.gson.annotations.SerializedName

// Auth Models
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val password: String,
    val referralCode: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

data class AuthData(
    val userId: Int,
    val name: String,
    val email: String,
    val walletBalance: Double,
    val referralCode: String,
    val token: String
)

// Wallet Models
data class WalletBalanceResponse(
    val success: Boolean,
    val data: WalletBalance?
)

data class WalletBalance(
    val balance: Double,
    val totalEarned: Double,
    val totalWithdrawn: Double
)

data class WithdrawalRequest(
    val amount: Double,
    val paymentMethod: String,
    val accountDetails: String
)

// Task Models
data class TasksResponse(
    val success: Boolean,
    val data: TasksData?
)

data class TasksData(
    val tasks: List<Task>
)

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("task_type") val taskType: String,
    val reward: Double,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("company_name") val companyName: String?,
    @SerializedName("company_logo") val companyLogo: String?,
    val isCompleted: Boolean
)

// Payment Models
data class CreateOrderRequest(
    val amount: Double
)

data class CreateOrderResponse(
    val success: Boolean,
    val data: OrderData?
)

data class OrderData(
    val orderId: String,
    val amount: Double,
    val currency: String,
    val key: String
)

data class VerifyPaymentRequest(
    @SerializedName("razorpay_order_id") val razorpayOrderId: String,
    @SerializedName("razorpay_payment_id") val razorpayPaymentId: String,
    @SerializedName("razorpay_signature") val razorpaySignature: String
)
```

## Step 4: Setup Retrofit Client

Create `RetrofitClient.kt`:

```kotlin
package com.example.payc.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.payc.com/" // Change to your API URL
    
    private var token: String? = null
    
    fun setToken(newToken: String?) {
        token = newToken
    }
    
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        chain.proceed(requestBuilder.build())
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

## Step 5: Implement Razorpay Payment

Create `PaymentManager.kt`:

```kotlin
package com.example.payc.payment

import android.app.Activity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentManager(
    private val activity: Activity,
    private val listener: PaymentResultListener
) {
    
    fun startPayment(orderId: String, amount: Double, key: String) {
        val checkout = Checkout()
        checkout.setKeyID(key)
        
        try {
            val options = JSONObject()
            options.put("name", "PayC")
            options.put("description", "Wallet Recharge")
            options.put("order_id", orderId)
            options.put("currency", "INR")
            options.put("amount", (amount * 100).toInt()) // amount in paise
            
            val prefill = JSONObject()
            prefill.put("email", "user@example.com")
            prefill.put("contact", "9876543210")
            options.put("prefill", prefill)
            
            checkout.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

## Step 6: Usage Example

In your Activity/Fragment:

```kotlin
class DepositActivity : AppCompatActivity(), PaymentResultListener {
    
    private lateinit var paymentManager: PaymentManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        paymentManager = PaymentManager(this, this)
        
        // Create order and start payment
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createOrder(
                    CreateOrderRequest(amount = 1000.0)
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val orderData = response.body()?.data
                    orderData?.let {
                        paymentManager.startPayment(
                            orderId = it.orderId,
                            amount = it.amount,
                            key = it.key
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        // Verify payment on backend
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.verifyPayment(
                    VerifyPaymentRequest(
                        razorpayOrderId = orderId,
                        razorpayPaymentId = razorpayPaymentId ?: "",
                        razorpaySignature = signature
                    )
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    // Payment verified successfully
                    Toast.makeText(this@DepositActivity, "Payment Successful!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }
}
```

## Step 7: Store Token Securely

Use DataStore to save JWT token:

```kotlin
class TokenManager(private val context: Context) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
        RetrofitClient.setToken(token)
    }
    
    suspend fun getToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[TOKEN_KEY]
    }
    
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
        RetrofitClient.setToken(null)
    }
}
```

## Step 8: Error Handling

Create a common error handler:

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            ApiResult.Success(response.body()!!)
        } else {
            ApiResult.Error(
                message = response.message() ?: "Unknown error",
                code = response.code()
            )
        }
    } catch (e: Exception) {
        ApiResult.Error(message = e.message ?: "Network error")
    }
}
```

## Testing

1. Update `BASE_URL` in `RetrofitClient.kt` to your API endpoint
2. Test authentication flow (register/login)
3. Test wallet operations
4. Test Razorpay payment integration
5. Test task completion flow

## Security Notes

- Always use HTTPS in production
- Store tokens securely using DataStore or EncryptedSharedPreferences
- Validate all user inputs
- Handle API errors gracefully
- Implement proper session management
