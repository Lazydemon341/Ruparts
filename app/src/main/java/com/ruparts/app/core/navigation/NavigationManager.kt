package com.ruparts.app.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    suspend fun navigateToAuth(showAuthError: Boolean = false) {
        _navigationEvents.emit(NavigationEvent.NavigateToAuth(showAuthError))
    }

    sealed class NavigationEvent {
        data class NavigateToAuth(val showAuthError: Boolean) : NavigationEvent()
    }
}
