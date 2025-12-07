package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.databinding.FragmentDocumentsBinding
import org.devaxiom.safedocs.ui.document.DocumentAdapter
import org.devaxiom.safedocs.ui.document.DocumentViewModel
import org.devaxiom.safedocs.data.security.SessionManager
import org.devaxiom.safedocs.ui.auth.LoginBottomSheetFragment
import org.devaxiom.safedocs.ui.auth.LoginPrompt
import org.devaxiom.safedocs.ui.guest.GuestUiController
import org.devaxiom.safedocs.events.AuthEventBus
import org.devaxiom.safedocs.ui.util.PostLoginRefresher

class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DocumentViewModel by viewModels()
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Centralized guest banner behavior
        val sessionManager = SessionManager(requireContext())
            GuestUiController.bind(
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            bannerView = binding.guestBanner,
            signInButton = binding.btnGuestSignIn,
            promptMessage = getString(R.string.guest_prompt_message),
            onAuthenticated = {
                binding.swipeRefresh.isRefreshing = true
                viewModel.fetchDocuments("PERSONAL")
            }
        )

            PostLoginRefresher.bind(this, viewLifecycleOwner) {
                binding.swipeRefresh.isRefreshing = true
                viewModel.fetchDocuments("PERSONAL")
            }

        binding.btnUpload.setOnClickListener {
            // Use direct navigation to avoid Safe Args issues
            findNavController().navigate(R.id.action_documents_to_upload)
        }

        viewModel.documents.observe(viewLifecycleOwner) { documents ->
            binding.swipeRefresh.isRefreshing = false
            if (documents != null) {
                documentAdapter.submitList(documents)
                binding.tvEmpty.visibility = if (documents.isEmpty()) View.VISIBLE else View.GONE
            } else {
                documentAdapter.submitList(emptyList())
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.swipeRefresh.isRefreshing = false
            val isGuest = sessionManager.isGuest()
            if (isGuest) {
                // Suppress raw 400 and show friendly banner
                binding.guestBanner.visibility = View.VISIBLE
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error loading documents: $error", Toast.LENGTH_LONG).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchDocuments("PERSONAL")
        }

        // Search & Filter UI
        binding.recyclerDocuments.setPadding(16, 16, 16, 16)
        // TODO: Add a top app bar search and filter actions wired to below

        // Example hooks (values to be set from UI once added):
        var currentSearch: String? = null
        var currentCategory: String? = null
        var expiryFrom: String? = null
        var expiryTo: String? = null

        fun refreshWithFilters() {
            binding.swipeRefresh.isRefreshing = true
            viewModel.fetchDocuments("PERSONAL", currentSearch, currentCategory, expiryFrom, expiryTo)
        }

        // Initial load
        binding.swipeRefresh.isRefreshing = true
        if (!sessionManager.isGuest()) {
            refreshWithFilters()
        } else {
            binding.swipeRefresh.isRefreshing = false
            // Keep list empty for guests and encourage sign-in via banner
            documentAdapter.submitList(emptyList())
            binding.tvEmpty.visibility = View.GONE
        }

        parentFragmentManager.setFragmentResultListener(
            "login_success",
            viewLifecycleOwner
        ) { _, _ ->
            binding.guestBanner.visibility = View.GONE
            binding.swipeRefresh.isRefreshing = true
            viewModel.fetchDocuments("PERSONAL")
        }
    }

    private fun setupRecyclerView() {
        documentAdapter = DocumentAdapter { document ->
            // Use direct navigation with a bundle to pass arguments
            val bundle = bundleOf("documentId" to document.id)
            findNavController().navigate(R.id.action_global_to_document_details, bundle)
        }
        binding.recyclerDocuments.apply {
            adapter = documentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
