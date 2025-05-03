package com.example.eatright.data.api

import com.example.eatright.data.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface MealApiService {

    @GET("search.php?f=b") // Sample: get meals starting with 'b'
    suspend fun getMeals(): MealResponse

    @GET("lookup.php")
    suspend fun getMealById(
        @Query("i") mealId: String
    ): MealResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category: String
    ): MealResponse

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): MealResponse



}
