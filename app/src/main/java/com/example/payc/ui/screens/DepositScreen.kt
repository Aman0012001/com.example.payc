package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(onBackClick: () -> Unit) {

    var amount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Easypaisa") }

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
                    title = { Text("Deposit Funds", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Primary
                            )
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

                // AMOUNT INPUT
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { it / 4 }
                ) {
                    Column {
                        Text(
                            "Enter Amount",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("RS 0.00", color = OnSurface.copy(alpha = 0.3f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                                focusedContainerColor = Surface.copy(alpha = 0.6f),
                                unfocusedContainerColor = Surface.copy(alpha = 0.4f),
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                                cursorColor = Primary
                            ),
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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

                // ACCOUNT INFO CARD
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400))
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Send Payment To:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Account Title: ", color = OnSurface.copy(alpha = 0.6f))
                                Text("PayC Official", fontWeight = FontWeight.Bold, color = OnSurface)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Account Number: ", color = OnSurface.copy(alpha = 0.6f))
                                Text("0300-1234567", fontWeight = FontWeight.Bold, color = OnSurface)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "Please send the exact amount to the above account and upload the screenshot below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // UPLOAD PROOF
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 600))
                ) {
                    Column {
                        Text(
                            "Upload Proof",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Surface.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable { }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.UploadFile,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    "Tap to upload screenshot",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 800)) + slideInVertically { it }
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
                        Text("Submit Deposit", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) color.copy(alpha = 0.2f) else Surface.copy(alpha = 0.4f),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) color else Primary.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .width(130.dp)
            .height(70.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) color else OnSurface.copy(alpha = 0.7f)
            )
        }
    }
}
