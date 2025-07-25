package com.example.reciperealm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reciperealm.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setupActionListeners()
    }

    private fun setupActionListeners() {
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val fullName = binding.fullnameEditText.text.toString().trim() // Ambil full name

            // Validasi sederhana
            if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                Toast.makeText(baseContext, "Harap isi semua kolom.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("AUTH_SUCCESS", "createUserWithEmail:success")
                        Toast.makeText(baseContext, "Registrasi berhasil.", Toast.LENGTH_SHORT).show()

                        // Kirim full name ke fungsi penyimpanan
                        saveAdditionalUserData(task.result.user?.uid, email, fullName)

                    } else {
                        Log.w("AUTH_FAIL", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Fungsi sudah diubah untuk menerima fullName
    private fun saveAdditionalUserData(userId: String?, username: String, fullName: String) {
        if (userId == null) return

        val db = Firebase.firestore
        val user = hashMapOf(
            "username" to username,
            "fullName" to fullName
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener { Log.d("FIRESTORE_SUCCESS", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("FIRESTORE_FAIL", "Error writing document", e) }
    }
}