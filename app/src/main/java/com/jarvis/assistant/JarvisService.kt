package com.jarvis.assistant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale

class JarvisService :
    Service(),
    TextToSpeech.OnInitListener {

    private lateinit var recognizer:
        SpeechRecognizer

    private lateinit var tts:
        TextToSpeech

    private val channelId =
        "jarvis_voice"

    override fun onCreate() {

        super.onCreate()

        createNotificationChannel()

        tts =
            TextToSpeech(
                applicationContext,
                this
            )

        startForeground(
            1001,
            createNotification()
        )

        setupRecognizer()

        speak(
            "JARVIS online. Main sun raha hoon."
        )

        listen()
    }

    private fun setupRecognizer() {

        if (
            !SpeechRecognizer
                .isRecognitionAvailable(this)
        ) {
            return
        }

        recognizer =
            SpeechRecognizer
                .createSpeechRecognizer(this)

        recognizer.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(
                    params: Bundle?
                ) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(
                    rmsdB: Float
                ) {}

                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {}

                override fun onEndOfSpeech() {
                    listen()
                }

                override fun onError(
                    error: Int
                ) {
                    retryListening()
                }

                override fun onResults(
                    results: Bundle?
                ) {

                    val resultsList =
                        results?.getStringArrayList(
                            SpeechRecognizer
                                .RESULTS_RECOGNITION
                        )

                    val command =
                        resultsList
                            ?.firstOrNull()

                    if (
                        !command.isNullOrBlank()
                    ) {
                        processCommand(command)
                    }

                    listen()
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )
    }

    private fun retryListening() {

        Handler(
            Looper.getMainLooper()
        ).postDelayed(
            {
                listen()
            },
            1200
        )
    }

    private fun listen() {

        if (
            !::recognizer.isInitialized
        ) {
            return
        }

        val intent =
            Intent(
                RecognizerIntent
                    .ACTION_RECOGNIZE_SPEECH
            )

        intent.putExtra(
            RecognizerIntent
                .EXTRA_LANGUAGE_MODEL,
            RecognizerIntent
                .LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent
                .EXTRA_LANGUAGE,
            "hi-IN"
        )

        Handler(
            Looper.getMainLooper()
        ).postDelayed(
            {

                try {
                    recognizer.startListening(intent)
                } catch (_: Exception) {
                }

            },
            500
        )
    }

    private fun processCommand(
        command: String
    ) {

        val lower =
            command.lowercase(
                Locale.getDefault()
            )

        when {

            lower.contains("save") ||
            lower.contains("idea") ||
            lower.contains("save kar") -> {

                MemoryStore.save(
                    this,
                    command
                )

                speak(
                    "Theek hai. Maine ise save kar liya."
                )
            }

            lower.contains("memory") ||
            lower.contains("yaad") -> {

                val memories =
                    MemoryStore.read(this)

                if (
                    memories.isEmpty()
                ) {

                    speak(
                        "Meri memory abhi empty hai."
                    )

                } else {

                    speak(
                        memories
                            .takeLast(3)
                            .joinToString(". ")
                    )
                }
            }

            lower.contains("stop jarvis") -> {

                speak(
                    "JARVIS offline."
                )

                stopSelf()
            }

            else -> {

                Thread {

                    val answer =
                        JarvisApi.ask(command)

                    Handler(
                        Looper.getMainLooper()
                    ).post {

                        speak(answer)
                    }

                }.start()
            }
        }
    }

    private fun speak(
        text: String
    ) {

        if (
            !::tts.isInitialized
        ) {
            return
        }

        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "jarvis_response"
        )
    }

    override fun onInit(
        status: Int
    ) {

        if (
            status ==
            TextToSpeech.SUCCESS
        ) {

            tts.language =
                Locale("hi", "IN")
        }
    }

    private fun createNotificationChannel() {

        val channel =
            NotificationChannel(
                channelId,
                "JARVIS Voice Assistant",
                NotificationManager
                    .IMPORTANCE_LOW
            )

        getSystemService(
            NotificationManager::class.java
        )
            .createNotificationChannel(channel)
    }

    private fun createNotification():
        Notification {

        return NotificationCompat
            .Builder(
                this,
                channelId
            )
            .setContentTitle(
                "JARVIS"
            )
            .setContentText(
                "Voice assistant active"
            )
            .setSmallIcon(
                android.R.drawable
                    .ic_btn_speak_now
            )
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {

        if (
            ::recognizer.isInitialized
        ) {
            recognizer.destroy()
        }

        if (
            ::tts.isInitialized
        ) {
            tts.shutdown()
        }

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        return null
    }
    }
