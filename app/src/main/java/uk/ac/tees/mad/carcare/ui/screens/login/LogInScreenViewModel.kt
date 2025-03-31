package uk.ac.tees.mad.carcare.ui.screens.login

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.carcare.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.carcare.model.dataclass.firebase.SignInResult
import uk.ac.tees.mad.carcare.model.dataclass.firebase.SignInState
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.utils.AppointmentSynchronizer
import uk.ac.tees.mad.carcare.model.utils.GoogleAuthUiClient

class LogInScreenViewModel(
    private val authRepository: AuthRepository,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val appointmentSynchronizer: AppointmentSynchronizer
) : ViewModel() {
    private val _logInResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val logInResult: StateFlow<AuthResult<Boolean>> = _logInResult.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _isLogInMode = MutableStateFlow(true)
    val isLogInMode = _isLogInMode.asStateFlow()

    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun switchSignInMode() {
        _isLogInMode.value = !_isLogInMode.value
    }

    fun logIn(email: String, pass: String) {
        authRepository.signIn(email, pass).onEach { result ->
            _logInResult.value = result
        }.launchIn(viewModelScope)
    }

    fun startSync() {
        appointmentSynchronizer.startSync()
    }

    // New functions for Google Sign-In
    fun resetState() {
        _signInState.value = SignInState()
    }

    fun onSignInResult(result: SignInResult) {
        _signInState.value = _signInState.value.copy(
            isSignInSuccessful = result.data != null, signInError = result.errorMessage
        )
        if (result.data != null) {
            _logInResult.value = AuthResult.Success(true)
        }
    }

    suspend fun logInWithGoogle(): IntentSender? {
        return try {
            googleAuthUiClient.signIn()
        } catch (e: Exception) {
            Log.e("LogInScreenViewModel", "Error during Google Sign-In", e)
            // Handle the exception appropriately, e.g., show an error to the user
            null
        }
    }

    fun signInWithIntent(intent: Intent) {
        viewModelScope.launch {
            val signInResult = googleAuthUiClient.signInWithIntent(intent)
            onSignInResult(signInResult)
        }
    }
}