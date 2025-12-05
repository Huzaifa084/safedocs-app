package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.auth.AuthViewModel
import org.devaxiom.safedocs.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrEmpty()) {
                Log.d("LoginFragment", "Got Google ID Token, attempting to log in with backend.")
                viewModel.loginWithGoogle(idToken,
                    onSuccess = {
                        Log.d("LoginFragment", "Backend login success! Navigating to documents.")
                        // It's good practice to navigate on the main thread.
                        activity?.runOnUiThread {
                            findNavController().navigate(R.id.action_login_to_documents)
                        }
                    },
                    onError = { errorMessage ->
                        Log.e("LoginFragment", "Backend login failed: $errorMessage")
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Login Failed: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            } else {
                 Log.w("LoginFragment", "Google ID Token is null or empty after successful sign-in.")
                 Toast.makeText(context, "Could not get Google credentials. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("LoginFragment", "Google Sign-In failed. Status code: ${e.statusCode}", e)
            Toast.makeText(context, "Google Sign-In failed. Please check your network connection and try again.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_server_client_id))
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(requireContext(), gso)
            val intent = client.signInIntent
            googleSignInLauncher.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
