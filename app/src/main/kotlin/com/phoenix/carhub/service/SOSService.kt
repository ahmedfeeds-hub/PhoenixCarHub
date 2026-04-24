package com.phoenix.carhub.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.IBinder
import android.telephony.SmsManager
import android.os.Build
import com.phoenix.carhub.data.model.SOSContact
import com.phoenix.carhub.util.LocationUtils
import java.net.URLEncoder

class SOSService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf()
        return START_NOT_STICKY
    }

    companion object {
        /**
         * Trigger SOS silently — sends SMS + opens WhatsApp for each contact.
         * Must be called from a coroutine on the main thread for intent handling.
         */
        fun triggerSOS(
            contacts: List<SOSContact>,
            location: Location,
            launchIntent: (Intent) -> Unit
        ) {
            val gpsLink = LocationUtils.buildGoogleMapsLink(location)
            val message = "IMP. Help Required $gpsLink"

            for (contact in contacts) {
                sendSms(contact.phoneNumber, message)
                if (contact.isWhatsApp) {
                    sendWhatsApp(contact.phoneNumber, message, launchIntent)
                }
            }
        }

        private fun sendSms(phoneNumber: String, message: String) {
            runCatching {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SmsManager.createForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }
        }

        private fun sendWhatsApp(
            phoneNumber: String,
            message: String,
            launchIntent: (Intent) -> Unit
        ) {
            runCatching {
                val encodedMsg = URLEncoder.encode(message, "UTF-8")
                val cleanNumber = phoneNumber.replace("+", "").replace(" ", "")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$cleanNumber?text=$encodedMsg")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                launchIntent(intent)
            }
        }
    }
}
