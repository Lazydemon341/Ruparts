package com.ruparts.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.ruparts.app.core.navigation.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    
    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up Jetpack Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        observeNavigationEvents()
    }
    
    private fun observeNavigationEvents() {
        navigationManager.navigationEvents
            .onEach { event ->
                when (event) {
                    is NavigationManager.NavigationEvent.NavigateToAuth -> {
                        // Navigate to auth fragment and clear the back stack
                        val bundle = bundleOf(
                            "showAuthError" to event.showAuthError
                        )
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.nav_graph, true)
                            .build()
                        navController.navigate(R.id.authFragment, bundle, navOptions)
                    }
                }
            }
            .launchIn(lifecycleScope)
    }
}
