package com.example.sesilogin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    private lateinit var tvGreeting: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var tvBadgeSesi: TextView
    private lateinit var btnLogout: Button
    private lateinit var menuPengaturan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        session = SessionManager(this)

        if (!session.adaDataProfil()) {
            kembaliKeLogin()
            return
        }

        tvGreeting = findViewById(R.id.tvGreeting)
        ivAvatar = findViewById(R.id.ivAvatar)
        tvNama = findViewById(R.id.tvNama)
        tvEmail = findViewById(R.id.tvEmail)
        tvWaktu = findViewById(R.id.tvWaktu)
        tvBadgeSesi = findViewById(R.id.tvBadgeSesi)
        btnLogout = findViewById(R.id.btnLogout)
        menuPengaturan = findViewById(R.id.menuPengaturan)

        // data profil dari SharedPreferences
        tvNama.text = session.ambilNama()
        tvEmail.text = session.ambilEmail()
        tvWaktu.text = formatWaktu(session.ambilWaktuLogin())
        tvGreeting.text = buatGreeting()
        tampilkanAvatar(session.ambilAvatar())
        tvBadgeSesi.text = "✓ Sesi aktif"

        menuPengaturan.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnLogout.setOnClickListener { keluar() }
    }

    private fun buatGreeting(): String {
        val jam = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val nama = session.ambilNama()
        return when (jam) {
            in 5..10 -> "Selamat pagi, $nama"
            in 11..14 -> "Selamat siang, $nama"
            in 15..18 -> "Selamat sore, $nama"
            else -> "Selamat malam, $nama"
        }
    }

    private fun tampilkanAvatar(index: Int) {
        when (index) {
            0, 1, 2 -> ivAvatar.setImageResource(R.drawable.ic_profil)
            else -> ivAvatar.setImageResource(R.drawable.ic_profil)
        }
    }

    private fun formatWaktu(millis: Long): String {
        if (millis == 0L) return "-"
        val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        return format.format(Date(millis))
    }

    private fun keluar() {
        session.hapusSesi()
        kembaliKeLogin()
    }

    private fun kembaliKeLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}