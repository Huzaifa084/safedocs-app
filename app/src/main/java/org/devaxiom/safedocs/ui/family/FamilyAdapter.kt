package org.devaxiom.safedocs.ui.family

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.data.model.FamilySummary
import org.devaxiom.safedocs.databinding.ItemFamilyCardBinding

class FamilyAdapter(private val onFamilyClick: (FamilySummary) -> Unit) :
    ListAdapter<FamilySummary, FamilyAdapter.FamilyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyViewHolder {
        val binding = ItemFamilyCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FamilyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FamilyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FamilyViewHolder(private val binding: ItemFamilyCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onFamilyClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(family: FamilySummary) {
            binding.tvFamilyName.text = family.familyName
            binding.tvMemberCount.text = "${family.memberCount} members"
            binding.chipRole.text = family.role
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FamilySummary>() {
        override fun areItemsTheSame(oldItem: FamilySummary, newItem: FamilySummary) = 
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FamilySummary, newItem: FamilySummary) = 
            oldItem == newItem
    }
}
