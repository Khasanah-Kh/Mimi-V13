package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EducationLevel
import com.example.model.LevelItem
import com.example.model.LevelStatus
import com.example.ui.components.CyberGradientButton
import com.example.ui.components.CyberProgressBar
import com.example.ui.components.GlowCard
import com.example.ui.components.MimiFoxMascot
import com.example.ui.theme.CardBg
import com.example.ui.theme.CyberGradient
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MimiUiState

@Composable
fun HomeScreen(
  uiState: MimiUiState,
  levels: List<LevelItem>,
  onSelectEducationLevel: (EducationLevel) -> Unit,
  onSelectClass: (String) -> Unit,
  onLevelClick: (LevelItem) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Greeting Section (no clicks needed)
    item {
      Spacer(modifier = Modifier.height(8.dp))
      TopGreetingSection(
        userName = uiState.userName,
        userSubtitle = uiState.userSubtitle,
        selectedLevel = uiState.selectedEducationLevel
      )
    }

    // Dynamic Category & Class Selection Bar (SD, SMP, SMA, SMK, Umum, Curhat ❤️)
    item {
      CategoryAndClassSelectionBar(
        selectedLevel = uiState.selectedEducationLevel,
        selectedClass = uiState.selectedClass,
        curriculumBadge = uiState.activeCurriculumBadge,
        onSelectLevel = onSelectEducationLevel,
        onSelectClass = onSelectClass
      )
    }

    // 3 Glow Cards Row: [🔥 7 Hari Streak] [⚡ 1.250 XP] [Progress 3/5 with blue-red bar]
    item {
      ThreeGlowCardsRow(
        streakDays = uiState.streakDays,
        totalXp = uiState.totalXp,
        progressCurrent = uiState.progressCurrent,
        progressTotal = uiState.progressTotal,
        isCurhat = uiState.selectedEducationLevel == EducationLevel.CURHAT
      )
    }

    // Floating XP Notification if active
    item {
      AnimatedVisibility(
        visible = uiState.latestXpNotification != null,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
      ) {
        uiState.latestXpNotification?.let { notif ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(PrimaryBlue.copy(alpha = 0.25f), PrimaryRed.copy(alpha = 0.25f))
                )
              )
              .border(1.dp, PrimaryBlue, RoundedCornerShape(12.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "⚡", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = notif,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // Learning Path Section Title with Dynamic Level Category Label
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = uiState.selectedEducationLevel.emoji, fontSize = 18.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "ALUR ${uiState.selectedEducationLevel.label.uppercase()}",
            color = if (uiState.selectedEducationLevel == EducationLevel.CURHAT) Color(0xFFF472B6) else PrimaryBlue,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp,
            letterSpacing = 1.sp
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "1 Tap = Belajar",
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    // Vertical Duolingo-style Learning Path (Dynamic for selected category)
    item {
      VerticalLearningPath(
        levels = levels,
        isCurhat = uiState.selectedEducationLevel == EducationLevel.CURHAT,
        onLevelClick = onLevelClick
      )
    }

    // Bottom extra spacer to avoid bottom nav overlap
    item {
      Spacer(modifier = Modifier.height(84.dp))
    }
  }
}

@Composable
private fun TopGreetingSection(
  userName: String,
  userSubtitle: String,
  selectedLevel: EducationLevel
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "Halo, $userName! 🔥",
        color = TextPrimary,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        letterSpacing = 0.2.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(
            if (selectedLevel == EducationLevel.CURHAT) Color(0xFFBE185D).copy(alpha = 0.25f)
            else PrimaryBlue.copy(alpha = 0.18f)
          )
          .border(
            0.8.dp,
            if (selectedLevel == EducationLevel.CURHAT) Color(0xFFF472B6) else PrimaryBlue.copy(alpha = 0.5f),
            RoundedCornerShape(6.dp)
          )
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Text(
          text = userSubtitle,
          color = if (selectedLevel == EducationLevel.CURHAT) Color(0xFFF472B6) else Color(0xFF60A5FA),
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }

    // Cyber Avatar / MIMI Mini Head
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(CardBg)
        .border(
          1.5.dp,
          if (selectedLevel == EducationLevel.CURHAT) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed)) else CyberGradient,
          CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      MimiFoxMascot(size = 36.dp, showPulse = false)
    }
  }
}

/**
 * Dynamic Category & Nested Class Selection Bar:
 * - Tier 1: Horizontal scrollable tab row for SD, SMP, SMA, SMK, Umum, Curhat ❤️
 * - Tier 2: Dynamic nested class picker:
 *     - SD: Kelas 1, 2, 3, 4, 5, 6
 *     - SMP: Kelas 7, 8, 9
 *     - SMA: Kelas 10, 11, 12
 *     - SMK: Kelas 10, 11, 12
 *     - Umum: Wawasan, Persiapan Kuliah, Karir
 *     - Curhat: Teman Curhat, Burnout & Stres, Motivasi Belajar
 */
