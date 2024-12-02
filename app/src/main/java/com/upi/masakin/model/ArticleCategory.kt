package com.upi.masakin.model

class ArticleCategory {
    enum class ArtikelCategory {
        CookingTips,       // Tips Memasak
        ChefProfile,       // Profil Chef
        Nutrition,         // Gizi dan Kesehatan
        KitchenTools,      // Review Peralatan
        CulinaryCulture,   // Budaya Kuliner
        SpecialRecipe,     // Resep Khusus
        IngredientGuide;   // Panduan Bahan

        fun toReadableString(): String {
            return name.replace(Regex("([a-z])([A-Z])"), "$1 $2")
        }
    }
}