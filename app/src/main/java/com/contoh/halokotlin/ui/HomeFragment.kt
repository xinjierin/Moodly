package com.contoh.halokotlin.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.contoh.halokotlin.MainActivity
import com.contoh.halokotlin.R
import com.contoh.halokotlin.data.DateUtil
import com.contoh.halokotlin.data.MoodRepository
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var repo: MoodRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = MoodRepository(requireContext())

        // Ikon lonceng → ke Profil buat atur pengingat
        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            if (it.itemId == R.id.action_reminder) (activity as? MainActivity)?.openTab(R.id.nav_profile)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val view = view ?: return
        val today = DateUtil.todayKey()
        val todayEntry = repo.get(today)

        view.findViewById<TextView>(R.id.tvDate).text = DateUtil.full(today)
        view.findViewById<TextView>(R.id.tvHello).text = "Halo, ${repo.userName}! 🌻"
        view.findViewById<TextView>(R.id.tvMoodQuestion).text =
            if (todayEntry == null) "Gimana perasaanmu hari ini?"
            else "Hari ini kamu ngerasa ${todayEntry.mood.label.lowercase()} ${todayEntry.mood.emoji}"

        // Pilih emoji → langsung buka layar Catat Mood dengan mood itu
        MoodViews.bindMoodRow(view.findViewById(R.id.moodRow), todayEntry?.mood) { mood ->
            (activity as? MainActivity)?.openAddMood(mood)
        }

        val streak = repo.streak()
        view.findViewById<TextView>(R.id.tvStreak).text =
            if (streak > 0) "🔥 Streak $streak hari" else "🔥 Belum ada streak"
        view.findViewById<TextView>(R.id.tvStreakMsg).text = when {
            streak == 0 -> "Catat mood hari ini buat mulai streak!"
            todayEntry == null -> "Jangan sampai putus, catat mood hari ini ya!"
            streak >= 7 -> "Keren! Udah seminggu lebih kamu rutin nyatet mood!"
            streak >= 3 -> "Kamu rajin banget nyatet mood minggu ini!"
            else -> "Mantap, lanjutin terus ya!"
        }

        val list = view.findViewById<LinearLayout>(R.id.recentList)
        list.removeAllViews()
        val recent = repo.all().take(3)
        view.findViewById<TextView>(R.id.tvEmpty).visibility = if (recent.isEmpty()) View.VISIBLE else View.GONE
        for (entry in recent) {
            MoodViews.addEntry(list, entry, DateUtil.short(entry.date)) {
                (activity as? MainActivity)?.openAddMood(date = entry.date)
            }
        }
    }
}
