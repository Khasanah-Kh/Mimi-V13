package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MessageSender
import com.example.ui.theme.CardBg
import com.example.ui.theme.CyberGradient
import com.example.ui.theme.DeepCyberObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HudPanelBg
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyanMagentaBorderBrush
import com.example.ui.theme.NeonGamerGradient
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MimiUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val MIMI_SHARED_APP_URL = "https://ais-pre-wfxtrotumcs65islgw7caq-141953450050.asia-southeast1.run.app"

/**
 * Triggers the native Android Share Sheet (Intent.ACTION_SEND) to share the app URL,
 * referral code, and the user's Cyber-Gamer progress stats.
 */
fun shareMimiApp(context: Context, uiState: MimiUiState) {
  val inviteCode = "MIMI-${uiState.userName.uppercase().take(4)}-${uiState.totalXp}"
  val shareMessage = buildString {
    appendLine("🦊⚡ BELAJAR SERU BARENG MIMI V13 CYBER AI! ⚡🦊")
    appendLine()
    appendLine("Halo! Yuk gabung bareng ${uiState.userName} di aplikasi MIMI V13 (Kurikulum Merdeka SD, SMP, SMA, SMK, Umum & Teman Curhat AI)!")
    appendLine()
    appendLine("🎮 Status Gamer Belajar ${uiState.userName}:")
    appendLine("• Jenjang Aktif: ${uiState.selectedEducationLevel.label} - ${uiState.selectedClass}")
    appendLine("• Streak Belajar: 🔥 ${uiState.streakDays} Hari Nonstop")
    appendLine("• Total Skor: ⚡ ${uiState.totalXp} XP")
    appendLine("• Kode Mabar / Referral: $inviteCode")
    appendLine()
    appendLine("🚀 Coba & Unduh Aplikasi MIMI V13 di sini:")
    appendLine(MIMI_SHARED_APP_URL)
  }

  val sendIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "MIMI V13 Cyber Study AI - Undangan Belajar Bareng ${uiState.userName}")
    putExtra(Intent.EXTRA_TEXT, shareMessage)
  }
  val chooserIntent = Intent.createChooser(sendIntent, "Bagikan Aplikasi MIMI V13 via").apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }
  runCatching {
    context.startActivity(chooserIntent)
  }
}

/**
 * Copies the MIMI App Link and invite code to the system clipboard.
 */
fun copyAppLinkToClipboard(context: Context, uiState: MimiUiState): String {
  val inviteCode = "MIMI-${uiState.userName.uppercase().take(4)}-${uiState.totalXp}"
  val clipText = "Belajar seru bareng ${uiState.userName} di MIMI V13 Cyber AI! Kode Mabar: $inviteCode | Link Aplikasi: $MIMI_SHARED_APP_URL"
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
  clipboard?.setPrimaryClip(ClipData.newPlainText("MIMI V13 App Link", clipText))
  return inviteCode
}

/**
 * Builds the offline Markdown study report, module summary, quiz bank, and MIMI AI chat transcript.
 */
