package com.example.payc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.payc.ui.navigation.BottomNavigationBar
import com.example.payc.ui.screens.*
import com.example.payc.ui.theme.PaycTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            PaycTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Login.route
    ) {
        // Auth Routes
        composable(Route.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Permission.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Route.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(Route.ForgotPassword.route) }
            )
        }

        composable(Route.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Route.Permission.route) {
                        popUpTo(Route.SignUp.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(Route.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.Permission.route) {
            PermissionScreen(
                onContinueClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        // Bottom Nav Screens
        composable(Route.Home.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(
                        onManagerClick = { navController.navigate(Route.Manager.route) },
                        onProfileClick = { navController.navigate(Route.Profile.route) },
                        onDepositClick = { navController.navigate(Route.Deposit.route) },
                        onWithdrawClick = { navController.navigate(Route.Withdraw.route) },
                        onNotificationClick = { navController.navigate(Route.Notification.route) }
                    )
                }
            }
        }

        composable(Route.Tasks.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    TaskScreen()
                }
            }
        }

        composable(Route.Invest.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    InvestScreen(
                        onStartTaskClick = { navController.navigate(Route.Tasks.route) }
                    )
                }
            }
        }

        composable(Route.Wallet.route) {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    WalletScreen(
                        onDepositClick = { navController.navigate(Route.Deposit.route) },
                        onWithdrawClick = { navController.navigate(Route.Withdraw.route) }
                    )
                }
            }
        }

        composable(Route.Profile.route) {
            // ProfileScreen handles its own Scaffold/BottomBar internally or as designed
            ProfileScreen(navController)
        }

        // Full Screen Routes (No Bottom Bar usually, or as per design)
        composable(Route.Deposit.route) {
            DepositScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.Withdraw.route) {
            WithdrawScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.Manager.route) {
            // ManagerScreen doesn't have explicit back button in UI, relying on system back
            ManagerScreen()
        }

        composable(Route.Notification.route) {
            // NotificationScreen doesn't have explicit back button in UI, relying on system back
            NotificationScreen()
        }

        composable(Route.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.BankDetails.route) {
            BankDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.TermsConditions.route) {
            TermsConditionsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}


/** Route definitions */
enum class Route(val route: String) {
    Login("login"),
    SignUp("signup"),
    ForgotPassword("forgot_password"),
    Permission("permission"),
    Home("home"),
    Tasks("tasks"),
    Invest("invest"),
    Wallet("wallet"),
    Profile("profile"),
    Deposit("deposit"),
    Withdraw("withdraw"),
    Manager("manager"),
    Notification("notification"),
    EditProfile("edit_profile"),
    BankDetails("bank_details"),
    ChangePassword("change_password"),
    PrivacyPolicy("privacy_policy"),
    TermsConditions("terms_conditions")
}
