package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.devaxiom.safedocs.auth.AuthViewModel
import androidx.navigation.fragment.findNavController
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

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
            startActivityForResult(intent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (!idToken.isNullOrEmpty()) {
                    viewModel.loginWithGoogle(idToken,
                        onSuccess = {
                            // TODO: persist token and user info
                            findNavController().navigate(R.id.action_login_to_documents)
                        },
                        onError = {
                            // TODO: show error to user
                        }
                    )
                }
            } catch (e: ApiException) {
                // TODO: show error
            }
        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