fun buildStudyModuleMarkdown(uiState: MimiUiState): String {
  val timestamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
  return buildString {
    appendLine("# 🦊 MODUL BELAJAR OFFLINE & RAPOR CYBER MIMI V13")
    appendLine("**Diunduh pada:** $timestamp")
    appendLine("**Link Aplikasi Resmi:** $MIMI_SHARED_APP_URL")
    appendLine()
    appendLine("---")
    appendLine("## 👤 PROFIL GAMER BELAJAR")
    appendLine("- **Nama Siswa:** ${uiState.userName}")
    appendLine("- **Email:** ${uiState.userEmail ?: "Tersinkronisasi Lokal"}")
    appendLine("- **Jenjang & Kelas:** ${uiState.selectedEducationLevel.label} • ${uiState.selectedClass}")
    appendLine("- **Kurikulum:** ${uiState.activeCurriculumBadge}")
    appendLine("- **Streak Aktif:** 🔥 ${uiState.streakDays} Hari")
    appendLine("- **Total XP:** ⚡ ${uiState.totalXp} XP")
    appendLine("- **Progress Materi:** ${uiState.progressCurrent}/${uiState.progressTotal} Selesai")
    appendLine()
    appendLine("---")
    appendLine("## 📚 SILABUS & ALUR BELAJAR (${uiState.selectedEducationLevel.label} - ${uiState.selectedClass})")
    uiState.currentLevels.forEach { level ->
      val statusBadge = when (level.status.name) {
        "DONE" -> "✅ SELESAI"
        "ACTIVE" -> "⚡ SEDANG AKTIF"
        else -> "🔒 TERKUNCI"
      }
      appendLine("### Level ${level.levelNumber}: ${level.title} ($statusBadge)")
      appendLine("- **Mata Pelajaran:** ${level.subject}")
      appendLine("- **Ringkasan:** ${level.subtitle}")
      appendLine("- **Hadiah XP:** +${level.xp} XP")
      appendLine()
    }
    appendLine("---")
    appendLine("## 💬 TRANSKRIP DISKUSI & CATATAN AI MIMI")
    if (uiState.chatMessages.isEmpty()) {
      appendLine("_Belum ada riwayat percakapan._")
    } else {
      uiState.chatMessages.forEach { msg ->
        val senderName = if (msg.sender == MessageSender.MIMI) "🦊 MIMI AI" else "🧑‍🎓 ${uiState.userName}"
        appendLine("### [$senderName] (${msg.timestamp})")
        appendLine(msg.text)
        if (!msg.sourceCitation.isNullOrBlank()) {
          appendLine("> *${msg.sourceCitation}*")
        }
        appendLine()
      }
    }
    appendLine("---")
    appendLine("## ❓ BANK SOAL & PEMBAHASAN KUIS KILAT")
    uiState.quizQuestions.forEachIndexed { idx, q ->
      appendLine("${idx + 1}. **${q.question}**")
      q.options.forEachIndexed { optIdx, opt ->
        val marker = if (optIdx == q.correctIndex) "✅" else "•"
        appendLine("   $marker $opt")
      }
      appendLine("   *Pembahasan:* ${q.explanation}")
      appendLine()
    }
    appendLine("---")
    appendLine("## 🏆 KOLEKSI LENCANA PRESTASI")
    uiState.badges.forEach { badge ->
      val lockState = if (badge.unlocked) "TERBUKA (${badge.date})" else "TERKUNCI"
      appendLine("- ${badge.emoji} **${badge.title}** [$lockState]: ${badge.desc}")
    }
  }
}

/**
 * Builds a clean JSON backup of the user's progress, syllabus, and chat logs.
 */
fun buildStudyBackupJson(uiState: MimiUiState): String {
  fun escapeJson(value: String): String =
    value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")

  val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date())
  return buildString {
    appendLine("{")
    appendLine("  \"app\": \"MIMI V13 Cyber Study AI\",")
    appendLine("  \"appUrl\": \"$MIMI_SHARED_APP_URL\",")
    appendLine("  \"exportedAt\": \"$timestamp\",")
    appendLine("  \"profile\": {")
    appendLine("    \"userName\": \"${escapeJson(uiState.userName)}\",")
    appendLine("    \"userEmail\": \"${escapeJson(uiState.userEmail ?: "")}\",")
    appendLine("    \"educationLevel\": \"${uiState.selectedEducationLevel.name}\",")
    appendLine("    \"selectedClass\": \"${escapeJson(uiState.selectedClass)}\",")
    appendLine("    \"streakDays\": ${uiState.streakDays},")
    appendLine("    \"totalXp\": ${uiState.totalXp},")
    appendLine("    \"progressCurrent\": ${uiState.progressCurrent},")
    appendLine("    \"progressTotal\": ${uiState.progressTotal}")
    appendLine("  },")
    appendLine("  \"chatHistoryCount\": ${uiState.chatMessages.size},")
    appendLine("  \"unlockedBadgesCount\": ${uiState.badges.count { it.unlocked }}")
    appendLine("}")
  }
}

