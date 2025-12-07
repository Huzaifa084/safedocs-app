package org.devaxiom.safedocs.ui.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.R

class FamilyMembersAdapter(private val onRemove: (String) -> Unit) : RecyclerView.Adapter<FamilyMembersAdapter.VH>() {
    private val items = mutableListOf<FamilyMember>()

    fun submit(newItems: List<FamilyMember>) {
        items.clear(); items.addAll(newItems); notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_family_member, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvEmail.text = item.email
        holder.btnRemove.setOnClickListener { onRemove(item.id) }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvMemberName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvMemberEmail)
        val btnRemove: View = itemView.findViewById(R.id.btnRemoveMember)
    }
}
