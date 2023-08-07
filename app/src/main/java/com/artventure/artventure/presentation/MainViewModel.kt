package com.artventure.artventure.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor():ViewModel() {
    private val _bottomNavVisibility: MutableLiveData<Int> = MutableLiveData<Int>(View.VISIBLE)
    val bottomNavVisibility: LiveData<Int>
        get() = _bottomNavVisibility

    fun setBottomNavVisibility(visibility:Int){
        _bottomNavVisibility.value = visibility
    }
}