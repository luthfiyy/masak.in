package com.upi.masakin.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let {
            Gson().fromJson(it, object : TypeToken<List<String>>() {}.type)
        }
    }
}