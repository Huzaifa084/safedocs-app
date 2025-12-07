package org.devaxiom.safedocs.ui.document

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.R
import org.devaxiom.safedocs.network.DocumentShare

class SharesAdapter(private val onRemove: (String) -> Unit) : RecyclerView.Adapter<SharesAdapter.VH>() {
    private val items = mutableListOf<DocumentShare>()

    fun submit(newItems: List<DocumentShare>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvEmail.text = item.email
        holder.btnRemove.setOnClickListener { onRemove(item.id) }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmail: TextView = itemView.findViewById(R.id.tvShareEmail)
        val btnRemove: View = itemView.findViewById(R.id.btnRemoveShare)
    }
}
