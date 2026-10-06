package com.example.sesilogin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    private lateinit var etPasswordLama: EditText
    private lateinit var etPasswordBaru: EditText
    private lateinit var etKonfirmasi: EditText
    private lateinit var btnSimpan: Button

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_settings
        )

        session = SessionManager(this)

        etPasswordLama =
            findViewById(
                R.id.etPasswordLama
            )

        etPasswordBaru =
            findViewById(
                R.id.etPasswordBaru
            )

        etKonfirmasi =
            findViewById(
                R.id.etKonfirmasi
            )

        btnSimpan =
            findViewById(
                R.id.btnSimpan
            )

        btnSimpan.setOnClickListener {

            ubahPassword()
        }
    }

    private fun ubahPassword() {

        val lama =
            etPasswordLama.text
                .toString()

        val baru =
            etPasswordBaru.text
                .toString()

        val konfirmasi =
            etKonfirmasi.text
                .toString()

        // =================================================
        // VALIDASI
        // =================================================

        if (lama.isEmpty()) {

            etPasswordLama.error =
                "Password lama wajib diisi"

            return
        }

        if (baru.length < 6) {

            etPasswordBaru.error =
                "Password minimal 6 karakter"

            return
        }

        if (baru != konfirmasi) {

            etKonfirmasi.error =
                "Password tidak sama"

            return
        }

        // =================================================
        // CEK PASSWORD LAMA
        // =================================================

        if (!session.cekPassword(lama)) {

            etPasswordLama.error =
                "Password lama salah"

            return
        }

        session.simpanPassword(baru)

        Toast.makeText(
            this,
            "Password berhasil diubah",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}