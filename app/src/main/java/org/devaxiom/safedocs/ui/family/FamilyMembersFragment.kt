package org.devaxiom.safedocs.ui.family

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.devaxiom.safedocs.databinding.FragmentFamilyMembersBinding

class FamilyMembersFragment : Fragment() {
    private var _binding: FragmentFamilyMembersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FamilyViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFamilyMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FamilyMembersAdapter { userId ->
            viewModel.removeMember(userId) { ok ->
                Toast.makeText(context, if (ok) "Removed" else "Remove failed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerMembers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMembers.adapter = adapter

        viewModel.members.observe(viewLifecycleOwner) { adapter.submit(it) }
        viewModel.fetchMembers()

        binding.btnInvite.setOnClickListener {
            val email = binding.etInviteEmail.text?.toString()?.trim()
            if (!email.isNullOrEmpty()) {
                viewModel.invite(email) { ok ->
                    Toast.makeText(context, if (ok) "Invited" else "Invite failed", Toast.LENGTH_SHORT).show()
                    if (ok) binding.etInviteEmail.setText("")
                }
            }
        }
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
