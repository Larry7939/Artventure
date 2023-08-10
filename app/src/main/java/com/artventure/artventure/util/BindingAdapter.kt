package com.artventure.artventure.util

import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapter {
    @BindingAdapter("imgRes")
    @JvmStatic
    fun img(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).into(imageView)
    }

    @BindingAdapter("onFocusChange")
    @JvmStatic
    fun onFocusChange(view: View, onFocusChangeListener: OnFocusChangeListener) {
        view.onFocusChangeListener = onFocusChangeListener
    }
}