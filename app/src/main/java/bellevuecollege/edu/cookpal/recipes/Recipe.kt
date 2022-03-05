package com.example.testrecipeapi

class Recipe {

    var image: String = "";
    var url: String = "";
    var label: String = "";
    var ingredients: ArrayList<Ingredient> = arrayListOf<Ingredient>();
    var totalTime: Double = 0.0;
    var cuisineType: ArrayList<String> = arrayListOf<String>();
    var mealType: ArrayList<String> = arrayListOf<String>();
    var dishType: ArrayList<String> = arrayListOf<String>();
}