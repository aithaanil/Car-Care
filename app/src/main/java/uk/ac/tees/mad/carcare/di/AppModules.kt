package uk.ac.tees.mad.carcare.di

import androidx.room.Room
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareAppointmentRepository
import uk.ac.tees.mad.carcare.model.repository.CarCareFirestoreRepository
import uk.ac.tees.mad.carcare.model.room.CarCareDatabase
import uk.ac.tees.mad.carcare.model.utils.AppointmentSynchronizer
import uk.ac.tees.mad.carcare.model.utils.GoogleAuthUiClient
import uk.ac.tees.mad.carcare.ui.screens.CarCareAppViewModel
import uk.ac.tees.mad.carcare.ui.screens.booking.BookingScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.confirmpage.AppointmentConfirmationScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.history.AppointmentHistoryScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.home.HomeScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.login.LogInScreenViewModel
import uk.ac.tees.mad.carcare.ui.screens.profileandsettings.ProfileAndSettingsScreenViewModel
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

    // Local Journal Data Database
    single {
        Room.databaseBuilder(
            androidApplication(), CarCareDatabase::class.java, "carcare_appointment_data_database"
        ).build()
    }
    single {
        val database = get<CarCareDatabase>()
        database.carCareAppointmentDao()
    }
    single { CarCareAppointmentRepository(get()) }

    single { AppointmentSynchronizer(get(), get(), get()) }

    viewModelOf(::CarCareAppViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::BookingScreenViewModel)
    viewModelOf(::AppointmentConfirmationScreenViewModel)
    viewModelOf(::AppointmentHistoryScreenViewModel)
    viewModelOf(::ProfileAndSettingsScreenViewModel)

}

val appModules = listOf(appModule)