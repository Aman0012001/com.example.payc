package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var generalError by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Animation states
    var isVisible by remember { mutableStateOf(true) }
    val buttonScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    

    /*
    LaunchedEffect(Unit) {
        isVisible = true
    }
    */

    fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    fun validateForm(): Boolean {
        var isValid = true
        
        if (email.isEmpty()) {
            emailError = "Email is required"
            isValid = false
        } else if (!validateEmail(email)) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = ""
        }
        
        if (password.isEmpty()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = ""
        }
        
        return isValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background) // Dark Background
    ) {
        // 1. Premium Aurora Background
        AuroraBackground()
        
        // 2. Floating Gold Particles
        GoldParticles()
        
        // 3. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(), // Handle status bars
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -it }
            ) {
                PremiumLogo()
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Glassmorphism Login Card
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000, delayMillis = 300)) + 
                        slideInVertically(tween(1000, delayMillis = 300)) { 100 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface.copy(alpha = 0.7f) // Semi-transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome Back",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Sign in to access your wallet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                        )
                        
                        // General Error Message
                        if (generalError.isNotEmpty()) {
                            Text(
                                text = generalError,
                                color = Error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                generalError = ""
                                if (showErrors) {
                                    emailError = if (it.isEmpty()) "Email is required" 
                                                else if (!validateEmail(it)) "Invalid email"
                                                else ""
                                }
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
                            singleLine = true,
                            enabled = !isLoading
                        )
                        if (emailError.isNotEmpty() && showErrors) {
                            Text(
                                text = emailError,
                                color = Error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                generalError = ""
                                if (showErrors) {
                                    passwordError = if (it.isEmpty()) "Password is required"
                                                   else if (it.length < 6) "Min 6 chars"
                                                   else ""
                                }
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
                            singleLine = true,
                            enabled = !isLoading
                        )
                        if (passwordError.isNotEmpty() && showErrors) {
                            Text(
                                text = passwordError,
                                color = Error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        // Forgot Password
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(
                                onClick = onForgotPasswordClick,
                                enabled = !isLoading
                            ) {
                                Text(
                                    "Forgot Password?", 
                                    color = Primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login Button
                        Button(
                            onClick = {
                                showErrors = true
                                if (validateForm() && !isLoading) {
                                    scope.launch {
                                        isLoading = true
                                        generalError = ""
                                        
                                        buttonScale.animateTo(0.95f, tween(100))
                                        buttonScale.animateTo(1f, tween(100))
                                        
                                        // Mock Login Delay
                                        delay(1500)
                                        
                                        isLoading = false
                                        
                                        // Always succeed for mock
                                        onLoginSuccess()
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
                                    "LOG IN",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign Up Link
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000, delayMillis = 600))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Don't have an account?", 
                        color = OnSurface.copy(alpha = 0.6f)
                    )
                    TextButton(
                        onClick = onSignUpClick,
                        enabled = !isLoading
                    ) {
                        Text(
                            "Create Account", 
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuroraBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    
    // Animate positions of 3 orbs
    val t by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        
        // Orb 1: Gold
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Primary.copy(alpha = 0.2f), Color.Transparent),
                center = Offset(
                    x = w * 0.5f + cos(t) * w * 0.3f,
                    y = h * 0.3f + sin(t) * h * 0.2f
                ),
                radius = w * 0.6f
            )
        )
        
        // Orb 2: Secondary (Darker Gold)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Secondary.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(
                    x = w * 0.5f + cos(t + 2f) * w * 0.3f,
                    y = h * 0.7f + sin(t + 2f) * h * 0.2f
                ),
                radius = w * 0.7f
            )
        )
        
        // Orb 3: Purple accent (subtle)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6650a4).copy(alpha = 0.1f), Color.Transparent),
                center = Offset(
                    x = w * 0.2f + sin(t) * w * 0.2f,
                    y = h * 0.5f + cos(t) * h * 0.2f
                ),
                radius = w * 0.5f
            )
        )
    }
}

@Composable
fun GoldParticles() {
    val particles = remember {
        List(20) {
            ParticleState(
                x = (0..100).random().toFloat(),
                y = (0..100).random().toFloat(),
                size = (2..6).random().toFloat(),
                speed = (2..6).random() / 10f
            )
        }
    }
    
    particles.forEach { particle ->
        FloatingGoldParticle(particle)
    }
}

@Composable
fun FloatingGoldParticle(particle: ParticleState) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f, // Move upwards
        animationSpec = infiniteRepeatable(
            animation = tween((5000 / particle.speed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = (5000 / particle.speed).toInt()
                0f at 0
                0.8f at (durationMillis / 2)
                0f at durationMillis
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = offsetY
            }
    ) {
        val xPos = size.width * (particle.x / 100f)
        val yPos = size.height * (particle.y / 100f)
        
        drawCircle(
            color = Primary.copy(alpha = alpha),
            radius = particle.size.dp.toPx(),
            center = Offset(xPos, yPos)
        )
    }
}

@Composable
fun PremiumLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Primary, Secondary),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    ),
                    shape = CircleShape
                )
        ) {
            Text(
                text = "P",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 56.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "PayC",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Primary,
            letterSpacing = 4.sp,
            modifier = Modifier.graphicsLayer {
                // Optional: Add text shader if needed, but simple gold text is clean
            }
        )
    }
}

data class ParticleState(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)
