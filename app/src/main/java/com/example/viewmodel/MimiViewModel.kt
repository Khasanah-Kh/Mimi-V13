package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BadgeItem
import com.example.model.BottomNavTab
import com.example.model.ChatMessage
import com.example.model.EducationLevel
import com.example.model.LevelItem
import com.example.model.LevelStatus
import com.example.model.MessageSender
import com.example.model.QuizQuestion
import com.example.data.UserProfile
import com.example.data.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "MimiViewModel"

/**
 * Immutable State holding nested education & grade selections,
 * dynamic syllabus levels, chat messages, and gamified progress.
 */
data class MimiUiState(
  val userName: String = "Raka",
  val userEmail: String? = null,
  val isCloudSynced: Boolean = false,
  val userSubtitle: String = "SMP Kelas 8 • Level 12",
  val streakDays: Int = 7,
  val totalXp: Int = 1250,
  val progressCurrent: Int = 3,
  val progressTotal: Int = 5,
  val currentTab: BottomNavTab = BottomNavTab.BERANDA,
  val isChatOpen: Boolean = false,
  // Robust Nested Selection State
  val selectedEducationLevel: EducationLevel = EducationLevel.SMP,
  val selectedClass: String = "Kelas 8",
  val gradeHistory: Map<EducationLevel, String> = mapOf(
    EducationLevel.SD to "Kelas 4",
    EducationLevel.SMP to "Kelas 8",
    EducationLevel.SMA to "Kelas 11",
    EducationLevel.SMK to "Kelas 11",
    EducationLevel.UMUM to "Wawasan",
    EducationLevel.CURHAT to "Teman Curhat"
  ),
  val currentLevels: List<LevelItem> = emptyList(),
  val activeLevelItem: LevelItem? = null,
  val activeLevelTitle: String = "IPA Tata Surya",
  val activeCurriculumBadge: String = "Kurikulum Merdeka 2026",
  // Chat State
  val chatMessages: List<ChatMessage> = emptyList(),
  val isMimiTyping: Boolean = false,
  val latestXpNotification: String? = null,
  // Quiz & Gamification State
  val quizQuestions: List<QuizQuestion> = emptyList(),
  val currentQuizIndex: Int = 0,
  val quizScore: Int = 0,
  val quizFinished: Boolean = false,
  val selectedQuizAnswer: Int? = null,
  val badges: List<BadgeItem> = emptyList()
)

