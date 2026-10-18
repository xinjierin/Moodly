package com.contoh.halokotlin.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.contoh.halokotlin.MainActivity
import com.contoh.halokotlin.R
import com.contoh.halokotlin.data.MoodRepository
import com.contoh.halokotlin.data.Reminder
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import java.util.Locale

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var repo: MoodRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = MoodRepository(requireContext())

        view.findViewById<MaterialButton>(R.id.btnEditName).setOnClickListener { editName() }

        val swReminder = view.findViewById<MaterialSwitch>(R.id.swReminder)
        swReminder.isChecked = repo.reminderEnabled
        swReminder.setOnCheckedChangeListener { _, checked ->
            repo.reminderEnabled = checked
            if (checked) (activity as? MainActivity)?.ensureNotificationPermission()
            Reminder.schedule(requireContext())
            render()
        }

        view.findViewById<MaterialButton>(R.id.btnTime).setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                repo.reminderHour = h
                repo.reminderMinute = m
                Reminder.schedule(requireContext())
                render()
            }, repo.reminderHour, repo.reminderMinute, true).show()
        }

        view.findViewById<MaterialButton>(R.id.btnClear).setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus semua catatan?")
                .setMessage("Semua catatan mood bakal hilang dan gak bisa dibalikin.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus") { _, _ ->
                    repo.clear()
                    Toast.makeText(requireContext(), "Semua catatan dihapus", Toast.LENGTH_SHORT).show()
                    render()
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val view = view ?: return
        view.findViewById<TextView>(R.id.tvName).text = repo.userName
        val total = repo.all().size
        view.findViewById<TextView>(R.id.tvSummary).text =
            "$total catatan · streak ${repo.streak()} hari"
        val btnTime = view.findViewById<MaterialButton>(R.id.btnTime)
        btnTime.text = String.format(Locale.US, "Jam %02d:%02d", repo.reminderHour, repo.reminderMinute)
        btnTime.isEnabled = repo.reminderEnabled
    }

    private fun editName() {
        val ctx = requireContext()
        val input = EditText(ctx)
        input.setText(repo.userName)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.setSelection(input.text.length)
        val wrap = FrameLayout(ctx)
        wrap.setPadding(ctx.dp(20), ctx.dp(8), ctx.dp(20), 0)
        wrap.addView(input)

        MaterialAlertDialogBuilder(ctx)
            .setTitle("Nama panggilan")
            .setView(wrap)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) repo.userName = name
                render()
            }
            .show()
    }
}
