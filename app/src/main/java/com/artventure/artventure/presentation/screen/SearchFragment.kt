package com.artventure.artventure.presentation.screen

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artventure.artventure.R
import com.artventure.artventure.binding.BindingFragment
import com.artventure.artventure.data.model.dto.CollectionDto
import com.artventure.artventure.databinding.FragmentSearchBinding
import com.artventure.artventure.presentation.MainViewModel
import com.artventure.artventure.presentation.SearchViewModel
import com.artventure.artventure.presentation.adapter.CollectionsAdapter
import com.artventure.artventure.presentation.adapter.SectorFilteringDto
import com.artventure.artventure.util.UiState
import com.artventure.artventure.util.extension.clearFocus
import com.artventure.artventure.util.extension.setRefineBottomSheet
import com.artventure.artventure.util.extension.setRefineBottomSheetClickListener
import com.artventure.artventure.util.extension.showToast
import com.artventure.artventure.util.type.RefiningBottomSheetType
import com.artventure.artventure.util.type.SortingType
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BindingFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    private val adapter by lazy {
        CollectionsAdapter(requireContext(), ::moveToDetail)
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

        /**키보드 검색버튼 클릭 이벤트 및 빈 검색어에 대한 예외처리*/
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
        setSortingButtonListener()
        setSearchPagingListener()
    }

    private fun warningEmptySearchWord() {
        requireContext().showToast(getString(R.string.warning_empty_search_word))
    }

    private fun initRefiningBottomSheet(sheetType: RefiningBottomSheetType) {
        val refineBottomSheetPair = requireContext().setRefineBottomSheet(
            sheetType = sheetType,
            sortingType = searchViewModel.sortingState.value!!,
            onFilteringConfirmed = ::onFilteringConfirmed,
            filteringStates = searchViewModel.filteringState.value!!
        )
        val refineBottomSheet = refineBottomSheetPair.first
        val bottomSheetBinding = refineBottomSheetPair.second
        refineBottomSheet.setRefineBottomSheetClickListener(
            bottomSheetBinding,
            sheetType,
            ::onSortingSelected
        )
        refineBottomSheet.show()
    }

    private fun setSortingButtonListener() {
        /**제작년도 정렬*/
        binding.btnSortByMnfctYear.setOnClickListener {
            if (searchViewModel.collections.value?.isNotEmpty() == true) {
                initRefiningBottomSheet(sheetType = RefiningBottomSheetType.YEAR_SORTING)
            }
        }
        /**부문 필터링*/
        binding.btnSortBySector.setOnClickListener {
            if (searchViewModel.collections.value?.isNotEmpty() == true) {
                initRefiningBottomSheet(sheetType = RefiningBottomSheetType.SECTOR_FILTERING)
            }
        }
    }

    private fun onSortingSelected(sortingType: SortingType) {
        searchViewModel.setSortingState(sortingType)
    }

    private fun onFilteringConfirmed(
        filteringState: List<SectorFilteringDto>,
        refineBottomSheet: BottomSheetDialog
    ) {
        searchViewModel.setFilteringState(filteringState)
        refineBottomSheet.dismiss()
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


    private fun addObserver() {
        searchViewModel.searchState.observe(viewLifecycleOwner) { state ->
            if (state == UiState.SUCCESS) {
                initAdapter()
            }
        }
        /**페이징 상태에 따른 예외처리*/
        searchViewModel.pagingState.observe(viewLifecycleOwner) { state ->
            if (state == UiState.SUCCESS) {
                pagingSearchResult()
            } else if (state == UiState.EMPTY) {
                requireContext().showToast(getString(R.string.warning_last_search_word))
            }
        }
        /**정렬 상태에 따른 텍스트 변경 작업*/
        searchViewModel.sortingState.observe(viewLifecycleOwner) { state ->
            initAdapter()
            if (state == SortingType.MNFT_ASCENDING) {
                binding.btnSortByMnfctYear.text = getString(R.string.mnft_year_ascending_order)
            } else if (state == SortingType.MNFT_DESCENDING) {
                binding.btnSortByMnfctYear.text = getString(R.string.mnft_year_descending_order)
            }
        }

        /**부문 필터링 상태에 따른 텍스트 변경 작업*/
        searchViewModel.filteringState.observe(viewLifecycleOwner) {
            if (searchViewModel.selectedFilteringState.size == searchViewModel.filteringState.value?.size) {
                binding.btnSortBySector.text = getString(R.string.entire_sector)
            } else {
                if (searchViewModel.selectedFilteringState.size == 1) {
                    binding.btnSortBySector.text = searchViewModel.selectedFilteringState[0]
                    binding.btnSortBySector.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.border_filtering_btn,
                        null
                    )
                } else if (searchViewModel.selectedFilteringState.isEmpty()) {
                    binding.btnSortBySector.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.border_refine_btn, null)
                    binding.btnSortBySector.text = getString(R.string.sector_not_selected)
                } else {
                    binding.btnSortBySector.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.border_filtering_btn,
                        null
                    )
                    binding.btnSortBySector.text =
                        StringBuilder("${searchViewModel.selectedFilteringState[0]} 외 ${searchViewModel.selectedFilteringState.size - 1}")
                }
            }
            initAdapter()
        }
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


    private fun initAdapter() {
        with(binding) {
            rvSearchResult.adapter = adapter.apply {
                submitList(
                    applyRefinementOnSearchResult(searchViewModel.collections.value)
                )
            }
        }
    }

    /**검색 결과에 대한 부문 필터링 및 정렬 작업 */
    private fun applyRefinementOnSearchResult(searchResult: MutableList<CollectionDto>?): List<CollectionDto> =
        sortSearchResult(filterSearchResult(searchResult))

    private fun sortSearchResult(searchResult: List<CollectionDto>?): List<CollectionDto> {
        return searchResult?.let {
            when (searchViewModel.sortingState.value) {
                SortingType.MNFT_ASCENDING -> it.sortedBy { dto -> dto.mnfctYear }
                SortingType.MNFT_DESCENDING -> it.sortedByDescending { dto -> dto.mnfctYear }
                else -> emptyList()
            }
        } ?: emptyList()
    }

    private fun filterSearchResult(searchResult: MutableList<CollectionDto>?): List<CollectionDto> {
        return searchResult?.let { result ->
            result.filter {
                it.sector in searchViewModel.selectedFilteringState
            }
        } ?: emptyList()
    }

    private fun moveToHome() {
        findNavController().navigate(R.id.action_SearchFragment_to_homeFragment)
        showMainBottomNav()
    }

    private fun showMainBottomNav() {
        mainViewModel.setBottomNavVisibility(View.VISIBLE)
    }

    private fun moveToDetail(content: CollectionDto) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(SEARCH_BUNDLE_KEY, content)
        intent.putExtra(SEARCH_INTENT_KEY, bundle)
        startActivity(intent)
    }


    companion object {
        const val SCROLL_TIME = 1000L
        const val SEARCH_INTENT_KEY = "search"
        const val SEARCH_BUNDLE_KEY = "search_content"
    }
}