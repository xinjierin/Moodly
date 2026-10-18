package com.contoh.halokotlin

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.contoh.halokotlin.data.ACTIVITIES
import com.contoh.halokotlin.data.DateUtil
import com.contoh.halokotlin.data.Mood
import com.contoh.halokotlin.data.MoodEntry
import com.contoh.halokotlin.data.MoodRepository
import com.contoh.halokotlin.ui.MoodViews
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class AddMoodActivity : AppCompatActivity() {

    private lateinit var repo: MoodRepository
    private lateinit var date: String
    private var mood: Mood = Mood.BAIK

    private lateinit var tvBigEmo: TextView
    private lateinit var tvBigLabel: TextView
    private lateinit var moodRow: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mood)

        repo = MoodRepository(this)
        date = intent.getStringExtra(EXTRA_DATE) ?: DateUtil.todayKey()
        val existing = repo.get(date)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        tvBigEmo = findViewById(R.id.tvBigEmo)
        tvBigLabel = findViewById(R.id.tvBigLabel)
        moodRow = findViewById(R.id.moodRow)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        val etNote = findViewById<TextInputEditText>(R.id.etNote)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        toolbar.setNavigationOnClickListener { finish() }
        tvDate.text = DateUtil.full(date)

        // Mood dari Beranda didahulukan, kalau gak ada pakai yang udah tersimpan
        val fromIntent = intent.getStringExtra(EXTRA_MOOD)
        mood = savedInstanceState?.getString(EXTRA_MOOD)?.let { Mood.fromName(it) }
            ?: fromIntent?.let { Mood.fromName(it) }
            ?: existing?.mood
            ?: Mood.BAIK
        renderMood()

        for (activity in ACTIVITIES) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = activity
            chip.isChecked = existing?.activities?.contains(activity) == true
            chipGroup.addView(chip)
        }

        if (existing != null) {
            toolbar.title = "Ubah Mood"
            etNote.setText(existing.note)
            toolbar.inflateMenu(R.menu.add_menu)
            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_delete) confirmDelete()
                true
            }
        }

        btnSave.setOnClickListener {
            val selected = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) selected.add(chip.text.toString())
            }
            val note = etNote.text?.toString()?.trim().orEmpty()
            repo.save(MoodEntry(date, mood, selected, note))
            Toast.makeText(this, "Mood ${mood.emoji} tersimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_MOOD, mood.name)
    }

    private fun renderMood() {
        tvBigEmo.text = mood.emoji
        tvBigLabel.text = mood.label
        MoodViews.bindMoodRow(moodRow, mood) {
            mood = it
            renderMood()
        }
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus catatan?")
            .setMessage("Catatan ${DateUtil.full(date)} bakal dihapus.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                repo.delete(date)
                Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()
    }

    companion object {
        const val EXTRA_MOOD = "mood"
        const val EXTRA_DATE = "date"
    }
}
