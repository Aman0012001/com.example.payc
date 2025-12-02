package com.example.payc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.Primary

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val type: NotificationType,
    val time: String,
    val isRead: Boolean = false
)

enum class NotificationType {
    SUCCESS, INFO, WARNING, ERROR
}

@Composable
fun NotificationScreen() {
    // Sample notifications for testing
    val notifications = listOf(
        Notification(
            1,
            "Payment Successful!",
            "₹1,000 has been added to your wallet.",
            NotificationType.SUCCESS,
            "2 minutes ago"
        ),
        Notification(
            2,
            "New Task Available",
            "Watch Product Video - Earn ₹10",
            NotificationType.INFO,
            "5 minutes ago"
        ),
        Notification(
            3,
            "Withdrawal Approved",
            "Your withdrawal request of ₹500 has been approved.",
            NotificationType.SUCCESS,
            "1 hour ago",
            true
        ),
        Notification(
            4,
            "Plan Purchased!",
            "You successfully purchased the Supreme Plan. Daily profit: ₹666",
            NotificationType.SUCCESS,
            "2 hours ago",
            true
        ),
        Notification(
            5,
            "Low Balance Warning",
            "Your wallet balance is low. Add funds to continue.",
            NotificationType.WARNING,
            "3 hours ago"
        ),
        Notification(
            6,
            "Task Completed",
            "You earned ₹25 for completing the survey task.",
            NotificationType.SUCCESS,
            "5 hours ago",
            true
        ),
        Notification(
            7,
            "New Referral!",
            "John Doe joined using your referral code!",
            NotificationType.INFO,
            "1 day ago",
            true
        ),
        Notification(
            8,
            "Withdrawal Pending",
            "Your withdrawal request is being processed.",
            NotificationType.INFO,
            "2 days ago",
            true
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Primary,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${notifications.count { !it.isRead }}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Notifications List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    val backgroundColor = if (notification.isRead) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val (icon, iconColor) = when (notification.type) {
        NotificationType.SUCCESS -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        NotificationType.INFO -> Icons.Default.Info to Color(0xFF2196F3)
        NotificationType.WARNING -> Icons.Default.Warning to Color(0xFFFF9800)
        NotificationType.ERROR -> Icons.Default.Error to Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (!notification.isRead) {
                        Surface(
                            shape = CircleShape,
                            color = Primary,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }
                }

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
