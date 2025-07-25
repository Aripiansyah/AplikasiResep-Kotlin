package com.example.reciperealm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private var recipeList: MutableList<Recipe>,
    private var favoriteRecipeIds: Set<String>,
    private val onItemClicked: (Recipe) -> Unit,
    private val onFavoriteClicked: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        val titleView: TextView = itemView.findViewById(R.id.recipeTitleTextView)
        val descView: TextView = itemView.findViewById(R.id.recipeDescTextView)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.imageView.setImageResource(recipe.imageResId)
        holder.titleView.text = recipe.title
        holder.descView.text = recipe.description

        if (favoriteRecipeIds.contains(recipe.id)) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite)
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }

        holder.itemView.setOnClickListener {
            onItemClicked(recipe)
        }

        holder.favoriteButton.setOnClickListener {
            onFavoriteClicked(recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun filterList(filteredList: List<Recipe>) {
        recipeList = filteredList.toMutableList()
        notifyDataSetChanged()
    }

    fun updateFavorites(newFavoriteIds: Set<String>) {
        this.favoriteRecipeIds = newFavoriteIds
        notifyDataSetChanged()
    }
}