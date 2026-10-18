package com.contoh.halokotlin.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {
    val ID = Locale("id", "ID")
    private val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun key(cal: Calendar): String = keyFormat.format(cal.time)

    fun todayKey(): String = key(Calendar.getInstance())

    fun parse(key: String): Calendar {
        val cal = Calendar.getInstance()
        cal.time = keyFormat.parse(key) ?: Date()
        return cal
    }

    fun format(key: String, pattern: String): String =
        SimpleDateFormat(pattern, ID).format(parse(key).time)

    // "Minggu, 18 Oktober 2026"
    fun full(key: String) = format(key, "EEEE, d MMMM yyyy")

    // "Sabtu, 17 Okt"
    fun short(key: String) = format(key, "EEEE, d MMM")

    // "Rabu, 14 Oktober"
    fun medium(key: String) = format(key, "EEEE, d MMMM")

    fun monthTitle(cal: Calendar): String = SimpleDateFormat("MMMM yyyy", ID).format(cal.time)

    fun addDays(key: String, days: Int): String {
        val cal = parse(key)
        cal.add(Calendar.DAY_OF_MONTH, days)
        return key(cal)
    }
}
