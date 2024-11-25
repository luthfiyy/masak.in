package com.upi.masakin.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chef_table")
data class Chef(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary Key diperlukan
    val name: String,
    val image: Int
)

