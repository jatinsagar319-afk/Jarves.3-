package com.jarvis.assistant

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS = 100
    }

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createInterface()
    }

    private fun createInterface() {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.setPadding(40, 80, 40, 40)

        root.setBackgroundColor(
            android.graphics.Color.rgb(3, 5, 9)
        )

        val title = TextView(this)

        title.text = "J A R V I S"
        title.textSize = 42f
        title.gravity = android.view.Gravity.CENTER
        title.setTextColor(
            android.graphics.Color.rgb(0, 190, 255)
        )

        root.addView(title)

        val subtitle = TextView(this)

        subtitle.text = "PERSONAL AI ASSISTANT"
        subtitle.textSize = 18f
        subtitle.gravity = android.view.Gravity.CENTER
        subtitle.setTextColor(
            android.graphics.Color.LTGRAY
        )

        root.addView(subtitle)

        statusText = TextView(this)

        statusText.text = "JARVIS OFFLINE\n\nAssistant is sleeping"
        statusText.textSize = 22f
        statusText.gravity = android.view.Gravity.CENTER
        statusText.setPadding(0, 80, 0, 80)
        statusText.setTextColor(
            android.graphics.Color.WHITE
        )

        root.addView(statusText)

        val activateButton = Button(this)

        activateButton.text = "ACTIVATE JARVIS"
        activateButton.textSize = 18f

        activateButton.setOnClickListener {
            activateJarvis()
        }

        root.addView(activateButton)

        val reminderButton = Button(this)

        reminderButton.text = "TEST REMINDER"
        reminderButton.textSize = 18f

        reminderButton.setOnClickListener {
            testReminder()
        }

        root.addView(reminderButton)

        val stopButton = Button(this)

        stopButton.text = "STOP JARVIS"
        stopButton.textSize = 18f

        stopButton.setOnClickListener {
            stopJarvis()
        }

        root.addView(stopButton)

        setContentView(root)
    }

    private fun activateJarvis() {

        requestPermissionsIfNeeded()

        try {

            val intent = Intent(
                this,
                JarvisService::class.java
            )

            ContextCompat.startForegroundService(
                this,
                intent
            )

            statusText.text =
                "JARVIS ONLINE\n\nListening..."

        } catch (e: Exception) {

            statusText.text =
                "JARVIS ERROR\n\n${e.message}"
        }
    }

    private fun stopJarvis() {

        val intent = Intent(
            this,
            JarvisService::class.java
        )

        stopService(intent)

        statusText.text =
            "JARVIS OFFLINE\n\nAssistant is sleeping"
    }

    private fun requestPermissionsIfNeeded() {

        val permissions = mutableListOf<String>()

        if (
            android.os.Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(
                Manifest.permission.RECORD_AUDIO
            )
        }

        if (
            android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        if (permissions.isNotEmpty()) {

            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_PERMISSIONS
            )
        }
    }

    private fun testReminder() {

        try {

            val alarmManager =
                getSystemService(Context.ALARM_SERVICE)
                    as AlarmManager

            val intent = Intent(
                this,
                AlarmReceiver::class.java
            )

            val pendingIntent =
                PendingIntent.getBroadcast(
                    this,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            val triggerTime =
                System.currentTimeMillis() + 60_000L

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )

            statusText.text =
                "REMINDER SET\n\nTest reminder in 1 minute"

        } catch (e: Exception) {

            statusText.text =
                "REMINDER ERROR\n\n${e.message}"
        }
    }
}
