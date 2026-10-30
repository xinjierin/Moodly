package com.contoh.halokotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val tvNama = findViewById<TextView>(R.id.tvNama)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvBio = findViewById<TextView>(R.id.tvBio)
        val btnKembali = findViewById<MaterialButton>(R.id.btnKembali)

        toolbar.setNavigationOnClickListener { finish() }

        // getStringExtra bisa null kalau kuncinya gak ada, jadi selalu kasih cadangan
        val nama = intent.getStringExtra(EXTRA_NAMA)
        val email = intent.getStringExtra(EXTRA_EMAIL)
        val bio = intent.getStringExtra(EXTRA_BIO)
        tvNama.text = nama ?: "(tidak ada data)"
        tvEmail.text = if (email.isNullOrEmpty()) "Email belum diisi" else email
        tvBio.text = if (bio.isNullOrEmpty()) "Belum ada cerita tentang kamu." else bio

        btnKembali.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_BIO = "extra_bio"
    }
}
