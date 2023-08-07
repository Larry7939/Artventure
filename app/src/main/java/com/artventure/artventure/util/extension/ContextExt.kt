package com.artventure.artventure.util.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
/**키보드 내리기(포커스 해제)*/
fun Context.clearFocus(view: View) {
    val imm: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}
