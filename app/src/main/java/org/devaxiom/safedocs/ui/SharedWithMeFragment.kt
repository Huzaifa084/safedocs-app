package org.devaxiom.safedocs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.databinding.FragmentSharedWithMeBinding
import org.devaxiom.safedocs.ui.shared.ViewPagerAdapter
import org.devaxiom.safedocs.ui.guest.GuestUiController
import org.devaxiom.safedocs.ui.util.PostLoginRefresher

class SharedWithMeFragment : Fragment() {

    private var _binding: FragmentSharedWithMeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        GuestUiController.bind(
            fragment = this,
            lifecycleOwner = viewLifecycleOwner,
            bannerView = binding.guestNudgeShared.root,
            signInButton = binding.guestNudgeShared.btnGoogle,
            promptMessage = getString(R.string.guest_prompt_message)
        ) {
            // Standardized post-login refresh for inner pages
            val newAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            binding.viewPager.adapter = newAdapter
        }

        // Also bind refresher in case other actions need refresh post-login
        PostLoginRefresher.bind(this, viewLifecycleOwner) {
            val newAdapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            binding.viewPager.adapter = newAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Shared by Me"
                1 -> "Shared with Me"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
