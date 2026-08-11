package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.entities.ItemEntity
import com.example.data.db.entities.LoanEntity
import com.example.data.db.entities.TransactionEntity
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.*

data class DashboardUiState(
    val selectedShopFilter: String = "All Shops", // "All Shops", "Shop 1", "Shop 2"
    val totalInventoryCount: Int = 0,
    val marketplaceCount: Int = 0,
    val activeLoansCount: Int = 0,
    val loansDueTodayCount: Int = 0,
    val overdueLoansCount: Int = 0,
    val totalRevenueKsh: Double = 0.0,
    val totalDisbursedKsh: Double = 0.0,
    val recentItems: List<ItemEntity> = emptyList(),
    val loansDueTodayList: List<LoanEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false
)

class DashboardViewModel(private val repository: MagTechRepository) : ViewModel() {

    private val _selectedShop = MutableStateFlow("All Shops")
    val selectedShop: StateFlow<String> = _selectedShop.asStateFlow()

    fun selectShopFilter(shop: String) {
        _selectedShop.value = shop
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        _selectedShop,
        repository.allItems,
        repository.allLoans,
        repository.getLoansDueToday(),
        repository.allTransactions
    ) { shop, items, loans, dueToday, txs ->
        val filteredItems = items.filter { shop == "All Shops" || it.shopLocation == shop }
        val filteredLoans = loans.filter { shop == "All Shops" || it.shopLocation == shop }
        val filteredDueToday = dueToday.filter { shop == "All Shops" || it.shopLocation == shop }
        val filteredOverdue = loans.filter { (it.status == "OVERDUE" || (it.dueDate < System.currentTimeMillis() && it.status != "REDEEMED")) && (shop == "All Shops" || it.shopLocation == shop) }
        val filteredTxs = txs.filter { shop == "All Shops" || it.shopLocation == shop }

        val activeLoansCount = filteredLoans.count { it.status == "ACTIVE" || it.status == "DUE_TODAY" || it.status == "OVERDUE" }
        val marketplaceCount = filteredItems.count { it.isPublishedToMarketplace }

        val totalRevenue = filteredTxs
            .filter { it.type == "LOAN_REPAYMENT" || it.type == "MARKETPLACE_SALE" }
            .sumOf { it.amount }

        val totalDisbursed = filteredTxs
            .filter { it.type == "LOAN_DISBURSED" }
            .sumOf { it.amount }

        DashboardUiState(
            selectedShopFilter = shop,
            totalInventoryCount = filteredItems.size,
            marketplaceCount = marketplaceCount,
            activeLoansCount = activeLoansCount,
            loansDueTodayCount = filteredDueToday.size,
            overdueLoansCount = filteredOverdue.size,
            totalRevenueKsh = totalRevenue,
            totalDisbursedKsh = totalDisbursed,
            recentItems = filteredItems.take(5),
            loansDueTodayList = filteredDueToday,
            recentTransactions = filteredTxs.take(5),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )
}
