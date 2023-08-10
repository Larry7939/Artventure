package com.artventure.artventure.util.extension

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.artventure.artventure.R
import com.artventure.artventure.databinding.BottomsheetSectorFilterBinding
import com.artventure.artventure.databinding.BottomsheetYearSortBinding
import com.artventure.artventure.databinding.CustomDialogBinding
import com.artventure.artventure.presentation.adapter.SectorFilteringBottomSheetAdapter
import com.artventure.artventure.presentation.adapter.SectorFilteringDto
import com.artventure.artventure.util.type.RefiningBottomSheetType
import com.artventure.artventure.util.type.SortingType
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Context.clearFocus(view: View) {
    val imm: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**정렬 및 필터링 BottomSheet 초기화 작업*/
fun Context.setRefineBottomSheet(
    sheetType: RefiningBottomSheetType,
    sortingType: SortingType,
    filteringStates: List<SectorFilteringDto>,
    onFilteringConfirmed: (List<SectorFilteringDto>, BottomSheetDialog) -> Unit
): Pair<BottomSheetDialog, ViewDataBinding> {
    val bottomSheetDialog = BottomSheetDialog(
        this, R.style.BottomSheetDialogTheme
    )
    when (sheetType) {
        RefiningBottomSheetType.YEAR_SORTING -> {
            val binding = DataBindingUtil.inflate<BottomsheetYearSortBinding>(
                LayoutInflater.from(this@setRefineBottomSheet),
                R.layout.bottomsheet_year_sort,
                null,
                false
            )
            if (sortingType == SortingType.MNFT_ASCENDING){
                binding.tvMnftYearAscendingOrder.setTextColor(ContextCompat.getColor(this,R.color.G1))
                binding.tvMnftYearDescendingOrder.setTextColor(ContextCompat.getColor(this,R.color.G4))
            }
            else if(sortingType == SortingType.MNFT_DESCENDING){
                binding.tvMnftYearAscendingOrder.setTextColor(ContextCompat.getColor(this,R.color.G4))
                binding.tvMnftYearDescendingOrder.setTextColor(ContextCompat.getColor(this,R.color.G1))
            }
            bottomSheetDialog.setContentView(binding.root)
            return Pair(bottomSheetDialog, binding)
        }

        RefiningBottomSheetType.SECTOR_FILTERING -> {
            val binding = DataBindingUtil.inflate<BottomsheetSectorFilterBinding>(
                LayoutInflater.from(this@setRefineBottomSheet),
                R.layout.bottomsheet_sector_filter,
                null,
                false
            )
            val adapter by lazy { SectorFilteringBottomSheetAdapter(this@setRefineBottomSheet) }
            binding.rvSectorFilter.adapter = adapter.apply {
                submitList(
                    filteringStates
                )
            }
            binding.btnConfirm.setOnClickListener {
                onFilteringConfirmed(adapter.currentList, bottomSheetDialog)
            }
            bottomSheetDialog.setContentView(binding.root)
            return Pair<BottomSheetDialog, ViewDataBinding>(bottomSheetDialog, binding)
        }
    }
}

/**정렬 BottomSheet에 대한 클릭 이벤트 콜백 전달 작업*/
fun BottomSheetDialog.setRefineBottomSheetClickListener(
    binding: ViewDataBinding,
    sheetType: RefiningBottomSheetType,
    onSortingSelected: (SortingType) -> Unit,
) {
    this.setOnShowListener {
        if (sheetType == RefiningBottomSheetType.YEAR_SORTING) {
            val mnftYearDescendingButton =
                (binding as BottomsheetYearSortBinding).tvMnftYearDescendingOrder
            val mnftYearAscendingButton = binding.tvMnftYearAscendingOrder
            mnftYearDescendingButton.setOnClickListener {
                onSortingSelected(SortingType.MNFT_DESCENDING)
                dismiss()
            }
            mnftYearAscendingButton.setOnClickListener {
                onSortingSelected(SortingType.MNFT_ASCENDING)
                dismiss()
            }
        }
    }
}

fun Context.setCustomDialog(
    layoutInflater: LayoutInflater,
    viewGroup: ViewGroup?,
    description: String,
    cancelBtnText: String,
    confirmBtnText: String = getString(R.string.custom_dialog_cancel)
): AlertDialog {
    val dialogBinding = CustomDialogBinding.inflate(layoutInflater,viewGroup,false)

    with(dialogBinding) {
        tvDialogDesc.text = description
        btnDialogCancel.text = cancelBtnText
        btnDialogConfirm.text = confirmBtnText
    }

    val build = AlertDialog.Builder(this).apply {
        setView(dialogBinding.root)
    }
    val dialog = build.create()
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

fun AlertDialog.setDialogClickListener(listener: (which: AppCompatButton) -> Unit) {
    this.setOnShowListener {
        val cancelButton = findViewById<AppCompatButton>(R.id.btn_dialog_cancel)
        val confirmButton = findViewById<AppCompatButton>(R.id.btn_dialog_confirm)
        cancelButton.setOnClickListener {
            listener(confirmButton)
            this.dismiss()
        }
        confirmButton.setOnClickListener {
            listener(confirmButton)
            this.dismiss()
        }
    }
}
