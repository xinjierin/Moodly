package com.contoh.halokotlin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.contoh.halokotlin.data.MoodRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val repo = MoodRepository(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
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

            if (nama.isEmpty()) {
                tvStatus.text = "Nama belum diisi"
                return@setOnClickListener
            }
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvStatus.text = "Format email belum bener"
                return@setOnClickListener
            }

            repo.userName = nama
            repo.userEmail = email
            repo.userBio = bio
            tvStatus.text = "Profil disimpan: $nama"
            Toast.makeText(this, "Halo, $nama!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AboutActivity::class.java)
            intent.putExtra(AboutActivity.EXTRA_NAMA, nama)
            intent.putExtra(AboutActivity.EXTRA_EMAIL, email)
            intent.putExtra(AboutActivity.EXTRA_BIO, bio)
            startActivity(intent)
        }

        btnSimpan.setOnLongClickListener {
            tvStatus.text = "Tekan biasa untuk menyimpan"
            true
        }
    }
}
