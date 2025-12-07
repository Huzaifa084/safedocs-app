package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.databinding.FragmentFamilyBinding
import org.devaxiom.safedocs.ui.document.DocumentAdapter
import org.devaxiom.safedocs.ui.document.DocumentViewModel
import org.devaxiom.safedocs.ui.guest.GuestUiController
import org.devaxiom.safedocs.ui.util.PostLoginRefresher

class FamilyFragment : Fragment() {

    private var _binding: FragmentFamilyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DocumentViewModel by viewModels()
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        GuestUiController.bind(
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            bannerView = binding.guestBannerFamily,
            signInButton = binding.btnGuestSignInFamily,
            promptMessage = getString(R.string.guest_prompt_message)
        ) {
            binding.swipeRefreshFamily.isRefreshing = true
            viewModel.fetchDocuments("FAMILY")
        }

        PostLoginRefresher.bind(this, viewLifecycleOwner) {
            binding.swipeRefreshFamily.isRefreshing = true
            viewModel.fetchDocuments("FAMILY")
        }

        viewModel.documents.observe(viewLifecycleOwner) { documents ->
            binding.swipeRefreshFamily.isRefreshing = false
            if (documents.isNullOrEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerFamilyDocuments.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.recyclerFamilyDocuments.visibility = View.VISIBLE
                documentAdapter.submitList(documents)
            }
        }

        binding.swipeRefreshFamily.setOnRefreshListener {
            viewModel.fetchDocuments("FAMILY")
        }

        binding.btnInvite.setOnClickListener {
            // TODO: show invite dialog and call /api/family/invite
        }

        // Initial load
        binding.swipeRefreshFamily.isRefreshing = true
        viewModel.fetchDocuments("FAMILY")
    }

    private fun setupRecyclerView() {
        documentAdapter = DocumentAdapter { document ->
            val bundle = bundleOf("documentId" to document.id)
            findNavController().navigate(R.id.action_global_to_document_details, bundle)
        }
        binding.recyclerFamilyDocuments.apply {
            adapter = documentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
