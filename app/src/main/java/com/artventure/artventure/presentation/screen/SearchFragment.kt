package com.artventure.artventure.presentation.screen

import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var recyclerViewState: Parcelable

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
                        searchViewModel.initSearchIndex()
                    } else {
                        warningEmptySearchWord()
                    }
                    return true
                }
                return false
            }
        })
        binding.btnSearch.setOnClickListener {
            if (binding.etSearch.text?.isNotEmpty() == true) {
                requireContext().clearFocus(binding.etSearch)
                searchViewModel.searchCollection()
                searchViewModel.initSearchIndex()
            } else {
                warningEmptySearchWord()
            }

        }
        setSearchPagingListener()
    }

    private fun setSearchPagingListener() {
        /**스크롤의 위치가 마지막 아이템의 위치와 동일할 시 추가 index 호출*/
        binding.rvSearchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var lastScrollTime = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (!recyclerView.canScrollVertically(1)) {
                        val lastPosition =
                            (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        val totalCount = recyclerView.adapter?.itemCount
                        /**스크롤 이벤트 중복 호출 방지*/
                        if (SystemClock.elapsedRealtime() - lastScrollTime > SCROLL_TIME) {
                            if (totalCount != null) {
                                if (lastPosition == totalCount - 1 && searchViewModel.checkIndexValidation()) {
                                    /**인덱스 증가 및 페이징 호출*/
                                    searchViewModel.updateSearchIndex()
                                    searchViewModel.pagingCollection()
                                }
                            }
                        }
                        lastScrollTime = SystemClock.elapsedRealtime().toInt()
                    }
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /**페이징 후 스크롤 초기화 시에 사용될 스크롤 위치 저장*/
                recyclerViewState =
                    (recyclerView.layoutManager as LinearLayoutManager).onSaveInstanceState()!!
            }
        })
    }

    private fun pagingSearchResult() {
        with(binding) {
            initAdapter()
            /**어댑터 초기화 후 스크롤 위치 복원*/
            if (::recyclerViewState.isInitialized) {
                (rvSearchResult.layoutManager as LinearLayoutManager).onRestoreInstanceState(
                    recyclerViewState
                )
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
        searchViewModel.pagingState.observe(viewLifecycleOwner) { state ->
            if (state == UiState.SUCCESS) {
                pagingSearchResult()
            } else if (state == UiState.EMPTY) {
                requireContext().showToast(getString(R.string.warning_last_search_word))
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
    companion object{
        const val SCROLL_TIME = 1000L
    }
}