package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.db.MagTechDatabase
import com.example.data.repository.MagTechRepository
import com.example.ui.ai.AiAssistantScreen
import com.example.ui.ai.AiAssistantViewModel
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.components.MagTechBottomNavBar
import com.example.ui.customer.CustomerListScreen
import com.example.ui.customer.CustomerListViewModel
import com.example.ui.customer.CustomerProfileScreen
import com.example.ui.customer.CustomerProfileViewModel
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.dashboard.DashboardViewModel
import com.example.ui.inventory.InventoryListScreen
import com.example.ui.inventory.InventoryListViewModel
import com.example.ui.inventory.ItemDetailScreen
import com.example.ui.inventory.ItemDetailViewModel
import com.example.ui.loan.NewLoanScreen
import com.example.ui.loan.NewLoanViewModel
import com.example.ui.marketplace.MarketplaceScreen
import com.example.ui.marketplace.MarketplaceViewModel
import com.example.ui.purchase.DirectPurchaseScreen
import com.example.ui.purchase.DirectPurchaseViewModel
import com.example.ui.navigation.Screen
import com.example.ui.reports.ReportsScreen
import com.example.ui.reports.ReportsViewModel
import com.example.ui.sms.SmartSmsScreen
import com.example.ui.sms.SmartSmsViewModel
import com.example.ui.theme.MagTechTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MagTechDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = MagTechRepository(
            itemDao = database.itemDao(),
            customerDao = database.customerDao(),
            loanDao = database.loanDao(),
            smsLogDao = database.smsLogDao(),
            transactionDao = database.transactionDao()
        )

        setContent {
            MagTechTheme {
                MagTechApp(repository = repository, application = application)
            }
        }
    }
}

@Composable
fun MagTechApp(
    repository: MagTechRepository,
    application: android.app.Application
) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(application) }
    val authState by authViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Login.route

    val mainTabRoutes = listOf(
        Screen.Dashboard.route,
        Screen.InventoryList.route,
        Screen.Marketplace.route,
        Screen.CustomerList.route,
        Screen.SmartSms.route
    )

    val showBottomBar = authState.isLoggedIn && currentRoute in mainTabRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                MagTechBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authState.isLoggedIn) Screen.Dashboard.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Login
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Dashboard
            composable(Screen.Dashboard.route) {
                val viewModel = remember { DashboardViewModel(repository) }
                DashboardScreen(
                    viewModel = viewModel,
                    userRole = authState.userRole,
                    userName = authState.userName,
                    onNavigateToNewLoan = { navController.navigate(Screen.NewLoan.route) },
                    onNavigateToDirectPurchase = { navController.navigate(Screen.DirectPurchase.route) },
                    onNavigateToMarketplace = { navController.navigate(Screen.Marketplace.route) },
                    onNavigateToSmartSms = { navController.navigate(Screen.SmartSms.route) },
                    onNavigateToAiAssistant = { navController.navigate(Screen.AiAssistant.route) },
                    onNavigateToItemDetail = { itemId -> navController.navigate(Screen.ItemDetail.createRoute(itemId)) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // New Loan (< 1 min fast intake)
            composable(Screen.NewLoan.route) {
                val viewModel = remember { NewLoanViewModel(repository) }
                NewLoanScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onLoanCreated = { loanId ->
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    }
                )
            }

            // Direct Purchase
            composable(Screen.DirectPurchase.route) {
                val viewModel = remember { DirectPurchaseViewModel(repository) }
                DirectPurchaseScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onItemPurchased = { itemId ->
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    }
                )
            }

            // Inventory Master List
            composable(Screen.InventoryList.route) {
                val viewModel = remember { InventoryListViewModel(repository) }
                InventoryListScreen(
                    viewModel = viewModel,
                    onNavigateToItemDetail = { itemId -> navController.navigate(Screen.ItemDetail.createRoute(itemId)) },
                    onNavigateToNewLoan = { navController.navigate(Screen.NewLoan.route) }
                )
            }

            // Item Detail
            composable(
                route = Screen.ItemDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.LongType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
                val viewModel = remember(itemId) { ItemDetailViewModel(repository, itemId) }
                ItemDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCustomerProfile = { customerId ->
                        navController.navigate(Screen.CustomerProfile.createRoute(customerId))
                    },
                    onNavigateToSmartSms = { navController.navigate(Screen.SmartSms.route) }
                )
            }

            // Marketplace Catalog
            composable(Screen.Marketplace.route) {
                val viewModel = remember { MarketplaceViewModel(repository) }
                MarketplaceScreen(
                    viewModel = viewModel,
                    onNavigateToItemDetail = { itemId -> navController.navigate(Screen.ItemDetail.createRoute(itemId)) }
                )
            }

            // Customer List
            composable(Screen.CustomerList.route) {
                val viewModel = remember { CustomerListViewModel(repository) }
                CustomerListScreen(
                    viewModel = viewModel,
                    onNavigateToCustomerProfile = { customerId ->
                        navController.navigate(Screen.CustomerProfile.createRoute(customerId))
                    }
                )
            }

            // Customer Profile
            composable(
                route = Screen.CustomerProfile.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                val viewModel = remember(customerId) { CustomerProfileViewModel(repository, customerId) }
                CustomerProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { itemId -> navController.navigate(Screen.ItemDetail.createRoute(itemId)) },
                    onNavigateToSmartSms = { navController.navigate(Screen.SmartSms.route) }
                )
            }

            // Smart SMS Reminders AI
            composable(Screen.SmartSms.route) {
                val viewModel = remember { SmartSmsViewModel(application, repository) }
                SmartSmsScreen(viewModel = viewModel)
            }

            // Reports & Financial Audit
            composable(Screen.Reports.route) {
                val viewModel = remember { ReportsViewModel(repository) }
                ReportsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // AI Valuation & Chat Assistant
            composable(Screen.AiAssistant.route) {
                val viewModel = remember { AiAssistantViewModel(repository) }
                AiAssistantScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
