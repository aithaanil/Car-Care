package uk.ac.tees.mad.carcare.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareFirestoreRepository

open class CarCareAppViewModel(
    private val authRepository: AuthRepository,
    private val carCareFirestoreRepository: CarCareFirestoreRepository
) : ViewModel() {
    fun launchCatching(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            Log.d(ERROR_TAG, throwable.message.orEmpty())
        }, block = block
    )

    companion object {
        const val ERROR_TAG = "CARCARE APP ERROR"
    }

    fun logOut() {
        authRepository.signOut()
    }
}