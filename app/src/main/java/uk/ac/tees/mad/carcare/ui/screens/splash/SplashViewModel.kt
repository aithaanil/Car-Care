package uk.ac.tees.mad.carcare.ui.screens.splash

import androidx.lifecycle.ViewModel
import uk.ac.tees.mad.carcare.model.repository.AuthRepository
import uk.ac.tees.mad.carcare.model.utils.AppointmentSynchronizer
import uk.ac.tees.mad.carcare.ui.navigation.SubGraph
import uk.ac.tees.mad.carcare.ui.screens.CarCareAppViewModel

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val appointmentSynchronizer: AppointmentSynchronizer
) : ViewModel() {

    fun onAppStart(openAndPopUp: (Any, Any) -> Unit) {
        if(authRepository.isSignedIn()){
            appointmentSynchronizer.startSync()
            openAndPopUp(SubGraph.HomeGraph, SubGraph.SplashScreenGraph)
        } else {
            openAndPopUp(SubGraph.AuthGraph, SubGraph.SplashScreenGraph)
        }
    }

    fun openAuthGraph(openAndPopUp: (Any, Any) -> Unit) {
        openAndPopUp(SubGraph.AuthGraph, SubGraph.SplashScreenGraph)
    }
}