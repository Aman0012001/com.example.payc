package com.example.payc.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.payc.ui.theme.*

@Composable
fun TaskScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = remember { listOf("Available", "Completed") }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /** HEADER **/
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically { -it }
            ) {
                Text(
                    "My Tasks",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            /** TAB ROW **/
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 200))
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Primary,
                            height = 3.dp
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            color = Primary.copy(alpha = 0.2f)
                        )
                    },
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight =
                                    if (selectedTab == index) FontWeight.Bold
                                    else FontWeight.Normal,
                                    color =
                                    if (selectedTab == index) Primary
                                    else OnSurface.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        )
                    }
                }
            }

            /** Fake completed list */
            val completedPlans = emptyList<InvestmentPlan>()

            val displayedPlans =
                if (selectedTab == 0) samplePlans else completedPlans

            /** TASK LIST **/
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 400)) +
                        slideInVertically { it / 4 }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(displayedPlans) { plan ->
                        PremiumTaskItem(plan)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumTaskItem(plan: InvestmentPlan) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = plan.accentColor.copy(alpha = 0.5f)
            )
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            plan.colorStart.copy(alpha = 0.1f),
                            plan.colorEnd.copy(alpha = 0.05f)
                        )
                    )
                )
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                /** LEFT SIDE — INFO **/
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = plan.colorStart.copy(alpha = 0.2f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                plan.name.take(1),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = plan.colorEnd
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            plan.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Deposit: RS ${plan.deposit}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                /** RIGHT SIDE — PROFIT + BUTTON **/
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "RS ${plan.dailyProfit}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            "Daily Profit",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurface.copy(alpha = 0.5f)
                        )
                    }

                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 0.dp
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            "Start",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
