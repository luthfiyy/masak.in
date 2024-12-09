package com.upi.masakin.model

class ArticleCategory {
    enum class ArtikelCategory {
        CookingTips,
        ChefProfile,
        Nutrition,
        KitchenTools,
        CulinaryCulture,
        SpecialRecipe,
        IngredientGuide;

        fun toReadableString(): String {
            return name.replace(Regex("([a-z])([A-Z])"), "$1 $2")
        }
    }
}