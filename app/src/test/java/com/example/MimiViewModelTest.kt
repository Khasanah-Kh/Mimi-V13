package com.example

import com.example.model.EducationLevel
import com.example.model.LevelStatus
import com.example.viewmodel.MimiViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MimiViewModelTest {

  @Test
  fun testInitialState() {
    val viewModel = MimiViewModel()
    val state = viewModel.uiState.value

    assertEquals("Raka", state.userName)
    assertEquals(7, state.streakDays)
    assertEquals(1250, state.totalXp)
    assertEquals(3, state.progressCurrent)
    assertEquals(5, state.progressTotal)
    assertEquals(4, state.currentLevels.size)
    assertEquals(LevelStatus.DONE, state.currentLevels[0].status)
    assertEquals(LevelStatus.ACTIVE, state.currentLevels[1].status)
    assertEquals(LevelStatus.LOCKED, state.currentLevels[2].status)
    assertEquals(LevelStatus.LOCKED, state.currentLevels[3].status)
  }

  @Test
  fun testChatInitialMessage() {
    val viewModel = MimiViewModel()
    val messages = viewModel.uiState.value.chatMessages

    assertTrue(messages.isNotEmpty())
    assertTrue(messages[0].text.contains("Woy Raka! Planet itu kayak antrian kantin"))
  }

  @Test
  fun testOpenAndCloseChat() {
    val viewModel = MimiViewModel()
    assertEquals(false, viewModel.uiState.value.isChatOpen)

    viewModel.openChat()
    assertEquals(true, viewModel.uiState.value.isChatOpen)

    viewModel.closeChat()
    assertEquals(false, viewModel.uiState.value.isChatOpen)
  }

  @Test
  fun testEducationLevelSwitching() {
    val viewModel = MimiViewModel()
    viewModel.setEducationLevel(EducationLevel.CURHAT)
    assertEquals(EducationLevel.CURHAT, viewModel.uiState.value.selectedEducationLevel)
    assertTrue(viewModel.getSuggestedPrompts().any { it.contains("curhat", ignoreCase = true) || it.contains("capek", ignoreCase = true) })
  }

  @Test
  fun testAllCategoriesHaveDynamicLevels() {
    val viewModel = MimiViewModel()
    EducationLevel.values().forEach { level ->
      viewModel.setEducationLevel(level)
      assertEquals(4, viewModel.uiState.value.currentLevels.size)
      assertEquals(level, viewModel.uiState.value.selectedEducationLevel)
      assertNotNull(viewModel.uiState.value.activeLevelItem)
    }
  }

  @Test
  fun testSourceCitationPresence() {
    val viewModel = MimiViewModel()
    val initialMsg = viewModel.uiState.value.chatMessages.firstOrNull()
    assertNotNull(initialMsg)
    assertNotNull(initialMsg?.sourceCitation)
  }

  @Test
  fun testClassSelectionForLevels() {
    val viewModel = MimiViewModel()

    // Test SD Kelas 1-6
    viewModel.setEducationLevel(EducationLevel.SD)
    assertEquals(6, EducationLevel.SD.classes.size)
    viewModel.setSelectedClass("Kelas 4")
    assertEquals("Kelas 4", viewModel.uiState.value.selectedClass)
    assertTrue(viewModel.uiState.value.userSubtitle.contains("Kelas 4"))

    // Test SMP Kelas 7-9
    viewModel.setEducationLevel(EducationLevel.SMP)
    assertEquals(3, EducationLevel.SMP.classes.size)
    viewModel.setSelectedClass("Kelas 9")
    assertEquals("Kelas 9", viewModel.uiState.value.selectedClass)

    // Test SMA Kelas 10-12
    viewModel.setEducationLevel(EducationLevel.SMA)
    assertEquals(3, EducationLevel.SMA.classes.size)
    viewModel.setSelectedClass("Kelas 11")
    assertEquals("Kelas 11", viewModel.uiState.value.selectedClass)

    // Test SMK Kelas 10-12
    viewModel.setEducationLevel(EducationLevel.SMK)
    assertEquals(3, EducationLevel.SMK.classes.size)
    viewModel.setSelectedClass("Kelas 12")
    assertEquals("Kelas 12", viewModel.uiState.value.selectedClass)
  }
}
