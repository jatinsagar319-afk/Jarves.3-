package com.jarvis.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jarvis.assistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            startJarvis()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            requestPermissionsForJarvis()
        }

        binding.btnStop.setOnClickListener {

            stopService(
                Intent(
                    this,
                    JarvisService::class.java
                )
            )

            binding.status.text =
                "JARVIS OFFLINE"

            binding.detail.text =
                "Assistant stopped"
        }

        binding.btnReminder.setOnClickListener {

            AlarmReceiver.schedule(
                this,
                System.currentTimeMillis() + 60_000,
                "JARVIS reminder"
            )

            Toast.makeText(
                this,
                "Reminder set for 1 minute",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestPermissionsForJarvis() {

        val permissions =
            mutableListOf(
                Manifest.permission.RECORD_AUDIO
            )

        if (
            android.os.Build.VERSION.SDK_INT >= 33
        ) {
            permissions.add(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        permissionLauncher.launch(
            permissions.toTypedArray()
        )
    }

    private fun startJarvis() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                this,
                "Microphone permission required",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        ContextCompat.startForegroundService(
            this,
            Intent(
                this,
                JarvisService::class.java
            )
        )

        binding.status.text =
            "JARVIS ONLINE"

        binding.detail.text =
            "Voice assistant is active"
    }
}
