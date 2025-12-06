package org.devaxiom.safedocs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.devaxiom.safedocs.data.security.TokenManager
import org.devaxiom.safedocs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the Toolbar as the app's action bar
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        // Conditional start destination logic
        if (tokenManager.getToken() != null) {
            navGraph.setStartDestination(R.id.documentsFragment)
        }
        navController.graph = navGraph

        // --- Standard Bottom Nav and ActionBar setup ---
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.documentsFragment,
                R.id.sharedWithMeFragment,
                R.id.familyFragment,
                R.id.profileFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Show/hide bottom nav and toolbar based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevelDestination = appBarConfiguration.topLevelDestinations.contains(destination.id)
            binding.navView.isVisible = isTopLevelDestination
            supportActionBar?.setDisplayHomeAsUpEnabled(!isTopLevelDestination)
            supportActionBar?.setDisplayShowHomeEnabled(!isTopLevelDestination)

            // Also hide the entire app bar for the login screen
            if (destination.id == R.id.loginFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }
    }

    // Handle the Up button navigation
    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
