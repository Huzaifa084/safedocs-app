package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.databinding.FragmentFamilyBinding
import org.devaxiom.safedocs.ui.family.FamilyAdapter
import org.devaxiom.safedocs.ui.family.FamilyOperationState
import org.devaxiom.safedocs.ui.family.FamilyViewModel
import org.devaxiom.safedocs.ui.guest.GuestUiController
import org.devaxiom.safedocs.ui.util.PostLoginRefresher

class FamilyFragment : Fragment() {

    private var _binding: FragmentFamilyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FamilyViewModel by viewModels()
    private lateinit var familyAdapter: FamilyAdapter

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
        setupListeners()
        observeViewModel()

        GuestUiController.bind(
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            bannerView = binding.guestNudgeFamily.root,
            signInButton = binding.guestNudgeFamily.btnGoogle,
            titleResId = R.string.guest_family_title,
            subtitleResId = R.string.guest_family_subtitle,
            iconResId = R.drawable.ic_nav_family
        ) {
            loadFamilies()
        }

        PostLoginRefresher.bind(this, viewLifecycleOwner) {
            loadFamilies()
        }

        // Initial load
        val sessionManager = org.devaxiom.safedocs.data.security.SessionManager(requireContext())
        if (!sessionManager.isGuest()) {
            loadFamilies()
        } else {
             binding.fabCreateFamily.isVisible = false
             binding.swipeRefreshFamily.isEnabled = false // Disable pull-to-refresh for guest
        }
    }

    private fun setupRecyclerView() {
        familyAdapter = FamilyAdapter { family ->
            // Navigate to Profile
            val bundle = bundleOf("familyId" to family.id)
            findNavController().navigate(R.id.action_family_to_profile, bundle)
        }
        binding.recyclerFamilies.apply {
            adapter = familyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.swipeRefreshFamily.setOnRefreshListener {
            loadFamilies()
        }

        binding.fabCreateFamily.setOnClickListener {
            showCreateFamilyDialog()
        }

        binding.btnCreateFamilyEmpty.setOnClickListener {
            showCreateFamilyDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.families.observe(viewLifecycleOwner) { families ->
            binding.swipeRefreshFamily.isRefreshing = false
            if (families.isNullOrEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerFamilies.visibility = View.GONE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.recyclerFamilies.visibility = View.VISIBLE
                familyAdapter.submitList(families)
            }
        }

        viewModel.operationState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FamilyOperationState.Loading -> binding.swipeRefreshFamily.isRefreshing = true
                is FamilyOperationState.Success -> {
                    binding.swipeRefreshFamily.isRefreshing = false
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is FamilyOperationState.Error -> {
                    binding.swipeRefreshFamily.isRefreshing = false
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun loadFamilies() {
        binding.swipeRefreshFamily.isRefreshing = true
        viewModel.fetchFamilies()
    }

    private fun showCreateFamilyDialog() {
        val input = EditText(requireContext())
        input.hint = "Family Name (e.g. Smith's House)"
        
        // Add some margin/padding
        val container = android.widget.FrameLayout(requireContext())
        val params = android.widget.FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val margin = (16 * resources.displayMetrics.density).toInt()
        params.marginStart = margin
        params.marginEnd = margin
        input.layoutParams = params
        container.addView(input)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create New Family")
            .setView(container) 
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.createFamily(name)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
