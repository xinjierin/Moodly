package com.contoh.halokotlin.data

// Skor dipakai buat grafik: makin tinggi makin bagus mood-nya
enum class Mood(val emoji: String, val label: String, val score: Int) {
    SENANG("😄", "Senang", 5),
    BAIK("🙂", "Baik", 4),
    BIASA("😐", "Biasa", 3),
    SEDIH("😢", "Sedih", 2),
    KESAL("😠", "Kesal", 1);

    companion object {
        fun fromName(name: String?): Mood = values().firstOrNull { it.name == name } ?: BAIK
    }
}

val ACTIVITIES = listOf(
    "📚 Kuliah",
    "👯 Teman",
    "☕ Ngopi",
    "🎮 Main game",
    "😴 Istirahat",
    "🏃 Olahraga",
    "👨‍👩‍👧 Keluarga"
)

data class MoodEntry(
    val date: String, // format yyyy-MM-dd
    val mood: Mood,
    val activities: List<String>,
    val note: String
)
