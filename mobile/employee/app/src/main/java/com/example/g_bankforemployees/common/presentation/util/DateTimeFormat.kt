package com.example.g_bankforemployees.common.presentation.util

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale("ru"))
@SuppressLint("NewApi")
private val DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))

@SuppressLint("NewApi")
fun formatDateTime(dateTime: String): String {
    return runCatching {
        try {
            val instant = Instant.parse(dateTime)
            return@runCatching DATE_TIME_FORMAT.withZone(ZoneId.systemDefault()).format(instant)
        } catch (_: Exception) { }
        try {
            val localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            return@runCatching DATE_TIME_FORMAT.format(localDateTime)
        } catch (_: Exception) { }
        try {
            val localDate = LocalDate.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE)
            return@runCatching DATE_ONLY_FORMAT.format(localDate)
        } catch (_: Exception) { }
        dateTime
    }.getOrElse { dateTime }
}
