package com.example.eatright.data.model

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals")
    val meals: List<Meal>?
)

data class Meal(
    @SerializedName("idMeal")
    val id: String,

    @SerializedName("strMeal")
    val name: String,

    @SerializedName("strMealThumb")
    val imageUrl: String,

    @SerializedName("strCategory")
    val category: String? = null,

    @SerializedName("strArea")
    val area: String? = null,

    @SerializedName("strInstructions")
    val instructions: String? = null
)
