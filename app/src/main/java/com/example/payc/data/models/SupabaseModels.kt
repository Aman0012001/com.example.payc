package com.example.payc.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val phone: String? = null,
    @SerialName("referral_code")
    val referralCode: String? = null,
    @SerialName("referred_by")
    val referredBy: String? = null,
    @SerialName("bank_account_number")
    val bankAccountNumber: String? = null,
    @SerialName("bank_ifsc_code")
    val bankIfscCode: String? = null,
    @SerialName("bank_account_holder")
    val bankAccountHolder: String? = null,
    @SerialName("is_verified")
    val isVerified: Boolean = false,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class Wallet(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val balance: Double = 0.0,
    @SerialName("total_earned")
    val totalEarned: Double = 0.0,
    @SerialName("total_withdrawn")
    val totalWithdrawn: Double = 0.0,
    @SerialName("total_deposited")
    val totalDeposited: Double = 0.0,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class InvestmentPlan(
    val id: String,
    val name: String,
    @SerialName("deposit_amount")
    val depositAmount: Double,
    @SerialName("daily_profit")
    val dailyProfit: Double,
    @SerialName("duration_days")
    val durationDays: Int,
    val description: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class UserInvestment(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("plan_id")
    val planId: String,
    val amount: Double,
    @SerialName("daily_profit")
    val dailyProfit: Double,
    @SerialName("total_earned")
    val totalEarned: Double = 0.0,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String? = null,
    val status: String = "active",
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class Transaction(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val type: String,
    val amount: Double,
    val status: String = "pending",
    @SerialName("payment_method")
    val paymentMethod: String? = null,
    @SerialName("payment_id")
    val paymentId: String? = null,
    @SerialName("reference_id")
    val referenceId: String? = null,
    val description: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class Notification(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    val title: String,
    val message: String,
    val type: String = "info",
    @SerialName("is_read")
    val isRead: Boolean = false,
    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class Task(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("investment_id")
    val investmentId: String? = null,
    val title: String,
    val description: String? = null,
    @SerialName("reward_amount")
    val rewardAmount: Double,
    val status: String = "pending",
    @SerialName("completed_at")
    val completedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String
)
