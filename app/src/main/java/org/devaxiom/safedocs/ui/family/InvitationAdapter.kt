package org.devaxiom.safedocs.ui.family

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.data.model.FamilyInvitation
import org.devaxiom.safedocs.databinding.ItemInvitationBinding

class InvitationAdapter(
    private val onAccept: (String) -> Unit,
    private val onReject: (String) -> Unit
) : ListAdapter<FamilyInvitation, InvitationAdapter.InvitationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val binding = ItemInvitationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InvitationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InvitationViewHolder(private val binding: ItemInvitationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(invite: FamilyInvitation) {
            val familyName = invite.familyName ?: "Unknown Family"
            binding.tvInviteMessage.text = "Invited to join '$familyName'"
            
            val inviterName = invite.invitedByName ?: "Someone"
            val inviterEmail = invite.invitedByEmail ?: "Unknown Email"
            
            // Format: "by Name (email)"
            // If name is null/empty, just use email? Or "Someone" as fallback.
            // Let's use clean logic
            val inviterText = if (invite.invitedByName != null && invite.invitedByEmail != null) {
                "by ${invite.invitedByName} (${invite.invitedByEmail})"
            } else if (invite.invitedByEmail != null) {
                "by ${invite.invitedByEmail}"
            } else {
                "by ${invite.invitedByName ?: "Family Admin"}"
            }
            
            binding.tvInvitedBy.text = inviterText
            
            binding.btnAccept.setOnClickListener {
                onAccept(invite.inviteId)
            }
            
            binding.btnReject.setOnClickListener {
                onReject(invite.inviteId)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FamilyInvitation>() {
        override fun areItemsTheSame(oldItem: FamilyInvitation, newItem: FamilyInvitation) = 
            oldItem.inviteId == newItem.inviteId

        override fun areContentsTheSame(oldItem: FamilyInvitation, newItem: FamilyInvitation) = 
            oldItem == newItem
    }
}
