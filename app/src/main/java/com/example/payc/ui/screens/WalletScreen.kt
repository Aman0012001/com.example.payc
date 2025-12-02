package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*
import kotlinx.coroutines.delay


data class Transaction(
    val id: Int,
    val title: String,
    val date: String,
    val amount: String,
    val isIncoming: Boolean
)

val sampleTransactions = listOf(
    Transaction(1, "Deposit", "Today, 10:30 AM", "+RS 50.00", true),
    Transaction(2, "Investment", "Yesterday, 2:15 PM", "-RS 20.00", false),
    Transaction(3, "Task Reward", "Nov 23, 9:00 AM", "+RS 1.50", true),
    Transaction(4, "Withdrawal", "Nov 20, 4:45 PM", "-RS 100.00", false),
    Transaction(5, "Referral Bonus", "Nov 18, 11:20 AM", "+RS 5.00", true)
)

@Composable
fun WalletScreen(onDepositClick: () -> Unit, onWithdrawClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Aurora Background
        AuroraBackground()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Wallet",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                        Surface(
                            shape = CircleShape,
                            color = Primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Wallet,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(800, delayMillis = 200)
                    )
                ) {
                    PremiumBalanceSection(onDepositClick, onWithdrawClick)
                }
            }
            
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400))
                ) {
                    PremiumTransactionHistory()
                }
            }
        }
    }
}

@Composable
fun PremiumBalanceSection(onDepositClick: () -> Unit, onWithdrawClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.15f),
                            Secondary.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.3f),
                            Secondary.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Balance Display
                Surface(
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurface.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "RS 12,450.00",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary,
                    letterSpacing = (-1).sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Deposit Options
                Text(
                    "Deposit via",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDepositClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37B546)
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Easypaisa", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDepositClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Jazzcash", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Withdrawal Options
                Text(
                    "Quick Withdraw",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumWithdrawChip("1k", onWithdrawClick)
                    PremiumWithdrawChip("3k", onWithdrawClick)
                    PremiumWithdrawChip("10k", onWithdrawClick)
                    PremiumWithdrawChip("50k", onWithdrawClick)
                }
            }
        }
    }
}

@Composable
fun PremiumWithdrawChip(amount: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Surface.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.3f)),
        modifier = Modifier.width(70.dp).height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                amount,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}

@Composable
fun PremiumTransactionHistory() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(2500)
                        val nextIndex = (listState.firstVisibleItemIndex + 1) % 1000
                        listState.animateScrollToItem(nextIndex)
                    }
                }

                androidx.compose.foundation.lazy.LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(1000) { index ->
                        val originalIndex = index % sampleTransactions.size
                        PremiumTransactionItem(sampleTransactions[originalIndex])
                    }
                }
                
                // Gradient overlays
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Surface.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Surface.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun PremiumTransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (transaction.isIncoming) 
                    Color(0xFF10B981).copy(alpha = 0.2f)
                else 
                    Color(0xFFEF4444).copy(alpha = 0.2f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (transaction.isIncoming) 
                            Icons.Default.ArrowDownward
                        else 
                            Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (transaction.isIncoming) 
                            Color(0xFF10B981)
                        else 
                            Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface.copy(alpha = 0.5f)
                )
            }
        }
        Text(
            text = transaction.amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (transaction.isIncoming) 
                Color(0xFF10B981)
            else 
                OnSurface
        )
    }
}
