package com.example.gpssmsr

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class LocationNoty : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "location",
            "LocationSr",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}