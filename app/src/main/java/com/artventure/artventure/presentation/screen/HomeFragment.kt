package com.artventure.artventure.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.artventure.artventure.R
import com.artventure.artventure.binding.BindingFragment
import com.artventure.artventure.databinding.FragmentHomeBinding
import com.artventure.artventure.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class HomeFragment : BindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
    }

    private fun addListener(){
        binding.btnSearch.setOnClickListener {
            viewModel.setBottomNavVisibility(View.GONE)
            it.findNavController().navigate(R.id.action_homeFragment_to_SearchFragment)
        }
    }
}