package org.devaxiom.safedocs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import org.devaxiom.safedocs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                // Define top-level destinations where the Up button should not be shown
                R.id.documentsFragment,
                R.id.sharedWithMeFragment,
                R.id.familyFragment,
                R.id.profileFragment,
                R.id.loginFragment // The login screen is also a top-level destination
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // This listener now correctly handles UI visibility for all screens.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevelDestination = appBarConfiguration.topLevelDestinations.contains(destination.id)
            // The bottom nav should only be visible on the main app screens, not login.
            val isMainAppScreen = destination.id != R.id.loginFragment
            
            binding.navView.isVisible = isMainAppScreen
            supportActionBar?.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
