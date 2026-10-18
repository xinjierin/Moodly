package com.contoh.halokotlin.ui

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.contoh.halokotlin.R
import com.contoh.halokotlin.data.Mood
import com.contoh.halokotlin.data.MoodEntry

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

object MoodViews {

    // Baris 5 emoji mood, yang kepilih dikasih lingkaran kuning
    fun bindMoodRow(row: LinearLayout, selected: Mood?, onClick: (Mood) -> Unit) {
        row.removeAllViews()
        val inflater = LayoutInflater.from(row.context)
        for (mood in Mood.values()) {
            val item = inflater.inflate(R.layout.item_mood, row, false)
            val emo = item.findViewById<TextView>(R.id.emo)
            val label = item.findViewById<TextView>(R.id.label)
            emo.text = mood.emoji
            label.text = mood.label
            val isSelected = mood == selected
            emo.setBackgroundResource(if (isSelected) R.drawable.bg_emo_selected else R.drawable.bg_emo)
            emo.scaleX = if (isSelected) 1.1f else 1f
            emo.scaleY = if (isSelected) 1.1f else 1f
            label.setTextColor(
                ContextCompat.getColor(row.context, if (isSelected) R.color.primary_text else R.color.muted)
            )
            label.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            item.setOnClickListener { onClick(mood) }
            row.addView(item)
        }
    }

    fun subtitle(entry: MoodEntry): String {
        if (entry.note.isNotBlank()) return entry.note
        if (entry.activities.isNotEmpty()) return entry.activities.joinToString(", ")
        return "Tanpa catatan"
    }

    // Satu baris catatan (emoji + judul + keterangan), dikasih garis pemisah kalau bukan yang pertama
    fun addEntry(
        list: LinearLayout,
        entry: MoodEntry,
        title: String,
        subtitle: String = subtitle(entry),
        onClick: (() -> Unit)? = null
    ): View {
        val inflater = LayoutInflater.from(list.context)
        if (list.childCount > 0) inflater.inflate(R.layout.item_divider, list, true)
        val row = inflater.inflate(R.layout.item_entry, list, false)
        row.findViewById<TextView>(R.id.emo).text = entry.mood.emoji
        row.findViewById<TextView>(R.id.title).text = title
        row.findViewById<TextView>(R.id.subtitle).text = subtitle
        if (onClick != null) row.setOnClickListener { onClick() } else row.background = null
        list.addView(row)
        return row
    }

    fun addEmptyText(list: ViewGroup, text: String) {
        val tv = TextView(list.context)
        tv.text = text
        tv.textSize = 12f
        tv.setTextColor(ContextCompat.getColor(list.context, R.color.muted))
        tv.setPadding(0, list.context.dp(8), 0, list.context.dp(4))
        list.addView(tv)
    }
}
