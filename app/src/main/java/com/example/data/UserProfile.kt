package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val userId: String = "",
  val displayName: String = "",
  val email: String? = null,
  val photoUrl: String? = null,
  val selectedEducationLevel: String = "SMP",
  val selectedClass: String = "Kelas 8",
  val streakDays: Long = 7L,
  val totalXp: Long = 1250L,
  val progressCurrent: Long = 3L,
  val progressTotal: Long = 5L,
  val completedLevels: List<String> = emptyList(),
  val completedQuizzes: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)
