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
            Toast.makeText(context, "Error loading documents: $error", Toast.LENGTH_LONG).show()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchDocuments("PERSONAL")
        }

        // Initial load
        binding.swipeRefresh.isRefreshing = true
        viewModel.fetchDocuments("PERSONAL")
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
