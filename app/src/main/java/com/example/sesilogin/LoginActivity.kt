package com.example.sesilogin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbIngatSaya: CheckBox
    private lateinit var rgAvatar: RadioGroup
    private lateinit var tvPesanMasuk: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)

        if (session.sudahLogin()) {
            bukaDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cbIngatSaya = findViewById(R.id.cbIngatSaya)
        rgAvatar = findViewById(R.id.rgAvatar)
        tvPesanMasuk = findViewById(R.id.tvPesanMasuk)

        etEmail.setText(session.ambilEmailTerakhir())

        findViewById<Button>(R.id.btnMasuk).setOnClickListener { prosesLogin() }
    }

    private fun prosesLogin() {
        val email = etEmail.text.toString().trim()
        val sandi = etPassword.text.toString()

        tvPesanMasuk.text = ""

        if (email.isEmpty()) {
            etEmail.error = getString(R.string.pesan_email_kosong)
            etEmail.requestFocus()
            return
        }
        if (sandi.length < 6) {
            etPassword.error = getString(R.string.pesan_sandi_pendek)
            etPassword.requestFocus()
            return
        }

        if (!session.cekLogin(email, sandi)) {
            tvPesanMasuk.text = getString(R.string.pesan_login_gagal)
            return
        }

        val nama = email.substringBefore("@").replaceFirstChar { it.uppercase() }

        val avatarIndex = rgAvatar.indexOfChild(
            findViewById(rgAvatar.checkedRadioButtonId)
        ).coerceAtLeast(0)

        session.simpanSesi(nama, email, avatarIndex, cbIngatSaya.isChecked)

        bukaDashboard()
    }

    private fun bukaDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}