package com.contoh.halokotlin.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.contoh.halokotlin.MainActivity
import com.contoh.halokotlin.R
import com.contoh.halokotlin.data.DateUtil
import com.contoh.halokotlin.data.MoodRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var repo: MoodRepository
    private val month: Calendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    private var selectedDate: String = DateUtil.todayKey()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = MoodRepository(requireContext())

        savedInstanceState?.getString(KEY_SELECTED)?.let {
            selectedDate = it
            val cal = DateUtil.parse(it)
            month.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1)
        }

        view.findViewById<ImageButton>(R.id.btnPrev).setOnClickListener {
            month.add(Calendar.MONTH, -1)
            renderCalendar()
        }
        view.findViewById<ImageButton>(R.id.btnNext).setOnClickListener {
            month.add(Calendar.MONTH, 1)
            renderCalendar()
        }

        val searchBox = view.findViewById<View>(R.id.searchBox)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        view.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            if (it.itemId == R.id.action_search) {
                val show = searchBox.visibility != View.VISIBLE
                searchBox.visibility = if (show) View.VISIBLE else View.GONE
                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                if (show) {
                    etSearch.requestFocus()
                    imm?.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    etSearch.setText("")
                    imm?.hideSoftInputFromWindow(etSearch.windowToken, 0)
                }
            }
            true
        }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = renderSearch(s?.toString().orEmpty())
        })
    }

    override fun onResume() {
        super.onResume()
        renderCalendar()
        view?.let { renderSearch(it.findViewById<EditText>(R.id.etSearch).text.toString()) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED, selectedDate)
    }

    private fun renderCalendar() {
        val view = view ?: return
        val ctx = requireContext()
        view.findViewById<TextView>(R.id.tvMonth).text = DateUtil.monthTitle(month)

        val year = month.get(Calendar.YEAR)
        val m = month.get(Calendar.MONTH)
        val moods = repo.inMonth(year, m).associateBy { it.date }
        val today = DateUtil.todayKey()

        val grid = view.findViewById<LinearLayout>(R.id.calGrid)
        grid.removeAllViews()

        // Header hari, minggu dimulai Senin
        val header = newRow()
        for (d in listOf("S", "S", "R", "K", "J", "S", "M")) {
            val tv = TextView(ctx)
            tv.text = d
            tv.gravity = Gravity.CENTER
            tv.textSize = 11f
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.muted))
            tv.setPadding(0, 0, 0, ctx.dp(4))
            header.addView(tv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        grid.addView(header)

        val first = month.clone() as Calendar
        // Calendar.MONDAY = 2 → Senin jadi kolom 0
        val offset = (first.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val days = first.getActualMaximum(Calendar.DAY_OF_MONTH)
        val cellSize = ctx.dp(38)

        var row = newRow()
        repeat(offset) { row.addView(View(ctx), LinearLayout.LayoutParams(0, cellSize, 1f)) }
        for (day in 1..days) {
            val cal = month.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, day)
            val key = DateUtil.key(cal)
            val entry = moods[key]

            val cell = TextView(ctx)
            cell.gravity = Gravity.CENTER
            if (entry != null) {
                cell.text = entry.mood.emoji
                cell.textSize = 18f
            } else {
                cell.text = day.toString()
                cell.textSize = 11f
                cell.setTextColor(ContextCompat.getColor(ctx, R.color.muted))
            }
            when (key) {
                selectedDate -> cell.setBackgroundResource(R.drawable.bg_selected_day)
                today -> cell.setBackgroundResource(R.drawable.bg_today)
            }
            cell.setOnClickListener {
                selectedDate = key
                renderCalendar()
            }

            val frame = LinearLayout(ctx)
            frame.gravity = Gravity.CENTER
            frame.addView(cell, LinearLayout.LayoutParams(cellSize, cellSize))
            row.addView(frame, LinearLayout.LayoutParams(0, cellSize + ctx.dp(4), 1f))

            if (row.childCount == 7) {
                grid.addView(row)
                row = newRow()
            }
        }
        if (row.childCount > 0) {
            while (row.childCount < 7) row.addView(View(ctx), LinearLayout.LayoutParams(0, cellSize, 1f))
            grid.addView(row)
        }

        renderDetail()
    }

    private fun newRow(): LinearLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    private fun renderDetail() {
        val view = view ?: return
        val entry = repo.get(selectedDate)
        view.findViewById<TextView>(R.id.tvDetailDate).text = DateUtil.medium(selectedDate)
        val box = view.findViewById<LinearLayout>(R.id.detailBox)
        box.removeAllViews()
        val btn = view.findViewById<MaterialButton>(R.id.btnDetail)
        val isFuture = selectedDate > DateUtil.todayKey()

        if (entry != null) {
            val sub = buildString {
                append(if (entry.note.isNotBlank()) entry.note else "Tanpa catatan")
                if (entry.activities.isNotEmpty()) append("\n").append(entry.activities.joinToString("  "))
            }
            MoodViews.addEntry(box, entry, entry.mood.label, sub)
            btn.text = "Ubah"
        } else {
            MoodViews.addEmptyText(
                box, if (isFuture) "Tanggal ini belum dateng 😄" else "Belum ada catatan di tanggal ini."
            )
            btn.text = "Catat mood"
        }
        btn.visibility = if (isFuture) View.GONE else View.VISIBLE
        btn.setOnClickListener { (activity as? MainActivity)?.openAddMood(date = selectedDate) }
    }

    private fun renderSearch(query: String) {
        val view = view ?: return
        val results = view.findViewById<LinearLayout>(R.id.searchResults)
        val info = view.findViewById<TextView>(R.id.tvSearchInfo)
        results.removeAllViews()
        if (query.isBlank()) {
            info.text = "Ketik kata kunci buat nyari."
            return
        }
        val found = repo.search(query)
        info.text = if (found.isEmpty()) "Gak ketemu catatan dengan \"$query\"." else "${found.size} catatan ketemu"
        for (entry in found) {
            MoodViews.addEntry(results, entry, DateUtil.short(entry.date)) {
                selectedDate = entry.date
                val cal = DateUtil.parse(entry.date)
                month.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1)
                renderCalendar()
            }
        }
    }

    companion object {
        private const val KEY_SELECTED = "selected"
    }
}