@Composable
private fun CategoryAndClassSelectionBar(
  selectedLevel: EducationLevel,
  selectedClass: String,
  curriculumBadge: String,
  onSelectLevel: (EducationLevel) -> Unit,
  onSelectClass: (String) -> Unit
) {
  val isCurhat = selectedLevel == EducationLevel.CURHAT

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0xFF0F172A))
      .border(
        width = 1.2.dp,
        brush = if (isCurhat) Brush.horizontalGradient(listOf(Color(0xFFEC4899).copy(alpha = 0.5f), PrimaryRed.copy(alpha = 0.3f)))
        else Brush.horizontalGradient(listOf(PrimaryBlue.copy(alpha = 0.5f), PrimaryRed.copy(alpha = 0.3f))),
        shape = RoundedCornerShape(18.dp)
      )
      .padding(vertical = 10.dp)
  ) {
    // 1. Horizontal Scrollable Tab Row (Jenjang SD, SMP, SMA, SMK, Umum, Curhat)
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
      items(EducationLevel.values()) { level ->
        val isSelected = level == selectedLevel
        val isLevelCurhat = level == EducationLevel.CURHAT

        val categoryScale by animateFloatAsState(
          targetValue = if (isSelected) 1.05f else 1.0f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
          ),
          label = "cat_scale_${level.name}"
        )

        val categoryBorderWidth by animateDpAsState(
          targetValue = if (isSelected) 1.8.dp else 1.dp,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
          ),
          label = "cat_border_${level.name}"
        )

        val (bgBrush, borderColor, textColor) = when {
          isSelected && isLevelCurhat -> Triple(
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
            .height(42.dp)
            .scale(categoryScale)
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(categoryBorderWidth, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelectLevel(level) }
            .padding(horizontal = 14.dp)
            .testTag("home_category_${level.name.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(text = level.emoji, fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = level.label,
              color = textColor,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Sub-header displaying active context
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (isCurhat) "PILIH TOPIK CURHAT:" else "PILIH KELAS ${selectedLevel.label}:",
        color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF60A5FA),
        fontWeight = FontWeight.Black,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
      )
      Text(
        text = "$selectedClass Aktif",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 2. Conditionally Rendered Horizontal Scrollable Class Row for the active jenjang
    AnimatedContent(
      targetState = selectedLevel,
      transitionSpec = {
        (slideInHorizontally(spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) { it / 5 } + fadeIn(tween(180)))
          .togetherWith(slideOutHorizontally(tween(120)) { -it / 5 } + fadeOut(tween(120)))
      },
      label = "nested_class_picker_anim"
    ) { targetLevel ->
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
      ) {
        items(targetLevel.classes) { cls ->
          val isClassSelected = cls == selectedClass

          val gradeScale by animateFloatAsState(
            targetValue = if (isClassSelected) 1.06f else 1.0f,
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            label = "grade_scale_$cls"
          )

          val gradeBorderWidth by animateDpAsState(
            targetValue = if (isClassSelected) 1.5.dp else 0.8.dp,
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMedium
            ),
            label = "grade_border_$cls"
          )

          val (classBg, classBorder, classTextColor) = when {
            isClassSelected && isCurhat -> Triple(
              Color(0xFFBE185D).copy(alpha = 0.4f),
              Color(0xFFF472B6),
              Color.White
            )
            isClassSelected -> Triple(
              PrimaryBlue.copy(alpha = 0.3f),
              PrimaryBlue,
              Color.White
            )
            else -> Triple(
              Color(0xFF131D2E),
              Color(0xFF1E293B),
              Color(0xFF94A3B8)
            )
          }

          Box(
            modifier = Modifier
              .height(36.dp)
              .scale(gradeScale)
              .clip(RoundedCornerShape(10.dp))
              .background(classBg)
              .border(gradeBorderWidth, classBorder, RoundedCornerShape(10.dp))
              .clickable { onSelectClass(cls) }
              .padding(horizontal = 12.dp)
              .testTag("home_class_${targetLevel.name.lowercase()}_${cls.replace(" ", "_").lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              AnimatedVisibility(
                visible = isClassSelected,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)) + fadeIn()
              ) {
                Row {
                  Text(
                    text = "✓",
                    color = if (isCurhat) Color(0xFFF472B6) else PrimaryBlue,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                }
              }
              Text(
                text = cls,
                color = classTextColor,
                fontWeight = if (isClassSelected) FontWeight.ExtraBold else FontWeight.Medium,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // 3. Official Curriculum Badge indicator
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF131D2E))
        .border(0.8.dp, if (isCurhat) Color(0xFFEC4899).copy(alpha = 0.3f) else PrimaryBlue.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = curriculumBadge,
          color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF93C5FD),
          fontSize = 10.5.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = if (isCurhat) "Aman & Nyaman ❤️" else "Resmi 📚",
          color = Color(0xFF6EE7B7),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun ThreeGlowCardsRow(
  streakDays: Int,
  totalXp: Int,
  progressCurrent: Int,
  progressTotal: Int,
  isCurhat: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Card 1: [🔥 7 Hari Streak]
    GlowCard(
      modifier = Modifier
        .weight(1f)
        .height(86.dp),
      borderWidth = 1.4.dp,
      glowBrush = Brush.linearGradient(
        listOf(PrimaryRed, Color(0xFFF59E0B).copy(alpha = 0.6f))
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🔥", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "$streakDays Hari",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Streak Aktif",
          color = Color(0xFFFCA5A5),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }

    // Card 2: [⚡ 1.250 XP]
    val formattedXp = String.format("%,d", totalXp).replace(',', '.')
    GlowCard(
      modifier = Modifier
        .weight(1f)
        .height(86.dp),
      borderWidth = 1.4.dp,
      glowBrush = Brush.linearGradient(
        if (isCurhat) listOf(Color(0xFFEC4899), PrimaryRed)
        else listOf(PrimaryBlue, Color(0xFF06B6D4).copy(alpha = 0.6f))
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = if (isCurhat) "❤️" else "⚡", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = formattedXp,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = if (isCurhat) "Poin Curhat" else "Total XP",
          color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF93C5FD),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }
    }

    // Card 3: [Progress 3/5 with blue-red bar]
    GlowCard(
      modifier = Modifier
        .weight(1.15f)
        .height(86.dp),
      borderWidth = 1.4.dp,
      glowBrush = Brush.linearGradient(
        if (isCurhat) listOf(Color(0xFFEC4899), PrimaryRed)
        else listOf(PrimaryBlue, PrimaryRed)
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Progress",
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "$progressCurrent/$progressTotal",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        CyberProgressBar(
          progress = progressCurrent.toFloat() / progressTotal.toFloat(),
          modifier = Modifier.fillMaxWidth(),
          height = 7.dp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Materi Aktif",
          color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF60A5FA),
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}

/**
 * Vertical Learning Path in Duolingo style with glowing vertical line #3B82F6
 */
@Composable
private fun VerticalLearningPath(
  levels: List<LevelItem>,
  isCurhat: Boolean,
  onLevelClick: (LevelItem) -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "path_pulse")
  val lineAlpha by infiniteTransition.animateFloat(
    initialValue = 0.65f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "line_pulse"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    // Glowing Vertical Line #3B82F6 running precisely through the center of 48.dp node circles (x = 24.dp)
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .matchParentSize()
    ) {
      val centerX = 24.dp.toPx()

      // Outer soft glow
      drawLine(
        brush = Brush.verticalGradient(
          colors = listOf(
            NeonGreen.copy(alpha = 0.5f * lineAlpha),
            PrimaryBlue.copy(alpha = 0.4f * lineAlpha),
            PrimaryBlue.copy(alpha = 0.25f * lineAlpha),
            Color.Transparent
          )
        ),
        start = Offset(centerX, 24.dp.toPx()),
        end = Offset(centerX, size.height - 24.dp.toPx()),
        strokeWidth = 10.dp.toPx()
      )

      // Core electric blue glowing line #3B82F6
      drawLine(
        brush = Brush.verticalGradient(
          colors = listOf(
            NeonGreen,
            if (isCurhat) Color(0xFFEC4899).copy(alpha = lineAlpha) else PrimaryBlue.copy(alpha = lineAlpha),
            PrimaryBlue.copy(alpha = lineAlpha * 0.85f),
            PrimaryBlue.copy(alpha = 0.3f),
            Color.Transparent
          )
        ),
        start = Offset(centerX, 24.dp.toPx()),
        end = Offset(centerX, size.height - 24.dp.toPx()),
        strokeWidth = 3.5.dp.toPx()
      )
    }

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      levels.forEach { level ->
        when (level.status) {
          LevelStatus.DONE -> LevelDoneCard(level = level)
          LevelStatus.ACTIVE -> LevelActiveCard(
            level = level,
            isCurhat = isCurhat,
            onStartClick = { onLevelClick(level) }
          )
          LevelStatus.LOCKED -> LevelLockedCard(level = level)
        }
      }

      // Bonus Node: Peti Hadiah XP (Duolingo style)
      BonusRewardNode(isCurhat = isCurhat)
    }
  }
}

/**
 * Level 1: DONE green check +100 XP (small card)
 */
@Composable
private fun LevelDoneCard(level: LevelItem) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Node circle with green check
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(Color(0xFF064E3B))
        .border(2.dp, NeonGreen, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Selesai",
        tint = NeonGreen,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    // Small Done Card
    GlowCard(
      modifier = Modifier
        .weight(1f)
        .height(64.dp),
      borderWidth = 1.dp,
      glowBrush = Brush.horizontalGradient(
        listOf(NeonGreen.copy(alpha = 0.6f), Color(0xFF047857).copy(alpha = 0.3f))
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Level ${level.levelNumber}: ${level.title}",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "✅", fontSize = 12.sp)
          }
          Text(
            text = level.subtitle,
            color = Color(0xFF6EE7B7),
            fontSize = 11.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(NeonGreen.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "+${level.xp} XP",
            color = NeonGreen,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

/**
 * Level 2: ACTIVE big card with blue glow,
 * play button blue-red gradient, text "Tap untuk belajar sama MIMI" + button "MULAI +50 XP" gradient blue to red
 */
@Composable
private fun LevelActiveCard(
  level: LevelItem,
  isCurhat: Boolean,
  onStartClick: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    // Glowing Node Circle with Mascot
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(if (isCurhat) Color(0xFF831843) else Color(0xFF1E3A8A))
        .border(
          2.5.dp,
          if (isCurhat) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed)) else CyberGradient,
          CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      MimiFoxMascot(size = 38.dp, showPulse = true)
    }

    Spacer(modifier = Modifier.width(14.dp))

    // ACTIVE Big Card with blue glow (or pink glow for Curhat)
    GlowCard(
      modifier = Modifier
        .weight(1f)
        .testTag("level_2_card"),
      borderWidth = 2.dp,
      glowBrush = if (isCurhat) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed, Color(0xFFEC4899)))
      else Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryRed, PrimaryBlue)),
      cornerRadius = 18.dp,
      onClick = onStartClick
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Top row: Level Badge + Play Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  if (isCurhat) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed))
                  else CyberGradient
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "LEVEL ${level.levelNumber} • SEDANG AKTIF",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }

          // Round Play Button with Blue-Red gradient
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                if (isCurhat) Brush.horizontalGradient(listOf(Color(0xFFEC4899), PrimaryRed))
                else CyberGradient
              )
              .clickable(onClick = onStartClick)
              .testTag("level_2_play_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Mulai Belajar",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Title & Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = level.iconEmoji, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = level.title,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Text subtitle
        Text(
          text = level.subtitle,
          color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF60A5FA),
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Kupas materi '${level.title}' bareng MIMI dari buku resmi & jurnal terpercaya!",
          color = TextSecondary,
          fontSize = 12.sp,
          lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Big Button "MULAI +50 XP" gradient blue to red
        CyberGradientButton(
          text = if (isCurhat) "MULAI CURHAT (+50 XP)" else "MULAI +50 XP",
          onClick = onStartClick,
          icon = if (isCurhat) "❤️" else "⚡",
          modifier = Modifier.fillMaxWidth(),
          testTag = "mulai_button_level_2"
        )
      }
    }
  }
}

