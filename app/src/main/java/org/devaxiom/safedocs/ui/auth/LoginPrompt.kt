package org.devaxiom.safedocs.ui.auth

import androidx.fragment.app.FragmentManager

object LoginPrompt {
    fun show(fragmentManager: FragmentManager, message: String? = null) {
        LoginBottomSheetFragment.newInstance(message)
            .show(fragmentManager, "LoginBottomSheet")
    }
}
