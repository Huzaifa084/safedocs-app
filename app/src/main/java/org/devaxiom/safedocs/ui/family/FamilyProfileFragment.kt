package org.devaxiom.safedocs.ui.family

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.data.security.SessionManager
import org.devaxiom.safedocs.databinding.FragmentFamilyProfileBinding

class FamilyProfileFragment : Fragment() {

    private var _binding: FragmentFamilyProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FamilyViewModel by viewModels()
    private lateinit var memberAdapter: FamilyMemberAdapter
    private var currentFamilyId: String? = null
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFamilyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        currentFamilyId = arguments?.getString("familyId")
        if (currentFamilyId == null) {
            findNavController().popBackStack()
            return
        }

        // We need currentUserId to check if self is HEAD or MEMBER in the list
        currentUserId = SessionManager(requireContext()).getUserId()

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.getFamilyProfile(currentFamilyId!!)
        
        // Removed manual toolbar handling, relying on MainActivity setup
    }

    private fun setupRecyclerView() {
        memberAdapter = FamilyMemberAdapter { member ->
            // Handle member click if needed (e.g. remove member if HEAD)
            val isHead = viewModel.familyProfile.value?.headUserId == currentUserId
            if (isHead && member.userId != currentUserId) {
                 showRemoveMemberDialog(member.userId, member.name ?: "Member")
            }
        }
        binding.recyclerFamilyMembers.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupListeners() {
        binding.btnInviteMember.setOnClickListener {
             showInviteDialog()
        }
        
        binding.btnLeaveFamily.setOnClickListener {
             showLeaveDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.familyProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.tvProfileName.text = profile.familyName
                binding.tvProfileHead.text = "Head: ${profile.headName ?: "Unknown"}" // Updated ID
                
                memberAdapter.submitList(profile.members)
                
                val isHead = profile.headUserId == currentUserId
                binding.btnInviteMember.isVisible = isHead
                binding.btnEditFamilyName.isVisible = isHead // New edit button
                // Head shouldn't see 'Leave' in simple implementation unless last member.
            }
        }
        
        viewModel.operationState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FamilyOperationState.Loading -> binding.progressBarProfile.isVisible = true
                is FamilyOperationState.Success -> {
                    binding.progressBarProfile.isVisible = false
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    if (state.message == "Left family") {
                        findNavController().popBackStack()
                    }
                }
                is FamilyOperationState.Error -> {
                    binding.progressBarProfile.isVisible = false
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> binding.progressBarProfile.isVisible = false
            }
        }
    }
    
    private fun showInviteDialog() {
        val input = EditText(requireContext())
        input.hint = "Email Address"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Invite Member")
            .setView(input)
            .setPositiveButton("Invite") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isNotEmpty()) {
                    viewModel.inviteMember(currentFamilyId!!, email)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showRemoveMemberDialog(userId: String, name: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Member")
            .setMessage("Are you sure you want to remove $name?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeMember(currentFamilyId!!, userId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showLeaveDialog() {
         MaterialAlertDialogBuilder(requireContext())
            .setTitle("Leave Family")
            .setMessage("Are you sure? You will lose access to family documents.")
            .setPositiveButton("Leave") { _, _ ->
                viewModel.leaveFamily(currentFamilyId!!)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
