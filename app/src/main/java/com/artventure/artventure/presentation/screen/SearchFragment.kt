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
import com.artventure.artventure.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

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
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (view?.text?.isNotEmpty() == true) {
                        requireContext().clearFocus(binding.etSearch)
                        searchViewModel.searchCollection()
                    } else {
                        warningEmptySearchWord()
                    }
                    return true
                }
                return false
            }
        })
        binding.btnSearch.setOnClickListener {
            if(binding.etSearch.text?.isNotEmpty() == true){
                requireContext().clearFocus(binding.etSearch)
                searchViewModel.searchCollection()
            }
            else{
                warningEmptySearchWord()
            }

        }
    }

    private fun warningEmptySearchWord() {
        requireContext().showToast(getString(R.string.warning_empty_search_word))
    }

    private fun addObserver() {
        searchViewModel.searchState.observe(viewLifecycleOwner) { state ->
            if (state == UiState.SUCCESS) {
                initAdapter()
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