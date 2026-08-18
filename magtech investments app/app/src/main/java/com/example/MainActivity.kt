package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.MagTechBottomNavigationBar
import com.example.ui.screens.*
import com.example.ui.theme.MagTechTheme
import com.example.ui.viewmodel.MagTechViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MagTechTheme {
                val viewModel: MagTechViewModel = viewModel()
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                val userSession by viewModel.userSession.collectAsState()
                val uiMessage by viewModel.uiMessage.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Display Toast / Snackbar messages from ViewModel
                LaunchedEffect(uiMessage) {
                    uiMessage?.let { msg ->
                        scope.launch {
                            snackbarHostState.showSnackbar(msg)
                            viewModel.clearUiMessage()
                        }
                    }
                }

                val startDestination = if (userSession != null) Screen.Dashboard.route else Screen.Login.route

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (userSession != null && currentRoute != Screen.Login.route) {
                            MagTechBottomNavigationBar(
                                currentRoute = currentRoute,
                                onNavigateToRoute = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Dashboard.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToCreateLoan = { navController.navigate(Screen.CreateLoan.route) },
                                onNavigateToLoanDetail = { loanId -> navController.navigate(Screen.LoanDetail.createRoute(loanId)) },
                                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                                onNavigateToSmsAi = { navController.navigate(Screen.SmsAi.route) },
                                onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.CreateLoan.route) {
                            CreateLoanScreen(
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = Screen.LoanDetail.route,
                            arguments = listOf(navArgument("loanId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val loanId = backStackEntry.arguments?.getString("loanId") ?: ""
                            LoanDetailScreen(
                                loanId = loanId,
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onNavigateToSmsAi = { navController.navigate(Screen.SmsAi.route) },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Inventory.route) {
                            InventoryScreen(
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Marketplace.route) {
                            MarketplaceScreen(
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Transactions.route) {
                            TransactionsScreen(
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.SmsAi.route) {
                            SmsAiScreen(
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.AiAssistant.route) {
                            AiAssistantScreen(
                                viewModel = viewModel,
                                onNavigateHome = { navController.popBackStack() },
                                onLogout = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
