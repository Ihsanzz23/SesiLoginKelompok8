package com.example.sesilogin

import android.content.Context
import java.security.MessageDigest
import java.security.SecureRandom

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(NAMA_PREFS, Context.MODE_PRIVATE)
    private val akun = context.getSharedPreferences(NAMA_AKUN, Context.MODE_PRIVATE)

    companion object {
        private const val NAMA_PREFS = "sesi_login"
        private const val NAMA_AKUN = "akun_login"

        // kunci sesi
        private const val KEY_SUDAH_LOGIN = "sudah_login"   // hasil centang "Ingat saya"
        private const val KEY_NAMA = "nama"
        private const val KEY_EMAIL = "email"
        private const val KEY_WAKTU = "waktu_login"
        private const val KEY_AVATAR = "avatar"

        // kunci akun
        private const val KEY_EMAIL_AKUN = "email_akun"
        private const val KEY_SALT = "salt"
        private const val KEY_HASH = "password_hash"
        private const val KEY_EMAIL_TERAKHIR = "email_terakhir"

        // akun demo
        const val EMAIL_DEMO = "kelompok4@gmail.com"
        const val SANDI_DEMO = "123456"

        private const val BATAS_SESSION = 7L * 24L * 60L * 60L * 1000L
    }

    init {
        if (!akun.contains(KEY_HASH)) {
            akun.edit().putString(KEY_EMAIL_AKUN, EMAIL_DEMO).apply()
            simpanPassword(SANDI_DEMO)
        }
    }

    // ================= LOGIN =================

    fun cekLogin(email: String, password: String): Boolean {
        val emailAkun = akun.getString(KEY_EMAIL_AKUN, "") ?: ""
        return email.equals(emailAkun, ignoreCase = true) && cekPassword(password)
    }

    fun simpanSesi(nama: String, email: String, avatarIndex: Int, ingatSaya: Boolean) {
        prefs.edit()
            .putBoolean(KEY_SUDAH_LOGIN, ingatSaya)
            .putString(KEY_NAMA, nama)
            .putString(KEY_EMAIL, email)
            .putLong(KEY_WAKTU, System.currentTimeMillis())
            .putInt(KEY_AVATAR, avatarIndex)
            .apply()
        akun.edit().putString(KEY_EMAIL_TERAKHIR, email).apply()
    }

    // ================= CEK SESI =================

    fun sudahLogin(): Boolean {
        if (!prefs.getBoolean(KEY_SUDAH_LOGIN, false)) return false
        val waktu = ambilWaktuLogin()
        if (waktu == 0L || System.currentTimeMillis() - waktu > BATAS_SESSION) {
            hapusSesi()
            return false
        }
        return true
    }

    fun adaDataProfil(): Boolean = ambilNama().isNotEmpty() && ambilEmail().isNotEmpty()

    fun ambilNama(): String = prefs.getString(KEY_NAMA, "") ?: ""
    fun ambilEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun ambilWaktuLogin(): Long = prefs.getLong(KEY_WAKTU, 0L)
    fun ambilAvatar(): Int = prefs.getInt(KEY_AVATAR, 0)

    fun ambilEmailTerakhir(): String = akun.getString(KEY_EMAIL_TERAKHIR, "") ?: ""

    // ================= LOGOUT =================

    fun hapusSesi() {
        prefs.edit().clear().apply()
    }

    // ================= SANDI (di-hash + salt) =================

    fun simpanPassword(password: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.toHex()
        akun.edit()
            .putString(KEY_SALT, salt)
            .putString(KEY_HASH, hash(salt, password))
            .apply()
    }

    fun cekPassword(password: String): Boolean {
        val salt = akun.getString(KEY_SALT, null) ?: return false
        val tersimpan = akun.getString(KEY_HASH, null) ?: return false
        return tersimpan == hash(salt, password)
    }

    private fun hash(salt: String, password: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest((salt + password).toByteArray(Charsets.UTF_8))
            .toHex()

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
}