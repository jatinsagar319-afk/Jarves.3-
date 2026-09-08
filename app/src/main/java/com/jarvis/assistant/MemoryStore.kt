package com.jarvis.assistant

import android.content.Context
import org.json.JSONArray

object MemoryStore {

    private const val PREF =
        "jarvis_memory"

    private const val KEY =
        "saved_items"

    fun save(
        context: Context,
        text: String
    ) {

        if (text.isBlank()) return

        val old =
            read(context).toMutableList()

        old.add(text)

        val json =
            JSONArray()

        old.forEach {
            json.put(it)
        }

        context
            .getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                KEY,
                json.toString()
            )
            .apply()
    }

    fun read(
        context: Context
    ): List<String> {

        val raw =
            context
                .getSharedPreferences(
                    PREF,
                    Context.MODE_PRIVATE
                )
                .getString(
                    KEY,
                    "[]"
                )

        val json =
            JSONArray(raw)

        val result =
            mutableListOf<String>()

        for (
            i in 0 until json.length()
        ) {
            result.add(
                json.getString(i)
            )
        }

        return result
    }
}
