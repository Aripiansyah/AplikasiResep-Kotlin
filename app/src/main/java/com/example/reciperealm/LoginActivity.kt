package com.example.reciperealm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciperealm.databinding.LoginActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // 1. Deklarasikan variabel binding dan Firebase Auth
    private lateinit var binding: LoginActivityBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Inisialisasi Firebase Auth
        auth = Firebase.auth

        // Cek jika user sudah login sebelumnya, langsung ke Dashboard
        checkCurrentUser()

        // Panggil listener
        setupActionListeners()
    }

    // Fungsi untuk cek user yang sedang login
    private fun checkCurrentUser() {
        if (auth.currentUser != null) {
            // Jika sudah ada user yang login, langsung pindah ke Dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
            finish() // Tutup LoginActivity
        }
    }

    private fun setupActionListeners() {
        // Aksi saat tombol login diklik
        binding.loginButton.setOnClickListener {
            // 3. Ambil teks dari EditText saat tombol diklik
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Panggil fungsi login dari Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Jika login berhasil
                        Toast.makeText(baseContext, "Login berhasil.", Toast.LENGTH_SHORT).show()

                        // Pindah ke Dashboard
                        val intent = Intent(this, DashboardActivity::class.java)
                        // Hapus semua activity sebelumnya dari tumpukan
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Jika login gagal
                        Toast.makeText(baseContext, "Login gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Aksi untuk teks "Registrasi"
        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}