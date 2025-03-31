package uk.ac.tees.mad.carcare.model.utils

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.carcare.R
import uk.ac.tees.mad.carcare.model.dataclass.firebase.SignInResult
import uk.ac.tees.mad.carcare.model.dataclass.firebase.UserDataGoogle

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val auth: FirebaseAuth
) {

    companion object {
        private const val TAG = "GoogleAuthUiClient"
    }

    init {
        Log.d(TAG, "GoogleAuthUiClient: Constructor called")
        Log.d(TAG, "oneTapClient: $oneTapClient")
        Log.d(TAG, "auth: $auth")
    }

    suspend fun signIn(): IntentSender? {
        val request = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false).build()
        ).setAutoSelectEnabled(true).build()
        return try {
            Log.d(TAG, "signIn: ok")
            oneTapClient.beginSignIn(request).await().pendingIntent.intentSender
        } catch (e: Exception) {
            Log.e(TAG, "signIn: ", e)
            throw e // Re-throw the exception for better debugging
        }
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = try {
            oneTapClient.getSignInCredentialFromIntent(intent)
        } catch (e: ApiException) {
            Log.e(TAG, "signInWithIntent: ", e)
            return SignInResult(
                data = null, errorMessage = "Error getting credentials: ${e.message}"
            )
        }

        return try {
            val googleIdToken = credential.googleIdToken
            val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = auth.signInWithCredential(googleCredential).await()

            val user = authResult.user
            SignInResult(
                data = UserDataGoogle(
                    userId = user?.uid ?: "",
                    username = user?.displayName,
                    profilePictureUrl = user?.photoUrl.toString()
                ), errorMessage = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "signInWithIntent: ", e)
            SignInResult(data = null, errorMessage = "Authentication failed: ${e.message}")
        }
    }

    fun getSignedInUser(): UserDataGoogle? {
        return auth.currentUser?.run {
            UserDataGoogle(
                userId = uid, username = displayName, profilePictureUrl = photoUrl.toString()
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "signOut: ", e)
        }
    }
}