package com.contoh.halokotlin

import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.contoh.halokotlin.data.MoodRepository
import com.contoh.halokotlin.data.ProfileSummary
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val repo = MoodRepository(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val tilNama = findViewById<TextInputLayout>(R.id.tilNama)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilBio = findViewById<TextInputLayout>(R.id.tilBio)
        val etNama = findViewById<TextInputEditText>(R.id.etNama)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etBio = findViewById<TextInputEditText>(R.id.etBio)
        val btnBatal = findViewById<MaterialButton>(R.id.btnBatal)
        val btnSimpan = findViewById<MaterialButton>(R.id.btnSimpan)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        toolbar.setNavigationOnClickListener { finish() }

        if (repo.hasName) etNama.setText(repo.userName)
        etEmail.setText(repo.userEmail)
        etBio.setText(repo.userBio)

        btnBatal.setOnClickListener { finish() }

        btnSimpan.setOnClickListener {
            // Input dibaca di sini, bukan di onCreate, biar yang kebaca isi terbaru
            val nama = etNama.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val bio = etBio.text?.toString()?.trim().orEmpty()

            // Semua kolom dicek sekaligus biar error-nya keliatan barengan
            tilNama.error = ProfileSummary.validateName(nama)
            tilEmail.error =
                if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Format email belum bener"
                else null
            tilBio.error =
                if (bio.length > ProfileSummary.BIO_MAX) "Maksimal ${ProfileSummary.BIO_MAX} karakter" else null
            if (tilNama.error != null || tilEmail.error != null || tilBio.error != null) {
                tvStatus.text = "Cek lagi kolom yang merah ya"
                return@setOnClickListener
            }

            repo.userName = nama
            repo.userEmail = email
            repo.userBio = bio
            Toast.makeText(this, "Profil disimpan", Toast.LENGTH_SHORT).show()

            // Tutup form biar tombol Back dari Tentang Saya langsung balik ke Profil
            startActivity(AboutActivity.newIntent(this))
            finish()
        }

        btnSimpan.setOnLongClickListener {
            tvStatus.text = "Tekan biasa untuk menyimpan"
            true
        }
    }
}
