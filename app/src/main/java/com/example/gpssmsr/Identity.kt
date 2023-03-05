package com.example.gpssmsr

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class Identity private constructor() {

    companion object {
        private const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"
        private var uniqueID: String? = null

        @Synchronized
        fun getUUID(context: Context): String {
            if (uniqueID == null) {
                val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE)
                uniqueID = sharedPreferences.getString(PREF_UNIQUE_ID, null)
                if (uniqueID == null) {
                    uniqueID = UUID.randomUUID().toString()
                    sharedPreferences.edit().putString(PREF_UNIQUE_ID, uniqueID).apply()
                }
            }
            return uniqueID!!
        }
    }
}