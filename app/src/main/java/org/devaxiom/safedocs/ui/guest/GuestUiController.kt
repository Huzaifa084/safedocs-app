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
     *
     * @param titleResId Optional resource ID for the title text.
     * @param subtitleResId Optional resource ID for the subtitle text.
     * @param iconResId Optional resource ID for the icon drawable.
     */
    fun bind(
        fragment: Fragment,
        lifecycleOwner: LifecycleOwner,
        bannerView: View,
        signInButton: View,
        titleResId: Int? = null,
        subtitleResId: Int? = null,
        iconResId: Int? = null,
        onAuthenticated: () -> Unit
    ) {
        val context = fragment.requireContext()
        val sessionManager = SessionManager(context)

        // customize banner content if resources are provided
        if (titleResId != null) {
            bannerView.findViewById<android.widget.TextView>(org.devaxiom.safedocs.R.id.tvTitle)?.setText(titleResId)
        }
        if (subtitleResId != null) {
            bannerView.findViewById<android.widget.TextView>(org.devaxiom.safedocs.R.id.tvSubtitle)?.setText(subtitleResId)
        }
        if (iconResId != null) {
            bannerView.findViewById<android.widget.ImageView>(org.devaxiom.safedocs.R.id.ivIcon)?.setImageResource(iconResId)
        }

        // Initialize observable state and set initial visibility
        AuthState.init(context)
        bannerView.visibility = if (sessionManager.isGuest()) View.VISIBLE else View.GONE

        signInButton.setOnClickListener {
            // Trigger the bottom sheet prompt, using title as the prompt message if available
            val promptMsg = if (subtitleResId != null) context.getString(subtitleResId) else "Sign in to continue"
            LoginPrompt.show(fragment.parentFragmentManager, promptMsg)
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
