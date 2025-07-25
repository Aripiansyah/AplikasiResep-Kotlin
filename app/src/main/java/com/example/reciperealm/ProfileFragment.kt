package com.example.reciperealm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reciperealm.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Cukup dua baris ini untuk menyiapkan view dengan View Binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Panggil fungsi untuk memuat data dan setup tombol setelah view dibuat
        loadUserData()
        setupLogoutButton()
    }

    private fun loadUserData() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            // Tampilkan email pengguna yang sedang login
            binding.emailTextView.text = currentUser.email

            // Ambil data tambahan (fullName) dari Firestore
            Firebase.firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Ambil data "fullName" dan tampilkan
                        val fullName = document.getString("fullName") ?: "Nama Tidak Ditemukan"
                        binding.fullnameTextView.text = fullName
                    }
                }
        }
    }

    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            // Pindah ke LoginActivity setelah logout
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
            Toast.makeText(activity, "Anda telah logout.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Penting untuk membersihkan binding agar tidak terjadi memory leak
        _binding = null
    }
}