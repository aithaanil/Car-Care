package uk.ac.tees.mad.carcare.ui.screens.login

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.carcare.model.dataclass.firebase.AuthResult
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.ui.screens.CarCareAppViewModel

class LogInScreenViewModel(
    private val authRepository: AuthRepository
): CarCareAppViewModel() {
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
}