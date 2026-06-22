package com.pinflow.compose.autofill

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Listens for incoming SMS via the SMS Retriever API — no [android.permission.READ_SMS] required.
 *
 * Fails silently when Play Services or the retriever is unavailable.
 */
internal class SmsOtpRetriever(
    private val context: Context,
    private val onMessage: (String) -> Unit,
) {
    private var receiver: BroadcastReceiver? = null

    fun start() {
        runCatching {
            SmsRetriever.getClient(context)
                .startSmsRetriever()
                .addOnSuccessListener { registerReceiver() }
                .addOnFailureListener { /* keep manual input working */ }
        }
    }

    fun stop() {
        receiver?.let { registered ->
            runCatching { context.unregisterReceiver(registered) }
            receiver = null
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        if (receiver != null) return

        val smsReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
                if (status.statusCode != CommonStatusCodes.SUCCESS) return

                val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
                if (message.isNotBlank()) {
                    onMessage(message)
                }
            }
        }

        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(smsReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(smsReceiver, filter)
        }
        receiver = smsReceiver
    }
}
