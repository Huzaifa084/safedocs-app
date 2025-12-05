package org.devaxiom.safedocs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController

        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.documentsFragment,
                R.id.sharedWithMeFragment,
                R.id.familyFragment,
                R.id.profileFragment
            )
        )
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfig)
        NavigationUI.setupWithNavController(bottomNav, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showBottom = destination.id != R.id.loginFragment
            bottomNav.visibility = if (showBottom) View.VISIBLE else View.GONE
            if (!showBottom) {
                toolbar.title = getString(R.string.app_name)
            }
        }
    }
}
