package com.ruparts.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.ruparts.app.core.navigation.NavigationManager
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.model.MainUiEffect
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var navigationManager: NavigationManager

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var logoutButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var userName: TextView

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        headerView = navigationView.getHeaderView(0)
        userName = headerView.findViewById(R.id.user_name)
        logoutButton = findViewById(R.id.nav_logout_button)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        setupNavigation()

        logoutButton.setOnClickListener {
            viewModel.logout()
        }

        collectUiEffects()
        collectScreenState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.inflateMenu(R.menu.navigation_menu)
        navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            toolbar.subtitle = null
            when(destination.id) {
                R.id.authFragment -> toolbar.isVisible = false
                else -> toolbar.isVisible = true
            }
        }
    }

    private fun collectScreenState() {
        viewModel.screenState.collectWhileStarted(lifecycleOwner = this) { state ->
            userName.text = state.userName
        }
    }

    private fun collectUiEffects() {
        viewModel.uiEffect.collectWhileStarted(lifecycleOwner = this) { effect ->
            when (effect) {
                is MainUiEffect.NavigateToAuth -> {
                    val bundle = bundleOf(
                        "showAuthError" to effect.showError
                    )
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()
                    navController.navigate(R.id.authFragment, bundle, navOptions)
                    drawerLayout.closeDrawers()
                }
            }
        }
    }
}
