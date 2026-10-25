package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.EducationLevel
import com.example.model.MessageSender
import com.example.ui.components.MimiFoxMascot
import com.example.ui.theme.CardBg
import com.example.ui.theme.CyberGradient
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.MimiChatBg
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MimiChatBottomSheet(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  messages: List<ChatMessage>,
  isTyping: Boolean,
  suggestedPrompts: List<String>,
  selectedLevel: EducationLevel,
  selectedClass: String,
  onSelectLevel: (EducationLevel) -> Unit,
  onSelectClass: (String) -> Unit,
  onSendMessage: (String) -> Unit,
  latestXpNotification: String?
) {
  if (!isOpen) return

  val sheetState: SheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkNavyBg,
    contentColor = TextPrimary,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 8.dp)
          .width(42.dp)
          .height(4.dp)
          .clip(CircleShape)
          .background(PrimaryBlue.copy(alpha = 0.5f))
      )
    },
    modifier = Modifier
      .fillMaxHeight(0.85f)
      .testTag("mimi_chat_bottom_sheet")
  ) {
    ChatBottomSheetContent(
      onDismiss = onDismiss,
      messages = messages,
      isTyping = isTyping,
      suggestedPrompts = suggestedPrompts,
      selectedLevel = selectedLevel,
      selectedClass = selectedClass,
      onSelectLevel = onSelectLevel,
      onSelectClass = onSelectClass,
      onSendMessage = onSendMessage,
      latestXpNotification = latestXpNotification
    )
  }
}

@Composable
private fun ChatBottomSheetContent(
  onDismiss: () -> Unit,
  messages: List<ChatMessage>,
  isTyping: Boolean,
  suggestedPrompts: List<String>,
  selectedLevel: EducationLevel,
  selectedClass: String,
  onSelectLevel: (EducationLevel) -> Unit,
  onSelectClass: (String) -> Unit,
  onSendMessage: (String) -> Unit,
  latestXpNotification: String?
) {
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  var inputText by remember { mutableStateOf("") }

  LaunchedEffect(messages.size, isTyping) {
    if (messages.isNotEmpty()) {
      coroutineScope.launch {
        listState.animateScrollToItem(messages.size - 1)
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .imePadding()
  ) {
    // Header (Cyber MIMI Fox & Online Status)
    ChatHeader(
      onDismiss = onDismiss,
      selectedLevel = selectedLevel,
      selectedClass = selectedClass,
      latestXpNotification = latestXpNotification
    )

    // Jenjang & Kelas Switcher Strip (SD, SMP, SMA, SMK, Umum, Curhat ❤️)
    LevelAndClassPickerStrip(
      selectedLevel = selectedLevel,
      selectedClass = selectedClass,
      onSelectLevel = onSelectLevel,
      onSelectClass = onSelectClass
    )

    // Chat Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(vertical = 10.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        when (msg.sender) {
          MessageSender.USER -> UserChatBubble(msg)
          MessageSender.MIMI -> MimiChatBubble(msg, isCurhat = selectedLevel == EducationLevel.CURHAT)
        }
      }

      // Typing Indicator
      if (isTyping) {
        item {
          MimiTypingIndicator(isCurhat = selectedLevel == EducationLevel.CURHAT)
        }
      }
    }

    // Suggested Quick Chips (Dynamic per level / Curhat)
    QuickChipsRow(
      prompts = suggestedPrompts,
      onPromptSelected = { prompt ->
        onSendMessage(prompt)
      }
    )

    // Glowing Input Bar
    ChatInputBar(
      text = inputText,
      onTextChanged = { inputText = it },
      isCurhat = selectedLevel == EducationLevel.CURHAT,
      onSend = {
        if (inputText.isNotBlank()) {
          onSendMessage(inputText.trim())
          inputText = ""
        }
      }
    )
  }
}