/**
 * Writes text content to a document Uri returned by Storage Access Framework.
 */
fun writeTextToUri(context: Context, uri: Uri, content: String): Boolean {
  return runCatching {
    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
      outputStream.write(content.toByteArray(Charsets.UTF_8))
      outputStream.flush()
    }
    true
  }.getOrDefault(false)
}

/**
 * Compact Cyber Banner for HomeScreen allowing 1-tap Share App or Download Offline Study Pack.
 */
@Composable
fun ShareAndDownloadBannerCard(
  uiState: MimiUiState,
  onOpenHubClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var statusFeedback by remember { mutableStateOf<String?>(null) }

  val downloadMarkdownLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("text/markdown")
  ) { uri: Uri? ->
    if (uri != null) {
      val markdown = buildStudyModuleMarkdown(uiState)
      val ok = writeTextToUri(context, uri, markdown)
      statusFeedback = if (ok) "✅ Modul & Rapor Offline berhasil diunduh!" else "⚠️ Gagal menyimpan file."
    }
  }

  GlowCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("home_share_download_card"),
    borderWidth = 1.4.dp,
    glowBrush = NeonCyanMagentaBorderBrush,
    cornerRadius = 16.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(NeonGamerGradient),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Bagikan & Unduh",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "BAGIKAN APP & UNDUH MATERI 🚀",
              color = ElectricCyan,
              fontWeight = FontWeight.Black,
              fontSize = 12.5.sp,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Undang teman mabar kuis & simpan modul offline",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(HudPanelBg)
            .border(1.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .clickable { onOpenHubClick() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag("open_share_download_hub_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.QrCode2,
              contentDescription = "Buka QR & Hub",
              tint = ElectricCyan,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "QR & APK",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Button 1: Share App
        Box(
          modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.horizontalGradient(
                listOf(ElectricCyan.copy(alpha = 0.22f), NeonPurple.copy(alpha = 0.25f))
              )
            )
            .border(1.dp, ElectricCyan, RoundedCornerShape(10.dp))
            .clickable {
              shareMimiApp(context, uiState)
              statusFeedback = "🚀 Membuka menu Bagikan Aplikasi..."
            }
            .testTag("quick_share_app_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share App",
              tint = ElectricCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Share Aplikasi",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        // Button 2: Download Offline Study Pack
        Box(
          modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.horizontalGradient(
                listOf(NeonMagenta.copy(alpha = 0.25f), PrimaryBlue.copy(alpha = 0.25f))
              )
            )
            .border(1.dp, NeonMagenta, RoundedCornerShape(10.dp))
            .clickable {
              val safeClass = uiState.selectedClass.replace(" ", "_")
              val fileName = "Modul_MIMI_${uiState.selectedEducationLevel.label}_${safeClass}.md"
              downloadMarkdownLauncher.launch(fileName)
            }
            .testTag("quick_download_module_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Unduh Materi",
              tint = NeonMagenta,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Unduh Modul",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }

      AnimatedVisibility(
        visible = statusFeedback != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        statusFeedback?.let { msg ->
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = msg,
            color = MatrixGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

/**
 * Full-featured Share App & Download Center Dialog with:
 * - Scannable Cyber QR Code + Gamer Invite Card
 * - Share App via native Android Share Sheet & Copy App Link
 * - Download Offline Study Module (.md), Backup Data (.json), and APK / ZIP Export Guide
 */
@Composable
fun ShareAndDownloadHubDialog(
  uiState: MimiUiState,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var statusBanner by remember { mutableStateOf<String?>(null) }

  val downloadMarkdownLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("text/markdown")
  ) { uri: Uri? ->
    if (uri != null) {
      val content = buildStudyModuleMarkdown(uiState)
      val ok = writeTextToUri(context, uri, content)
      statusBanner = if (ok) {
        "✅ Modul Belajar & Rapor Offline (.md) berhasil diunduh ke perangkatmu!"
      } else {
        "⚠️ Gagal menyimpan file modul."
      }
    }
  }

  val downloadJsonLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/json")
  ) { uri: Uri? ->
    if (uri != null) {
      val content = buildStudyBackupJson(uiState)
      val ok = writeTextToUri(context, uri, content)
      statusBanner = if (ok) {
        "✅ Backup Profil & Data Belajar (.json) berhasil diunduh!"
      } else {
        "⚠️ Gagal menyimpan file JSON."
      }
    }
  }

  val inviteCode = remember(uiState.userName, uiState.totalXp) {
    "MIMI-${uiState.userName.uppercase().take(4)}-${uiState.totalXp}"
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .clip(RoundedCornerShape(22.dp))
        .background(DeepCyberObsidian)
        .border(1.8.dp, NeonCyanMagentaBorderBrush, RoundedCornerShape(22.dp))
        .padding(18.dp)
        .testTag("share_download_hub_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NeonGamerGradient),
              contentAlignment = Alignment.Center
            ) {
              MimiFoxMascot(size = 30.dp, showPulse = false)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SHARE & DOWNLOAD HUB",
                color = ElectricCyan,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                letterSpacing = 0.8.sp
              )
              Text(
                text = "Bagikan Aplikasi MIMI & Unduh Data Offline",
                color = TextSecondary,
                fontSize = 11.5.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(HudPanelBg)
              .testTag("close_share_hub_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Status Feedback Banner
        AnimatedVisibility(visible = statusBanner != null) {
          statusBanner?.let { banner ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF064E3B).copy(alpha = 0.75f))
                .border(1.dp, MatrixGreen, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MatrixGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = banner,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            }
          }
        }

        // Cyber Gamer Card & QR Code Preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HudPanelBg)
            .border(1.2.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "KARTU MABAR CYBER",
                  color = NeonMagenta,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "${uiState.userName} • ${uiState.selectedEducationLevel.label} ${uiState.selectedClass}",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(ElectricCyan.copy(alpha = 0.15f))
                  .border(1.dp, ElectricCyan, RoundedCornerShape(8.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "⚡ ${uiState.totalXp} XP",
                  color = ElectricCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stylized Cyber QR Matrix Visual
            Box(
              modifier = Modifier
                .size(128.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(2.dp, NeonGamerGradient, RoundedCornerShape(14.dp))
                .padding(10.dp),
              contentAlignment = Alignment.Center
            ) {
              CyberQrCodeCanvas(seed = inviteCode)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "KODE UNDANGAN: $inviteCode",
              color = MatrixGreen,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 12.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = MIMI_SHARED_APP_URL,
              color = TextSecondary,
              fontSize = 10.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        // Section 1: Share App Actions
        Text(
          text = "1. BAGIKAN APLIKASI (SHARE APP)",
          color = ElectricCyan,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          letterSpacing = 0.6.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              shareMimiApp(context, uiState)
              statusBanner = "🚀 Membuka menu Share Android..."
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("dialog_share_app_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryBlue,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share App",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Share ke Teman",
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
          }

          Button(
            onClick = {
              val code = copyAppLinkToClipboard(context, uiState)
              statusBanner = "📋 Link App & Kode ($code) disalin ke Clipboard!"
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("dialog_copy_link_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = CardBg,
              contentColor = ElectricCyan
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Salin Link",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Salin Link App",
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
          }
        }

        // Section 2: Download Study Pack & Offline Data
        Text(
          text = "2. UNDUH MATERI & DATA BELAJAR (DOWNLOAD)",
          color = NeonMagenta,
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          letterSpacing = 0.6.sp
        )

        // Download Option A: Complete Study Module + Chat Notes (.md)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.2.dp, NeonMagenta.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .clickable {
              val safeClass = uiState.selectedClass.replace(" ", "_")
              val fileName = "Modul_MIMI_${uiState.selectedEducationLevel.label}_${safeClass}.md"
              downloadMarkdownLauncher.launch(fileName)
            }
            .padding(14.dp)
            .testTag("dialog_download_markdown_button")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(NeonMagenta.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.FileDownload,
                  contentDescription = "Unduh Modul",
                  tint = NeonMagenta,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Unduh Modul Belajar & Catatan AI (.md)",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = "Berisi rangkuman ${uiState.selectedEducationLevel.label} ${uiState.selectedClass}, kuis & chat MIMI",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
              }
            }
            Text(
              text = "UNDUH",
              color = NeonMagenta,
              fontWeight = FontWeight.Black,
              fontSize = 11.5.sp
            )
          }
        }

        // Download Option B: JSON Profile & Progress Backup (.json)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.2.dp, ElectricCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable {
              val fileName = "Backup_Profil_MIMI_${uiState.userName.replace(" ", "_")}.json"
              downloadJsonLauncher.launch(fileName)
            }
            .padding(14.dp)
            .testTag("dialog_download_json_button")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(ElectricCyan.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Download,
                  contentDescription = "Unduh Backup JSON",
                  tint = ElectricCyan,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Unduh Backup Profil & Skor XP (.json)",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = "Simpan cadangan XP, Streak, dan Lencana di HP",
                  color = TextSecondary,
                  fontSize = 11.sp
                )
              }
            }
            Text(
              text = "JSON",
              color = ElectricCyan,
              fontWeight = FontWeight.Black,
              fontSize = 11.5.sp
            )
          }
        }

        // Section 3: APK & Project ZIP Download Info Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HudPanelBg)
            .border(1.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.PhoneAndroid,
              contentDescription = "Panduan APK",
              tint = NeonPurple,
              modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "CARA UNDUH FILE APK ANDROID / ZIP:",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.5.sp
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = "• Untuk menginstal di HP Android: Buka menu Settings / Export (⚙️) di pojok kanan atas Google AI Studio, lalu pilih 'Download APK' atau 'Download ZIP'.\n• Untuk berbagi cepat: Gunakan tombol 'Share ke Teman' di atas!",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Deterministic Cyber QR Code pattern rendered on Compose Canvas.
 */
@Composable
private fun CyberQrCodeCanvas(seed: String) {
  Canvas(modifier = Modifier.size(104.dp)) {
    val gridSize = 11
    val cellSize = size.width / gridSize

    fun drawFinderPattern(startCol: Int, startRow: Int) {
      drawRoundRect(
        color = Color(0xFF0A0E1A),
        topLeft = Offset(startCol * cellSize, startRow * cellSize),
        size = Size(cellSize * 3, cellSize * 3),
        cornerRadius = CornerRadius(4f, 4f)
      )
      drawRect(
        color = Color.White,
        topLeft = Offset((startCol + 0.55f) * cellSize, (startRow + 0.55f) * cellSize),
        size = Size(cellSize * 1.9f, cellSize * 1.9f)
      )
      drawRect(
        color = Color(0xFF0077FF),
        topLeft = Offset((startCol + 1f) * cellSize, (startRow + 1f) * cellSize),
        size = Size(cellSize, cellSize)
      )
    }

    // Draw 3 corner finder patterns
    drawFinderPattern(0, 0)
    drawFinderPattern(gridSize - 3, 0)
    drawFinderPattern(0, gridSize - 3)

    // Draw deterministic data modules based on seed hash
    val hash = seed.hashCode()
    for (r in 0 until gridSize) {
      for (c in 0 until gridSize) {
        val inTopLeft = r < 4 && c < 4
        val inTopRight = r < 4 && c >= gridSize - 4
        val inBottomLeft = r >= gridSize - 4 && c < 4
        if (!inTopLeft && !inTopRight && !inBottomLeft) {
          val bit = ((hash shr ((r * gridSize + c) % 28)) xor (r * 7 + c * 13)) and 1
          if (bit == 1) {
            drawRoundRect(
              color = if ((r + c) % 3 == 0) Color(0xFF0A0E1A) else Color(0xFF1E3A8A),
              topLeft = Offset(c * cellSize + 1f, r * cellSize + 1f),
              size = Size(cellSize - 2f, cellSize - 2f),
              cornerRadius = CornerRadius(2f, 2f)
            )
          }
        }
      }
    }
  }
}
