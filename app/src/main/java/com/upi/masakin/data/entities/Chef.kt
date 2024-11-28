package com.upi.masakin.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "chef_table")
data class Chef(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary Key
    val name: String,
    val description: String, // Deskripsi Chef
    val image: Int // Gambar Chef
) : Parcelable




