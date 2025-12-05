package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

        viewModel.documents.observe(viewLifecycleOwner) { documents ->
            binding.swipeRefresh.isRefreshing = false
            if (documents != null) {
                documentAdapter.submitList(documents)
                binding.tvEmpty.visibility = if (documents.isEmpty()) View.VISIBLE else View.GONE
            } else {
                // Handle the case where the document list is null
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
        documentAdapter = DocumentAdapter { /* TODO: Handle document click */ }
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
