package com.aistudio.magtechinvestments.nbi26.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object NewLoan : Screen("new_loan")
    object DirectPurchase : Screen("direct_purchase")
    object InventoryList : Screen("inventory_list")
    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Long) = "item_detail/$itemId"
    }
    object Marketplace : Screen("marketplace")
    object CustomerList : Screen("customer_list")
    object CustomerProfile : Screen("customer_profile/{customerId}") {
        fun createRoute(customerId: Long) = "customer_profile/$customerId"
    }
    object SmartSms : Screen("smart_sms")
    object Reports : Screen("reports")
    object AiAssistant : Screen("ai_assistant")
}
