package com.artventure.artventure.presentation.screen

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.artventure.artventure.R
import com.artventure.artventure.binding.BindingFragment
import com.artventure.artventure.databinding.FragmentSearchBinding
import com.artventure.artventure.presentation.MainViewModel
import com.artventure.artventure.presentation.SearchViewModel
import com.artventure.artventure.presentation.adapter.SearchAdapter
import com.artventure.artventure.util.UiState
import com.artventure.artventure.util.extension.clearFocus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : BindingFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    private val adapter by lazy {
        SearchAdapter(requireContext())
    }
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            moveToHome()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = searchViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        addListener()
        initAdapter()
        addObserver()
    }

    private fun addListener() {
        binding.ibBack.setOnClickListener {
            moveToHome()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

        binding.etSearch.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    requireContext().clearFocus(binding.etSearch)
                    searchViewModel.searchCollection()
                    return true
                }
                return false
            }
        })
    }

    private fun addObserver() {
        searchViewModel.collections.observe(viewLifecycleOwner) {}
        searchViewModel.searchState.observe(viewLifecycleOwner){state ->
            when (state) {
                UiState.LOADING -> {
                    Timber.d("Loading")
                }
                UiState.EMPTY -> {
                    Timber.d("Empty")
                }
                UiState.SUCCESS -> {
                    initAdapter()
                }
                else -> {
                    Timber.e("Error")
                }
            }
        }
    }

    private fun initAdapter() {
        with(binding) {
            rvSearchResult.adapter = adapter.apply {
                submitList(
                    searchViewModel.collections.value
                )
            }
        }
    }

    private fun moveToHome() {
        findNavController().navigate(R.id.action_SearchFragment_to_homeFragment)
        showMainBottomNav()
    }

    private fun showMainBottomNav() {
        mainViewModel.setBottomNavVisibility(View.VISIBLE)
    }
}