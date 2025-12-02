@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.payc.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onManagerClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onNotificationClick: () -> Unit = {}
) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800)) + slideInVertically { -it }
                ) {
                    PremiumHeaderSection(onProfileClick, onNotificationClick)
                }
            }
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically { it / 2 }
                ) {
                    PremiumBalanceCard()
                }
            }
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400))
                ) {
                    PremiumActionButtons(onManagerClick, onDepositClick, onWithdrawClick)
                }
            }
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 600))
                ) {
                    PremiumReferralSection(context = androidx.compose.ui.platform.LocalContext.current)
                }
            }
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 800))
                ) {
                    PremiumSponsoredSection()
                }
            }
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 1000))
                ) {
                    PremiumRecentActivitySection()
                }
            }
        }
    }
}

@Composable
fun PremiumHeaderSection(onProfileClick: () -> Unit, onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Alex Johnson",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Notification Icon with Badge
            Box {
                Surface(
                    onClick = onNotificationClick,
                    shape = CircleShape,
                    color = Surface.copy(alpha = 0.5f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                // Notification Badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF4444),
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                ) {}
            }
            
            // Profile Avatar
            Surface(
                onClick = onProfileClick,
                shape = CircleShape,
                color = Primary,
                modifier = Modifier.size(44.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Primary.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "AJ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumBalanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Primary.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface.copy(alpha = 0.7f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.1f),
                            Secondary.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Total Balance",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RS 12,450.00",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary,
                            letterSpacing = (-1).sp
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.2f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Wallet,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    PremiumStatItem(
                        label = "Profit",
                        value = "+RS 340.50",
                        color = Color(0xFF10B981),
                        icon = Icons.Default.ArrowUpward
                    )
                    PremiumStatItem(
                        label = "Today",
                        value = "+RS 12.00",
                        color = Color(0xFF10B981),
                        icon = Icons.Default.DateRange
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumStatItem(label: String, value: String, color: Color, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun PremiumActionButtons(
    onManagerClick: () -> Unit,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumActionButton(
            icon = Icons.Default.Add,
            label = "Deposit",
            onClick = onDepositClick,
            modifier = Modifier.weight(1f)
        )
        PremiumActionButton(
            icon = Icons.Default.ArrowUpward,
            label = "Withdraw",
            onClick = onWithdrawClick,
            modifier = Modifier.weight(1f)
        )
        PremiumActionButton(
            icon = Icons.Default.Star,
            label = "Manager",
            onClick = onManagerClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PremiumActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface.copy(alpha = 0.6f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Primary.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PremiumReferralSection(context: android.content.Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.9f),
                            Secondary.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Refer & Earn",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Get 5% Commission on every deposit!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val sendIntent: android.content.Intent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "Join PayC and earn money! Use my referral code: AJ123 https://payc.app/ref/aj123"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Invite Friends", fontWeight = FontWeight.Bold)
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.15f),
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

private val sponsoredCompanies = listOf(
    SponsoredCompany("Amazon", "https://logo.clearbit.com/amazon.com"),
    SponsoredCompany("Temu", "https://logo.clearbit.com/temu.com"),
    SponsoredCompany("KFC", "https://logo.clearbit.com/kfc.com"),
    SponsoredCompany("Adidas", "https://logo.clearbit.com/adidas.com"),
    SponsoredCompany("Noon", "https://logo.clearbit.com/noon.com"),
    SponsoredCompany("Nike", "https://logo.clearbit.com/nike.com")
)

@Composable
fun PremiumSponsoredSection() {
    Column {
        Text(
            text = "Trusted Partners",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sponsoredCompanies) { company ->
                PremiumSponsoredLogo(company)
            }
        }
    }
}

data class SponsoredCompany(val name: String, val logoUrl: String)

@Composable
fun PremiumSponsoredLogo(company: SponsoredCompany) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.size(80.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = company.logoUrl,
                    contentDescription = company.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Text(
            text = company.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = OnSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun PremiumRecentActivitySection() {
    Column {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
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
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(1000) { index ->
                        val activityType = index % 3
                        when (activityType) {
                            0 -> PremiumActivityItem(
                                title = "Task Reward",
                                date = "Just now",
                                amount = "+RS 5.00",
                                isPositive = true
                            )
                            1 -> PremiumActivityItem(
                                title = "Investment Return",
                                date = "2 mins ago",
                                amount = "+RS 12.50",
                                isPositive = true
                            )
                            2 -> PremiumActivityItem(
                                title = "Withdrawal",
                                date = "5 mins ago",
                                amount = "-RS 50.00",
                                isPositive = false
                            )
                        }
                    }
                }
                
                // Gradient overlays
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
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
                        .height(50.dp)
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
fun PremiumActivityItem(title: String, date: String, amount: String, isPositive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isPositive) Color(0xFF10B981).copy(alpha = 0.2f)
                else Color(0xFFEF4444).copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowDownward
                        else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface.copy(alpha = 0.5f)
                )
            }
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) Color(0xFF10B981) else OnSurface
        )
    }
}
