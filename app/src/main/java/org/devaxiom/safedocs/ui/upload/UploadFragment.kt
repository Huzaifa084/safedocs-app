package org.devaxiom.safedocs.ui.upload

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
import org.devaxiom.safedocs.databinding.FragmentUploadBinding
import org.devaxiom.safedocs.ui.auth.LoginBottomSheetFragment

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UploadViewModel by viewModels()

    private var selectedFileUri: Uri? = null

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedFileUri = it
                binding.tvSelectedFileName.text = "File selected: ${it.path}"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle guest mode: show nudge if guest, else show form
        org.devaxiom.safedocs.ui.guest.GuestUiController.bind(
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            bannerView = binding.guestNudge.root,
            signInButton = binding.guestNudge.btnGoogle,
            titleResId = org.devaxiom.safedocs.R.string.guest_upload_title,
            subtitleResId = org.devaxiom.safedocs.R.string.guest_upload_subtitle,
            iconResId = org.devaxiom.safedocs.R.drawable.ic_upload
        ) {
            // On successful login, the GuestUiController hides the banner.
            // We just need to ensure the form is visible.
            binding.uploadFormGroup.isVisible = true
        }

        // Initially hide form if guest (GuestUiController shows banner)
        val sessionManager = org.devaxiom.safedocs.data.security.SessionManager(requireContext())
        binding.uploadFormGroup.isVisible = !sessionManager.isGuest()

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.rgVisibility.setOnCheckedChangeListener { _, checkedId ->
            binding.tilShareWith.isVisible = (checkedId == binding.rbShared.id)
        }

        binding.btnSelectFile.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        binding.btnUpload.setOnClickListener {
            handleUpload()
        }
    }

    private fun observeViewModel() {
        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UploadState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.btnUpload.isEnabled = false
                }

                is UploadState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.btnUpload.isEnabled = true
                    Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                is UploadState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.btnUpload.isEnabled = true
                    Toast.makeText(context, "Upload failed: ${state.message}", Toast.LENGTH_LONG)
                        .show()
                }

                is UploadState.RequireAuth -> {
                    binding.progressBar.isVisible = false
                    binding.btnUpload.isEnabled = true
                    // Show a reusable bottom sheet to prompt sign-in
                    LoginBottomSheetFragment.newInstance(message = state.message)
                        .show(parentFragmentManager, "LoginBottomSheet")
                }
            }
        }
    }

    private fun handleUpload() {
        val title = binding.etTitle.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val selectedRadioButton =
            view?.findViewById<RadioButton>(binding.rgVisibility.checkedRadioButtonId)
        val visibility = selectedRadioButton?.text.toString().uppercase()
        val shareWith = binding.etShareWith.text.toString().trim().takeIf { it.isNotEmpty() }

        if (title.isEmpty() || selectedFileUri == null) {
            Toast.makeText(context, "Title and file are required", Toast.LENGTH_SHORT).show()
            return
        }

        selectedFileUri?.let {
            viewModel.uploadDocument(title, category, visibility, shareWith, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
