package com.example.ypay.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.ypay.R
import com.example.ypay.presentation.base.MainActivity

class NotificationService : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.e("TAG", "onCreate: called")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("TAG", "onStartCommand: Called ")
        startForegroundService()
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startForegroundService() {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(11,notification)
    }

    fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(): NotificationChannel {
        val channel = NotificationChannel(
            "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        return channel

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(): Notification {
        val notification = Notification.Builder(this, "channel_id")
            .setContentText("Welcome to YPay")
            .setContentTitle("YPay")
            .setContentIntent(getPendingIntent())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
        return notification
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy: called")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}