package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.devaxiom.safedocs.databinding.FragmentFamilyBinding

class FamilyFragment : Fragment() {

    private var _binding: FragmentFamilyBinding? = null
    private val binding get() = _binding!!

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
        binding.tvEmptyFamily.visibility = View.VISIBLE
        binding.btnInvite.setOnClickListener {
            // TODO: show invite dialog and call /api/family/invite
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
