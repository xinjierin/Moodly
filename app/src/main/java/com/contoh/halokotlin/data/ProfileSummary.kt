package com.contoh.halokotlin.data

import kotlin.math.roundToInt

// Olah data profil + catatan mood jadi teks siap tampil di layar Tentang Saya
object ProfileSummary {

    // Huruf, spasi, titik, dan petik (buat nama kayak "Nur'aini" atau "M. Rizki")
    private val NAME_PATTERN = Regex("^[\\p{L} .']+$")
    const val BIO_MAX = 150

    // Null kalau valid, selain itu pesan error-nya
    fun validateName(name: String): String? = when {
        name.isEmpty() -> "Nama belum diisi"
        !NAME_PATTERN.matches(name) -> "Nama cuma boleh huruf dan spasi"
        name.count { it.isLetter() } < 2 -> "Nama minimal 2 huruf"
        else -> null
    }

    // "rina putri ayu" → "RP"
    fun initials(name: String): String =
        name.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }

    fun firstName(name: String): String = name.trim().substringBefore(" ")

    fun greeting(hour: Int, name: String): String {
        val time = when (hour) {
            in 4..10 -> "Selamat pagi" to "☀️"
            in 11..14 -> "Selamat siang" to "🌤️"
            in 15..17 -> "Selamat sore" to "🌇"
            else -> "Selamat malam" to "🌙"
        }
        return "${time.first}, ${firstName(name)}! ${time.second}"
    }

    // "12 hari nyatet · streak 3 hari\nPaling sering: 😄 Senang (58%)"
    fun stats(entries: List<MoodEntry>, streak: Int): String {
        if (entries.isEmpty()) return "Belum ada catatan mood."
        val dominant = dominant(entries)!!
        val pct = (dominant.second * 100f / entries.size).roundToInt()
        return "${entries.size} hari nyatet · streak $streak hari\n" +
            "Paling sering: ${dominant.first.emoji} ${dominant.first.label} ($pct%)"
    }

    fun message(entries: List<MoodEntry>): String = when (dominant(entries)?.first) {
        null -> "Yuk mulai catat mood hari ini!"
        Mood.SENANG, Mood.BAIK -> "Mood kamu lagi bagus, pertahanin!"
        Mood.BIASA -> "Lumayan stabil. Coba selipin aktivitas yang bikin seneng."
        Mood.SEDIH, Mood.KESAL -> "Kayaknya lagi berat ya. Istirahat dan cerita ke orang terdekat 🤍"
    }

    private fun dominant(entries: List<MoodEntry>): Pair<Mood, Int>? =
        entries.groupingBy { it.mood }.eachCount().maxByOrNull { it.value }?.toPair()
}
