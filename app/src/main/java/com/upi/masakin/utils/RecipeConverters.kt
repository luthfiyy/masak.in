package com.upi.masakin.utils

import androidx.room.TypeConverter

class RecipeConverters {
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(separator = "||") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        return data?.split("||") ?: emptyList()
    }
}