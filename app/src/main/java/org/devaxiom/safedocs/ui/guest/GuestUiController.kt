package org.devaxiom.safedocs.ui.guest

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import org.devaxiom.safedocs.data.security.SessionManager
import org.devaxiom.safedocs.events.AuthEventBus
import org.devaxiom.safedocs.ui.auth.LoginPrompt
import org.devaxiom.safedocs.data.auth.AuthState

object GuestUiController {
    /**
     * Binds a guest banner's visibility and button to global auth state.
     * Shows banner when in guest mode and hides it after login.
     */
    fun bind(
        fragment: Fragment,
        lifecycleOwner: LifecycleOwner,
        bannerView: View,
        signInButton: View,
        promptMessage: String,
        onAuthenticated: () -> Unit
    ) {
        val context = fragment.requireContext()
        val sessionManager = SessionManager(context)

        // Initialize observable state and set initial visibility
        AuthState.init(context)
        bannerView.visibility = if (sessionManager.isGuest()) View.VISIBLE else View.GONE

        signInButton.setOnClickListener {
            LoginPrompt.show(fragment.parentFragmentManager, promptMessage)
        }

        // Global unauthenticated events
        AuthEventBus.unauthenticated.observe(lifecycleOwner) {
            bannerView.visibility = View.VISIBLE
        }

        // Observe AuthState for early banner hide/show at app start
        AuthState.isGuest.observe(lifecycleOwner) { isGuest ->
            bannerView.visibility = if (isGuest) View.VISIBLE else View.GONE
        }

        // Bottom sheet login success
        fragment.parentFragmentManager.setFragmentResultListener("login_success", lifecycleOwner) { _, _ ->
            bannerView.visibility = View.GONE
            onAuthenticated()
        }
    }
}
