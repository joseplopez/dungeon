package com.game.dungeon.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun signInAnonymously(): String? {
        Log.d("AuthRepository", "Attempting anonymous sign-in...")
        return try {
            val result = auth.signInAnonymously().await()
            val uid = result.user?.uid
            Log.d("AuthRepository", "Sign-in SUCCESS. UID: $uid")
            uid
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign-in ERROR: ${e.message}", e)
            null
        }
    }
}