class MimiViewModel(
  private val userRepository: UserRepository? = null,
  private val currentUserId: String? = null,
  initialName: String = "Raka",
  initialEmail: String? = null
) : ViewModel() {

  private val _uiState = MutableStateFlow(
    MimiUiState(
      userName = initialName,
      userEmail = initialEmail
    )
  )
  val uiState: StateFlow<MimiUiState> = _uiState.asStateFlow()

  init {
    val initialLevel = EducationLevel.SMP
    val initialGrade = "Kelas 8"
    val levels = resolveLevelsFor(initialLevel, initialGrade)
    val activeItem = levels.firstOrNull { it.status == LevelStatus.ACTIVE } ?: levels.first()

    _uiState.update {
      it.copy(
        userName = initialName,
        userEmail = initialEmail,
        selectedEducationLevel = initialLevel,
        selectedClass = initialGrade,
        currentLevels = levels,
        activeLevelItem = activeItem,
        activeLevelTitle = activeItem.title,
        userSubtitle = "${initialLevel.label} $initialGrade • Level 12",
        activeCurriculumBadge = getCurriculumBadge(initialLevel, initialGrade)
      )
    }

    initChat(initialLevel, initialGrade, activeItem)
    initQuiz()
    initBadges()

    // Real-time Cloud Persistence sync
    if (userRepository != null && currentUserId != null) {
      viewModelScope.launch {
        try {
          userRepository.observeProfile(currentUserId).collect { cloudProfile ->
            if (cloudProfile != null) {
              val cloudEducationLevel = try {
                EducationLevel.valueOf(cloudProfile.selectedEducationLevel)
              } catch (e: Exception) {
                _uiState.value.selectedEducationLevel
              }
              val cloudGrade = cloudProfile.selectedClass.ifBlank { _uiState.value.selectedClass }
              val updatedLevels = resolveLevelsFor(cloudEducationLevel, cloudGrade)
              val activeItemUpdated = updatedLevels.firstOrNull { it.status == LevelStatus.ACTIVE } ?: updatedLevels.first()

              _uiState.update { current ->
                current.copy(
                  userName = cloudProfile.displayName.ifBlank { current.userName },
                  userEmail = cloudProfile.email ?: current.userEmail,
                  totalXp = cloudProfile.totalXp.toInt(),
                  streakDays = cloudProfile.streakDays.toInt(),
                  progressCurrent = cloudProfile.progressCurrent.toInt(),
                  progressTotal = cloudProfile.progressTotal.toInt(),
                  selectedEducationLevel = cloudEducationLevel,
                  selectedClass = cloudGrade,
                  currentLevels = updatedLevels,
                  activeLevelItem = activeItemUpdated,
                  activeLevelTitle = activeItemUpdated.title,
                  userSubtitle = "${cloudEducationLevel.label} $cloudGrade • Level 12",
                  activeCurriculumBadge = getCurriculumBadge(cloudEducationLevel, cloudGrade),
                  isCloudSynced = true
                )
              }
            } else {
              // Create initial profile in Firestore
              val newProfile = UserProfile(
                userId = currentUserId,
                displayName = initialName,
                email = initialEmail,
                selectedEducationLevel = _uiState.value.selectedEducationLevel.name,
                selectedClass = _uiState.value.selectedClass,
                streakDays = _uiState.value.streakDays.toLong(),
                totalXp = _uiState.value.totalXp.toLong(),
                progressCurrent = _uiState.value.progressCurrent.toLong(),
                progressTotal = _uiState.value.progressTotal.toLong(),
                completedLevels = emptyList(),
                completedQuizzes = 0L
              )
              userRepository.saveOrSyncProfile(newProfile)
              _uiState.update { it.copy(isCloudSynced = true) }
            }
          }
        } catch (e: Exception) {
          Log.e(TAG, "Failed syncing user profile with Firestore", e)
        }
      }
    }
  }

  /**
   * Step 1 of Nested Selection: Choose Category (SD, SMP, SMA, SMK, Umum, Curhat)
   * Restores the remembered grade for this category (or defaults safely).
   */
  fun setEducationLevel(level: EducationLevel) {
    val rememberedGrade = _uiState.value.gradeHistory[level]
    val validatedGrade = if (rememberedGrade != null && level.classes.contains(rememberedGrade)) {
      rememberedGrade
    } else {
      level.classes.firstOrNull() ?: "General"
    }

    val updatedLevels = resolveLevelsFor(level, validatedGrade)
    val activeItem = updatedLevels.firstOrNull { it.status == LevelStatus.ACTIVE } ?: updatedLevels.first()

    _uiState.update { current ->
      val updatedHistory = current.gradeHistory.toMutableMap()
      updatedHistory[level] = validatedGrade

      current.copy(
        selectedEducationLevel = level,
        selectedClass = validatedGrade,
        gradeHistory = updatedHistory,
        currentLevels = updatedLevels,
        activeLevelItem = activeItem,
        activeLevelTitle = activeItem.title,
        userSubtitle = "${level.label} $validatedGrade • Level 12",
        activeCurriculumBadge = getCurriculumBadge(level, validatedGrade)
      )
    }

    sendMimiGreeting(level, validatedGrade, activeItem)

    if (userRepository != null) {
      viewModelScope.launch {
        try {
          userRepository.updateEducationSelection(level.name, validatedGrade)
        } catch (e: Exception) {
          Log.w(TAG, "Failed to persist education selection to cloud", e)
        }
      }
    }
  }

  /**
   * Step 2 of Nested Selection: Choose Specific Grade Level
   * (e.g. Kelas 1-6 for SD, Kelas 7-9 for SMP, Kelas 10-12 for SMA/SMK).
   */
  fun setSelectedClass(grade: String) {
    val level = _uiState.value.selectedEducationLevel
    if (!level.classes.contains(grade)) return // Validation guard

    val updatedLevels = resolveLevelsFor(level, grade)
    val activeItem = updatedLevels.firstOrNull { it.status == LevelStatus.ACTIVE } ?: updatedLevels.first()

    _uiState.update { current ->
      val updatedHistory = current.gradeHistory.toMutableMap()
      updatedHistory[level] = grade

      current.copy(
        selectedClass = grade,
        gradeHistory = updatedHistory,
        currentLevels = updatedLevels,
        activeLevelItem = activeItem,
        activeLevelTitle = activeItem.title,
        userSubtitle = "${level.label} $grade • Level 12",
        activeCurriculumBadge = getCurriculumBadge(level, grade)
      )
    }

    if (userRepository != null) {
      viewModelScope.launch {
        try {
          userRepository.updateEducationSelection(level.name, grade)
        } catch (e: Exception) {
          Log.w(TAG, "Failed to persist grade selection to cloud", e)
        }
      }
    }
  }

  fun selectLevelItem(levelItem: LevelItem) {
    _uiState.update {
      it.copy(
        activeLevelItem = levelItem,
        activeLevelTitle = levelItem.title,
        isChatOpen = true
      )
    }
  }

  fun setTab(tab: BottomNavTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  fun openChat() {
    _uiState.update { it.copy(isChatOpen = true) }
  }

  // Firebase AI Logic - Gemini 3.8 Flash model (lazily initialized)
  private val geminiModel by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash"
    )
  }

  fun closeChat() {
    _uiState.update { it.copy(isChatOpen = false) }
  }

  fun dismissXpNotification() {
    _uiState.update { it.copy(latestXpNotification = null) }
  }

  fun sendMessage(userText: String) {
    if (userText.isBlank()) return

    val userMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      sender = MessageSender.USER,
      text = userText,
      timestamp = getCurrentTime()
    )

    _uiState.update { state ->
      state.copy(
        chatMessages = state.chatMessages + userMsg,
        isMimiTyping = true
      )
    }

    viewModelScope.launch {
      val level = _uiState.value.selectedEducationLevel
      val grade = _uiState.value.selectedClass
      val topic = _uiState.value.activeLevelTitle

      val systemContext = """
        Kamu adalah MIMI, maskot rubah cyber pintar, asyik, dan suportif di aplikasi belajar "MIMI V13".
        Profil Siswa:
        - Nama: ${_uiState.value.userName}
        - Jenjang & Kelas: ${level.label} - $grade
        - Topik / Materi Aktif: $topic

        Gaya & Karakter MIMI:
        - Bahasa: Santai, gaul, akrab kayak kakak kelas idaman ('Woy Raka!', 'Santuy bro/sis 😎', 'Gini logikanya...').
        - Jika topik CURHAT (kesehatan mental / stres / burnout / motivasi): Dengarkan dengan tulus dan penuh kasih sayang, validasi perasaannya terlebih dahulu, jangan pernah menggurui atau meremehkan masalah siswa, berikan saran praktis yang menenangkan.
        - Jika materi akademik (SD, SMP, SMA, SMK, Umum): Jelaskan konsep yang rumit dengan perumpamaan / analogi seru anak muda (dunia game, jajan kantin, superhero, dll) agar langsung paham.
        - Selalu sertakan kutipan sumber buku teks resmi atau jurnal ilmiah terpercaya di baris paling akhir dengan format:
          [Sumber: Buku Teks Kemendikbudristek Kurikulum Merdeka / Jurnal Terkait]
        - Berikan satu pertanyaan pemantik atau tantangan mini di akhir agar siswa semakin tertarik!
        - Jaga panjang respon sekitar 2 sampai 4 paragraf padat dan nyaman dibaca di layar HP 9:16.
      """.trimIndent()

      val prompt = """
        $systemContext

        Pesan dari ${_uiState.value.userName}:
        $userText
      """.trimIndent()

      val mimiMsgId = UUID.randomUUID().toString()
      var geminiSuccess = false

      try {
        val accumulatedText = StringBuilder()
        var hasAddedPlaceholder = false

        // Realistic coroutine timeout window (60s) for model inference
        withTimeout(60_000L) {
          geminiModel.generateContentStream(prompt).collect { chunk ->
            val piece = chunk.text.orEmpty()
            if (piece.isNotEmpty()) {
              if (!hasAddedPlaceholder) {
                hasAddedPlaceholder = true
                val initialMsg = ChatMessage(
                  id = mimiMsgId,
                  sender = MessageSender.MIMI,
                  text = piece,
                  timestamp = getCurrentTime()
                )
                _uiState.update { state ->
                  state.copy(
                    chatMessages = state.chatMessages + initialMsg,
                    isMimiTyping = false
                  )
                }
              }
              accumulatedText.append(piece)
              val currentFullText = accumulatedText.toString()
              _uiState.update { state ->
                val updatedList = state.chatMessages.map { msg ->
                  if (msg.id == mimiMsgId) msg.copy(text = currentFullText) else msg
                }
                state.copy(chatMessages = updatedList)
              }
            }
          }
        }

        val fullResponse = accumulatedText.toString().trim()
        if (fullResponse.isNotBlank()) {
          geminiSuccess = true
          val citation = extractOrBuildCitation(fullResponse, level, grade, topic)
          val cleanText = cleanCitationFromText(fullResponse)
          val xpGain = 50
          val newXp = _uiState.value.totalXp + xpGain

          _uiState.update { state ->
            val updatedList = state.chatMessages.map { msg ->
              if (msg.id == mimiMsgId) {
                msg.copy(
                  text = cleanText,
                  xpAwarded = xpGain,
                  sourceCitation = citation
                )
              } else msg
            }
            state.copy(
              chatMessages = updatedList,
              isMimiTyping = false,
              totalXp = newXp,
              latestXpNotification = "+$xpGain XP Gemini AI! ⚡ Total: $newXp XP"
            )
          }
        }
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "Gemini generation timed out", e)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e(TAG, "Gemini generation fallback to local engine", e)
      }

      // If Gemini did not succeed (e.g. offline testing, timeout, or network issue), use local curriculum response
      if (!geminiSuccess) {
        delay(380)
        val (reply, xp, citation) = generateMimiResponse(
          userText,
          level,
          grade,
          topic
        )
        val mimiMsg = ChatMessage(
          id = mimiMsgId,
          sender = MessageSender.MIMI,
          text = reply,
          timestamp = getCurrentTime(),
          xpAwarded = xp,
          sourceCitation = citation
        )

        _uiState.update { state ->
          val newXp = state.totalXp + xp
          val updatedMessages = if (state.chatMessages.any { it.id == mimiMsgId }) {
            state.chatMessages.map { if (it.id == mimiMsgId) mimiMsg else it }
          } else {
            state.chatMessages + mimiMsg
          }
          state.copy(
            chatMessages = updatedMessages,
            isMimiTyping = false,
            totalXp = newXp,
            latestXpNotification = "+$xp XP Masuk! ⚡ Total: $newXp XP"
          )
        }
      }
    }
  }

  private fun extractOrBuildCitation(
    text: String,
    level: EducationLevel,
    grade: String,
    topic: String
  ): String {
    val regex = Regex("""\[Sumber:\s*(.+?)\]""", RegexOption.IGNORE_CASE)
    val match = regex.find(text)
    return if (match != null) {
      "📖 " + match.groupValues[1].trim()
    } else {
      when (level) {
        EducationLevel.CURHAT -> "📖 Buku Bimbingan Konseling Remaja & Jurnal Psikologi Pendidikan"
        EducationLevel.SD -> "📖 Buku Tematik Terpadu SD/MI $grade Kemendikbudristek"
        EducationLevel.SMP -> "📖 Buku Teks Utama SMP $grade Kemendikbudristek & Kurikulum Merdeka"
        EducationLevel.SMA -> "📖 Buku Siswa SMA $grade Kemendikbudristek & Referensi Sains Terapan"
        EducationLevel.SMK -> "📖 Modul Kejuruan Vokasi SMK $grade & Standar Industri"
        EducationLevel.UMUM -> "📖 Jurnal Ilmiah Populer, Ensiklopedia Sains & Edukasi Terpadu"
      }
    }
  }

  private fun cleanCitationFromText(text: String): String {
    return text.replace(Regex("""\[Sumber:\s*(.+?)\]""", RegexOption.IGNORE_CASE), "").trim()
  }

  fun getSuggestedPrompts(): List<String> {
    val level = _uiState.value.selectedEducationLevel
    val grade = _uiState.value.selectedClass

    return when (level) {
      EducationLevel.CURHAT -> when (grade) {
        "Burnout & Stres" -> listOf(
          "Kak, rasanya mau nangis tugas numpuk banget 🥺",
          "Gimana cara ngilangin cemas pas malem?",
          "Boleh izin istirahat dulu gak hari ini?",
          "Kepalaku berat banget mikirin masa depan..."
        )
        "Motivasi Belajar" -> listOf(
          "Lagi ngerasa gak sepinter temen sekelas 💔",
          "Kasih kata-kata penyemangat dong MIMI ✨",
          "Gimana cara biar gak males belajar lagi?",
          "Aku takut ngecewain orang tua..."
        )
        else -> listOf(
          "Kak, boleh curhat santai sebentar? 🤗",
          "Temen sekelompok gak ada yang ngerjain tugas 😤",
          "Lagi kesel sama hari ini...",
          "Makasih ya udah selalu dengerin Raka ❤️"
        )
      }
      EducationLevel.SD -> listOf(
        "Kenapa pelangi warnanya ada 7? 🌈",
        "Bumi muter kok kita gak pusing? 🌍",
        "Trik kilat hafal perkalian matematika! 🔢",
        "Hewan apa yang paling cepet lari di bumi? 🐆"
      )
      EducationLevel.SMP -> when (grade) {
        "Kelas 7" -> listOf(
          "Cara gampang operasi bilangan bulat negatif 📐",
          "Zat dan perubahannya bedanya apa sih? 🧪",
          "Struktur teks deskripsi yang bener gimana? 📝",
          "Kenapa zaman praaksara berakhir? 🏺"
        )
        "Kelas 9" -> listOf(
          "Rumus fungsi kuadrat dan titik puncak 📈",
          "Gimana cara kerja listrik dinamis di rumah? ⚡",
          "Bioteknologi konvensional vs modern 🧬",
          "Dampak globalisasi positif & negatif 🌐"
        )
        else -> listOf(
          "Kenapa Pluto dicoret dari daftar planet? 🪐",
          "Berapa panasnya inti Matahari? ☀️",
          "Hukum Kepler 1, 2, 3 maksudnya apa? 🚀",
          "Jurus kilat aljabar suku sejenis 📐"
        )
      }
      EducationLevel.SMA -> when (grade) {
        "Kelas 10" -> listOf(
          "Cara hitung resultan vektor fisika 🎯",
          "Struktur atom dan ikatan kimia kovalen 🧪",
          "Hukum Newton gerak benda miring ⚖️",
          "Persamaan eksponen dan logaritma 📊"
        )
        "Kelas 12" -> listOf(
          "Bocoran tipe soal HOTS TPS UTBK-SNBT 🎯",
          "Integral substitusi dan parsial cepet 📈",
          "Teori relativitas khusus Einstein E=mc² ⚛️",
          "Sintesis protein: transkripsi & translasi DNA 🧬"
        )
        else -> listOf(
          "Termodinamika: Hukum 1 & 2 dan Entropi ⚙️",
          "Gelombang mekanik dan bunyi Doppler 🌊",
          "Struktur jaringan tumbuhan dan fotosintesis 🌿",
          "Limit fungsi aljabar dan turunan dasar 📐"
        )
      }
      EducationLevel.SMK -> listOf(
        "Penerapan logika IoT & sensor ESP32 industri 🔌",
        "Standar K3 keselamatan kerja bengkel / lab ⚠️",
        "Troubleshooting rangkaian motor & hidrolik ⚙️",
        "Tips menyusun portofolio magang industri top 💼"
      )
      EducationLevel.UMUM -> listOf(
        "Misteri Lubang Hitam (Black Hole) & Horizon 🌌",
        "Metode belajar Feynman agar paham konsep apapun 🧠",
        "Dasar literasi finansial & compound interest 📈",
        "Cara melatih nalar kritis anti-hoaks medsos 💡"
      )
    }
  }

  fun answerQuiz(optionIndex: Int) {
    val state = _uiState.value
    if (state.selectedQuizAnswer != null || state.quizFinished) return

    val currentQ = state.quizQuestions.getOrNull(state.currentQuizIndex) ?: return
    val isCorrect = optionIndex == currentQ.correctIndex
    val addedXp = if (isCorrect) 50 else 10

    _uiState.update {
      it.copy(
        selectedQuizAnswer = optionIndex,
        quizScore = if (isCorrect) it.quizScore + 1 else it.quizScore,
        totalXp = it.totalXp + addedXp,
        latestXpNotification = if (isCorrect) "+50 XP Jawaban Benar! 🔥" else "+10 XP Terus Belajar!"
      )
    }

    viewModelScope.launch {
      delay(1200)
      if (state.currentQuizIndex + 1 < state.quizQuestions.size) {
        _uiState.update {
          it.copy(
            currentQuizIndex = it.currentQuizIndex + 1,
            selectedQuizAnswer = null
          )
        }
      } else {
        _uiState.update { it.copy(quizFinished = true) }
      }
    }
  }

  fun restartQuiz() {
    _uiState.update {
      it.copy(
        currentQuizIndex = 0,
        quizScore = 0,
        quizFinished = false,
        selectedQuizAnswer = null
      )
    }
  }

  fun challengeRival() {
    val xpGain = 75
    _uiState.update {
      it.copy(
        totalXp = it.totalXp + xpGain,
        latestXpNotification = "Menang Lawan Arya! +$xpGain XP 🏆"
      )
    }
  }

  /**
   * Generates grade-specific level syllabus dynamically for the chosen category and grade.
   */
  private fun resolveLevelsFor(level: EducationLevel, grade: String): List<LevelItem> {
    return when (level) {
      EducationLevel.SD -> when (grade) {
        "Kelas 1", "Kelas 2", "Kelas 3" -> listOf(
          LevelItem(1, "Berhitung Ceria 1-100", "Matematika SD", 100, LevelStatus.DONE, "Selesai • Penjumlahan & pengurangan ceria", "🔢", level),
          LevelItem(2, "Mengenal Hewan & Tumbuhan", "IPAS Dasar", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🐾", level, "Habitat & jenis hewan"),
          LevelItem(3, "Membaca Cerita Bergambar", "Bahasa Indonesia", 120, LevelStatus.LOCKED, "Terkunci • Kosakata & suku kata", "📚", level),
          LevelItem(4, "Pancasila Sahabat Cilik", "Pendidikan Karakter", 150, LevelStatus.LOCKED, "Terkunci • Gotong royong & sopan santun", "⭐", level)
        )
        else -> listOf(
          LevelItem(1, "Operasi Hitung & Pecahan", "Matematika Kelas 4-6", 100, LevelStatus.DONE, "Selesai • KPK, FPB & pecahan senilai", "🔢", level),
          LevelItem(2, "Ekosistem & Gaya Gerak", "IPAS Tingkat Lanjut", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🌿", level, "Rantai makanan & magnet"),
          LevelItem(3, "Teks Narasi & Puisi", "Bahasa Indonesia", 120, LevelStatus.LOCKED, "Terkunci • Paragraf utama & rima puisi", "📖", level),
          LevelItem(4, "Kerajaan Nusantara & Peta", "Sejarah & Geografi SD", 150, LevelStatus.LOCKED, "Terkunci • Majapahit, Sriwijaya & peta pulau", "🗺️", level)
        )
      }
      EducationLevel.SMP -> when (grade) {
        "Kelas 7" -> listOf(
          LevelItem(1, "Bilangan Bulat & Pecahan", "Matematika SMP 7", 100, LevelStatus.DONE, "Selesai • Garis bilangan & FPB", "📐", level),
          LevelItem(2, "Klasifikasi Materi & Zat", "IPA Fisika-Kimia 7", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🧪", level, "Wujud zat & kalor"),
          LevelItem(3, "Teks Deskripsi & Cerita Fantasi", "Bahasa Indonesia 7", 120, LevelStatus.LOCKED, "Terkunci • Struktur cerpen & majas", "📝", level),
          LevelItem(4, "Masa Praaksara Indonesia", "IPS Sejarah 7", 150, LevelStatus.LOCKED, "Terkunci • Zaman batu & perundagian", "🏺", level)
        )
        "Kelas 9" -> listOf(
          LevelItem(1, "Fungsi Kuadrat & Peluang", "Matematika SMP 9", 100, LevelStatus.DONE, "Selesai • Grafik parabola & rumus diskriminan", "📊", level),
          LevelItem(2, "Listrik Dinamis & Bioteknologi", "IPA Terapan SMP 9", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "⚡", level, "Hukum Ohm & fermentasi"),
          LevelItem(3, "Teks Diskusi & Pidato", "Bahasa Indonesia 9", 120, LevelStatus.LOCKED, "Terkunci • Argumen pro-kontra & retorika", "🎙️", level),
          LevelItem(4, "Kemerdekaan & Kerjasama Dunia", "IPS Sejarah 9", 150, LevelStatus.LOCKED, "Terkunci • Proklamasi & diplomasi PBB", "🌏", level)
        )
        else -> listOf(
          LevelItem(1, "Aljabar & Persamaan Linear", "Matematika SMP 8", 100, LevelStatus.DONE, "Selesai • Suku sejenis & SPLDV", "📐", level),
          LevelItem(2, "IPA Tata Surya", "IPA Terpadu Kelas 8", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🪐", level, "Tata Surya & revolusi planet"),
          LevelItem(3, "Bahasa Indonesia - Teks Eksplanasi", "Bahasa Indonesia 8", 120, LevelStatus.LOCKED, "Terkunci • Sebab-akibat fenomena alam", "📖", level),
          LevelItem(4, "Sejarah Pergerakan Nasional", "IPS Sejarah 8", 150, LevelStatus.LOCKED, "Terkunci • Budi Utomo & Sumpah Pemuda", "🏛️", level)
        )
      }
      EducationLevel.SMA -> when (grade) {
        "Kelas 10" -> listOf(
          LevelItem(1, "Vektor & Persamaan Eksponen", "Matematika SMA 10", 100, LevelStatus.DONE, "Selesai • Geometri analitis & logaritma", "📊", level),
          LevelItem(2, "Hukum Newton & Stoikiometri", "Fisika-Kimia SMA 10", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "⚛️", level, "Gaya gesek & konsep mol"),
          LevelItem(3, "Teks Anekdot & Negosiasi", "Bahasa Indonesia 10", 120, LevelStatus.LOCKED, "Terkunci • Kritik sosial & kompromi", "💬", level),
          LevelItem(4, "Litosfer & Dinamika Penduduk", "Geografi & Sosiologi 10", 150, LevelStatus.LOCKED, "Terkunci • Tektonik lempeng & demografi", "🌋", level)
        )
        "Kelas 12" -> listOf(
          LevelItem(1, "Integral & Kaidah Pencacahan", "Matematika Lanjut 12", 100, LevelStatus.DONE, "Selesai • Luas daerah integral & permutasi", "📈", level),
          LevelItem(2, "Relativitas & Fisika Kuantum", "Fisika Modern 12", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🔭", level, "Foton & dilatasi waktu"),
          LevelItem(3, "Genetika & Bioteknologi Modern", "Biologi Sel 12", 120, LevelStatus.LOCKED, "Terkunci • Rekayasa genetika & CRISPR", "🧬", level),
          LevelItem(4, "Strategi Lolos SNBT / UTBK PTN", "Bimbel Intensif", 150, LevelStatus.LOCKED, "Terkunci • Bedah soal TPS & literasi", "🎯", level)
        )
        else -> listOf(
          LevelItem(1, "Fungsi Kuadrat & Matriks", "Matematika SMA 11", 100, LevelStatus.DONE, "Selesai • Determinan & invers matriks", "📊", level),
          LevelItem(2, "Termodinamika & Gelombang", "Fisika Kelas 11", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "⚛️", level, "Siklus Carnot & efek Doppler"),
          LevelItem(3, "Sistem Organ & Imunologi", "Biologi Manusia 11", 120, LevelStatus.LOCKED, "Terkunci • Sirkulasi darah & antibodi", "🫀", level),
          LevelItem(4, "Larutan Asam Basa & Titrasi", "Kimia Larutan 11", 150, LevelStatus.LOCKED, "Terkunci • pH meter & indikator alami", "🧪", level)
        )
      }
      EducationLevel.SMK -> listOf(
        LevelItem(1, "Dasar K3 & Keselamatan Kerja", "Dasar Kejuruan", 100, LevelStatus.DONE, "Selesai • Standar SOP bengkel & industri", "⚠️", level),
        LevelItem(2, "Logika Pemrograman & IoT Industri", "Teknologi & Otomasi", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🔌", level, "Mikrokontroler & sensor SCADA"),
        LevelItem(3, "Sistem Mekanikal & Hidrolik", "Teknik Mesin & Elektro", 120, LevelStatus.LOCKED, "Terkunci • Pemeliharaan mesin & torsi", "⚙️", level),
        LevelItem(4, "Portofolio Siswa & PKL Industri", "Persiapan Karir", 150, LevelStatus.LOCKED, "Terkunci • Sertifikasi kejuruan BNSP", "💼", level)
      )
      EducationLevel.UMUM -> listOf(
        LevelItem(1, "Teknik Belajar Feynman", "Metakognisi & Riset", 100, LevelStatus.DONE, "Selesai • Menguasai topik rumit dalam 20 menit", "🧠", level),
        LevelItem(2, "Kosmologi & Black Hole", "Sains Populer Dunia", 50, LevelStatus.ACTIVE, "Tap untuk belajar sama MIMI", "🌌", level, "Lubang hitam & gravitasi Einstein"),
        LevelItem(3, "Literasi Finansial & Investasi", "Manajemen Keuangan", 120, LevelStatus.LOCKED, "Terkunci • Bunga majemuk & diversifikasi", "📈", level),
        LevelItem(4, "Critical Thinking & Nalar Kritis", "Filsafat & Logika", 150, LevelStatus.LOCKED, "Terkunci • Deteksi logical fallacies", "💡", level)
      )
      EducationLevel.CURHAT -> when (grade) {
        "Burnout & Stres" -> listOf(
          LevelItem(1, "Mengakui Rasa Lelah Fisik & Jiwa", "Ruang Tenang", 100, LevelStatus.DONE, "Selesai • Istirahat tanpa rasa bersalah", "🕯️", level),
          LevelItem(2, "Melepaskan Beban & Overthinking", "Solusi Hangat", 50, LevelStatus.ACTIVE, "Tap untuk curhat santai sama MIMI", "💙", level, "Pernapasan rileks & grounding"),
          LevelItem(3, "Mengurai Tekanan Tugas Sekolah", "Manajemen Ekspektasi", 120, LevelStatus.LOCKED, "Terkunci • Menolak tugas di luar batas diri", "🌿", level),
          LevelItem(4, "Menjaga Kesehatan Mental Siswa", "Self-Care Rutin", 150, LevelStatus.LOCKED, "Terkunci • Tidur lelap & pikiran damai", "✨", level)
        )
        "Motivasi Belajar" -> listOf(
          LevelItem(1, "Menemukan Kembali Semangat Belajar", "Motivasi Dari Dalam", 100, LevelStatus.DONE, "Selesai • Mengingat mimpi masa depanmu", "🔥", level),
          LevelItem(2, "Bangkit Dari Nilai Kurang Memuaskan", "Growth Mindset", 50, LevelStatus.ACTIVE, "Tap untuk curhat santai sama MIMI", "🌱", level, "Nilai rapor bukan harga dirimu"),
          LevelItem(3, "Menghilangkan Rasa Takut Ujian", "Ketenangan Jiwa", 120, LevelStatus.LOCKED, "Terkunci • Afirmasi positif sebelum tes", "🛡️", level),
          LevelItem(4, "Fokus Pada Proses, Bukan Angka", "Mindful Student", 150, LevelStatus.LOCKED, "Terkunci • Menjadi versi terbaik diri sendiri", "⭐", level)
        )
        else -> listOf(
          LevelItem(1, "Tempat Cerita Tanpa Dihakimi", "Ruang Teman Curhat", 100, LevelStatus.DONE, "Selesai • Cerita apa saja, MIMI dengerin", "🛋️", level),
          LevelItem(2, "Mengatasi Masalah Teman & Sekolah", "Solusi Persahabatan", 50, LevelStatus.ACTIVE, "Tap untuk curhat santai sama MIMI", "🤝", level, "Menghadapi drama pertemanan"),
          LevelItem(3, "Membangun Rasa Percaya Diri", "Self-Love Siswa", 120, LevelStatus.LOCKED, "Terkunci • Berhenti membandingkan diri", "💖", level),
          LevelItem(4, "Menemukan Ketenangan Setiap Hari", "Inner Peace", 150, LevelStatus.LOCKED, "Terkunci • Bersyukur & tersenyum hari ini", "☀️", level)
        )
      }
    }
  }

  private fun getCurriculumBadge(level: EducationLevel, grade: String): String {
    return when (level) {
      EducationLevel.SD -> "Kurikulum Merdeka SD • Fase A/B/C"
      EducationLevel.SMP -> "Kurikulum Merdeka SMP • Fase D"
      EducationLevel.SMA -> "Kurikulum Merdeka SMA • Fase E/F"
      EducationLevel.SMK -> "Kurikulum Vokasi SMK • Link & Match Industri"
      EducationLevel.UMUM -> "Lifelong Learning & Literasi Ilmiah"
      EducationLevel.CURHAT -> "Layanan Dukungan & Konseling Siswa MIMI"
    }
  }

  private fun sendMimiGreeting(level: EducationLevel, className: String, activeLevel: LevelItem) {
    val greetingText = when (level) {
      EducationLevel.CURHAT ->
        "Halo Raka ❤️ Mode '$className' sekarang aktif. Lagi ngerasa tertekan, capek sekolah, atau butuh teman ngobrol? Cerita santai aja ya, MIMI siap dengerin tanpa nge-judge sama sekali!"
      EducationLevel.SD ->
        "Halo adik Raka! 🎒 Selamat datang di $className! Modul aktif kita: '${activeLevel.title}'. Mau tanya hal-hal ajaib tentang alam sekitar atau berhitung ceria? Tanya MIMI yuk!"
      EducationLevel.SMP ->
        "Woy Raka! Mode $className aktif nih 😎 Modul aktif: '${activeLevel.title}'. Siap kupas tuntas materi dari buku kurikulum resmi & fakta jurnal terpercaya!"
      EducationLevel.SMA ->
        "Halo Raka! Jenjang $className siap 🎓 Modul kita: '${activeLevel.title}'. Disesuaikan dengan standar silabus Kemendikbud & jurnal ilmiah internasional. Siap bedah konsep apa hari ini?"
      EducationLevel.SMK ->
        "Semangat Raka! Mode $className (Vokasi Terapan) aktif 🛠️ Modul aktif: '${activeLevel.title}'. Kita bahas penerapan nyata di industri dan sains teknik!"
      EducationLevel.UMUM ->
        "Halo Raka! Selamat datang di Pengetahuan Umum & Lifelong Learning 🌐 Modul aktif: '${activeLevel.title}'. Siap eksplorasi wawasan luas dan sains populer dunia!"
    }

    val greetingMsg = ChatMessage(
      id = UUID.randomUUID().toString(),
      sender = MessageSender.MIMI,
      text = greetingText,
      timestamp = getCurrentTime(),
      sourceCitation = if (level == EducationLevel.CURHAT) "📚 Sahabat & Konseling Siswa MIMI" else "📚 Kurikulum Merdeka & Jurnal Referensi Resmi"
    )
    _uiState.update { it.copy(chatMessages = it.chatMessages + greetingMsg) }
  }

  private fun initChat(level: EducationLevel, className: String, activeLevel: LevelItem) {
    val initialMsg = ChatMessage(
      id = "mimi_init",
      sender = MessageSender.MIMI,
      text = "Woy Raka! Planet itu kayak antrian kantin, semua muterin matahari 😎 Mau bahas apa kita hari ini di Bab ${activeLevel.title}?",
      timestamp = getCurrentTime(),
      sourceCitation = "📖 Buku IPA SMP Kelas 8 Kemendikbudristek & NASA Solar System Exploration"
    )
    _uiState.update { it.copy(chatMessages = listOf(initialMsg)) }
  }

  private fun initQuiz() {
    val questions = listOf(
      QuizQuestion(
        id = 1,
        question = "Planet apa yang punya cincin paling megah dan indah di Tata Surya?",
        options = listOf("Jupiter", "Saturnus", "Uranus", "Neptunus"),
        correctIndex = 1,
        explanation = "Saturnus punya sistem cincin spektakuler yang terdiri dari milyaran bongkahan es dan debu! (Sumber: Buku IPA Terpadu Kemendikbud)"
      ),
      QuizQuestion(
        id = 2,
        question = "Mengapa Pluto pada tahun 2006 diklasifikasikan ulang menjadi 'Planet Kerdil' oleh IAU?",
        options = listOf(
          "Ukurannya terlalu panas",
          "Belum membersihkan orbitnya dari benda lain",
          "Warnanya berubah jadi hijau",
          "Keluar dari Tata Surya"
        ),
        correctIndex = 1,
        explanation = "Resolusi Majelis Umum IAU 2006 di Praha: Syarat planet penuh harus membersihkan lingkungan orbitnya. Orbit Pluto masih berpotongan dengan Sabuk Kuiper!"
      ),
      QuizQuestion(
        id = 3,
        question = "Planet terpanas di tata surya bukanlah Merkurius, melainkan...",
        options = listOf("Mars", "Venus", "Bumi", "Matahari"),
        correctIndex = 1,
        explanation = "Venus mencapai 465°C karena efek rumah kaca ekstrem atmosfer CO2 yang super tebal! (Jurnal Astronomi & Astrofisika)"
      ),
      QuizQuestion(
        id = 4,
        question = "Sederhanakan bentuk aljabar: 4x + 7 - 2x + 3",
        options = listOf("2x + 10", "6x + 10", "2x + 4", "6x + 4"),
        correctIndex = 0,
        explanation = "Kumpulin suku sejenis: (4x - 2x) + (7 + 3) = 2x + 10. (Buku Matematika SMP Kelas 8 Kurikulum Merdeka)"
      )
    )
    _uiState.update { it.copy(quizQuestions = questions) }
  }

  private fun initBadges() {
    val badgesList = listOf(
      BadgeItem(
        id = "streak_7",
        title = "7 Hari Streak 🔥",
        desc = "Belajar nonstop 7 hari berturut-turut tanpa bolos!",
        emoji = "🔥",
        unlocked = true,
        date = "24 Okt 2026"
      ),
      BadgeItem(
        id = "aljabar_ace",
        title = "Master Aljabar 📐",
        desc = "Selesaikan Level 1 Matematika dengan skor sempurna.",
        emoji = "📐",
        unlocked = true,
        date = "22 Okt 2026"
      ),
      BadgeItem(
        id = "solar_explorer",
        title = "Penjelajah Tata Surya 🪐",
        desc = "Chat bareng MIMI & kuasai materi rotasi planet.",
        emoji = "🪐",
        unlocked = false,
        date = "Sedang berlangsung"
      ),
      BadgeItem(
        id = "curhat_zen",
        title = "Sahabat Sejati MIMI ❤️",
        desc = "Saling curhat & temukan motivasi belajar sehat.",
        emoji = "❤️",
        unlocked = true,
        date = "24 Okt 2026"
      ),
      BadgeItem(
        id = "cyber_warrior",
        title = "Juara 1v1 Tantangan ⚔️",
        desc = "Kalahkan 5 siswa SMP lain di arena tantangan harian.",
        emoji = "⚔️",
        unlocked = true,
        date = "20 Okt 2026"
      )
    )
    _uiState.update { it.copy(badges = badgesList) }
  }

  private fun generateMimiResponse(
    query: String,
    level: EducationLevel,
    grade: String,
    activeTopic: String
  ): Triple<String, Int, String> {
    val lower = query.lowercase()

    // 1. CURHAT MODE (Lebih peka, hangat, semanusia mungkin, no judging)
    if (level == EducationLevel.CURHAT || lower.contains("capek") || lower.contains("stres") || lower.contains("takut") || lower.contains("insecure") || lower.contains("curhat")) {
      return when {
        lower.contains("capek") || lower.contains("tugas") -> {
          Triple(
            "Raka, sini peluk jauh dulu... 🤗 Wajar banget kamu capek. Kadang tugas sekolah rasanya gak ada habisnya ya. Dengerin kata MIMI: Kamu berhak istirahat 30 menit sekarang. Mandi air hangat, dengerin lagu favorit, atau rebahan tanpa mikirin sekolah dulu. Istirahat itu bagian dari proses belajar, bukan dosa kok! Nanti pas pikiran udah adem, kita cicil satu per satu bareng MIMI ya. Semangat ya jagoan!",
            30,
            "📖 Sumber: Buku Bimbingan Konseling Remaja & Jurnal Psikologi Pendidikan"
          )
        }
        lower.contains("takut") || lower.contains("ujian") -> {
          Triple(
            "Tarik napas panjang lewat hidung 4 detik... tahan 4 detik... hembuskan perlahan lewat mulut 6 detik. Lakuin 3 kali ya Raka. 💙 Rasa takut itu tanda kamu peduli sama masa depan kamu, itu normal banget! Tapi inget: Nilai ujian bukan ukuran harga dirimu sebagai manusia. Yang penting malam ini tidur cukup minimal 7 jam biar otak kamu bisa recall materi besok. MIMI doain dari sini, kamu pasti bisa lewatin ini!",
            35,
            "📖 Sumber: Harvard Health & Panduan Menghadapi Test Anxiety Kemendikbud"
          )
        }
        lower.contains("temen") || lower.contains("kelompok") -> {
          Triple(
            "Haha aduh klasik banget ya 'beban kelompok' itu! 😤 Rasanya gondok banget pas kita ngerjain sendirian tapi nama mereka numpang doang. Tips dari kakak: Kerjain bagian kamu dengan rapi, terus buat catatan kontribusi di lembar tugas. Gak usah ribut emosi di grup, santai tapi tegas. Anggap ini latihan kepemimpinan kamu buat masa depan!",
            25,
            "📖 Sumber: Modul Pembelajaran Kolaboratif Kurikulum Merdeka"
          )
        }
        lower.contains("insecure") || lower.contains("nilai") -> {
          Triple(
            "Raka, setiap orang punya 'timeline' mekarnya sendiri-sendiri 🌸 Nilai rapor cuma motret kemampuan kamu di satu hari tertentu, bukan masa depanmu 10 tahun ke depan. Temanmu mungkin jago ngafal sejarah, tapi kamu mungkin punya logika analitis atau kreativitas yang luar biasa. Bandingkan diri kamu sama Raka yang kemarin, bukan sama orang lain ya!",
            30,
            "📖 Sumber: Growth Mindset Theory (Dr. Carol Dweck) & Jurnal Karakter Siswa"
          )
        }
        else -> {
          Triple(
            "Makasih ya Raka udah mau cerita ke MIMI ❤️ Kadang kita cuma butuh didengerin tanpa dikuliahi. Kamu udah berjuang hebat banget hari ini, dan MIMI bangga sama kamu. Apapun yang lagi kamu rasain, kamu gak sendirian ya!",
            25,
            "📖 Sahabat Setia Siswa MIMI V13"
          )
        }
      }
    }

    // 2. ACADEMIC & SUBJECT INQUIRIES
    return when {
      lower.contains("pluto") -> {
        Triple(
          "Nah ini nih topik seru banget! Pluto itu bukan dipecat karena males, bro 😂 Tapi pada Sidang Umum International Astronomical Union (IAU) 2006 di Praha, disepakati 3 syarat resmi sebuah benda langit disebut Planet:\n1. Mengorbit Matahari\n2. Cukup bulat (punya gravitasi seimbang)\n3. 'Cleared the neighborhood' alias orbitnya bersih dari batuan liar lain.\nNah, Pluto gagal di syarat ke-3 karena orbitnya masih tumpang tindih sama ribuan asteroid es di Sabuk Kuiper. Jadi Pluto dikelompokkan ke 'Planet Kerdil' (Dwarf Planet) bareng Eris, Haumea, dan Makemake! Catet nih ya buat ulangan!",
          35,
          "📖 Sumber: Resolusi IAU 2006 & Buku IPA SMP Kelas 8 Bab 7 Kemendikbudristek"
        )
      }
      lower.contains("matahari") || lower.contains("panas") -> {
        Triple(
          "Gokil panasnya, Raka! Permukaannya (fotosfer) aja sekitar 5.500°C, tapi di inti dalamnya tempat reaksi fusi nuklir hidrogen jadi helium bisa tembus 15 JUTA derajat Celsius! ☀️\nBumi kita untung ada di 'Habitable Zone' (Zona Layak Huni), jadi pas banget. Kalo deket kayak Venus (465°C karena efek rumah kaca ekstrem CO2) langsung gosong, kalo jauh kayak Neptunus (-214°C) langsung jadi es batu!",
          30,
          "📖 Sumber: Buku IPA Terpadu Kemendikbud & NASA Heliophysics Journal"
        )
      }
      lower.contains("kepler") || lower.contains("hukum") -> {
        Triple(
          "Hukum Kepler itu sebenernya simpel kalo dibayangin! Johannes Kepler nemuin 3 aturan gerak planet:\n1. Hukum I: Orbit planet itu BENTUK ELIPS (bukan bulat sempurna), dan Matahari ada di salah satu titik fokusnya.\n2. Hukum II: Garis edar planet menyapu luas yang sama dalam waktu yang sama (kalo deket matahari ngebut, kalo jauh melambat).\n3. Hukum III: Kuadrat periode revolusi sebanding sama pangkat tiga jarak rata-rata ke matahari (T² ∝ R³). Keren kan matematikanya alam semesta!",
          40,
          "📖 Sumber: Jurnal Astronomi Klasik & Buku Fisika SMA/SMP Kemendikbudristek"
        )
      }
      lower.contains("urutan") || lower.contains("planet") -> {
        Triple(
          "Pake jembatan keledai super asik ini ya Raka: 'Me-Ve-Bu-Ma-Ju-Sat-Ur-Nep' 🚀\n1. Merkurius (terdekat & terkecil)\n2. Venus (terpanas, bintang fajar)\n3. Bumi (satu-satunya rumah kita!)\n4. Mars (planet merah berkarat besi)\n5. Jupiter (si raksasa gas pelindung bumi)\n6. Saturnus (raja cincin es)\n7. Uranus (planet es yang rotasinya miring rebahan 98°)\n8. Neptunus (planet biru berangin kencang supersonik)!",
          35,
          "📖 Sumber: Buku IPA Siswa Kelas 7-8 Kurikulum Merdeka & NASA JPL"
        )
      }
      lower.contains("aljabar") || lower.contains("rumus") -> {
        Triple(
          "Kunci aljabar itu cuma satu: JANGAN campurin apel sama jeruk! 🍎🍊\nSuku sejenis itu yang variabel dan pangkatnya persis sama. Contoh: 3x² cuma bisa dijumlah sama 5x², gak bisa dijumlah sama 2x doang. Kalo mau pindah ruas melewati tanda sama dengan (=), tandanya wajib dibalik: plus (+) jadi minus (-), kali (×) jadi bagi (÷). Gampang banget kan?",
          30,
          "📖 Sumber: Buku Matematika SMP Kelas 8 Bab 1 Kurikulum Merdeka"
        )
      }
      lower.contains("utbk") || lower.contains("snbt") || lower.contains("kuliah") -> {
        Triple(
          "Bocoran strategi tembus SNBT/UTBK nih Raka! 🎯\n1. Kuasai Tes Potensi Skolastik (TPS): Penalaran Umum, Kuantitatif, dan Pemahaman Bacaan (PPU). Ini pembobotan paling stabil.\n2. Latihan soal tipe High Order Thinking Skills (HOTS) minimal 20 soal sehari.\n3. Analisis kesalahan di buku khusus, jangan cuma baca kunci jawaban.\nMulai cicil dari sekarang, gak ada kata terlalu awal!",
          35,
          "📖 Sumber: Pedoman Resmi BP3 Kemendikbudristek & Analisis Skor UTBK"
        )
      }
      else -> {
        Triple(
          "Pertanyaan bagus di $level $grade untuk modul '$activeTopic', Raka! Konsep intinya itu logis dan aplikatif banget. Mau MIMI kupas teorinya dari buku teks, atau mau langsung bedah studi kasus/contoh soal kilat? 😎",
          20,
          "📖 Sumber: Basis Pengetahuan Buku Kurikulum Merdeka & Jurnal Terverifikasi"
        )
      }
    }
  }

  private fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
  }
}
