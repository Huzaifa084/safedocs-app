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
    
    // Filter State
    private var currentSearch: String? = null
    private var currentCategory: String? = null
    private var expiryFrom: String? = null
    private var expiryTo: String? = null

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
            bannerView = binding.guestNudge.root,
            signInButton = binding.guestNudge.btnGoogle,
            titleResId = R.string.guest_documents_title,
            subtitleResId = R.string.guest_documents_subtitle,
            iconResId = R.drawable.ic_nav_documents,
            onAuthenticated = {
                refreshWithFilters()
            }
        )

        PostLoginRefresher.bind(this, viewLifecycleOwner) {
            refreshWithFilters()
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
                // Suppress raw errors and show friendly guest nudge
                binding.guestNudge.root.visibility = View.VISIBLE
            } else if (!error.isNullOrEmpty()) {
                Toast.makeText(context, "Error loading documents: $error", Toast.LENGTH_LONG).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshWithFilters()
        }

        // Search & Filter UI
        binding.recyclerDocuments.setPadding(16, 16, 16, 16)
        // TODO: Add a top app bar search and filter actions wired to below



        binding.chipGroupInfo.setOnCheckedChangeListener { _, checkedId ->
            refreshWithFilters()
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
            binding.guestNudge.root.visibility = View.GONE
            refreshWithFilters()
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

    private fun refreshWithFilters() {
        if (_binding == null) return
        binding.swipeRefresh.isRefreshing = true
        
        val visibility = when (binding.chipGroupInfo.checkedChipId) {
            org.devaxiom.safedocs.R.id.chipFamily -> "FAMILY"
            org.devaxiom.safedocs.R.id.chipShared -> "SHARED"
            else -> "PERSONAL"
        }
        
        viewModel.fetchDocuments(
            visibility = visibility,
            search = currentSearch,
            category = currentCategory,
            expiryFrom = expiryFrom,
            expiryTo = expiryTo
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
