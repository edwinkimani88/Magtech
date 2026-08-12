package com.aistudio.magtechinvestments.nbi26.data.sms

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast

class SmsManagerService(private val context: Context) {

    fun sendSmsDirectOrIntent(phoneNumber: String, messageText: String): Boolean {
        return try {
            val smsManager: SmsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, messageText, null, null)
            Toast.makeText(context, "SMS Sent to $phoneNumber", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            // Fallback to launch SMS app intent
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$phoneNumber")
                    putExtra("sms_body", messageText)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            } catch (ex: Exception) {
                Toast.makeText(context, "SMS Simulated (Permission or app missing)", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}
