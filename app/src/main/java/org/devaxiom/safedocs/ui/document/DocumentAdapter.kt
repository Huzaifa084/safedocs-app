package org.devaxiom.safedocs.ui.document

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.data.model.Document
import org.devaxiom.safedocs.databinding.ItemDocumentBinding

class DocumentAdapter(private val onClick: (Document) -> Unit) :
    ListAdapter<Document, DocumentAdapter.DocumentViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocumentViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DocumentViewHolder(private val binding: ItemDocumentBinding, private val onClick: (Document) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(document: Document) {
            binding.tvDocumentTitle.text = document.title
            binding.tvDocumentCategory.text = document.category
            itemView.setOnClickListener {
                onClick(document)
            }
        }
    }
}

object DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem == newItem
    }
}
