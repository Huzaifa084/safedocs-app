package org.devaxiom.safedocs.ui.auth

import androidx.fragment.app.FragmentManager

/**
 * Central navigation helper for auth actions.
 */
object AuthNavigator {
    fun showSignIn(fm: FragmentManager, message: String) {
        LoginPrompt.show(fm, message)
    }
}
