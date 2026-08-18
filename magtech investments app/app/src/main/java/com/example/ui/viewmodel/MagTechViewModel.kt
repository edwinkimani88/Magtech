package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.OpenRouterAiService
import com.example.data.local.DataStoreManager
import com.example.data.models.*
import com.example.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object CreateLoan : Screen("create_loan")
    object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: String) = "loan_detail/$loanId"
    }
    object Inventory : Screen("inventory")
    object Marketplace : Screen("marketplace")
    object Transactions : Screen("transactions")
    object SmsAi : Screen("sms_ai")
    object AiAssistant : Screen("ai_assistant")
}

class MagTechViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SupabaseRepository.INSTANCE
    private val dataStoreManager = DataStoreManager(application)

    val userSession: StateFlow<UserSession?> = dataStoreManager.userSessionFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val shopFilter: StateFlow<String> = dataStoreManager.selectedShopFilterFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "all"
    )

    val loans: StateFlow<List<Loan>> = repository.loans
    val products: StateFlow<List<Product>> = repository.products
    val transactions: StateFlow<List<TransactionRecord>> = repository.transactions
    val customers: StateFlow<List<Customer>> = repository.customers
    val sales: StateFlow<List<SaleRecord>> = repository.sales

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _aiChatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Sasa Boss! Welcome to Bazu AI for MagTech Investments. Ask me anything about loans, overdue payments, or inventory across Shop 1 & Shop 2!" to false
        )
    )
    val aiChatMessages: StateFlow<List<Pair<String, Boolean>>> = _aiChatMessages.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun setShopFilter(shopId: String) {
        viewModelScope.launch {
            dataStoreManager.saveShopFilter(shopId)
        }
    }

    fun registerAdminAccount(
        email: String,
        pass: String,
        fullName: String,
        shopId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val registered = repository.registerCustomAdmin(email, pass, fullName, shopId)
            _isLoading.value = false
            if (registered) {
                _uiMessage.value = "Admin account registered successfully! You can now log in with your unique password."
                onSuccess()
            } else {
                _uiMessage.value = "Failed to register account. Check email format and password length (min 4 chars)."
            }
        }
    }

    fun loginAdmin(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val session = repository.authenticateAdmin(email, pass)
            _isLoading.value = false
            if (session != null) {
                dataStoreManager.saveUserSession(session)
                onSuccess()
            } else {
                _uiMessage.value = "Login failed. Check admin email and password."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearSession()
        }
    }

    fun createLoan(
        customerName: String,
        customerIdNumber: String,
        customerPhone: String,
        loanAmount: Double,
        amountPayable: Double,
        dueDate: String,
        notes: String,
        itemCategory: String,
        itemName: String,
        itemDescription: String,
        itemCondition: String,
        photoUrls: List<String>,
        onSuccess: () -> Unit
    ) {
        val session = userSession.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createLoan(
                    customerName = customerName,
                    customerIdNumber = customerIdNumber,
                    customerPhone = customerPhone,
                    loanAmount = loanAmount,
                    amountPayable = amountPayable,
                    dueDate = dueDate,
                    notes = notes,
                    itemCategory = itemCategory,
                    itemName = itemName,
                    itemDescription = itemDescription,
                    itemCondition = itemCondition,
                    photoUrls = photoUrls,
                    shopId = session.shopId,
                    adminName = session.fullName
                )
                _uiMessage.value = "Loan created successfully for $customerName!"
                onSuccess()
            } catch (e: Exception) {
                _uiMessage.value = "Error creating loan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recordPayment(loanId: String, amount: Double, method: String, onSuccess: () -> Unit) {
        val session = userSession.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loan = repository.recordLoanPayment(loanId, amount, method, session.fullName)
                if (loan != null) {
                    _uiMessage.value = "Payment KSh ${String.format("%,.0f", amount)} recorded!"
                    onSuccess()
                } else {
                    _uiMessage.value = "Loan not found."
                }
            } catch (e: Exception) {
                _uiMessage.value = "Error recording payment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun extendLoan(loanId: String, feePaid: Double, newDueDate: String, onSuccess: () -> Unit) {
        val session = userSession.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loan = repository.extendLoan(loanId, feePaid, newDueDate, session.fullName)
                if (loan != null) {
                    _uiMessage.value = "Loan extended to $newDueDate successfully!"
                    onSuccess()
                } else {
                    _uiMessage.value = "Loan not found."
                }
            } catch (e: Exception) {
                _uiMessage.value = "Error extending loan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createProduct(
        name: String,
        category: String,
        condition: String,
        price: Double,
        source: String,
        description: String,
        photoUrls: List<String>,
        isMarketplaceVisible: Boolean,
        onSuccess: () -> Unit
    ) {
        val session = userSession.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createProduct(
                    name = name,
                    category = category,
                    condition = condition,
                    price = price,
                    source = source,
                    shopId = session.shopId,
                    description = description,
                    photoUrls = photoUrls,
                    isMarketplaceVisible = isMarketplaceVisible
                )
                _uiMessage.value = "Product '$name' added to Inventory and Marketplace!"
                onSuccess()
            } catch (e: Exception) {
                _uiMessage.value = "Error creating product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun recordSale(productId: String, saleAmount: Double, buyerName: String, buyerPhone: String, method: String, onSuccess: () -> Unit) {
        val session = userSession.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.recordSale(productId, saleAmount, buyerName, buyerPhone, method, session.shopId, session.fullName)
                _uiMessage.value = "Sale KSh ${String.format("%,.0f", saleAmount)} recorded!"
                onSuccess()
            } catch (e: Exception) {
                _uiMessage.value = "Error recording sale: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateSmsForLoan(loan: Loan, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val msg = OpenRouterAiService.generateSmsMessage(
                customerName = loan.customerName,
                balancePayable = loan.balancePayable,
                dueDate = loan.dueDate,
                status = loan.status.name
            )
            _isLoading.value = false
            onResult(msg)
        }
    }

    fun sendAiAssistantQuery(query: String) {
        if (query.isBlank()) return
        val current = _aiChatMessages.value
        _aiChatMessages.value = current + (query to true)

        viewModelScope.launch {
            val allLoans = loans.value
            val activeLoans = allLoans.filter { it.status == LoanStatus.ACTIVE || it.status == LoanStatus.PARTIALLY_PAID }
            val overdueLoans = allLoans.filter { it.status == LoanStatus.OVERDUE }
            val totalBalance = activeLoans.sumOf { it.balancePayable } + overdueLoans.sumOf { it.balancePayable }
            val availableProducts = products.value.filter { it.status == "AVAILABLE" }

            val businessContext = """
                Total Active Loans: ${activeLoans.size}
                Total Overdue Loans: ${overdueLoans.size}
                Total Outstanding Balance Payable across shops: KSh ${String.format("%,.0f", totalBalance)}
                Overdue Customers: ${overdueLoans.joinToString { "${it.customerName} (KSh ${String.format("%,.0f", it.balancePayable)})" }}
                Products Available in Inventory: ${availableProducts.joinToString { "${it.name} - KSh ${String.format("%,.0f", it.price)} (${it.shopId})" }}
            """.trimIndent()

            val reply = OpenRouterAiService.queryAssistant(query, businessContext)
            _aiChatMessages.value = _aiChatMessages.value + (reply to false)
        }
    }
}
