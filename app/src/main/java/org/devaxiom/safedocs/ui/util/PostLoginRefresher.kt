package org.devaxiom.safedocs.ui.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 * Observes the global bottom-sheet login success and triggers a refresh callback.
 * Use this instead of re-creating adapters or duplicating fragment result listeners.
 */
object PostLoginRefresher {
    fun bind(fragment: Fragment, lifecycleOwner: LifecycleOwner, onRefresh: () -> Unit) {
        fragment.parentFragmentManager.setFragmentResultListener("login_success", lifecycleOwner) { _, _ ->
            onRefresh()
        }
    }
}