/**
 * Level 3 & 4: LOCKED gray 40% opacity
 */
@Composable
private fun LevelLockedCard(level: LevelItem) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .alpha(0.40f),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Locked Circle Node
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(Color(0xFF1E293B))
        .border(1.5.dp, Color(0xFF475569), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = "Terkunci",
        tint = Color(0xFF94A3B8),
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    // Locked Card
    Box(
      modifier = Modifier
        .weight(1f)
        .height(64.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(CardBg)
        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
        .padding(horizontal = 14.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Level ${level.levelNumber}: ${level.title}",
              color = Color(0xFFCBD5E1),
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "🔒", fontSize = 11.sp)
          }
          Text(
            text = level.subtitle,
            color = Color(0xFF64748B),
            fontSize = 11.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF334155))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "+${level.xp} XP",
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

/**
 * Bonus Checkpoint Node on path
 */
@Composable
private fun BonusRewardNode(isCurhat: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(if (isCurhat) Color(0xFF831843) else Color(0xFF312E81))
        .border(1.5.dp, if (isCurhat) Color(0xFFF472B6) else PrimaryBlue, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(text = if (isCurhat) "💖" else "🎁", fontSize = 22.sp)
    }

    Spacer(modifier = Modifier.width(14.dp))

    Box(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF131D2E))
        .border(
          1.dp,
          if (isCurhat) Color(0xFFF472B6).copy(alpha = 0.35f) else PrimaryBlue.copy(alpha = 0.3f),
          RoundedCornerShape(14.dp)
        )
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isCurhat) "Peti Self-Care & Motivasi" else "Peti Hadiah XP Mingguan",
            color = if (isCurhat) Color(0xFFF472B6) else Color(0xFF93C5FD),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Text(
            text = "Buka setelah Level 2 selesai • Bonus 250 XP",
            color = TextSecondary,
            fontSize = 11.sp
          )
        }
        Text(text = "⚡ +250", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}
