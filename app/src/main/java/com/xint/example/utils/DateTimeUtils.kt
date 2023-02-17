package com.xint.example.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private const val serverDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    private const val dateTimeFormat = "dd MMM yyyy, hh:mm a"
    const val timeFormat = "hh:mm a"

    fun formatDateFromString(
        inputFormat: String? = serverDateTimeFormat,
        outputFormat: String? = dateTimeFormat,
        inputDate: String?,
        timeZone: TimeZone? = TimeZone.getTimeZone("Etc/UTC")
    ): String {
        val parsed: Date?
        var outputDate = ""
        val input = SimpleDateFormat(inputFormat, Locale.getDefault())
        if (timeZone != null) {
            input.timeZone = timeZone
        }
        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        output.timeZone = TimeZone.getDefault()

        try {
            parsed = input.parse(inputDate ?: "")
            outputDate = output.format(parsed ?: "")
        } catch (e: ParseException) {
            LogUtils.error("DataTimeUtils Exception--> ${e.message}")
        }
        return outputDate
    }

    fun getDateTimeFromMillis(
        inputFormat: String? = serverDateTimeFormat,
        milliSeconds: Long,
        timeZone: TimeZone? = TimeZone.getTimeZone("Etc/UTC")
    ): String {
        val input = SimpleDateFormat(inputFormat, Locale.getDefault())
        if (timeZone != null) {
            input.timeZone = timeZone
        }
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return input.format(calendar.time);
        } catch (e: ParseException) {
            LogUtils.error("DataTimeUtils Exception--> ${e.message}")
        }
        return ""
    }

    fun getDateTimeFromFormat(timestamp: Long): String {
        return SimpleDateFormat(serverDateTimeFormat, Locale.US).format(Date(timestamp))
    }

}