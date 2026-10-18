package com.contoh.halokotlin.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

// Simpan data di SharedPreferences (format JSON), satu catatan per tanggal
class MoodRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("moodly", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString(KEY_NAME, null) ?: "Kamu"
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    // Belum pernah isi nama → tampilkan layar sambutan dulu
    val hasName: Boolean
        get() = !prefs.getString(KEY_NAME, null).isNullOrBlank()

    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER, value).apply()

    var reminderHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, 20)
        set(value) = prefs.edit().putInt(KEY_REMINDER_HOUR, value).apply()

    var reminderMinute: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_REMINDER_MINUTE, value).apply()

    private fun readAll(): JSONObject =
        try {
            JSONObject(prefs.getString(KEY_ENTRIES, "{}") ?: "{}")
        } catch (e: Exception) {
            JSONObject()
        }

    private fun writeAll(obj: JSONObject) {
        prefs.edit().putString(KEY_ENTRIES, obj.toString()).apply()
    }

    fun get(date: String): MoodEntry? {
        val obj = readAll().optJSONObject(date) ?: return null
        return fromJson(date, obj)
    }

    fun save(entry: MoodEntry) {
        val all = readAll()
        val obj = JSONObject()
        obj.put("mood", entry.mood.name)
        obj.put("activities", JSONArray(entry.activities))
        obj.put("note", entry.note)
        all.put(entry.date, obj)
        writeAll(all)
    }

    fun delete(date: String) {
        val all = readAll()
        all.remove(date)
        writeAll(all)
    }

    fun clear() {
        prefs.edit().remove(KEY_ENTRIES).apply()
    }

    // Semua catatan, terbaru duluan
    fun all(): List<MoodEntry> {
        val all = readAll()
        val list = mutableListOf<MoodEntry>()
        val keys = all.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            all.optJSONObject(k)?.let { list.add(fromJson(k, it)) }
        }
        return list.sortedByDescending { it.date }
    }

    fun inMonth(year: Int, month: Int): List<MoodEntry> {
        val prefix = String.format(java.util.Locale.US, "%04d-%02d-", year, month + 1)
        return all().filter { it.date.startsWith(prefix) }
    }

    // Hari berturut-turut sampai hari ini (atau kemarin kalau hari ini belum dicatat)
    fun streak(): Int {
        val dates = all().map { it.date }.toHashSet()
        var day = DateUtil.todayKey()
        if (day !in dates) day = DateUtil.addDays(day, -1)
        var count = 0
        while (day in dates) {
            count++
            day = DateUtil.addDays(day, -1)
        }
        return count
    }

    fun search(query: String): List<MoodEntry> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return all().filter { e ->
            e.note.lowercase().contains(q) ||
                e.mood.label.lowercase().contains(q) ||
                e.activities.any { it.lowercase().contains(q) } ||
                DateUtil.full(e.date).lowercase().contains(q)
        }
    }

    private fun fromJson(date: String, obj: JSONObject): MoodEntry {
        val arr = obj.optJSONArray("activities") ?: JSONArray()
        val acts = (0 until arr.length()).map { arr.optString(it) }
        return MoodEntry(date, Mood.fromName(obj.optString("mood")), acts, obj.optString("note"))
    }

    companion object {
        private const val KEY_ENTRIES = "entries"
        private const val KEY_NAME = "name"
        private const val KEY_REMINDER = "reminder"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"

        fun currentYearMonth(): Pair<Int, Int> {
            val c = Calendar.getInstance()
            return c.get(Calendar.YEAR) to c.get(Calendar.MONTH)
        }
    }
}
