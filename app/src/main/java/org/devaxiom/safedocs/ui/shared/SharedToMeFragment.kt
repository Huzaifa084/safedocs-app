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
import org.devaxiom.safedocs.databinding.FragmentSharedToMeBinding
import org.devaxiom.safedocs.ui.document.DocumentAdapter

class SharedToMeFragment : Fragment() {

    private var _binding: FragmentSharedToMeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedDocsViewModel by viewModels()
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedToMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.documents.observe(viewLifecycleOwner) { documents ->
            binding.swipeRefreshSharedToMe.isRefreshing = false
            documentAdapter.submitList(documents)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.swipeRefreshSharedToMe.isRefreshing = false
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
        }

        binding.swipeRefreshSharedToMe.setOnRefreshListener {
            viewModel.fetchSharedWithMeDocuments()
        }

        // Initial load
        binding.swipeRefreshSharedToMe.isRefreshing = true
        viewModel.fetchSharedWithMeDocuments()
    }

    private fun setupRecyclerView() {
        documentAdapter = DocumentAdapter { document ->
            val bundle = bundleOf("documentId" to document.id)
            findNavController().navigate(R.id.action_global_to_document_details, bundle)
        }
        binding.recyclerSharedToMe.apply {
            adapter = documentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
