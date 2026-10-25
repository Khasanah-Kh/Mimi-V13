package com.example.model

enum class MessageSender {
  USER,
  MIMI
}

enum class EducationLevel(
  val id: String,
  val label: String,
  val emoji: String,
  val classes: List<String>,
  val description: String
) {
  SD(
    id = "sd",
    label = "SD",
    emoji = "🎒",
    classes = listOf("Kelas 1", "Kelas 2", "Kelas 3", "Kelas 4", "Kelas 5", "Kelas 6"),
    description = "Berhitung ceria, alam sekitar & budi pekerti"
  ),
  SMP(
    id = "smp",
    label = "SMP",
    emoji = "⚡",
    classes = listOf("Kelas 7", "Kelas 8", "Kelas 9"),
    description = "IPA Terpadu, Aljabar & konsep sains esensial"
  ),
  SMA(
    id = "sma",
    label = "SMA",
    emoji = "🎓",
    classes = listOf("Kelas 10", "Kelas 11", "Kelas 12"),
    description = "Sains analitis, persiapan UTBK & jurnal riset"
  ),
  SMK(
    id = "smk",
    label = "SMK",
    emoji = "🛠️",
    classes = listOf("Kelas 10", "Kelas 11", "Kelas 12"),
    description = "Vokasi terapan, teknologi industri & portofolio"
  ),
  UMUM(
    id = "umum",
    label = "Umum",
    emoji = "🌐",
    classes = listOf("Wawasan", "Persiapan Kuliah", "Karir"),
    description = "Sains populer, kosmologi & metode berpikir kritis"
  ),
  CURHAT(
    id = "curhat",
    label = "Curhat",
    emoji = "❤️",
    classes = listOf("Teman Curhat", "Burnout & Stres", "Motivasi Belajar"),
    description = "Ruang aman tanpa judging, empati & pemulihan energi"
  )
}

data class ChatMessage(
  val id: String,
  val sender: MessageSender,
  val text: String,
  val timestamp: String,
  val xpAwarded: Int = 0,
  val sourceCitation: String? = null
)

enum class LevelStatus {
  DONE,
  ACTIVE,
  LOCKED
}

data class LevelItem(
  val levelNumber: Int,
  val title: String,
  val subject: String,
  val xp: Int,
  val status: LevelStatus,
  val subtitle: String,
  val iconEmoji: String,
  val educationLevel: EducationLevel = EducationLevel.SMP,
  val targetTopic: String = ""
)

enum class BottomNavTab(val title: String, val emoji: String) {
  BERANDA("Beranda", "🏠"),
  TANTANGAN("Tantangan", "⚔️"),
  KUIS("Kuis", "❓"),
  LENCANA("Lencana", "🏆"),
  PROFIL("Profil", "👤")
}

data class QuizQuestion(
  val id: Int,
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)

data class BadgeItem(
  val id: String,
  val title: String,
  val desc: String,
  val emoji: String,
  val unlocked: Boolean,
  val date: String
)
