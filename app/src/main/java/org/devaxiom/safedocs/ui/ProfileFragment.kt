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
import org.devaxiom.safedocs.data.security.UserManager
import org.devaxiom.safedocs.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userManager = UserManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind user info
        binding.tvUserName.text = userManager.getUserFullName() ?: "User"
        binding.tvUserEmail.text = userManager.getUserEmail() ?: "No email found"

        // Logout button logic
        binding.btnLogout.setOnClickListener {
            signOutAndNavigateToLogin()
        }
    }

    private fun signOutAndNavigateToLogin() {
        // 1. Sign out from Google to allow account switching
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // 2. Clear local session using the ViewModel
            viewModel.logout {
                // 3. Navigate back to the login screen on the main thread
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
