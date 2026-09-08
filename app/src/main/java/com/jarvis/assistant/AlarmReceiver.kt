package com.jarvis.assistant

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object AlarmReceiver {

    private const val CHANNEL =
        "jarvis_reminders"

    fun schedule(
        context: Context,
        time: Long,
        message: String
    ) {

        val intent =
            Intent(
                context,
                Receiver::class.java
            )

        intent.putExtra(
            "message",
            message
        )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                time.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                AlarmManager::class.java
            )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    class Receiver :
        BroadcastReceiver() {

        override fun onReceive(
            context: Context,
            intent: Intent
        ) {

            val manager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            if (
                Build.VERSION.SDK_INT >= 26
            ) {

                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL,
                        "JARVIS Reminders",
                        NotificationManager
                            .IMPORTANCE_HIGH
                    )
                )
            }

            val message =
                intent.getStringExtra(
                    "message"
                )
                    ?: "JARVIS reminder"

            val notification =
                NotificationCompat
                    .Builder(
                        context,
                        CHANNEL
                    )
                    .setSmallIcon(
                        android.R.drawable
                            .ic_lock_idle_alarm
                    )
                    .setContentTitle(
                        "JARVIS"
                    )
                    .setContentText(
                        message
                    )
                    .setAutoCancel(true)
                    .build()

            manager.notify(
                System.currentTimeMillis()
                    .toInt(),
                notification
            )
        }
    }
}
