package org.devaxiom.safedocs.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.devaxiom.safedocs.databinding.FragmentDocumentDetailsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DocumentDetailsFragment : Fragment() {

    private var _binding: FragmentDocumentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DocumentDetailsViewModel by viewModels()

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val documentId = arguments?.getString("documentId")
        if (documentId == null) {
            Toast.makeText(context, "Error: Document ID missing", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        viewModel.fetchDocumentDetails(documentId)

        viewModel.document.observe(viewLifecycleOwner) { document ->
            document?.let { doc ->
                binding.tvDocumentTitle.text = doc.title
                binding.tvDocumentCategory.text = doc.category
                binding.tvOwnerName.text = doc.ownerName
                binding.chipVisibility.text = doc.visibility
                doc.expiryDate?.let {
                    binding.tvExpiryDate.text = dateFormat.format(it)
                }
            }
        }

        viewModel.downloadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DownloadState.Loading -> {
                    binding.btnDownload.isEnabled = false
                    binding.btnDownload.text = "Downloading..."
                }
                is DownloadState.Success -> {
                    binding.btnDownload.isEnabled = true
                    binding.btnDownload.text = "Download"
                    Toast.makeText(context, "File saved to Downloads/SafeDocs", Toast.LENGTH_LONG).show()
                }
                is DownloadState.Error -> {
                    binding.btnDownload.isEnabled = true
                    binding.btnDownload.text = "Download"
                    Toast.makeText(context, "Download failed: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnDownload.setOnClickListener {
            viewModel.document.value?.let { doc -> viewModel.downloadDocument(doc) }
        }

        binding.btnDelete.setOnClickListener {
            // TODO: Implement delete confirmation dialog
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
