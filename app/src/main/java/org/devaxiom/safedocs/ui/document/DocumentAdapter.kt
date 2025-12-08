package org.devaxiom.safedocs.ui.document

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.devaxiom.safedocs.data.model.Document
import org.devaxiom.safedocs.databinding.ItemDocumentBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DocumentAdapter(private val onClick: (Document) -> Unit) :
    ListAdapter<Document, DocumentAdapter.DocumentViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val binding =
            ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocumentViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DocumentViewHolder(
        private val binding: ItemDocumentBinding,
        private val onClick: (Document) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(document: Document) {
            binding.tvDocumentTitle.text = document.title
            binding.tvDocumentCategory.text = document.category

            if (document.visibility == "PERSONAL") {
                binding.chipVisibility.isVisible = false
                binding.tvFamilyName.isVisible = false
            } else {
                binding.chipVisibility.isVisible = true
                binding.chipVisibility.text = document.visibility
                
                if (document.visibility == "FAMILY" && !document.familyName.isNullOrEmpty()) {
                    binding.tvFamilyName.isVisible = true
                    binding.tvFamilyName.text = document.familyName
                } else {
                    binding.tvFamilyName.isVisible = false
                }
            }

            document.expiryDate?.let {
                binding.tvExpiryDate.text = "Expires: ${dateFormat.format(it)}"
                binding.llExpiryInfo.isVisible = true

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 30)
                if (it.before(calendar.time)) {
                    binding.ivExpiryWarning.isVisible = true
                } else {
                    binding.ivExpiryWarning.isVisible = false
                }
            } ?: run {
                binding.llExpiryInfo.isVisible = false
            }

            // The click listener now simply calls the passed-in lambda.
            // The fragment is responsible for handling the navigation.
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
