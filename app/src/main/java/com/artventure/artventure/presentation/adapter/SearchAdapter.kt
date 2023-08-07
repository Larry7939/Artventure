package com.artventure.artventure.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.databinding.ItemCollectionBinding
import com.bumptech.glide.Glide
import timber.log.Timber

class SearchAdapter(private val context: Context) :
    ListAdapter<CollectionDto, SearchAdapter.SearchViewHolder>(SearchCollectionItemCallback()) {
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(ItemCollectionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class SearchViewHolder(private val binding: ItemCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: CollectionDto) {
            with(binding) {
                collectionData = data
            }
        }
    }
}

class SearchCollectionItemCallback : DiffUtil.ItemCallback<CollectionDto>() {
    override fun areItemsTheSame(oldItem: CollectionDto, newItem: CollectionDto): Boolean {
        return oldItem.titleKor == newItem.titleKor
    }

    override fun areContentsTheSame(oldItem: CollectionDto, newItem: CollectionDto): Boolean {
        return oldItem == newItem
    }
}