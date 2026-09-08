package com.jarvis.assistant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale

class JarvisService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    companion object {
        private const val CHANNEL_ID = "jarvis_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS")
            .setContentText("JARVIS is listening")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        textToSpeech = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale("hi", "IN")
            }
        }

        startListening()
    }

    private fun startListening() {

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            speak("Mere phone par voice recognition available nahi hai.")
            return
        }

        speechRecognizer?.destroy()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onError(error: Int) {
                restartListening()
            }

            override fun onResults(results: Bundle?) {

                val matches =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                val command = matches?.firstOrNull()?.trim()

                if (!command.isNullOrEmpty()) {
                    handleCommand(command)
                }

                restartListening()
            }
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "hi-IN"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            "hi-IN"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            false
        )

        speechRecognizer?.startListening(intent)
    }

    private fun restartListening() {

        android.os.Handler(mainLooper).postDelayed({
            try {
                startListening()
            } catch (_: Exception) {
            }
        }, 700)
    }

    private fun handleCommand(command: String) {

        val text = command.lowercase(Locale.getDefault()).trim()

        // ==============================
        // WHATSAPP
        // ==============================

        if (
            text.contains("whatsapp") &&
            (
                text.contains("open") ||
                text.contains("kholo") ||
                text.contains("chalao") ||
                text.contains("चलाओ") ||
                text.contains("खोलो")
            )
        ) {
            openApp(
                "com.whatsapp",
                "WhatsApp"
            )
            return
        }

        // ==============================
        // YOUTUBE
        // ==============================

        if (
            text.contains("youtube") &&
            (
                text.contains("open") ||
                text.contains("kholo") ||
                text.contains("chalao")
            )
        ) {
            openApp(
                "com.google.android.youtube",
                "YouTube"
            )
            return
        }

        // ==============================
        // CHROME
        // ==============================

        if (
            text.contains("chrome") &&
            (
                text.contains("open") ||
                text.contains("kholo") ||
                text.contains("chalao")
            )
        ) {
            openApp(
                "com.android.chrome",
                "Chrome"
            )
            return
        }

        // ==============================
        // INSTAGRAM
        // ==============================

        if (
            text.contains("instagram") &&
            (
                text.contains("open") ||
                text.contains("kholo") ||
                text.contains("chalao")
            )
        ) {
            openApp(
                "com.instagram.android",
                "Instagram"
            )
            return
        }

        // ==============================
        // PHONE / DIALER
        // ==============================

        if (
            text.contains("phone kholo") ||
            text.contains("dialer kholo") ||
            text.contains("call app kholo")
        ) {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                speak("Phone khol diya.")
            } catch (_: Exception) {
                speak("Phone application nahi mil rahi.")
            }

            return
        }

        // ==============================
        // SETTINGS
        // ==============================

        if (
            text.contains("settings kholo") ||
            text.contains("setting kholo") ||
            text.contains("settings open")
        ) {
            try {
                val intent = Intent(
                    android.provider.Settings.ACTION_SETTINGS
                )

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)

                speak("Settings khol diya.")
            } catch (_: Exception) {
                speak("Settings nahi khol paya.")
            }

            return
        }

        // ==============================
        // STOP JARVIS
        // ==============================

        if (
            text.contains("stop jarvis") ||
            text.contains("jarvis stop") ||
            text.contains("jarvis band") ||
            text.contains("band ho jao")
        ) {
            speak("Theek hai. Main standby mode mein ja raha hoon.")

            android.os.Handler(mainLooper).postDelayed({
                stopSelf()
            }, 1200)

            return
        }

        // ==============================
        // MEMORY
        // ==============================

        if (
            text.contains("memory") ||
            text.contains("yaad hai") ||
            text.contains("meri yaad")
        ) {
            val memories = MemoryStore.getMemories(this)

            if (memories.isEmpty()) {
                speak("Abhi meri memory mein kuch save nahi hai.")
            } else {
                val latest = memories.takeLast(3)

                val answer = latest.joinToString(
                    separator = ". "
                )

                speak("Mujhe ye yaad hai: $answer")
            }

            return
        }

        // ==============================
        // SAVE MEMORY
        // ==============================

        if (
            text.startsWith("save ") ||
            text.startsWith("note ") ||
            text.startsWith("yaad rakhna ") ||
            text.startsWith("idea ")
        ) {

            val memory = command
                .replaceFirst(
                    Regex(
                        "^(save|note|yaad rakhna|idea)\\s+",
                        RegexOption.IGNORE_CASE
                    ),
                    ""
                )
                .trim()

            if (memory.isNotEmpty()) {

                MemoryStore.saveMemory(
                    this,
                    memory
                )

                speak("Theek hai. Maine ise yaad rakh liya.")
            }

            return
        }

        // ==============================
        // OTHERWISE AI BACKEND
        // ==============================

        speak("Is command ke liye mera AI server abhi connected nahi hai.")
    }

    private fun openApp(
        packageName: String,
        appName: String
    ) {

        try {

            val packageManager = packageManager

            val launchIntent =
                packageManager.getLaunchIntentForPackage(packageName)

            if (launchIntent != null) {

                launchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )

                startActivity(launchIntent)

                speak("$appName khol diya.")

            } else {

                speak("$appName phone mein installed nahi hai.")
            }

        } catch (_: Exception) {

            speak("$appName kholne mein problem aa gayi.")
        }
    }

    private fun speak(text: String) {

        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "JARVIS_RESPONSE"
        )
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "JARVIS Assistant",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {

        speechRecognizer?.destroy()

        speechRecognizer = null

        textToSpeech?.stop()
        textToSpeech?.shutdown()

        textToSpeech = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
