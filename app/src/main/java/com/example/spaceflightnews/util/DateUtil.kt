package com.example.spaceflightnews.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtil {
    fun formatDate(dateString: String): String {
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val date = sdfInput.parse(dateString.replace("Z", "").substring(0, 19))
            sdfOutput.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }


}