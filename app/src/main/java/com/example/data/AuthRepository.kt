package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthRepository"

sealed interface AuthResult {
  data class Success(val user: FirebaseUser) : AuthResult
  object Cancelled : AuthResult
  data class Error(val message: String) : AuthResult
}

class AuthRepository(
  private val context: Context,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
  private val credentialManager = CredentialManager.create(context)

  val currentUser: FirebaseUser?
    get() = auth.currentUser

  val authState: Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      trySend(firebaseAuth.currentUser)
    }
    auth.addAuthStateListener(listener)
    awaitClose { auth.removeAuthStateListener(listener) }
  }

  suspend fun signInWithGoogle(): AuthResult {
    return try {
      val serverClientId = context.getString(R.string.default_web_client_id)
      val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

      val response = credentialManager.getCredential(
        request = request,
        context = context
      )

      val credential = response.credential
      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
      val idToken = googleIdTokenCredential.idToken

      val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
      val authResult = auth.signInWithCredential(firebaseCredential).await()
      val user = authResult.user ?: error("Firebase sign in succeeded but user is null")
      Log.d(TAG, "Google Sign-In successful for user: ${user.uid} (${user.email})")
      AuthResult.Success(user)
    } catch (e: GetCredentialCancellationException) {
      Log.w(TAG, "User cancelled Google Sign-In prompt", e)
      AuthResult.Cancelled
    } catch (e: GetCredentialException) {
      Log.e(TAG, "Credential Manager error during sign in", e)
      AuthResult.Error(e.localizedMessage ?: "Gagal masuk dengan Google")
    } catch (e: Exception) {
      Log.e(TAG, "Unexpected error during sign in", e)
      AuthResult.Error(e.localizedMessage ?: "Terjadi kesalahan saat masuk")
    }
  }

  suspend fun signOut() {
    try {
      auth.signOut()
      credentialManager.clearCredentialState(ClearCredentialStateRequest())
      Log.d(TAG, "User signed out successfully")
    } catch (e: Exception) {
      Log.w(TAG, "Error clearing credential state during sign out", e)
    }
  }
}
