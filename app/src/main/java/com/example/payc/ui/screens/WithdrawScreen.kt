package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(onBackClick: () -> Unit) {

    var amount by remember { mutableStateOf("") }
    var accountTitle by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Easypaisa") }

    val quickAmounts = listOf("1000", "3000", "10000", "50000")

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        AuroraBackground()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Withdraw Funds", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // AMOUNT INPUT SECTION
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { it / 4 }
                ) {
                    Column {
                        Text(
                            "Withdraw Amount",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("RS 0.00", color = OnSurface.copy(alpha = 0.3f))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                                cursorColor = Primary
                            ),
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // QUICK AMOUNTS FIXED CHIPS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickAmounts.forEach { quickAmount ->

                                SuggestionChip(
                                    onClick = { amount = quickAmount },
                                    label = {
                                        Text(
                                            quickAmount,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (amount == quickAmount)
                                            Primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),

                                        labelColor = if (amount == quickAmount)
                                            Primary
                                        else OnSurface.copy(alpha = 0.7f)
                                    ),

                                    // FIXED BORDER
                                    border = BorderStroke(1.dp, Color.Gray),

                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Available Balance: RS 12,450.00",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary.copy(alpha = 0.8f)
                        )
                    }
                }


                // PAYMENT METHOD
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200))
                ) {
                    Column {
                        Text(
                            "Select Payment Method",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            PaymentMethodCard(
                                name = "Easypaisa",
                                selected = selectedMethod == "Easypaisa",
                                color = Color(0xFF37B546),
                                onClick = { selectedMethod = "Easypaisa" }
                            )

                            PaymentMethodCard(
                                name = "Jazzcash",
                                selected = selectedMethod == "Jazzcash",
                                color = Color(0xFFD32F2F),
                                onClick = { selectedMethod = "Jazzcash" }
                            )
                        }
                    }
                }


                // ACCOUNT DETAILS
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400))
                ) {
                    Column {

                        Text(
                            "Account Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ACCOUNT TITLE
                        Text(
                            "Account Title",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = accountTitle,
                            onValueChange = { accountTitle = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("e.g. Alex Johnson", color = OnSurface.copy(alpha = 0.3f))
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                                cursorColor = Primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ACCOUNT NUMBER
                        Text(
                            "Account Number",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("e.g. 03001234567", color = OnSurface.copy(alpha = 0.3f))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                                cursorColor = Primary
                            )
                        )
                    }
                }


                Spacer(modifier = Modifier.weight(1f))

                // SUBMIT BUTTON
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically { it }
                ) {
                    Button(
                        onClick = { onBackClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Request Withdrawal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    name: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        border = if (selected) BorderStroke(2.dp, color) else null,
        modifier = Modifier
            .height(80.dp)
            .width(140.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) color else OnSurface.copy(alpha = 0.7f)
            )
        }
    }
}
