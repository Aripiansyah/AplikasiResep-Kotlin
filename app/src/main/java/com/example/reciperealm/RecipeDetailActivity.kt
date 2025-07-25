package com.example.reciperealm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reciperealm.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    companion object {
        const val EXTRA_RECIPE_ID = "extra_recipe_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val recipeId = intent.getStringExtra(EXTRA_RECIPE_ID)

        if (recipeId != null) {
            val recipe = RecipeData.findRecipeById(recipeId)
            if (recipe != null) {
                displayRecipeDetails(recipe)
            }
        }
    }

    private fun displayRecipeDetails(recipe: Recipe) {
        binding.recipeDetailImageView.setImageResource(recipe.imageResId)
        binding.recipeDetailTitleTextView.text = recipe.title
        binding.recipeDetailDescTextView.text = recipe.description
        binding.ingredientsTextView.text = recipe.ingredients
        binding.stepsTextView.text = recipe.steps
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}