package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*

// ==================== EDIT PROFILE SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBackClick: () -> Unit) {
    var name by remember { mutableStateOf("Alex Johnson") }
    var email by remember { mutableStateOf("alex.johnson@example.com") }
    var phone by remember { mutableStateOf("03001234567") }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Primary
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AuroraBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Profile Picture
                        Surface(
                            shape = CircleShape,
                            color = Primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextButton(onClick = { /* TODO: Upload photo */ }) {
                            Text("Change Photo", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { 100 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Name Field
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = Primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = Primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        // Phone Field
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it.filter { c -> c.isDigit() } },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = Primary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(tween(800, delayMillis = 400)) { it }
                ) {
                    Button(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== BANK DETAILS SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankDetailsScreen(onBackClick: () -> Unit) {
    var accountTitle by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Primary
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AuroraBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Add Your Bank Account",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                "Link your bank account for seamless withdrawals",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { 100 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = accountTitle,
                            onValueChange = { accountTitle = it },
                            label = { Text("Account Title") },
                            placeholder = { Text("e.g., Alex Johnson") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it.filter { c -> c.isDigit() } },
                            label = { Text("Account Number") },
                            placeholder = { Text("e.g., 1234567890") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank Name") },
                            placeholder = { Text("e.g., State Bank") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        OutlinedTextField(
                            value = ifscCode,
                            onValueChange = { ifscCode = it.uppercase() },
                            label = { Text("IFSC / Branch Code") },
                            placeholder = { Text("e.g., SBIN0001234") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(tween(800, delayMillis = 400)) { it }
                ) {
                    Button(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Save Bank Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== CHANGE PASSWORD SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBackClick: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Primary
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AuroraBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Secure Your Account",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                "Choose a strong password to keep your account safe",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { 100 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Current Password
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary) },
                            trailingIcon = {
                                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                    Icon(
                                        if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null,
                                        tint = OnSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        // New Password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary) },
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null,
                                        tint = OnSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                        
                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm New Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Primary) },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        null,
                                        tint = OnSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = OnSurface.copy(alpha = 0.2f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(tween(800, delayMillis = 400)) { it }
                ) {
                    Button(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Update Password", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== PRIVACY POLICY SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Primary
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AuroraBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    Column {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "Last Updated: December 2024",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                PolicySection(
                                    title = "1. Information We Collect",
                                    content = "We collect information you provide directly to us, including your name, email address, phone number, and payment information when you use our services."
                                )
                                
                                PolicySection(
                                    title = "2. How We Use Your Information",
                                    content = "We use the information we collect to provide, maintain, and improve our services, process transactions, send you technical notices and support messages."
                                )
                                
                                PolicySection(
                                    title = "3. Data Security",
                                    content = "We implement appropriate security measures to protect your personal information against unauthorized access, alteration, disclosure, or destruction."
                                )
                                
                                PolicySection(
                                    title = "4. Your Rights",
                                    content = "You have the right to access, update, or delete your personal information at any time. Contact us for assistance with your data rights."
                                )
                                
                                PolicySection(
                                    title = "5. Contact Us",
                                    content = "If you have any questions about this Privacy Policy, please contact us at support@payc.com"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TERMS & CONDITIONS SCREEN ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsConditionsScreen(onBackClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Primary
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            AuroraBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    Column {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "Effective Date: December 2024",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                PolicySection(
                                    title = "1. Acceptance of Terms",
                                    content = "By accessing and using PayC, you accept and agree to be bound by the terms and provision of this agreement."
                                )
                                
                                PolicySection(
                                    title = "2. Use of Service",
                                    content = "You agree to use the service only for lawful purposes and in accordance with these Terms. You must not use the service in any way that violates any applicable laws or regulations."
                                )
                                
                                PolicySection(
                                    title = "3. Account Responsibilities",
                                    content = "You are responsible for maintaining the confidentiality of your account and password. You agree to accept responsibility for all activities that occur under your account."
                                )
                                
                                PolicySection(
                                    title = "4. Payment Terms",
                                    content = "All payments are processed securely. We reserve the right to refuse or cancel any transaction for any reason. Refunds are subject to our refund policy."
                                )
                                
                                PolicySection(
                                    title = "5. Limitation of Liability",
                                    content = "PayC shall not be liable for any indirect, incidental, special, consequential or punitive damages resulting from your use of or inability to use the service."
                                )
                                
                                PolicySection(
                                    title = "6. Changes to Terms",
                                    content = "We reserve the right to modify these terms at any time. We will notify users of any material changes via email or through the app."
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface.copy(alpha = 0.8f),
            lineHeight = 20.sp
        )
    }
}
