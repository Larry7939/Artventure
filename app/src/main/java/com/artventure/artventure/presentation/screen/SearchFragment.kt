package com.artventure.artventure.presentation.screen

import android.os.Bundle
import android.view.View
import com.artventure.artventure.R
import com.artventure.artventure.binding.BindingFragment
import com.artventure.artventure.databinding.FragmentSearchBinding

class SearchFragment : BindingFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val mainViewModel: MainViewModel by activityViewModels()
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
    private fun addListener() {
        binding.ibBack.setOnClickListener {
            moveToHome()
        }
    private fun moveToHome() {
        findNavController().navigate(R.id.action_SearchFragment_to_homeFragment)
        showMainBottomNav()
    }

    private fun showMainBottomNav() {
        mainViewModel.setBottomNavVisibility(View.VISIBLE)
    }
}