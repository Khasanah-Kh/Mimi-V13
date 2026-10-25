package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val TAG = "UserRepository"

class UserRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private fun requireUserId(): String {
    return auth.currentUser?.uid ?: error("User must be authenticated to perform this operation")
  }

  fun observeProfile(userId: String): Flow<UserProfile?> {
    val docRef = db.collection("users").document(userId)
    return docRef.snapshots().map { snapshot ->
      if (snapshot.exists()) {
        snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
      } else {
        null
      }
    }.catch { e ->
      if (e is Exception) {
        handleFirestoreError(e, OperationType.GET, docRef.path)
      }
      throw e
    }
  }

  suspend fun getProfile(userId: String): UserProfile? {
    val docRef = db.collection("users").document(userId)
    return try {
      val snapshot = docRef.get().await()
      if (snapshot.exists()) {
        snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
      } else {
        null
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  suspend fun saveOrSyncProfile(profile: UserProfile): UserProfile {
    val currentUserId = requireUserId()
    require(profile.userId == currentUserId) { "Cannot modify another user's profile" }
    val docRef = db.collection("users").document(currentUserId)

    return try {
      val existing = docRef.get().await()
      if (!existing.exists()) {
        // Brand new profile creation
        val newProfile = profile.copy(
          userId = currentUserId,
          createdAt = null,
          updatedAt = null
        )
        docRef.set(newProfile).await()
        Log.d(TAG, "Created initial profile in Firestore for user $currentUserId")
        newProfile
      } else {
        // Update existing profile, preserving original createdAt
        val existingCreatedAt = existing.getTimestamp("createdAt")
        val updatedProfile = profile.copy(
          userId = currentUserId,
          createdAt = existingCreatedAt,
          updatedAt = null
        )
        docRef.set(updatedProfile).await()
        Log.d(TAG, "Updated existing profile in Firestore for user $currentUserId")
        updatedProfile
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun updateEducationSelection(level: String, selectedClass: String) {
    val currentUserId = requireUserId()
    val docRef = db.collection("users").document(currentUserId)
    try {
      val existing = docRef.get().await()
      if (existing.exists()) {
        val existingCreatedAt = existing.getTimestamp("createdAt")
        val currentProfile = existing.toObject(UserProfile::class.java) ?: UserProfile(userId = currentUserId)
        val updated = currentProfile.copy(
          selectedEducationLevel = level,
          selectedClass = selectedClass,
          createdAt = existingCreatedAt,
          updatedAt = null
        )
        docRef.set(updated).await()
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun addXpAndProgress(xpGained: Int, levelTitle: String? = null) {
    val currentUserId = requireUserId()
    val docRef = db.collection("users").document(currentUserId)
    try {
      val existing = docRef.get().await()
      if (existing.exists()) {
        val existingCreatedAt = existing.getTimestamp("createdAt")
        val currentProfile = existing.toObject(UserProfile::class.java) ?: UserProfile(userId = currentUserId)
        val updatedCompletedLevels = if (levelTitle != null && !currentProfile.completedLevels.contains(levelTitle)) {
          currentProfile.completedLevels + levelTitle
        } else {
          currentProfile.completedLevels
        }
        val newXp = currentProfile.totalXp + xpGained
        val newProgress = (currentProfile.progressCurrent + 1).coerceAtMost(currentProfile.progressTotal)
        val updated = currentProfile.copy(
          totalXp = newXp,
          progressCurrent = newProgress,
          completedLevels = updatedCompletedLevels,
          createdAt = existingCreatedAt,
          updatedAt = null
        )
        docRef.set(updated).await()
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }
}
