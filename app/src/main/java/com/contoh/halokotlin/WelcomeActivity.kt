package com.contoh.halokotlin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.contoh.halokotlin.data.MoodRepository
import com.contoh.halokotlin.data.ProfileSummary
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// Layar sambutan pertama kali buka app: minta nama panggilan dan email
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tilName = findViewById<TextInputLayout>(R.id.tilName)
        val etName = findViewById<TextInputEditText>(R.id.etName)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val btnStart = findViewById<MaterialButton>(R.id.btnStart)

        fun submit() {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            tilName.error = ProfileSummary.validateName(name)
            if (tilName.error != null) return
            // Email boleh kosong, tapi kalau diisi formatnya harus bener
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Format email belum bener"
                return
            }
            val repo = MoodRepository(this)
            repo.userName = name
            repo.userEmail = email
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        etEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else false
        }
        etName.setOnFocusChangeListener { _, _ -> tilName.error = null }
        etEmail.setOnFocusChangeListener { _, _ -> tilEmail.error = null }
        btnStart.setOnClickListener { submit() }
    }
}
