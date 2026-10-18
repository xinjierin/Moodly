package com.contoh.halokotlin.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.contoh.halokotlin.R
import com.contoh.halokotlin.data.DateUtil
import com.contoh.halokotlin.data.Mood
import com.contoh.halokotlin.data.MoodEntry
import com.contoh.halokotlin.data.MoodRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.Calendar
import kotlin.math.roundToInt

class StatsFragment : Fragment(R.layout.fragment_stats) {

    private lateinit var repo: MoodRepository
    private var allTime = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = MoodRepository(requireContext())
        allTime = savedInstanceState?.getBoolean(KEY_ALL_TIME) ?: false

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_range) {
                val anchor = toolbar.findViewById<View>(R.id.action_range) ?: toolbar
                val popup = PopupMenu(requireContext(), anchor)
                popup.menu.add(0, 1, 0, "Bulan ini")
                popup.menu.add(0, 2, 1, "Semua waktu")
                popup.setOnMenuItemClickListener { item ->
                    allTime = item.itemId == 2
                    render()
                    true
                }
                popup.show()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ALL_TIME, allTime)
    }

    private fun render() {
        val view = view ?: return
        val (year, month) = MoodRepository.currentYearMonth()
        val entries = if (allTime) repo.all() else repo.inMonth(year, month)

        val dominant = entries.groupingBy { it.mood }.eachCount().maxByOrNull { it.value }?.key
        view.findViewById<TextView>(R.id.tvDominant).text =
            dominant?.let { "${it.emoji} ${it.label}" } ?: "–"
        view.findViewById<TextView>(R.id.tvCount).text = "${entries.size} hari"
        view.findViewById<TextView>(R.id.tvDistTitle).text =
            if (allTime) "Sebaran mood semua waktu" else "Sebaran mood bulan ini"

        renderWeek(view.findViewById(R.id.bars))
        renderDistribution(view.findViewById(R.id.dist), entries)
    }

    // Grafik batang Senin–Minggu minggu ini, tinggi batang = skor mood (1–5)
    private fun renderWeek(bars: LinearLayout) {
        val ctx = requireContext()
        bars.removeAllViews()
        val monday = Calendar.getInstance()
        val shift = (monday.get(Calendar.DAY_OF_WEEK) + 5) % 7
        monday.add(Calendar.DAY_OF_MONTH, -shift)
        val names = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        val maxBar = ctx.dp(90)

        for (i in 0 until 7) {
            val day = monday.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, i)
            val entry = repo.get(DateUtil.key(day))

            val col = LinearLayout(ctx)
            col.orientation = LinearLayout.VERTICAL
            col.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

            val emo = TextView(ctx)
            emo.text = entry?.mood?.emoji ?: ""
            emo.textSize = 12f
            emo.gravity = Gravity.CENTER
            col.addView(emo)

            val bar = View(ctx)
            bar.setBackgroundResource(R.drawable.bg_bar)
            val h = if (entry == null) ctx.dp(3) else maxBar * entry.mood.score / 5
            if (entry == null) bar.alpha = 0.25f
            col.addView(bar, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h).apply {
                topMargin = ctx.dp(2)
            })

            val label = TextView(ctx)
            label.text = names[i]
            label.textSize = 10f
            label.gravity = Gravity.CENTER
            label.setTextColor(ContextCompat.getColor(ctx, R.color.muted))
            label.setPadding(0, ctx.dp(4), 0, 0)
            col.addView(label)

            bars.addView(col, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                marginStart = ctx.dp(4)
                marginEnd = ctx.dp(4)
            })
        }
    }

    private fun renderDistribution(dist: LinearLayout, entries: List<MoodEntry>) {
        dist.removeAllViews()
        val inflater = LayoutInflater.from(dist.context)
        val counts = entries.groupingBy { it.mood }.eachCount()
        val total = entries.size
        for (mood in Mood.values()) {
            val row = inflater.inflate(R.layout.item_dist, dist, false)
            val pct = if (total == 0) 0 else ((counts[mood] ?: 0) * 100f / total).roundToInt()
            row.findViewById<TextView>(R.id.emo).text = mood.emoji
            row.findViewById<LinearProgressIndicator>(R.id.progress).progress = pct
            row.findViewById<TextView>(R.id.pct).text = "$pct%"
            dist.addView(row)
        }
    }

    companion object {
        private const val KEY_ALL_TIME = "all_time"
    }
}
