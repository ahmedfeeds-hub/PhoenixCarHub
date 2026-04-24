package com.phoenix.carhub.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/** Placeholder service for background weather scheduling via WorkManager in future phases. */
class WeatherService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf()
        return START_NOT_STICKY
    }
}
