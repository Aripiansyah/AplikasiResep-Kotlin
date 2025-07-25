package com.example.reciperealm
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reciperealm.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter
    private var currentFavoriteList: List<Recipe> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Hanya setup RecyclerView di sini
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Muat data setiap kali fragment ini aktif
        loadFavoriteRecipes()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            recipeList = mutableListOf(),
            favoriteRecipeIds = emptySet(),
            onItemClicked = {
                val intent = Intent(activity, RecipeDetailActivity::class.java)
                intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, it.id)
                startActivity(intent)
            },
            onFavoriteClicked = {
                toggleFavoriteStatus(it.id)
            }
        )
        binding.favoriteRecipesRecyclerView.adapter = recipeAdapter
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun loadFavoriteRecipes() {
        binding.progressBar.visibility = View.VISIBLE
        binding.favoriteRecipesRecyclerView.visibility = View.GONE
        binding.emptyTextView.visibility = View.GONE

        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            binding.progressBar.visibility = View.GONE
            binding.emptyTextView.visibility = View.VISIBLE
            binding.emptyTextView.text = "Silakan login untuk melihat favorit"
            return
        }

        Firebase.firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = View.GONE
                if (document != null && document.exists()) {
                    val favoriteIds = document.get("favoriteRecipes") as? List<String> ?: listOf()

                    if (favoriteIds.isEmpty()) {
                        binding.emptyTextView.visibility = View.VISIBLE
                        binding.favoriteRecipesRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyTextView.visibility = View.GONE
                        binding.favoriteRecipesRecyclerView.visibility = View.VISIBLE

                        currentFavoriteList = favoriteIds.mapNotNull { RecipeData.findRecipeById(it) }
                        recipeAdapter.filterList(currentFavoriteList)
                        recipeAdapter.updateFavorites(currentFavoriteList.map { it.id }.toSet())
                    }
                } else {
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.favoriteRecipesRecyclerView.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.emptyTextView.visibility = View.VISIBLE
                binding.emptyTextView.text = "Gagal memuat data."
            }
    }

    private fun toggleFavoriteStatus(recipeId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(userId)
            .update("favoriteRecipes", FieldValue.arrayRemove(recipeId))
            .addOnSuccessListener {
                Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                // Panggil lagi untuk refresh
                loadFavoriteRecipes()
            }
    }

    fun performSearch(query: String) {
        val filteredList = if (query.isEmpty()) {
            currentFavoriteList
        } else {
            currentFavoriteList.filter {
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