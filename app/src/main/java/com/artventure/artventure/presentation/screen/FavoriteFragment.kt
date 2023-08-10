package com.artventure.artventure.presentation.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import com.artventure.artventure.R
import com.artventure.artventure.binding.BindingFragment
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.databinding.FragmentFavoriteBinding
import com.artventure.artventure.presentation.MainViewModel
import com.artventure.artventure.presentation.adapter.CollectionsAdapter
import com.artventure.artventure.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class FavoriteFragment : BindingFragment<FragmentFavoriteBinding>(R.layout.fragment_favorite) {
    private val adapter by lazy {
        CollectionsAdapter(requireContext(), ::moveToDetail)
    }
    private val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteCollection()
        addObserver()
    }

    private fun moveToDetail(content: CollectionDto) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(FAVORITE_BUNDLE_KEY, content)
        intent.putExtra(FAVORITE_INTENT_KEY, bundle)
        startActivity(intent)
    }

    private fun initAdapter() {
        with(binding) {
            rvFavoriteCollections.adapter = adapter.apply {
                submitList(
                    viewModel.collections
                )
            }
        }
    }
    private fun addObserver() {
        viewModel.dbState.observe(viewLifecycleOwner) { state ->
            if (state == UiState.SUCCESS) {
                initAdapter()
            }
        }
    }

    companion object {
        const val FAVORITE_INTENT_KEY = "favorite"
        const val FAVORITE_BUNDLE_KEY = "favorite_content"
    }
}