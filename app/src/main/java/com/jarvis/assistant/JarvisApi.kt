package com.jarvis.assistant

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object JarvisApi {

    private const val SERVER_URL =
        "https://YOUR-BACKEND/jarvis"

    private val client =
        OkHttpClient.Builder()
            .connectTimeout(
                20,
                TimeUnit.SECONDS
            )
            .readTimeout(
                60,
                TimeUnit.SECONDS
            )
            .build()

    fun ask(
        question: String
    ): String {

        return try {

            val json =
                JSONObject()
                    .put(
                        "message",
                        question
                    )

            val requestBody =
                json.toString()
                    .toRequestBody(
                        "application/json"
                            .toMediaType()
                    )

            val request =
                Request.Builder()
                    .url(SERVER_URL)
                    .post(requestBody)
                    .build()

            client
                .newCall(request)
                .execute()
                .use { response ->

                    if (!response.isSuccessful) {
                        return "AI server se response nahi mila."
                    }

                    val result =
                        response.body
                            ?.string()
                            ?: return "Empty response."

                    JSONObject(result)
                        .optString(
                            "answer",
                            "Mujhe answer nahi mila."
                        )
                }

        } catch (e: Exception) {

            "Internet ya AI server connection mein problem hai."
        }
    }
}
