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
import android.app.AlertDialog
import org.devaxiom.safedocs.data.model.Document
import org.devaxiom.safedocs.R
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import android.util.Patterns

class DocumentDetailsFragment : Fragment() {

    private var _binding: FragmentDocumentDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DocumentDetailsViewModel by viewModels()
    private var pickedFileUri: Uri? = null
    private lateinit var pickFileLauncher: ActivityResultLauncher<String>

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentDetailsBinding.inflate(inflater, container, false)
        // Register file picker before Fragment is created to avoid IllegalStateException
        pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            pickedFileUri = uri
        }
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
        viewModel.fetchShares(documentId)

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
            viewModel.document.value?.let { doc ->
                viewModel.deleteDocument(doc.id) { ok, msg ->
                    if (ok) {
                        Toast.makeText(context, "Document deleted", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Delete failed: ${msg ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            viewModel.document.value?.let { doc ->
                showEditDialog(doc)
            }
        }

        // Confirm before deleting
        binding.btnDelete.setOnClickListener {
            val ctx = requireContext()
            AlertDialog.Builder(ctx)
                .setTitle("Delete Document")
                .setMessage("Are you sure you want to delete this document?")
                .setPositiveButton("Delete") { d, _ ->
                    viewModel.document.value?.let { doc ->
                        viewModel.deleteDocument(doc.id) { ok, msg ->
                            if (ok) {
                                Toast.makeText(context, "Document deleted", Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            } else {
                                Toast.makeText(context, "Delete failed: ${msg ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    d.dismiss()
                }
                .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                .show()
        }
        binding.btnAddShare.setOnClickListener {
            val email = binding.etShareEmail.text?.toString()?.trim()
            if (!email.isNullOrEmpty()) {
                viewModel.addShare(documentId, email) { ok ->
                    Toast.makeText(context, if (ok) "Shared" else "Share failed", Toast.LENGTH_SHORT).show()
                    if (ok) binding.etShareEmail.setText("")
                }
            }
        }

        val sharesAdapter = SharesAdapter { shareId ->
            viewModel.removeShare(documentId, shareId) { ok ->
                Toast.makeText(context, if (ok) "Removed" else "Remove failed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerShares.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerShares.adapter = sharesAdapter
        viewModel.shares.observe(viewLifecycleOwner) { shares ->
            sharesAdapter.submit(shares)
        }

        // Hide share section for non-shared documents to avoid 400 errors
        viewModel.document.observe(viewLifecycleOwner) { doc ->
            val isShared = doc?.visibility == "SHARED"
            binding.tvSharesLabel.visibility = if (isShared) View.VISIBLE else View.GONE
            binding.recyclerShares.visibility = if (isShared) View.VISIBLE else View.GONE
            binding.tilShareEmail.visibility = if (isShared) View.VISIBLE else View.GONE
            binding.btnAddShare.visibility = if (isShared) View.VISIBLE else View.GONE
        }
    }

    private fun showEditDialog(doc: Document) {
        val ctx = requireContext()
        val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_edit_document, null)
        val etTitle = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditTitle)
        val etCategory = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditCategory)
        val etExpiry = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditExpiry)
        val etShareWith = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditShareWith)
        val tilTitle = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilEditTitle)
        val tilCategory = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilEditCategory)
        val tilShareWith = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilEditShareWith)
        val btnPickFile = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnPickFile)
        val tvPickedFileName = dialogView.findViewById<android.widget.TextView>(R.id.tvPickedFileName)
        etTitle.setText(doc.title)
        etCategory.setText(doc.category)
        etExpiry.setText(doc.expiryDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "")

        btnPickFile.setOnClickListener {
            pickFileLauncher.launch("*/*")
        }

        // Material Date Picker for expiry
        etExpiry.isFocusable = false
        etExpiry.isClickable = true
        etExpiry.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select expiry date")
                .build()
            picker.addOnPositiveButtonClickListener { selection ->
                // selection is a UTC millis
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etExpiry.setText(sdf.format(java.util.Date(selection)))
            }
            picker.show(parentFragmentManager, "expiry_picker")
        }

        val dialog = AlertDialog.Builder(ctx)
            .setTitle("Edit Document")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()
        dialog.setOnShowListener {
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            fun validate(): Boolean {
                val titleOk = !etTitle.text.isNullOrBlank()
                val categoryOk = !etCategory.text.isNullOrBlank()
                val emailText = etShareWith.text?.toString()?.trim()
                val emailOk = emailText.isNullOrEmpty() || Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
                tilTitle.error = if (!titleOk) "Title is required" else null
                tilCategory.error = if (!categoryOk) "Category is required" else null
                tilShareWith.error = if (!emailOk) "Invalid email" else null
                return titleOk && categoryOk && emailOk
            }
            fun updatePickedFileName() {
                tvPickedFileName.text = pickedFileUri?.let { getDisplayName(it) } ?: "No file selected"
            }
            updatePickedFileName()
            positive.isEnabled = validate()
            val watcher = object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { positive.isEnabled = validate() }
                override fun afterTextChanged(s: android.text.Editable?) {}
            }
            etTitle.addTextChangedListener(watcher)
            etCategory.addTextChangedListener(watcher)
            etShareWith.addTextChangedListener(watcher)
            positive.setOnClickListener {
                val updated = doc.copy(
                    title = etTitle.text?.toString()?.trim() ?: doc.title,
                    category = etCategory.text?.toString()?.trim() ?: doc.category
                )
                val expiryStr = etExpiry.text?.toString()?.trim()?.ifEmpty { null }
                val shareWith = etShareWith.text?.toString()?.trim()?.ifEmpty { null }
                viewModel.updateDocument(updated, expiryStr, shareWith, pickedFileUri) { ok, msg ->
                    Toast.makeText(context, if (ok) "Updated" else "Update failed: ${msg ?: "Unknown"}", Toast.LENGTH_LONG).show()
                    if (ok) dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun getDisplayName(uri: Uri): String {
        var name = "selected_file"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = cursor.getString(index)
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
