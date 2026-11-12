package com.contoh.halokotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.contoh.halokotlin.data.MoodRepository
import com.contoh.halokotlin.data.ProfileSummary
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val tvInisial = findViewById<TextView>(R.id.tvInisial)
        val tvSapaan = findViewById<TextView>(R.id.tvSapaan)
        val tvNama = findViewById<TextView>(R.id.tvNama)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvBio = findViewById<TextView>(R.id.tvBio)
        val tvRingkasan = findViewById<TextView>(R.id.tvRingkasan)
        val tvPesan = findViewById<TextView>(R.id.tvPesan)
        val btnKembali = findViewById<MaterialButton>(R.id.btnKembali)

        toolbar.setNavigationOnClickListener { finish() }

        // Semua teks di sini udah diolah sebelum pindah layar, tinggal ditampilin.
        // getStringExtra bisa null kalau kuncinya gak ada, jadi selalu kasih cadangan
        val email = intent.getStringExtra(EXTRA_EMAIL)
        val bio = intent.getStringExtra(EXTRA_BIO)
        tvInisial.text = intent.getStringExtra(EXTRA_INISIAL) ?: "?"
        tvSapaan.text = intent.getStringExtra(EXTRA_SAPAAN) ?: "Halo!"
        tvNama.text = intent.getStringExtra(EXTRA_NAMA) ?: "(tidak ada data)"
        tvEmail.text = if (email.isNullOrEmpty()) "Email belum diisi" else email
        tvBio.text = if (bio.isNullOrEmpty()) "Belum ada cerita tentang kamu." else bio
        tvRingkasan.text = intent.getStringExtra(EXTRA_RINGKASAN) ?: "-"
        tvPesan.text = intent.getStringExtra(EXTRA_PESAN) ?: ""

        btnKembali.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_BIO = "extra_bio"
        const val EXTRA_INISIAL = "extra_inisial"
        const val EXTRA_SAPAAN = "extra_sapaan"
        const val EXTRA_RINGKASAN = "extra_ringkasan"
        const val EXTRA_PESAN = "extra_pesan"

        // Olah data profil + catatan mood, lalu bawa hasilnya ke layar Tentang Saya
        fun newIntent(context: Context): Intent {
            val repo = MoodRepository(context)
            val nama = repo.userName
            val entries = repo.all()
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return Intent(context, AboutActivity::class.java)
                .putExtra(EXTRA_NAMA, nama)
                .putExtra(EXTRA_EMAIL, repo.userEmail)
                .putExtra(EXTRA_BIO, repo.userBio)
                .putExtra(EXTRA_INISIAL, ProfileSummary.initials(nama))
                .putExtra(EXTRA_SAPAAN, ProfileSummary.greeting(hour, nama))
                .putExtra(EXTRA_RINGKASAN, ProfileSummary.stats(entries, repo.streak()))
                .putExtra(EXTRA_PESAN, ProfileSummary.message(entries))
        }
    }
}
