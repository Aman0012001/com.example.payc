package com.example.payc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.automirrored.filled.List // ✅ Correct AutoMirrored import
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// Updated bottom nav items (AutoMirrored for List)
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Tasks : BottomNavItem("tasks", Icons.AutoMirrored.Filled.List, "Work") // ✅ Fixed
    object Invest : BottomNavItem("invest", Icons.Filled.MonetizationOn, "My Tasks")
    object Wallet : BottomNavItem("wallet", Icons.Filled.AccountBalanceWallet, "Wallet")
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Tasks,
        BottomNavItem.Invest,
        BottomNavItem.Wallet
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.Black,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Color.Black else Color.Black.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) Color.Black else Color.Black.copy(alpha = 0.6f),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White.copy(alpha = 0.3f),
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Black.copy(alpha = 0.6f),
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Black.copy(alpha = 0.6f)
                ),
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
