package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val geminiApiKey = BuildConfig.GEMINI_API_KEY
    private val openRouterApiKey = BuildConfig.OPENROUTER_API_KEY

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateSmsReminder(
        customerName: String,
        itemName: String,
        balancePayable: Double,
        dueDateFormatted: String,
        urgency: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            You are MagTech Investments AI assistant in Nairobi, Kenya.
            Write a short, professional, friendly, respectful SMS reminder in natural English mixed with authentic Nairobi Sheng.
            DO NOT use formal Kiswahili or Tanzanian Swahili. Use modern Nairobi urban expressions like "Sasa", "Niaje", "Boss", "Kiongozi", "Clear loan ushikilie item yako poa".
            DO NOT sound threatening or debt-collector-like. Sound warm, confident, and businesslike.
            
            Customer Name: $customerName
            Item Collateral: $itemName
            Balance Payable: KSh ${balancePayable.toInt()}
            Due Date: $dueDateFormatted
            Urgency: $urgency
            
            Keep it under 160 characters if possible. Include exact amount and item name clearly.
        """.trimIndent()

        try {
            val responseText = callOpenRouterApi(prompt)
            if (responseText.isNotBlank()) responseText else fallbackSmsMessage(customerName, itemName, balancePayable, dueDateFormatted, urgency)
        } catch (e: Exception) {
            fallbackSmsMessage(customerName, itemName, balancePayable, dueDateFormatted, urgency)
        }
    }

    suspend fun estimateMarketValue(
        itemName: String,
        category: String,
        brand: String,
        condition: String
    ): ValuationResult = withContext(Dispatchers.IO) {
        val defaultValuation = getDefaultValuation(itemName, category)

        val prompt = """
            You are a Nairobi electronics market valuation expert for MagTech Investments.
            Estimate the market value in Kenyan Shillings (KSh) and recommended forced sale / loan limit for second-hand electronics in Kenya.
            
            Item: $itemName
            Category: $category
            Brand: $brand
            Condition: $condition
            
            Respond strictly in this format:
            MARKET_VALUE: [number in KSh]
            FORCED_SALE_VALUE: [number in KSh]
            ANALYSIS: [1 short sentence in Nairobi English + Sheng explaining the price logic]
        """.trimIndent()

        try {
            val responseText = callOpenRouterApi(prompt)
            if (responseText.isNotBlank()) {
                parseValuationResponse(responseText, defaultValuation)
            } else {
                defaultValuation
            }
        } catch (e: Exception) {
            defaultValuation
        }
    }

    suspend fun askBusinessAssistant(
        userQuery: String,
        shopSummaryContext: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            You are MagTech Investments AI Biashara Assistant in Nairobi, Kenya.
            The user is asking: "$userQuery"
            Current MagTech Business Context:
            $shopSummaryContext
            
            Answer in smart, confident English naturally blended with Nairobi Sheng (e.g., "Sasa Boss", "Bazu", "Niaje Kiongozi", "Uko sorted", "Poa boss").
            DO NOT use Tanzanian or formal coastal Swahili. Keep financial metrics clear in English.
        """.trimIndent()

        try {
            val responseText = callOpenRouterApi(prompt)
            if (responseText.isNotBlank()) responseText else "Sasa Boss! System iko ready. Kuna loans na inventory stock tayari ku-manage."
        } catch (e: Exception) {
            "Sasa Boss! Hapa MagTech biashara inaenda poa. Unaweza kuangalia Dashboard au ku-send SMS reminders kwa wateja."
        }
    }

    private fun callOpenRouterApi(prompt: String): String {
        try {
            val jsonBody = JSONObject().apply {
                put("model", "gpt-4o-mini")
                val messagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }
                put("messages", messagesArray)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer $openRouterApiKey")
                .addHeader("HTTP-Referer", "https://magtech.co.ke")
                .addHeader("X-Title", "MagTech Investments")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val responseJson = JSONObject(responseString)
                val choices = responseJson.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.optJSONObject("message")
                    if (message != null) {
                        val content = message.optString("content", "").trim()
                        if (content.isNotBlank()) return content
                    }
                }
            }
        } catch (e: Exception) {
            // Fallthrough to Gemini API
        }

        return callGeminiApi(prompt)
    }

    private fun callGeminiApi(prompt: String): String {
        if (geminiApiKey.isBlank() || geminiApiKey == "MY_GEMINI_API_KEY") {
            return ""
        }

        try {
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$geminiApiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                return ""
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            if (parts.length() == 0) return ""

            return parts.getJSONObject(0).optString("text", "").trim()
        } catch (e: Exception) {
            return ""
        }
    }

    private fun fallbackSmsMessage(
        customerName: String,
        itemName: String,
        balancePayable: Double,
        dueDateFormatted: String,
        urgency: String
    ): String {
        return when (urgency) {
            "DUE_TODAY" -> "Sasa $customerName! MagTech reminder: Loan yako ya KSh ${balancePayable.toInt()} ya $itemName iko due LEO ($dueDateFormatted). Karibu usambaze Kiongozi."
            "OVERDUE" -> "Niaje $customerName! MagTech hapa. Loan yako ya $itemName (Balance: KSh ${balancePayable.toInt()}) ilidue $dueDateFormatted. Clear sai ushikilie item yako poa."
            else -> "Sasa $customerName! Friendly reminder kutoka MagTech: Loan yako ya $itemName (KSh ${balancePayable.toInt()}) iko due $dueDateFormatted. Asante Boss!"
        }
    }

    private fun getDefaultValuation(itemName: String, category: String): ValuationResult {
        val lower = itemName.lowercase()
        return when {
            lower.contains("s23") || lower.contains("iphone 13") || lower.contains("ps5") ->
                ValuationResult(75000.0, 50000.0, "Hii item iko na demand sana Nairobi, resale value iko juu.")
            lower.contains("hp") || lower.contains("dell") || lower.contains("laptop") ->
                ValuationResult(45000.0, 30000.0, "Laptops za biashara zinaondoka haraka pale marketplace.")
            else ->
                ValuationResult(35000.0, 22000.0, "Standard electronic valuation kulingana na market ya sasa.")
        }
    }

    private fun parseValuationResponse(text: String, fallback: ValuationResult): ValuationResult {
        var marketVal = fallback.marketValue
        var forcedVal = fallback.forcedSaleValue
        var analysis = fallback.analysis

        text.lines().forEach { line ->
            if (line.contains("MARKET_VALUE:", ignoreCase = true)) {
                line.substringAfter(":").replace("[^0-9.]".toRegex(), "").toDoubleOrNull()?.let { marketVal = it }
            }
            if (line.contains("FORCED_SALE_VALUE:", ignoreCase = true)) {
                line.substringAfter(":").replace("[^0-9.]".toRegex(), "").toDoubleOrNull()?.let { forcedVal = it }
            }
            if (line.contains("ANALYSIS:", ignoreCase = true)) {
                analysis = line.substringAfter(":").trim()
            }
        }
        return ValuationResult(marketVal, forcedVal, analysis)
    }
}

data class ValuationResult(
    val marketValue: Double,
    val forcedSaleValue: Double,
    val analysis: String
)
