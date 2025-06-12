package com.ruparts.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.ruparts.app.core.navigation.NavigationManager
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.features.authorization.data.repository.AuthRepository
import com.ruparts.app.features.menu.presentation.MenuFragmentDirections
import com.ruparts.app.features.menu.presentation.model.MenuUiEffect
import com.ruparts.app.model.MainUiEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var navigationManager: NavigationManager

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var logoutButton: Button

    private lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up Jetpack Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        headerView = navigationView.getHeaderView(0)
        userName = headerView.findViewById(R.id.user_name)
        logoutButton = findViewById(R.id.nav_logout_button)

        navigationView.inflateMenu(R.menu.navigation_menu)

        lifecycleScope.launch {
            viewModel.screenState.collect { state ->
                userName.text = state.userName
            }
        }

        navigationView.setupWithNavController(navController)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_tasks -> navController.navigate(R.id.taskslistFragment)
//                R.id.nav_placement ->
//                R.id.nav_work_with_product ->
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        logoutButton.setOnClickListener {
            viewModel.logout()
        }

        observeNavigationEvents()
        collectUiEffects()
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

    private fun collectUiEffects() {
        lifecycleScope.launch {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is MainUiEffect.NavigateToAuth -> {
                        navController.navigate(R.id.authFragment)
                        drawerLayout.closeDrawers()
                    }
                }
            }
        }
    }

}
