package com.artventure.artventure.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.artventure.artventure.databinding.ItemBottomsheetSectorFilterBinding

data class SectorFilteringDto(
    val sector: String,
    var isSelected: Boolean
)

class SectorFilteringBottomSheetAdapter(private val context: Context) :
    ListAdapter<SectorFilteringDto, SectorFilteringBottomSheetAdapter.SectorViewHolder>(
        SectorFilteringItemCallback()
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }
    private val selectButtons = mutableListOf<Map<String, Button>>()

    inner class SectorViewHolder(private val binding: ItemBottomsheetSectorFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: SectorFilteringDto) {
            with(binding) {
                selectButtons.add(mapOf(data.sector to binding.btnSectorSelect))
                tvSectorName.text = data.sector
                btnSectorSelect.isSelected = data.isSelected

                /**부문 선택 행의 모든 영역에서 클릭 이벤트 발생*/
                binding.root.setOnClickListener {
                    btnSectorSelect.isSelected = !btnSectorSelect.isSelected
                    updateSelectedSector(tvSectorName.text.toString())
                }
                binding.btnSectorSelect.setOnClickListener {
                    btnSectorSelect.isSelected = !btnSectorSelect.isSelected
                    updateSelectedSector(tvSectorName.text.toString())
                }
            }
        }

        /**부문 체크 상태 업데이트*/
        private fun updateSelectedSector(sector: String) {
            currentList.map { item ->
                if (item.sector == sector) {
                    item.isSelected = !item.isSelected
                    if (sector == ALL_SECTOR) {
                        updateAllSectorSelection(item)
                        return
                    } else {
                        /**세부 부문 중 하나라도 체크 해제 시, 전체 부문 체크 해제*/
                        val allItem = currentList.find { it.sector == ALL_SECTOR }
                        allItem?.isSelected = false
                        for (button in selectButtons) {
                            if (button.containsKey(ALL_SECTOR)) {
                                button[ALL_SECTOR]?.isSelected = false
                            }
                        }
                        checkAllSectorSelected()
                    }
                }
            }
        }

        /**모든 세부 부문이 체크 되었을 때, 전체 부문 체크 설정*/
        private fun checkAllSectorSelected() {
            val allItem = currentList.find { it.sector == ALL_SECTOR }

            if (allItem != null) {
                val otherSelectedItems = currentList.filter { it != allItem && it.isSelected }
                if (otherSelectedItems.size == currentList.size - 1) {
                    allItem.isSelected = true
                    for (button in selectButtons) {
                        val key = ALL_SECTOR
                        if (button.containsKey(key)) {
                            button[key]?.isSelected = true
                        }
                    }
                }
            }
        }

        /**전체 부문 체크 상태에 따른 세부 부문들의 체크 상태 변경*/
        private fun updateAllSectorSelection(allItemSelection: SectorFilteringDto) {
            if (allItemSelection.isSelected) {
                for (items in selectButtons) {
                    for (buttons in items.values) {
                        buttons.isSelected = true
                    }
                }

                currentList.map { item ->
                    item.isSelected = true

                }
            } else {
                for (items in selectButtons) {
                    for (buttons in items.values) {
                        buttons.isSelected = false
                    }
                }
                currentList.map { item ->
                    item.isSelected = false
                }
            }
        }
    }

    companion object {
        const val ALL_SECTOR = "전체"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectorViewHolder {
        return SectorViewHolder(ItemBottomsheetSectorFilterBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SectorViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}

class SectorFilteringItemCallback : DiffUtil.ItemCallback<SectorFilteringDto>() {
    override fun areItemsTheSame(
        oldItem: SectorFilteringDto,
        newItem: SectorFilteringDto
    ): Boolean {
        return oldItem.sector == newItem.sector
    }

    override fun areContentsTheSame(
        oldItem: SectorFilteringDto,
        newItem: SectorFilteringDto
    ): Boolean {
        return oldItem == newItem
    }
}