package com.example.payc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InvestmentPlan(
    val id: Int,
    val name: String,
    val deposit: String,
    val dailyProfit: String,
    val colorStart: Color,
    val colorEnd: Color,
    val accentColor: Color
)

val samplePlans = listOf(
    InvestmentPlan(1, "Economy", "15,000", "333", Color(0xFF2C3E50), Color(0xFF4CA1AF), Color(0xFF4CA1AF)),
    InvestmentPlan(2, "Supreme", "30,000", "666", Color(0xFF4A00E0), Color(0xFF8E2DE2), Color(0xFF8E2DE2)),
    InvestmentPlan(3, "Silver", "60,000", "1,333", Color(0xFFBDC3C7), Color(0xFF2C3E50), Color(0xFFBDC3C7)),
    InvestmentPlan(4, "Platinum", "120,000", "2,666", Color(0xFF141E30), Color(0xFF243B55), Color(0xFF6DD5FA)),
    InvestmentPlan(5, "Gold", "240,000", "5,333", Color(0xFFF2994A), Color(0xFFF2C94C), Color(0xFFFFD700)),
    InvestmentPlan(6, "Diamond", "480,000", "10,666", Color(0xFF00F260), Color(0xFF0575E6), Color(0xFF00F260)),
    InvestmentPlan(7, "Master", "960,000", "21,333", Color(0xFFEB3349), Color(0xFFF45C43), Color(0xFFFF512F))
)

@Composable
fun InvestScreen(onStartTaskClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
            )

            Text(
                text = "Choose a plan to start earning",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(samplePlans) { plan ->
                    PremiumInvestmentCard(plan, onStartTaskClick)
                }
            }
        }
    }
}

@Composable
fun PremiumInvestmentCard(plan: InvestmentPlan, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(plan.colorStart, plan.colorEnd),
                        tileMode = TileMode.Mirror
                    )
                )
        ) {
            // Glass overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // TOP ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = plan.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // BADGE
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = plan.accentColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            plan.accentColor.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "High Return",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // BOTTOM ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {

                    Column {
                        Text(
                            text = "Daily Profit",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "RS ${plan.dailyProfit}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Deposit: RS ${plan.deposit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = plan.colorEnd
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = plan.accentColor
                        )
                    ) {
                        Text(
                            "Start Task",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
