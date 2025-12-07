package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.auth.AuthViewModel
import org.devaxiom.safedocs.data.security.SessionManager
import org.devaxiom.safedocs.databinding.FragmentProfileBinding
import org.devaxiom.safedocs.ui.guest.GuestUiController

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind user info from the correct SessionManager
        binding.tvUserName.text = sessionManager.getUserFullName() ?: "User"
        binding.tvUserEmail.text = sessionManager.getUserEmail() ?: "No email found"

        // Centralized guest nudge across tabs
        GuestUiController.bind(
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            bannerView = binding.guestNudge.root,
            signInButton = binding.guestNudge.btnGoogle,
            promptMessage = getString(R.string.guest_prompt_message)
        ) {
            // Update visible profile info after login
            binding.tvUserName.text = sessionManager.getUserFullName() ?: "User"
            binding.tvUserEmail.text = sessionManager.getUserEmail() ?: "No email found"
            setupLogout()
        }

        // Show either Logout (auth) or Nudge (guest)
        if (sessionManager.isGuest()) {
            binding.btnLogout.visibility = View.GONE
        } else {
            setupLogout()
        }
    }

    private fun setupLogout() {
        binding.btnLogout.visibility = View.VISIBLE
        // Logout button logic
        binding.btnLogout.setOnClickListener {
            signOutAndNavigate()
        }
    }

    private fun signOutAndNavigate() {
        // 1. Sign out from Google to allow account switching
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.signOut().addOnCompleteListener { 
            // 2. Clear local session using the ViewModel
            viewModel.logout {
                // 3. Navigate back to the login screen, clearing the back stack
                activity?.runOnUiThread {
                    findNavController().navigate(R.id.action_profile_to_login)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
