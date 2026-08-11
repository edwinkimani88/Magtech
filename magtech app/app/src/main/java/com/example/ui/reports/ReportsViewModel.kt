package com.example.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.entities.TransactionEntity
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.*

data class ReportsUiState(
    val selectedShopFilter: String = "All Shops", // "All Shops", "Shop 1", "Shop 2"
    val totalRevenue: Double = 0.0,
    val totalLoansDisbursed: Double = 0.0,
    val totalDirectPurchases: Double = 0.0,
    val totalTransactionsCount: Int = 0,
    val transactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false
)

class ReportsViewModel(private val repository: MagTechRepository) : ViewModel() {

    private val _selectedShopFilter = MutableStateFlow("All Shops")

    fun selectShopFilter(shop: String) {
        _selectedShopFilter.value = shop
    }

    val uiState: StateFlow<ReportsUiState> = combine(
        _selectedShopFilter,
        repository.allTransactions
    ) { shopFilter, txs ->
        val filteredTxs = txs.filter { shopFilter == "All Shops" || it.shopLocation == shopFilter }

        val revenue = filteredTxs
            .filter { it.type == "LOAN_REPAYMENT" || it.type == "MARKETPLACE_SALE" }
            .sumOf { it.amount }

        val disbursed = filteredTxs
            .filter { it.type == "LOAN_DISBURSED" }
            .sumOf { it.amount }

        val purchases = filteredTxs
            .filter { it.type == "DIRECT_PURCHASE" }
            .sumOf { it.amount }

        ReportsUiState(
            selectedShopFilter = shopFilter,
            totalRevenue = revenue,
            totalLoansDisbursed = disbursed,
            totalDirectPurchases = purchases,
            totalTransactionsCount = filteredTxs.size,
            transactions = filteredTxs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState(isLoading = true)
    )
}