@Composable
private fun ChatHeader(
  onDismiss: () -> Unit,
  selectedLevel: EducationLevel,
  selectedClass: String,
  latestXpNotification: String?
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0F172A))
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          listOf(Color.Transparent, PrimaryBlue.copy(alpha = 0.3f))
        ),
        shape = RectangleShape
      )
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Mascot + Info
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(CardBg)
            .border(
              1.5.dp,
              if (selectedLevel == EducationLevel.CURHAT) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed)) else CyberGradient,
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          MimiFoxMascot(size = 36.dp, showPulse = true)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (selectedLevel == EducationLevel.CURHAT) "MIMI Sahabat Curhat" else "MIMI",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  if (selectedLevel == EducationLevel.CURHAT) Color(0xFFBE185D).copy(alpha = 0.3f)
                  else PrimaryBlue.copy(alpha = 0.25f)
                )
                .border(
                  0.6.dp,
                  if (selectedLevel == EducationLevel.CURHAT) Color(0xFFF472B6) else PrimaryBlue,
                  RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 1.5.dp)
            ) {
              Text(
                text = "${selectedLevel.label} • $selectedClass",
                color = if (selectedLevel == EducationLevel.CURHAT) Color(0xFFF472B6) else Color(0xFF60A5FA),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(PrimaryBlue.copy(alpha = 0.35f), PrimaryRed.copy(alpha = 0.35f))
                  )
                )
                .border(0.6.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.5.dp)
            ) {
              Text(
                text = "⚡ Gemini 3.8",
                color = Color(0xFF7DD3FC),
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
              )
            }
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(NeonGreen)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = if (selectedLevel == EducationLevel.CURHAT) "Online • Tempat Nyaman Curhat Siswa ❤️" else "Online • Sumber Buku & Jurnal Resmi 📚",
              color = Color(0xFF6EE7B7),
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // Right: Close Button
      IconButton(
        onClick = onDismiss,
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Color(0xFF1E293B))
          .testTag("close_chat_button")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Tutup Chat",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(17.dp)
        )
      }
    }
  }
}

/**
 * Interactive Jenjang (SD, SMP, SMA, SMK, Umum, Curhat) & Kelas Selector Strip
 */
@Composable
private fun LevelAndClassPickerStrip(
  selectedLevel: EducationLevel,
  selectedClass: String,
  onSelectLevel: (EducationLevel) -> Unit,
  onSelectClass: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0F172A).copy(alpha = 0.95f))
      .padding(vertical = 6.dp)
  ) {
    // 1. Jenjang Level Row
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
      items(EducationLevel.values()) { level ->
        val isSelected = level == selectedLevel
        val isCurhat = level == EducationLevel.CURHAT

        val chipScale by animateFloatAsState(
          targetValue = if (isSelected) 1.05f else 1.0f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
          ),
          label = "bottom_sheet_level_scale_${level.name}"
        )

        val chipBorderWidth by animateDpAsState(
          targetValue = if (isSelected) 1.5.dp else 1.dp,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
          ),
          label = "bottom_sheet_level_border_${level.name}"
        )

        val (bgBrush, borderColor, textColor) = when {
          isSelected && isCurhat -> Triple(
            Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFFEF4444))),
            Color(0xFFF472B6),
            Color.White
          )
          isSelected -> Triple(
            CyberGradient,
            PrimaryBlue,
            Color.White
          )
          else -> Triple(
            SolidColor(Color(0xFF162032)),
            Color(0xFF334155),
            Color(0xFF94A3B8)
          )
        }

        Box(
          modifier = Modifier
            .scale(chipScale)
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .border(chipBorderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onSelectLevel(level) }
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .testTag("level_chip_${level.name.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = level.emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = level.label,
              color = textColor,
              fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
              fontSize = 11.5.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 2. Class Choices Row (e.g. Kelas 7, Kelas 8, Kelas 9 for SMP)
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
      items(selectedLevel.classes) { cls ->
        val isClassSelected = cls == selectedClass
        val isCurhat = selectedLevel == EducationLevel.CURHAT

        val classChipScale by animateFloatAsState(
          targetValue = if (isClassSelected) 1.06f else 1.0f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
          ),
          label = "bottom_sheet_class_scale_$cls"
        )

        val classBorderWidth by animateDpAsState(
          targetValue = if (isClassSelected) 1.2.dp else 0.8.dp,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
          ),
          label = "bottom_sheet_class_border_$cls"
        )

        Box(
          modifier = Modifier
            .scale(classChipScale)
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (isClassSelected) {
                if (isCurhat) Color(0xFFDB2777).copy(alpha = 0.35f) else PrimaryBlue.copy(alpha = 0.25f)
              } else Color(0xFF131D2E)
            )
            .border(
              classBorderWidth,
              if (isClassSelected) {
                if (isCurhat) Color(0xFFF472B6) else PrimaryBlue
              } else Color(0xFF1E293B),
              RoundedCornerShape(8.dp)
            )
            .clickable { onSelectClass(cls) }
            .padding(horizontal = 10.dp, vertical = 3.dp)
            .testTag("class_chip_$cls"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = cls,
            color = if (isClassSelected) Color.White else Color(0xFF94A3B8),
            fontWeight = if (isClassSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

/**
 * User Chat Bubble: Right aligned with Blue to Red gradient background
 */
@Composable
private fun UserChatBubble(msg: ChatMessage) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 280.dp)
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
          )
        )
        .background(CyberGradient)
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Column {
        Text(
          text = msg.text,
          color = Color.White,
          fontSize = 14.sp,
          lineHeight = 20.sp,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = msg.timestamp,
          color = Color.White.copy(alpha = 0.75f),
          fontSize = 10.sp,
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}

/**
 * MIMI Chat Bubble: Left aligned with #1E293B contrasting background and mascot avatar
 */
@Composable
private fun MimiChatBubble(msg: ChatMessage, isCurhat: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.Top
  ) {
    // Mini Mascot Avatar
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(Color(0xFF0F172A))
        .border(1.dp, if (isCurhat) Color(0xFFEC4899) else PrimaryBlue, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      MimiFoxMascot(size = 28.dp, showPulse = false)
    }

    Spacer(modifier = Modifier.width(8.dp))

    Box(
      modifier = Modifier
        .widthIn(max = 295.dp)
        .clip(
          RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
          )
        )
        .background(MimiChatBg)
        .border(
          1.dp,
          if (isCurhat) Color(0xFFEC4899).copy(alpha = 0.4f) else PrimaryBlue.copy(alpha = 0.25f),
          RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
        )
        .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isCurhat) "MIMI Sahabat Siswa ❤️" else "MIMI 🦊",
            color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF60A5FA),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.5.sp
          )

          if (msg.xpAwarded > 0) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(PrimaryRed.copy(alpha = 0.2f))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "+${msg.xpAwarded} XP",
                color = PrimaryRed,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Message text
        Text(
          text = msg.text,
          color = TextPrimary,
          fontSize = 13.5.sp,
          lineHeight = 20.sp,
          fontWeight = FontWeight.Normal
        )

        // Book / Journal verified source citation
        if (!msg.sourceCitation.isNullOrBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF0F172A).copy(alpha = 0.8f))
              .border(0.8.dp, PrimaryBlue.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = msg.sourceCitation,
              color = Color(0xFF93C5FD),
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Medium,
              lineHeight = 14.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = msg.timestamp,
          color = TextSecondary,
          fontSize = 10.sp,
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}

