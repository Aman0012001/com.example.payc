package com.example.payc.utils

object ValidationUtils {
    
    // Email validation
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
    
    // Phone validation (10-15 digits)
    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return false
        val phoneRegex = "^[0-9]{10,15}$".toRegex()
        return phone.matches(phoneRegex)
    }
    
    // Password validation
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }
    
    // Password strength message
    fun getPasswordStrengthMessage(password: String): String {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            else -> ""
        }
    }
    
    // Name validation
    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        if (name.length < 3) return false
        val nameRegex = "^[a-zA-Z\\s]+$".toRegex()
        return name.matches(nameRegex)
    }
    
    // Amount validation
    fun isValidAmount(amount: String): Boolean {
        if (amount.isBlank()) return false
        val amountValue = amount.toDoubleOrNull() ?: return false
        return amountValue > 0 && amountValue <= 100000
    }
    
    // Amount validation with minimum
    fun isValidAmount(amount: String, minAmount: Double): Boolean {
        if (amount.isBlank()) return false
        val amountValue = amount.toDoubleOrNull() ?: return false
        return amountValue >= minAmount && amountValue <= 100000
    }
    
    // Get amount error message
    fun getAmountErrorMessage(amount: String, minAmount: Double = 100.0): String {
        return when {
            amount.isBlank() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Invalid amount format"
            amount.toDouble() <= 0 -> "Amount must be positive"
            amount.toDouble() < minAmount -> "Minimum amount is ₹$minAmount"
            amount.toDouble() > 100000 -> "Maximum amount is ₹100,000"
            else -> ""
        }
    }
    
    // UPI ID validation
    fun isValidUPI(upiId: String): Boolean {
        if (upiId.isBlank()) return false
        val upiRegex = "^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$".toRegex()
        return upiId.matches(upiRegex)
    }
    
    // Account number validation (basic)
    fun isValidAccountNumber(accountNumber: String): Boolean {
        if (accountNumber.isBlank()) return false
        val accountRegex = "^[0-9]{9,18}$".toRegex()
        return accountNumber.matches(accountRegex)
    }
    
    // IFSC code validation
    fun isValidIFSC(ifsc: String): Boolean {
        if (ifsc.isBlank()) return false
        val ifscRegex = "^[A-Z]{4}0[A-Z0-9]{6}$".toRegex()
        return ifsc.matches(ifscRegex)
    }
    
    // Sanitize input (remove special characters)
    fun sanitizeInput(input: String): String {
        return input.trim().replace(Regex("[<>\"';]"), "")
    }
}
