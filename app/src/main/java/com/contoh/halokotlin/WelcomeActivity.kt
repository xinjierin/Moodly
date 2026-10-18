package com.contoh.halokotlin

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.contoh.halokotlin.data.MoodRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// Layar sambutan pertama kali buka app: minta nama panggilan
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tilName = findViewById<TextInputLayout>(R.id.tilName)
        val etName = findViewById<TextInputEditText>(R.id.etName)
        val btnStart = findViewById<MaterialButton>(R.id.btnStart)

        fun submit() {
            val name = etName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                tilName.error = "Isi nama dulu ya"
                return
            }
            MoodRepository(this).userName = name
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        etName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else false
        }
        etName.setOnFocusChangeListener { _, _ -> tilName.error = null }
        btnStart.setOnClickListener { submit() }
    }
}
