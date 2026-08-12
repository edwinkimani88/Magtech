package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.entities.ItemEntity
import com.example.data.db.entities.LoanEntity
import com.example.data.db.entities.TransactionEntity
import com.example.data.repository.MagTechRepository
import com.example.util.TimePeriod
import com.example.util.TimeFilterUtils
import kotlinx.coroutines.flow.*

data class DashboardUiState(
    val selectedShopFilter: String = "All Shops", // "All Shops", "Shop 1", "Shop 2"
    val selectedTimePeriod: TimePeriod = TimePeriod.TODAY, // Default Today
    val totalInventoryCount: Int = 0,
    val marketplaceCount: Int = 0,
    val activeLoansCount: Int = 0,
    val loansDueTodayCount: Int = 0,
    val overdueLoansCount: Int = 0,
    val totalRevenueKsh: Double = 0.0,
    val totalDisbursedKsh: Double = 0.0,
    val totalPurchasesKsh: Double = 0.0,
    val recentItems: List<ItemEntity> = emptyList(),
    val loansDueTodayList: List<LoanEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false
)

class DashboardViewModel(private val repository: MagTechRepository) : ViewModel() {

    private val _selectedShop = MutableStateFlow("All Shops")
    val selectedShop: StateFlow<String> = _selectedShop.asStateFlow()

    private val _selectedTimePeriod = MutableStateFlow(TimePeriod.TODAY)
    val selectedTimePeriod: StateFlow<TimePeriod> = _selectedTimePeriod.asStateFlow()

    fun selectShopFilter(shop: String) {
        _selectedShop.value = shop
    }

    fun selectTimePeriod(period: TimePeriod) {
        _selectedTimePeriod.value = period
    }

    private val _filterState = combine(_selectedShop, _selectedTimePeriod) { shop, period ->
        shop to period
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        _filterState,
        repository.allItems,
        repository.allLoans,
        repository.getLoansDueToday(),
        repository.allTransactions
    ) { (shop, period), items, loans, dueToday, txs ->
        val startTime = TimeFilterUtils.getStartTimestamp(period)

        val filteredItems = items.filter { shop == "All Shops" || it.shopLocation == shop }
        val filteredLoans = loans.filter { shop == "All Shops" || it.shopLocation == shop }
        val filteredDueToday = dueToday.filter { shop == "All Shops" || it.shopLocation == shop }
        val filteredOverdue = loans.filter { (it.status == "OVERDUE" || (it.dueDate < System.currentTimeMillis() && it.status != "REDEEMED")) && (shop == "All Shops" || it.shopLocation == shop) }

        // Filter transactions by shop AND time range!
        val filteredTxs = txs.filter {
            (shop == "All Shops" || it.shopLocation == shop) &&
            (period == TimePeriod.ALL_TIME || it.timestamp >= startTime)
        }

        val activeLoansCount = filteredLoans.count { it.status == "ACTIVE" || it.status == "DUE_TODAY" || it.status == "OVERDUE" }
        val marketplaceCount = filteredItems.count { it.isPublishedToMarketplace }

        val totalRevenue = filteredTxs
            .filter { it.type == "LOAN_REPAYMENT" || it.type == "MARKETPLACE_SALE" }
            .sumOf { it.amount }

        val totalDisbursed = filteredTxs
            .filter { it.type == "LOAN_DISBURSED" }
            .sumOf { it.amount }

        val totalPurchases = filteredTxs
            .filter { it.type == "DIRECT_PURCHASE" }
            .sumOf { it.amount }

        DashboardUiState(
            selectedShopFilter = shop,
            selectedTimePeriod = period,
            totalInventoryCount = filteredItems.size,
            marketplaceCount = marketplaceCount,
            activeLoansCount = activeLoansCount,
            loansDueTodayCount = filteredDueToday.size,
            overdueLoansCount = filteredOverdue.size,
            totalRevenueKsh = totalRevenue,
            totalDisbursedKsh = totalDisbursed,
            totalPurchasesKsh = totalPurchases,
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

