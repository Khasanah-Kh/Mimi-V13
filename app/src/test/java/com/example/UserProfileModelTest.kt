package com.example

import com.example.data.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UserProfileModelTest {

  @Test
  fun testUserProfileDefaults() {
    val profile = UserProfile(
      userId = "test-user-123",
      displayName = "Raka Test",
      email = "raka@example.com"
    )

    assertEquals("test-user-123", profile.userId)
    assertEquals("Raka Test", profile.displayName)
    assertEquals("raka@example.com", profile.email)
    assertEquals("SMP", profile.selectedEducationLevel)
    assertEquals("Kelas 8", profile.selectedClass)
    assertEquals(7L, profile.streakDays)
    assertEquals(1250L, profile.totalXp)
    assertNull(profile.createdAt)
    assertNull(profile.updatedAt)
  }

  @Test
  fun testUserProfileCopyPreservesCreatedAt() {
    val initial = UserProfile(
      userId = "user_456",
      displayName = "Budi",
      streakDays = 10L,
      totalXp = 2000L
    )

    val updated = initial.copy(
      totalXp = 2100L,
      streakDays = 11L,
      selectedClass = "Kelas 9"
    )

    assertEquals("user_456", updated.userId)
    assertEquals(2100L, updated.totalXp)
    assertEquals(11L, updated.streakDays)
    assertEquals("Kelas 9", updated.selectedClass)
  }
}
