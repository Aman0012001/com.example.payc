package com.example.payc.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.payc.ui.theme.*

/* -------------------------
     DATA CLASS + TIERS
-------------------------- */

data class ManagerTier(
    val title: String,
    val salary: String,
    val requirement: String,
    val color: Color
)

val managerTiers = listOf(
    ManagerTier("Economy Manager", "50,000", "20 Economy Members", Color(0xFF9E9E9E)),
    ManagerTier("Supreme Manager", "70,000", "20 Supreme Members", Color(0xFF8E2DE2)),
    ManagerTier("Silver Manager", "100,000", "20 Silver Members", Color(0xFFBDC3C7)),
    ManagerTier("Platinum Manager", "200,000", "20 Platinum Members", Color(0xFF6DD5FA)),
    ManagerTier("Gold Manager", "300,000", "20 Gold Members", Color(0xFFFFD700)),
    ManagerTier("Diamond Manager", "400,000", "20 Diamond Members", Color(0xFF00F260)),
    ManagerTier("Master Manager", "500,000", "20 Master Members", Color(0xFFFF512F)),
    ManagerTier("Economy Super Manager", "250,000", "Super Manager", Color(0xFF9E9E9E)),
    ManagerTier("Supreme Super Manager", "375,000", "Super Manager", Color(0xFF8E2DE2)),
    ManagerTier("Silver Super Manager", "500,000", "Super Manager", Color(0xFFBDC3C7)),
    ManagerTier("Platinum Super Manager", "1,000,000", "Super Manager", Color(0xFF6DD5FA)),
    ManagerTier("Gold Super Manager", "1,500,000", "Super Manager", Color(0xFFFFD700)),
    ManagerTier("Diamond Super Manager", "2,000,000", "Super Manager", Color(0xFF00F260)),
    ManagerTier("Master Super Manager", "2,500,000", "Super Manager", Color(0xFFFF512F))
)

/* -------------------------
        MAIN MANAGER SCREEN
-------------------------- */

@Composable
fun ManagerScreen() {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
            .systemBarsPadding()
    ) {

        // HEADER
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + slideInVertically { -it }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        "Manager Program",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                    Text(
                        "Lead • Earn • Grow",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Salary Tiers Title
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800, delayMillis = 300))
        ) {
            Text(
                "Salary Tiers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Salary Cards List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(managerTiers) { tier ->
                PremiumManagerTierCard(tier)
            }
        }
    }
}

/* -------------------------
      SALARY CARD (FIXED)
-------------------------- */

@Composable
fun PremiumManagerTierCard(tier: ManagerTier) {

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = tier.color.copy(alpha = 0.4f)
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            tier.color.copy(alpha = 0.12f),
                            tier.color.copy(alpha = 0.04f)
                        )
                    )
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // LEFT BADGE
                Surface(
                    shape = CircleShape,
                    color = tier.color.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = tier.title.take(1),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = tier.color
                        )
                    }
                }

                // MIDDLE CONTENT — FINAL FIX (NO BREAK ON ANY PHONE)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    // TITLE (FIXED)
                    Text(
                        text = tier.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // SUBTITLE
                    Text(
                        text = "Monthly Salary",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurface.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // SALARY (FIXED)
                    Text(
                        text = "RS ${tier.salary}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF10B981),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                // RIGHT SECTION
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tier.color.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = "Target",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = tier.color
                        )
                    }

                    Text(
                        text = tier.requirement,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
