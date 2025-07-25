package com.example.reciperealm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reciperealm.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1. Siapkan RecyclerView dengan SEMUA resep dari awal
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // 2. Setiap kali halaman tampil, cukup update status favoritnya
        loadFavoritesAndUpdateList()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            recipeList = RecipeData.allRecipes.toMutableList(), // Sumber data utama adalah semua resep
            favoriteRecipeIds = emptySet(), // Awalnya kosong, akan di-update
            onItemClicked = { selectedRecipe ->
                val intent = Intent(activity, RecipeDetailActivity::class.java)
                intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, selectedRecipe.id)
                startActivity(intent)
            },
            onFavoriteClicked = { favoriteRecipe ->
                toggleFavoriteStatus(favoriteRecipe.id)
            }
        )
        binding.recipesRecyclerView.adapter = recipeAdapter
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun loadFavoritesAndUpdateList() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            // Jika user logout, pastikan semua ikon favorit kosong
            if (::recipeAdapter.isInitialized) recipeAdapter.updateFavorites(emptySet())
            return
        }

        // Ambil daftar ID favorit dari Firestore
        Firebase.firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val favoriteIds = document.get("favoriteRecipes") as? List<String>
                // PERBARUI HANYA STATUS FAVORIT, BUKAN SELURUH DAFTAR RESEP
                if (::recipeAdapter.isInitialized) recipeAdapter.updateFavorites(favoriteIds?.toSet() ?: emptySet())
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal memuat status favorit", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleFavoriteStatus(recipeId: String) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "Silakan login untuk menambah favorit", Toast.LENGTH_SHORT).show()
            return
        }

        val userDocRef = Firebase.firestore.collection("users").document(userId)

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            val favoriteRecipes = snapshot.get("favoriteRecipes") as? List<String> ?: listOf()
            if (favoriteRecipes.contains(recipeId)) {
                transaction.update(userDocRef, "favoriteRecipes", FieldValue.arrayRemove(recipeId))
            } else {
                transaction.update(userDocRef, "favoriteRecipes", FieldValue.arrayUnion(recipeId))
            }
        }.addOnSuccessListener {
            // Setelah berhasil, muat ulang status favorit agar ikon terupdate
            loadFavoritesAndUpdateList()
        }
    }

    fun performSearch(query: String) {
        val filteredList = if (query.isEmpty()) {
            RecipeData.allRecipes
        } else {
            RecipeData.allRecipes.filter {
                it.title.lowercase().contains(query.lowercase())
            }
        }
        if (::recipeAdapter.isInitialized) {
            recipeAdapter.filterList(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}