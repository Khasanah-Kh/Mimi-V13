package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.example.data.AuthRepository
import com.example.data.AuthResult
import com.example.data.UserRepository
import com.example.model.BottomNavTab
import com.example.ui.components.CyberBottomBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KuisScreen
import com.example.ui.screens.LencanaScreen
import com.example.ui.screens.MimiChatBottomSheet
import com.example.ui.screens.ProfilScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.TantanganScreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.MyApplicationTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.MimiViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Configure App Check with launch intent before any UI, ViewModels, or Firebase calls
    configureAppCheck(this, intent)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        RootAppNavigation()
      }
    }
  }
}

@Composable
fun RootAppNavigation() {
  val context = LocalContext.current
  val authRepository = remember { AuthRepository(context) }
  val currentUser by authRepository.authState.collectAsStateWithLifecycle(initialValue = authRepository.currentUser)
  val coroutineScope = rememberCoroutineScope()

  var isSigningIn by remember { mutableStateOf(false) }
  var signInErrorMessage by remember { mutableStateOf<String?>(null) }

  val user = currentUser
  if (user == null) {
    // Unauthenticated: Render Sign-In Gate only with Google Sign-In
    SignInScreen(
      isLoading = isSigningIn,
      errorMessage = signInErrorMessage,
      onSignInClick = {
        isSigningIn = true
        signInErrorMessage = null
        coroutineScope.launch {
          when (val result = authRepository.signInWithGoogle()) {
            is AuthResult.Success -> {
              isSigningIn = false
            }
            is AuthResult.Cancelled -> {
              isSigningIn = false
            }
            is AuthResult.Error -> {
              isSigningIn = false
              signInErrorMessage = result.message
            }
          }
        }
      }
    )
  } else {
    // Authenticated: Pass custom database ID to Firestore and initialize MimiViewModel
    val viewModel: MimiViewModel = viewModel(
      key = user.uid,
      factory = viewModelFactory {
        initializer {
          val databaseId = context.getString(R.string.firestore_database_id)
          val db = FirebaseFirestore.getInstance(databaseId)
          val userRepository = UserRepository(db)
          MimiViewModel(
            userRepository = userRepository,
            currentUserId = user.uid,
            initialName = user.displayName?.ifBlank { "Siswa Cyber" } ?: "Siswa Cyber",
            initialEmail = user.email
          )
        }
      }
    )

    MimiApp(
      viewModel = viewModel,
      onSignOut = {
        coroutineScope.launch {
          authRepository.signOut()
        }
      }
    )
  }
}

@Composable
fun MimiApp(
  viewModel: MimiViewModel = viewModel(),
  onSignOut: () -> Unit = {}
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  // BackHandler to handle dismissing chat or returning to Home tab
  BackHandler(enabled = uiState.isChatOpen || uiState.currentTab != BottomNavTab.BERANDA) {
    if (uiState.isChatOpen) {
      viewModel.closeChat()
    } else if (uiState.currentTab != BottomNavTab.BERANDA) {
      viewModel.setTab(BottomNavTab.BERANDA)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkNavyBg,
    bottomBar = {
      CyberBottomBar(
        currentTab = uiState.currentTab,
        onTabSelected = { tab ->
          viewModel.setTab(tab)
        }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(DarkNavyBg)
        .statusBarsPadding()
        .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
      when (uiState.currentTab) {
        BottomNavTab.BERANDA -> {
          HomeScreen(
            uiState = uiState,
            levels = uiState.currentLevels,
            onSelectEducationLevel = { level -> viewModel.setEducationLevel(level) },
            onSelectClass = { cls -> viewModel.setSelectedClass(cls) },
            onLevelClick = { levelItem -> viewModel.selectLevelItem(levelItem) }
          )
        }
        BottomNavTab.TANTANGAN -> {
          TantanganScreen(
            uiState = uiState,
            onChallengeRival = { viewModel.challengeRival() }
          )
        }
        BottomNavTab.KUIS -> {
          KuisScreen(
            uiState = uiState,
            onAnswerSelected = { optionIndex -> viewModel.answerQuiz(optionIndex) },
            onRestartQuiz = { viewModel.restartQuiz() }
          )
        }
        BottomNavTab.LENCANA -> {
          LencanaScreen(
            badges = uiState.badges
          )
        }
        BottomNavTab.PROFIL -> {
          ProfilScreen(
            uiState = uiState,
            onSignOut = onSignOut
          )
        }
      }
    }

    // Mimi Chat Bottom Sheet (slides up when Level is tapped)
    MimiChatBottomSheet(
      isOpen = uiState.isChatOpen,
      onDismiss = { viewModel.closeChat() },
      messages = uiState.chatMessages,
      isTyping = uiState.isMimiTyping,
      suggestedPrompts = viewModel.getSuggestedPrompts(),
      selectedLevel = uiState.selectedEducationLevel,
      selectedClass = uiState.selectedClass,
      onSelectLevel = { level -> viewModel.setEducationLevel(level) },
      onSelectClass = { cls -> viewModel.setSelectedClass(cls) },
      onSendMessage = { text -> viewModel.sendMessage(text) },
      latestXpNotification = uiState.latestXpNotification
    )
  }
}
