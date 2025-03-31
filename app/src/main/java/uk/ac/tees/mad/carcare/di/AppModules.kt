package uk.ac.tees.mad.carcare.di

import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareFirestoreRepository
import uk.ac.tees.mad.carcare.model.utils.GoogleAuthUiClient
import uk.ac.tees.mad.carcare.ui.screens.CarCareAppViewModel
import uk.ac.tees.mad.carcare.ui.screens.booking.BookingScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.home.HomeScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.login.LogInScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.signup.SignUpScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.splash.SplashViewModel


val appModule = module {
    single { FirebaseAuth.getInstance() }

    singleOf(::AuthRepository)

    viewModelOf(::SplashViewModel)

    viewModelOf(::LogInScreenViewModel)
    viewModelOf(::SignUpScreenViewModel)

    // Google One Tap Client
    single { Identity.getSignInClient(androidContext()) }

    // GoogleAuthUiClient
    single {
        GoogleAuthUiClient(
            context = androidContext(), oneTapClient = get(), auth = get()
        )
    }

    single { FirebaseFirestore.getInstance() }
    single { CarCareFirestoreRepository(get()) }

    viewModelOf(::CarCareAppViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::BookingScreenViewModel)

}

val appModules = listOf(appModule)