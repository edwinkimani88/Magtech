package com.example.data.api

import android.util.Log
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

object OpenRouterAiService {

    private const val TAG = "OpenRouterAiService"
    private const val API_URL = "https://openrouter.ai/api/v1/chat/completions"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.OPENROUTER_API_KEY
            if (key.isNullOrEmpty() || key.contains("OPENROUTER_API_KEY")) {
                "" // API key must be set in .env file
            } else key
        } catch (e: Exception) {
            "" // API key must be set in .env file
        }
    }

    /**
     * Generate personalized SMS message in natural Nairobi Sheng + English
     */
    suspend fun generateSmsMessage(
        customerName: String,
        balancePayable: Double,
        dueDate: String,
        status: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val systemPrompt = """
            You are MagTech Investments AI SMS Assistant in Kitengela, Kenya (Chairman Rd & Deliverance Rd branches).
            Your job is to compose a short, respectful, clear SMS reminder to a customer about their loan balance.
            
            RULES:
            1. Use natural, authentic Nairobi/Kitengela Sheng + English (e.g. "Sasa Boss", "Bazu", "Tajiri", "Kiongozi", "Uko sorted", "Iko sawa", "Check hii").
            2. NEVER use Tanzanian Swahili or formal coastal Kiswahili.
            3. Must be polite, professional, direct, warm, and respectful.
            4. State the EXACT customer name, balance payable in KSh, and due date.
            5. Keep it under 200 characters so it fits in a standard SMS.
            6. Do not include quotes or meta information.
        """.trimIndent()

        val userPrompt = "Customer: $customerName, Balance Payable: KSh ${String.format("%,.0f", balancePayable)}, Due Date: $dueDate, Status: $status."

        try {
            val jsonBody = JSONObject().apply {
                put("model", "openai/gpt-4o-mini")
                put("temperature", 0.7)
                put("max_tokens", 150)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                })
            }

            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("HTTP-Referer", "https://magtech.co.ke")
                .addHeader("X-Title", "MagTech Android App")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = JSONObject(responseBody)
                val choices = jsonRes.getJSONArray("choices")
                if (choices.length() > 0) {
                    val content = choices.getJSONObject(0).getJSONObject("message").getString("content")
                    return@withContext content.trim().trim('"')
                }
            }
            Log.e(TAG, "OpenRouter API error code ${response.code}: $responseBody")
            fallbackSms(customerName, balancePayable, dueDate)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call OpenRouter AI: ${e.message}", e)
            fallbackSms(customerName, balancePayable, dueDate)
        }
    }

    /**
     * AI Assistant Query Handler for business insights
     */
    suspend fun queryAssistant(
        userQuery: String,
        businessContext: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val systemPrompt = """
            You are Bazu AI, the intelligent shop management assistant for MagTech Investments in Nairobi, Kenya.
            You answer the shop admin's questions based on real business data provided in context.
            
            TONE & LANGUAGE:
            - Professional, smart, quick, helpful.
            - Natural Kenyan English mixed with subtle Nairobi Sheng ("Sasa Boss", "Bazu", "Iko ready", "Check balance", "Uko sorted").
            - Be concise and provide direct numbers/stats from the business context.
            
            BUSINESS CONTEXT:
            $businessContext
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("model", "openai/gpt-4o-mini")
                put("temperature", 0.5)
                put("max_tokens", 350)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userQuery)
                    })
                })
            }

            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("HTTP-Referer", "https://magtech.co.ke")
                .addHeader("X-Title", "MagTech Assistant")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = JSONObject(responseBody)
                val choices = jsonRes.getJSONArray("choices")
                if (choices.length() > 0) {
                    return@withContext choices.getJSONObject(0).getJSONObject("message").getString("content").trim()
                }
            }
            "Sasa Boss! System iko active. Let me double check that query for you right away."
        } catch (e: Exception) {
            Log.e(TAG, "Assistant error: ${e.message}", e)
            "Sasa Boss! Network connection iko down kidogo, but everything in MagTech database is intact and secure."
        }
    }

    private fun fallbackSms(name: String, balance: Double, dueDate: String): String {
        return "Sasa $name, friendly reminder from MagTech Investments. Your balance is KSh ${String.format("%,.0f", balance)} due on $dueDate. Pay via M-PESA or visit our shop. Asante!"
    }
}