@Composable
private fun MimiTypingIndicator(isCurhat: Boolean) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(start = 42.dp, top = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(MimiChatBg)
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = if (isCurhat) "MIMI lagi nulis buat kamu... ❤️" else "MIMI lagi ngetik...",
          color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF93C5FD),
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = if (isCurhat) "✨" else "⚡", fontSize = 12.sp)
      }
    }
  }
}

/**
 * Suggested Quick Chips (Duolingo / TikTok style)
 */
@Composable
private fun QuickChipsRow(
  prompts: List<String>,
  onPromptSelected: (String) -> Unit
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(horizontal = 4.dp)
  ) {
    items(prompts) { prompt ->
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF162032))
          .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
          .clickable { onPromptSelected(prompt) }
          .padding(horizontal = 12.dp, vertical = 7.dp)
      ) {
        Text(
          text = prompt,
          color = Color(0xFF93C5FD),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

/**
 * Chat Input with glowing border and blue-red gradient send button
 */
@Composable
private fun ChatInputBar(
  text: String,
  onTextChanged: (String) -> Unit,
  isCurhat: Boolean,
  onSend: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0F172A))
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          listOf(PrimaryBlue.copy(alpha = 0.4f), Color.Transparent)
        ),
        shape = RectangleShape
      )
      .windowInsetsPadding(WindowInsets.navigationBars)
      .padding(horizontal = 12.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Glow Border Text Input
      Box(
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(Color(0xFF162032))
          .border(
            1.2.dp,
            if (isCurhat) Color(0xFFEC4899).copy(alpha = 0.6f) else PrimaryBlue.copy(alpha = 0.6f),
            RoundedCornerShape(24.dp)
          )
          .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        if (text.isEmpty()) {
          Text(
            text = if (isCurhat) "Ceritain apa aja, MIMI dengerin..." else "Tanya apa aja ke MIMI...",
            color = Color(0xFF64748B),
            fontSize = 13.5.sp
          )
        }
        BasicTextField(
          value = text,
          onValueChange = onTextChanged,
          textStyle = TextStyle(
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
          ),
          cursorBrush = SolidColor(if (isCurhat) Color(0xFFF472B6) else PrimaryBlue),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(onSend = { onSend() }),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_input_field")
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Gradient Send Button (Blue to Red or Pink to Red for Curhat)
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(
            if (isCurhat) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed))
            else CyberGradient
          )
          .clickable { onSend() }
          .testTag("send_chat_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Kirim Pesan",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
