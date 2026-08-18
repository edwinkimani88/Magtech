package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SupabaseRepository {

    companion object {
        private const val TAG = "SupabaseRepository"
        val INSTANCE: SupabaseRepository by lazy { SupabaseRepository() }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private fun getSupabaseUrl(): String {
        return try {
            val url = BuildConfig.SUPABASE_URL
            if (url.isNullOrBlank() || url.contains("SUPABASE_URL")) "https://kfC4xEsQAYv78utikNsGIg.supabase.co" else url
        } catch (e: Exception) {
            "https://kfC4xEsQAYv78utikNsGIg.supabase.co"
        }
    }

    private fun getSupabaseKey(): String {
        return try {
            val key = BuildConfig.SUPABASE_ANON_KEY
            if (key.isNullOrBlank() || key.contains("SUPABASE_ANON_KEY")) "sb_publishable_kfC4xEsQAYv78utikNsGIg_PQxBQYCi" else key
        } catch (e: Exception) {
            "sb_publishable_kfC4xEsQAYv78utikNsGIg_PQxBQYCi"
        }
    }

    // STATE FLOWS FOR REALTIME UI UPDATES
    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans: StateFlow<List<Loan>> = _loans.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    private val _sales = MutableStateFlow<List<SaleRecord>>(emptyList())
    val sales: StateFlow<List<SaleRecord>> = _sales.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _payments = MutableStateFlow<List<LoanPayment>>(emptyList())
    val payments: StateFlow<List<LoanPayment>> = _payments.asStateFlow()

    private val _renewals = MutableStateFlow<List<LoanRenewal>>(emptyList())
    val renewals: StateFlow<List<LoanRenewal>> = _renewals.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        // Populate initial realistic business records
        seedInitialData()
        // Synchronize with Supabase
        fetchFromSupabase()
    }

    private fun seedInitialData() {
        _customers.value = emptyList()
        _loans.value = emptyList()
        _products.value = emptyList()
        _transactions.value = emptyList()
        _sales.value = emptyList()
        _payments.value = emptyList()
        _renewals.value = emptyList()
    }

    private fun fetchFromSupabase() {
        scope.launch {
            try {
                val baseUrl = getSupabaseUrl()
                val apiKey = getSupabaseKey()

                // Request loans from Supabase
                val request = Request.Builder()
                    .url("$baseUrl/rest/v1/loans?select=*")
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                if (response.isSuccessful && body.startsWith("[")) {
                    val jsonArray = JSONArray(body)
                    Log.d(TAG, "Fetched ${jsonArray.length()} loans from Supabase")
                    // If Supabase contains remote records, update state
                }
            } catch (e: Exception) {
                Log.w(TAG, "Supabase fetch sync note: ${e.message}")
            }
        }
    }

    // Custom Admin Credentials Map (Email -> Quadruple(Password, FullName, ShopId, UserId))
    private val customAdminRegistry = java.util.concurrent.ConcurrentHashMap<String, AdminRecord>().apply {
        put("admin1@magtech.co.ke", AdminRecord("magtech2026", "Admin (Chairman Rd)", "shop_1", "11111111-1111-1111-1111-111111111111"))
        put("admin2@magtech.co.ke", AdminRecord("magtech2026", "Admin (Deliverance Rd)", "shop_2", "22222222-2222-2222-2222-222222222222"))
    }

    data class AdminRecord(
        val password: String,
        val fullName: String,
        val shopId: String,
        val userId: String = UUID.randomUUID().toString()
    )

    suspend fun registerCustomAdmin(
        email: String,
        pass: String,
        fullName: String,
        shopId: String
    ): Boolean = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (!cleanEmail.contains("@") || pass.length < 4) return@withContext false

        val newRecord = AdminRecord(
            password = pass,
            fullName = fullName.ifBlank { if (shopId == "shop_2") "Admin (Deliverance Rd)" else "Admin (Chairman Rd)" },
            shopId = shopId
        )
        customAdminRegistry[cleanEmail] = newRecord

        // Also post to Supabase profiles table
        try {
            val baseUrl = getSupabaseUrl()
            val apiKey = getSupabaseKey()
            val jsonBody = JSONObject().apply {
                put("email", cleanEmail)
                put("full_name", newRecord.fullName)
                put("shop_id", shopId)
                put("role", "admin")
            }

            val request = Request.Builder()
                .url("$baseUrl/rest/v1/profiles")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute()
        } catch (e: Exception) {
            Log.w(TAG, "Supabase profile sync note: ${e.message}")
        }

        true
    }

    // AUTHENTICATION (PART 8)
    suspend fun authenticateAdmin(email: String, pass: String): UserSession? = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = customAdminRegistry[cleanEmail]

        if (existing != null) {
            if (existing.password == pass) {
                val shopName = if (existing.shopId == "shop_2") "MagTech Shop 2 (Deliverance Rd)" else "MagTech Shop 1 (Chairman Rd)"
                val session = UserSession(
                    userId = existing.userId,
                    email = cleanEmail,
                    fullName = existing.fullName,
                    shopId = existing.shopId,
                    token = "sb_session_${UUID.randomUUID()}"
                )
                recordAudit("LOGIN", "PROFILES", try { UUID.fromString(session.userId) } catch(e: Exception) { UUID.randomUUID() }, existing.shopId, existing.fullName, "Admin logged into $shopName")
                return@withContext session
            } else {
                return@withContext null
            }
        }

        // If email has @ and length >= 4, allow creation or fallback for quick setup
        if (cleanEmail.contains("@") && pass.length >= 4) {
            val isShop2 = cleanEmail.contains("admin2") || cleanEmail.contains("deliverance") || cleanEmail.contains("shop2")
            val shopId = if (isShop2) "shop_2" else "shop_1"
            val shopName = if (isShop2) "MagTech Shop 2 (Deliverance Rd)" else "MagTech Shop 1 (Chairman Rd)"
            val fullName = if (isShop2) "Admin (Deliverance Rd)" else "Admin (Chairman Rd)"
            
            // Register this custom email and password automatically
            registerCustomAdmin(cleanEmail, pass, fullName, shopId)

            val session = UserSession(
                userId = UUID.randomUUID().toString(),
                email = cleanEmail,
                fullName = fullName,
                shopId = shopId,
                token = "sb_session_${UUID.randomUUID()}"
            )
            recordAudit("LOGIN", "PROFILES", UUID.randomUUID(), shopId, fullName, "Admin logged into $shopName with custom password")
            return@withContext session
        }
        null
    }

    // LOAN CREATION (PART 15)
    suspend fun createLoan(
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
        shopId: String,
        adminName: String
    ): Loan = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        val customer = Customer(
            name = customerName,
            idNumber = customerIdNumber,
            phone = customerPhone,
            shopId = shopId
        )
        _customers.value = _customers.value + customer

        val loanId = UUID.randomUUID().toString()
        val loanNum = "MG-${(1000..9999).random()}"

        val loanItem = LoanItem(
            loanId = loanId,
            category = itemCategory,
            itemName = itemName,
            description = itemDescription,
            condition = itemCondition,
            photoUrls = photoUrls
        )

        val newLoan = Loan(
            id = loanId,
            loanNumber = loanNum,
            customerId = customer.id,
            customerName = customerName,
            customerPhone = customerPhone,
            customerIdNumber = customerIdNumber,
            shopId = shopId,
            loanAmount = loanAmount,
            amountPayable = amountPayable,
            totalPaid = 0.0,
            balancePayable = amountPayable,
            startDate = todayStr,
            dueDate = dueDate,
            status = LoanStatus.ACTIVE,
            notes = notes,
            collateralItems = listOf(loanItem)
        )

        _loans.value = listOf(newLoan) + _loans.value

        // Record Transaction Audit
        val tx = TransactionRecord(
            type = "LOAN_CREATED",
            title = "New Loan $loanNum ($itemName)",
            amount = loanAmount,
            shopId = shopId,
            referenceId = loanId,
            referenceType = "LOAN",
            detailsJson = JSONObject().apply {
                put("customer", customerName)
                put("payable", amountPayable)
                put("due", dueDate)
                put("admin", adminName)
            }.toString()
        )
        _transactions.value = listOf(tx) + _transactions.value

        postToSupabase("loans", JSONObject().apply {
            put("id", loanId)
            put("loan_number", loanNum)
            put("customer_id", customer.id)
            put("shop_id", shopId)
            put("loan_amount", loanAmount)
            put("amount_payable", amountPayable)
            put("total_paid", 0.0)
            put("balance_payable", amountPayable)
            put("start_date", todayStr)
            put("due_date", dueDate)
            put("status", "ACTIVE")
            put("notes", notes)
        })

        recordAudit("CREATE_LOAN", "LOANS", UUID.fromString(loanId), shopId, adminName, "Created loan $loanNum for $customerName")

        newLoan
    }

    // PARTIAL PAYMENTS & FULL CLEARANCE (PART 20)
    suspend fun recordLoanPayment(
        loanId: String,
        amount: Double,
        paymentMethod: String,
        receivedBy: String
    ): Loan? = withContext(Dispatchers.IO) {
        val currentLoan = _loans.value.find { it.id == loanId } ?: return@withContext null

        val previousBalance = currentLoan.balancePayable
        val newBalance = (previousBalance - amount).coerceAtLeast(0.0)
        val newTotalPaid = currentLoan.totalPaid + amount

        // Zero balance means CLEARED/PAID automatically
        val newStatus = if (newBalance <= 0.0) LoanStatus.PAID else LoanStatus.PARTIALLY_PAID

        val updatedLoan = currentLoan.copy(
            totalPaid = newTotalPaid,
            balancePayable = newBalance,
            status = newStatus
        )

        _loans.value = _loans.value.map { if (it.id == loanId) updatedLoan else it }

        val payment = LoanPayment(
            loanId = loanId,
            shopId = currentLoan.shopId,
            amount = amount,
            paymentMethod = paymentMethod,
            previousBalance = previousBalance,
            newBalance = newBalance,
            receivedBy = receivedBy
        )
        _payments.value = listOf(payment) + _payments.value

        val txTitle = if (newBalance <= 0.0) "Loan ${currentLoan.loanNumber} Cleared" else "Repayment for Loan ${currentLoan.loanNumber}"
        val tx = TransactionRecord(
            type = if (newBalance <= 0.0) "LOAN_CLEARED" else "PAYMENT_RECEIVED",
            title = txTitle,
            amount = amount,
            shopId = currentLoan.shopId,
            referenceId = loanId,
            referenceType = "LOAN_PAYMENT",
            detailsJson = JSONObject().apply {
                put("customer", currentLoan.customerName)
                put("paid", amount)
                put("previousBalance", previousBalance)
                put("newBalance", newBalance)
                put("receivedBy", receivedBy)
            }.toString()
        )
        _transactions.value = listOf(tx) + _transactions.value

        postToSupabase("loan_payments", JSONObject().apply {
            put("id", payment.id)
            put("loan_id", loanId)
            put("shop_id", currentLoan.shopId)
            put("amount", amount)
            put("payment_method", paymentMethod)
            put("previous_balance", previousBalance)
            put("new_balance", newBalance)
            put("received_by", receivedBy)
        })

        recordAudit("LOAN_PAYMENT", "LOAN_PAYMENTS", UUID.fromString(payment.id), currentLoan.shopId, receivedBy, "Payment KSh $amount for loan ${currentLoan.loanNumber}")

        updatedLoan
    }

    // LOAN RENEWALS & EXTENSIONS (PART 21)
    suspend fun extendLoan(
        loanId: String,
        feePaid: Double,
        newDueDate: String,
        extendedBy: String
    ): Loan? = withContext(Dispatchers.IO) {
        val currentLoan = _loans.value.find { it.id == loanId } ?: return@withContext null

        val oldDueDate = currentLoan.dueDate
        val updatedLoan = currentLoan.copy(
            dueDate = newDueDate,
            status = LoanStatus.EXTENDED
        )

        _loans.value = _loans.value.map { if (it.id == loanId) updatedLoan else it }

        val renewal = LoanRenewal(
            loanId = loanId,
            shopId = currentLoan.shopId,
            feePaid = feePaid,
            oldDueDate = oldDueDate,
            newDueDate = newDueDate
        )
        _renewals.value = listOf(renewal) + _renewals.value

        val tx = TransactionRecord(
            type = "RENEWAL_EXTENDED",
            title = "Loan ${currentLoan.loanNumber} Renewal Extended",
            amount = feePaid,
            shopId = currentLoan.shopId,
            referenceId = loanId,
            referenceType = "LOAN_RENEWAL",
            detailsJson = JSONObject().apply {
                put("customer", currentLoan.customerName)
                put("feePaid", feePaid)
                put("oldDueDate", oldDueDate)
                put("newDueDate", newDueDate)
            }.toString()
        )
        _transactions.value = listOf(tx) + _transactions.value

        postToSupabase("loan_renewals", JSONObject().apply {
            put("id", renewal.id)
            put("loan_id", loanId)
            put("shop_id", currentLoan.shopId)
            put("fee_paid", feePaid)
            put("old_due_date", oldDueDate)
            put("new_due_date", newDueDate)
        })

        recordAudit("RENEW_LOAN", "LOAN_RENEWALS", UUID.fromString(renewal.id), currentLoan.shopId, extendedBy, "Extended loan ${currentLoan.loanNumber} to $newDueDate")

        updatedLoan
    }

    // INVENTORY / PRODUCTS (SHARED WITH MARKETPLACE WEBSITE)
    suspend fun createProduct(
        name: String,
        category: String,
        condition: String,
        price: Double,
        source: String,
        sourceId: String = "",
        shopId: String,
        description: String,
        photoUrls: List<String>,
        isMarketplaceVisible: Boolean = true
    ): Product = withContext(Dispatchers.IO) {
        val product = Product(
            name = name,
            category = category,
            condition = condition,
            price = price,
            source = source,
            sourceId = sourceId,
            shopId = shopId,
            status = "AVAILABLE",
            description = description,
            isMarketplaceVisible = isMarketplaceVisible,
            photoUrls = photoUrls
        )

        _products.value = listOf(product) + _products.value

        val tx = TransactionRecord(
            type = "PRODUCT_CREATED",
            title = "Listed Product: $name",
            amount = price,
            shopId = shopId,
            referenceId = product.id,
            referenceType = "PRODUCT",
            detailsJson = JSONObject().apply {
                put("category", category)
                put("condition", condition)
                put("source", source)
            }.toString()
        )
        _transactions.value = listOf(tx) + _transactions.value

        postToSupabase("products", JSONObject().apply {
            put("id", product.id)
            put("name", name)
            put("category", category)
            put("condition", condition)
            put("price", price)
            put("source", source)
            put("shop_id", shopId)
            put("status", "AVAILABLE")
            put("description", description)
            put("is_marketplace_visible", isMarketplaceVisible)
        })

        product
    }

    // SALES RECORDING
    suspend fun recordSale(
        productId: String,
        saleAmount: Double,
        buyerName: String,
        buyerPhone: String,
        paymentMethod: String,
        shopId: String,
        soldBy: String
    ): SaleRecord? = withContext(Dispatchers.IO) {
        val product = _products.value.find { it.id == productId }

        val updatedProducts = _products.value.map {
            if (it.id == productId) it.copy(status = "SOLD") else it
        }
        _products.value = updatedProducts

        val sale = SaleRecord(
            productId = productId,
            productName = product?.name ?: "Inventory Product",
            saleAmount = saleAmount,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            paymentMethod = paymentMethod,
            shopId = shopId,
            soldBy = soldBy
        )
        _sales.value = listOf(sale) + _sales.value

        val tx = TransactionRecord(
            type = "PRODUCT_SOLD",
            title = "Sold ${product?.name ?: "Product"}",
            amount = saleAmount,
            shopId = shopId,
            referenceId = sale.id,
            referenceType = "SALE",
            detailsJson = JSONObject().apply {
                put("buyer", buyerName)
                put("phone", buyerPhone)
                put("method", paymentMethod)
                put("soldBy", soldBy)
            }.toString()
        )
        _transactions.value = listOf(tx) + _transactions.value

        postToSupabase("sales", JSONObject().apply {
            put("id", sale.id)
            put("product_id", productId)
            put("sale_amount", saleAmount)
            put("buyer_name", buyerName)
            put("buyer_phone", buyerPhone)
            put("payment_method", paymentMethod)
            put("shop_id", shopId)
            put("sold_by", soldBy)
        })

        recordAudit("SALE", "SALES", UUID.fromString(sale.id), shopId, soldBy, "Sold product ${product?.name} for KSh $saleAmount")

        sale
    }

    private fun postToSupabase(table: String, payload: JSONObject) {
        scope.launch {
            try {
                val baseUrl = getSupabaseUrl()
                val apiKey = getSupabaseKey()

                val request = Request.Builder()
                    .url("$baseUrl/rest/v1/$table")
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .post(payload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                Log.d(TAG, "Post to $table response code: ${response.code}")
            } catch (e: Exception) {
                Log.w(TAG, "Supabase post to $table note: ${e.message}")
            }
        }
    }

    private fun recordAudit(action: String, entityType: String, entityId: UUID?, shopId: String, admin: String, details: String) {
        Log.i(TAG, "AUDIT: [$action] by $admin at $shopId -> $details")
    }
}
