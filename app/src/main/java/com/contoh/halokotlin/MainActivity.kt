package com.contoh.halokotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.contoh.halokotlin.data.Mood
import com.contoh.halokotlin.data.MoodRepository
import com.contoh.halokotlin.data.Reminder
import com.contoh.halokotlin.ui.HistoryFragment
import com.contoh.halokotlin.ui.HomeFragment
import com.contoh.halokotlin.ui.ProfileFragment
import com.contoh.halokotlin.ui.StatsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!MoodRepository(this).hasName) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNav)
        fab = findViewById(R.id.fab)

        fab.setOnClickListener { openAddMood() }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_history -> HistoryFragment()
                R.id.nav_stats -> StatsFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            // Tombol tambah cuma ada di Beranda (sesuai mockup)
            if (item.itemId == R.id.nav_home) fab.show() else fab.hide()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
            true
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
        } else if (supportFragmentManager.findFragmentById(R.id.container) !is HomeFragment) {
            fab.hide()
        }

        Reminder.createChannel(this)
    }

    fun openAddMood(mood: Mood? = null, date: String? = null) {
        val intent = Intent(this, AddMoodActivity::class.java)
        mood?.let { intent.putExtra(AddMoodActivity.EXTRA_MOOD, it.name) }
        date?.let { intent.putExtra(AddMoodActivity.EXTRA_DATE, it) }
        startActivity(intent)
    }

    fun openTab(itemId: Int) {
        bottomNav.selectedItemId = itemId
    }

    // Izin notifikasi baru wajib diminta di Android 13 ke atas
    fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        }
    }
}
