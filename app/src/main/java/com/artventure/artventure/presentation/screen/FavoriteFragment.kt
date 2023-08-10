package com.artventure.artventure.presentation.screen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import com.artventure.artventure.util.extension.setCustomDialog
import com.artventure.artventure.util.extension.setDialogClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class FavoriteFragment : BindingFragment<FragmentFavoriteBinding>(R.layout.fragment_favorite) {
    private val adapter by lazy {
        CollectionsAdapter(requireContext(), ::moveToDetail)
    }
    private val viewModel: MainViewModel by activityViewModels()
    lateinit var clearDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initDialog(container)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteCollection()
        addObserver()
        addListener()
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

    private fun initDialog(container: ViewGroup?) {
        clearDialog = requireActivity().setCustomDialog(
            layoutInflater,
            container,
            description = getString(R.string.alert_clear_collection),
            cancelBtnText = getString(R.string.custom_dialog_cancel),
            confirmBtnText = getString(R.string.custom_dialog_confirm)
        )
        addDialogListener()
    }

    private fun addDialogListener() {
        clearDialog.setDialogClickListener { which ->
            when (which) {
                clearDialog.findViewById<AppCompatButton>(R.id.btn_dialog_confirm) -> {
                    viewModel.clearFavoriteCollection()
                }
            }
        }
    }

    private fun addObserver() {
        viewModel.dbState.observe(viewLifecycleOwner) { state ->
            if (state == UiState.SUCCESS) {
                binding.rvFavoriteCollections.visibility = View.VISIBLE
                binding.groupEmptyWarning.visibility = View.GONE
                initAdapter()
            } else if (state == UiState.EMPTY) {
                binding.groupEmptyWarning.visibility = View.VISIBLE
                binding.rvFavoriteCollections.visibility = View.GONE
            }
        }
    }

    private fun addListener() {
        binding.btnClearCollection.setOnClickListener {
            clearDialog.show()
        }
    }


    companion object {
        const val FAVORITE_INTENT_KEY = "favorite"
        const val FAVORITE_BUNDLE_KEY = "favorite_content"
    }
}