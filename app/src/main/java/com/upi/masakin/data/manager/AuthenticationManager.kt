package com.upi.masakin.data.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber

object AuthenticationManager {
    private const val DATABASE_URL = "https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app"

    fun isUserAuthenticated(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser

        Timber.d("Authentication Check:")
        Timber.d("Current user: ${currentUser?.uid}")
        Timber.d("Is anonymous: ${currentUser?.isAnonymous}")
        Timber.d("Provider data: ${currentUser?.providerData?.map { it.providerId }}")
        return currentUser != null && (!currentUser.isAnonymous ||
                currentUser.providerData.any {
                    it.providerId == "fakestore.api" ||
                            it.providerId == "password"
                })
    }

    fun getCurrentUserId(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid.also {
            Timber.d("Getting current user ID: $it")
        }
    }

    fun getFavoriteReference(recipeId: String? = null): DatabaseReference {
        val database = FirebaseDatabase.getInstance(DATABASE_URL)
        val userId = getCurrentUserId() ?: throw IllegalStateException("User not authenticated")

        val reference = if (recipeId != null) {
            database.getReference("users/$userId/favorite_recipes/$recipeId")
        } else {
            database.getReference("users/$userId/favorite_recipes")
        }

        Timber.d("Created database reference: ${reference.path}")
        return reference
    }
}