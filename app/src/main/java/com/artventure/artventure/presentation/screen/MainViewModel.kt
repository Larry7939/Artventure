package com.artventure.artventure.presentation.screen

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    val bottomNavVisibility: MutableLiveData<Int> = MutableLiveData<Int>(View.VISIBLE)

    fun setBottomNavVisibility(visibility:Int){
        bottomNavVisibility.value = visibility
    }
}