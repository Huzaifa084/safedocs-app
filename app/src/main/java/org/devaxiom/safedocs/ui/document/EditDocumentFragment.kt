package org.devaxiom.safedocs.ui.document

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.databinding.FragmentEditDocumentBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditDocumentFragment : Fragment() {

    private var _binding: FragmentEditDocumentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditDocumentViewModel by viewModels()

    private var selectedFileUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @SuppressLint("SetTextI18n")
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedFileUri = it
                binding.tvSelectedFileName.text = "New file selected"
                binding.tvSelectedFileName.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val documentId = arguments?.getString("documentId")
        if (documentId == null) {
            Toast.makeText(context, "Error: Missing Document ID", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupListeners(documentId)
        observeViewModel()

        // Load data
        viewModel.fetchDocumentDetails(documentId)
    }

    private fun setupListeners(documentId: String) {
        binding.etExpiryDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnReplaceFile.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val category = binding.etCategory.text.toString().trim()
            val expiry = binding.etExpiryDate.text.toString().trim().takeIf { it.isNotEmpty() }
            
            // Map Radio Button to String
            // NOTE: The update API might not support visibility changes based on ApiService inspection, 
            // but we'll send what we can or adapt. 
            // The user's CURL didn't show visibility being updated in the URL, but the response showed it.
            // We will focus on Title, Category, Expiry, as per CURL.
            
            val selectedRbId = binding.rgVisibility.checkedRadioButtonId
            val selectedRb = binding.root.findViewById<RadioButton>(selectedRbId)
            val visibility = selectedRb?.text.toString().uppercase()

            if (title.isEmpty()) {
                binding.tilTitle.error = "Title is required"
                return@setOnClickListener
            }

            viewModel.updateDocument(
                documentId = documentId,
                title = title,
                category = category,
                visibility = visibility, // Might be ignored by repo if not in signature
                expiryDate = expiry,
                fileUri = selectedFileUri
            )
        }
    }

    private fun observeViewModel() {
        viewModel.document.observe(viewLifecycleOwner) { doc ->
            doc?.let {
                binding.etTitle.setText(it.title)
                binding.etCategory.setText(it.category)
                binding.etExpiryDate.setText(it.expiryDate?.let { date -> dateFormat.format(date) } ?: "")
                
                // Pre-select visibility
                when(it.visibility.uppercase()) {
                    "SHARED" -> binding.rbShared.isChecked = true
                    "FAMILY" -> binding.rbFamily.isChecked = true
                    else -> binding.rbPersonal.isChecked = true
                }
            }
        }

        viewModel.updateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnSave.isEnabled = false
                }
                is EditState.Success -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(context, "Document updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack() // Go back to details
                }
                is EditState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnSave.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.etExpiryDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
