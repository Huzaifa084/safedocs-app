package org.devaxiom.safedocs.ui.family

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.data.model.FamilyMember
import org.devaxiom.safedocs.databinding.ItemFamilyMemberBinding
import java.util.Locale

class FamilyMemberAdapter(private val onMemberClick: (FamilyMember) -> Unit) :
    ListAdapter<FamilyMember, FamilyMemberAdapter.MemberViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemFamilyMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MemberViewHolder(private val binding: ItemFamilyMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onMemberClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(member: FamilyMember) {
            // Name Fallback
            val displayName = member.name ?: member.email?.substringBefore("@") ?: "Unknown"
            binding.tvMemberName.text = displayName
            
            binding.tvMemberEmail.text = member.email ?: "No email"
            binding.chipRole.text = member.role.uppercase(Locale.getDefault())

            // Initials
            val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            binding.tvAvatarInitials.text = initial
            
            // Tint chip for HEAD role
            if (member.role.equals("HEAD", ignoreCase = true)) {
                 binding.chipRole.setChipBackgroundColorResource(R.color.primary)
            } else {
                 binding.chipRole.setChipBackgroundColorResource(android.R.color.darker_gray)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FamilyMember>() {
        override fun areItemsTheSame(oldItem: FamilyMember, newItem: FamilyMember) = 
            oldItem.userId == newItem.userId

        override fun areContentsTheSame(oldItem: FamilyMember, newItem: FamilyMember) = 
            oldItem == newItem
    }
}
