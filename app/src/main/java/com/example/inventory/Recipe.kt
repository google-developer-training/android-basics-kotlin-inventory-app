package com.example.inventory

class Recipe(

    val id: Int,
    val title: String,
    val image: String,
    val missedIngredients: List<Ingredient>,
    val usedIngredients: List<Ingredient>,
    val unusedIngredients: List<Ingredient>,
)

data class Ingredient(
    val id: Int,
    val name: String,
    val image: String
)