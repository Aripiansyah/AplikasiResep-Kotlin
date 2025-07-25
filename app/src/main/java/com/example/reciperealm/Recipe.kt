package com.example.reciperealm

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val imageResId: Int,
    val ingredients: String,
    val steps: String
)