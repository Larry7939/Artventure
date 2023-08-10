package com.artventure.artventure.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.databinding.ItemCollectionBinding

class CollectionsAdapter(
    private val context: Context,
    private val moveToDetail: (CollectionDto) -> Unit
) :
    ListAdapter<CollectionDto, CollectionsAdapter.CollectionsViewHolder>(CollectionItemCallback()) {
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        return CollectionsViewHolder(ItemCollectionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CollectionsViewHolder(private val binding: ItemCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CollectionDto) {
            with(binding) {
                collectionData = data
                root.setOnClickListener {
                    moveToDetail(data)
                }
            }
        }
    }
}

class CollectionItemCallback : DiffUtil.ItemCallback<CollectionDto>() {
    override fun areItemsTheSame(oldItem: CollectionDto, newItem: CollectionDto): Boolean {
        return (oldItem.titleKor == newItem.titleKor) && (oldItem.standard == newItem.standard)
    }

    override fun areContentsTheSame(oldItem: CollectionDto, newItem: CollectionDto): Boolean {
        return oldItem == newItem
    }
}