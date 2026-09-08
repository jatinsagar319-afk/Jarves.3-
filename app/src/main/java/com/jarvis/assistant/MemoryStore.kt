package com.jarvis.assistant

import android.content.Context
import org.json.JSONArray

object MemoryStore {

    private const val PREF_NAME = "jarvis_memory"
    private const val KEY_MEMORIES = "memories"

    fun saveMemory(
        context: Context,
        memory: String
    ) {

        if (memory.isBlank()) return

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val oldData = preferences.getString(
            KEY_MEMORIES,
            "[]"
        ) ?: "[]"

        val array = try {
            JSONArray(oldData)
        } catch (_: Exception) {
            JSONArray()
        }

        array.put(memory.trim())

        preferences.edit()
            .putString(
                KEY_MEMORIES,
                array.toString()
            )
            .apply()
    }

    fun getMemories(
        context: Context
    ): List<String> {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val data = preferences.getString(
            KEY_MEMORIES,
            "[]"
        ) ?: "[]"

        val array = try {
            JSONArray(data)
        } catch (_: Exception) {
            JSONArray()
        }

        val memories = mutableListOf<String>()

        for (i in 0 until array.length()) {

            val value = array.optString(i)

            if (value.isNotBlank()) {
                memories.add(value)
            }
        }

        return memories
    }

    fun clearMemories(
        context: Context
    ) {

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .remove(KEY_MEMORIES)
            .apply()
    }
}
