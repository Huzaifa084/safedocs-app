package org.devaxiom.safedocs.ui.shared

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
import org.devaxiom.safedocs.databinding.FragmentSharedByMeBinding
import org.devaxiom.safedocs.ui.document.DocumentAdapter
import org.devaxiom.safedocs.data.security.SessionManager

class SharedByMeFragment : Fragment() {

    private var _binding: FragmentSharedByMeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedDocsViewModel by viewModels()
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedByMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.documents.observe(viewLifecycleOwner) { documents ->
            binding.swipeRefreshSharedByMe.isRefreshing = false
            documentAdapter.submitList(documents)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.swipeRefreshSharedByMe.isRefreshing = false
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
        }

        binding.swipeRefreshSharedByMe.setOnRefreshListener {
            viewModel.fetchSharedByMeDocuments()
        }

        // Initial load with guest gating
        binding.swipeRefreshSharedByMe.isRefreshing = true
        val sessionManager = SessionManager(requireContext())
        if (sessionManager.isGuest()) {
            binding.swipeRefreshSharedByMe.isRefreshing = false
        } else {
            viewModel.fetchSharedByMeDocuments()
        }

        // Refresh after login from bottom sheet
        parentFragmentManager.setFragmentResultListener("login_success", viewLifecycleOwner) { _, _ ->
            binding.swipeRefreshSharedByMe.isRefreshing = true
            viewModel.fetchSharedByMeDocuments()
        }
    }

    private fun setupRecyclerView() {
        documentAdapter = DocumentAdapter { document ->
            val bundle = bundleOf("documentId" to document.id)
            findNavController().navigate(R.id.action_global_to_document_details, bundle)
        }
        binding.recyclerSharedByMe.apply {
            adapter = documentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
