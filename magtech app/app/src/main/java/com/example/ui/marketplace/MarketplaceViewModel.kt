package com.example.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.entities.ItemEntity
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MarketplaceUiState(
    val items: List<ItemEntity> = emptyList(),
    val selectedCategory: String = "All",
    val selectedShopFilter: String = "All", // "All", "Shop 1", "Shop 2"
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class MarketplaceViewModel(private val repository: MagTechRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _selectedShopFilter = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<MarketplaceUiState> = combine(
        repository.allItems,
        _selectedCategory,
        _selectedShopFilter,
        _searchQuery
    ) { allItems, category, shop, query ->
        val filtered = allItems.filter { item ->
            val matchesCategory = (category == "All" || item.category.equals(category, ignoreCase = true))
            val matchesShop = (shop == "All" || item.shopLocation == shop)
            val matchesQuery = query.isBlank() ||
                    item.itemName.contains(query, ignoreCase = true) ||
                    item.brand.contains(query, ignoreCase = true)
            matchesCategory && matchesShop && matchesQuery
        }
        MarketplaceUiState(
            items = filtered,
            selectedCategory = category,
            selectedShopFilter = shop,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MarketplaceUiState(isLoading = true)
    )

    fun setCategory(category: String) { _selectedCategory.value = category }
    fun setShopFilter(shop: String) { _selectedShopFilter.value = shop }
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun toggleMarketplacePublish(itemId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleMarketplacePublish(itemId, !currentStatus)
        }
    }

    fun updatePrice(itemId: Long, newPrice: Double) {
        viewModelScope.launch {
            repository.toggleMarketplacePublish(itemId, isPublished = true, price = newPrice)
        }
    }
}
