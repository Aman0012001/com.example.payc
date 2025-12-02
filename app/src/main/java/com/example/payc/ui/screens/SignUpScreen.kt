package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onLoginClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var generalError by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Animation states
    var visible by remember { mutableStateOf(true) }
    val buttonScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    

    /*
    LaunchedEffect(Unit) {
        visible = true
    }
    */

    fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    fun validatePhone(phone: String): Boolean {
        val phoneRegex = "^[0-9]{10,15}$".toRegex()
        return phone.matches(phoneRegex)
    }

    fun validateForm(): Boolean {
        var isValid = true
        
        if (name.isEmpty()) {
            nameError = "Name is required"
            isValid = false
        } else if (name.length < 3) {
            nameError = "Name must be at least 3 characters"
            isValid = false
        } else {
            nameError = ""
        }
        
        if (email.isEmpty()) {
            emailError = "Email is required"
            isValid = false
        } else if (!validateEmail(email)) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = ""
        }
        
        if (phone.isEmpty()) {
            phoneError = "Phone number is required"
            isValid = false
        } else if (!validatePhone(phone)) {
            phoneError = "Please enter a valid phone number (10-15 digits)"
            isValid = false
        } else {
            phoneError = ""
        }
        
        if (password.isEmpty()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            passwordError = "Password must be at least 8 characters"
            isValid = false
        } else if (!password.any { it.isDigit() }) {
            passwordError = "Password must contain at least one number"
            isValid = false
        } else if (!password.any { it.isUpperCase() }) {
            passwordError = "Password must contain at least one uppercase letter"
            isValid = false
        } else {
            passwordError = ""
        }
        
        return isValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Reuse Aurora Background from LoginScreen
        AuroraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -it }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "Join PayC today!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Surface.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // General Error Message
                    if (generalError.isNotEmpty()) {
                        Text(
                            text = generalError,
                            color = Error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            generalError = ""
                        },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Primary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedContainerColor = Background.copy(alpha = 0.5f),
                            unfocusedContainerColor = Background.copy(alpha = 0.3f),
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurface.copy(alpha = 0.6f),
                            cursorColor = Primary,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        ),
                        isError = nameError.isNotEmpty() && showErrors,
                        enabled = !isLoading
                    )
                    
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            generalError = ""
                        },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Primary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedContainerColor = Background.copy(alpha = 0.5f),
                            unfocusedContainerColor = Background.copy(alpha = 0.3f),
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurface.copy(alpha = 0.6f),
                            cursorColor = Primary,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        ),
                        isError = emailError.isNotEmpty() && showErrors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !isLoading
                    )
                    
                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { 
                            phone = it.filter { c -> c.isDigit() }
                            generalError = ""
                        },
                        label = { Text("Phone") },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = Primary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedContainerColor = Background.copy(alpha = 0.5f),
                            unfocusedContainerColor = Background.copy(alpha = 0.3f),
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurface.copy(alpha = 0.6f),
                            cursorColor = Primary,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        ),
                        isError = phoneError.isNotEmpty() && showErrors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        enabled = !isLoading
                    )
                    
                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            generalError = ""
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = OnSurface.copy(alpha = 0.6f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                            focusedContainerColor = Background.copy(alpha = 0.5f),
                            unfocusedContainerColor = Background.copy(alpha = 0.3f),
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurface.copy(alpha = 0.6f),
                            cursorColor = Primary,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface
                        ),
                        isError = passwordError.isNotEmpty() && showErrors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Sign Up Button
                    Button(
                        onClick = {
                            showErrors = true
                            if (validateForm() && !isLoading) {
                                scope.launch {
                                    isLoading = true
                                    generalError = ""
                                    
                                    buttonScale.animateTo(0.95f, tween(100))
                                    buttonScale.animateTo(1f, tween(100))
                                    
                                    // Mock Signup Delay
                                    kotlinx.coroutines.delay(1500)
                                    
                                    isLoading = false
                                    
                                    // Always succeed for mock
                                    onSignUpSuccess()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .scale(buttonScale.value),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "SIGN UP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login Link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = OnSurface.copy(alpha = 0.6f))
                TextButton(onClick = onLoginClick) {
                    Text("Log In", color = Primary, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
