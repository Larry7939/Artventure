package com.artventure.artventure.util.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

/**키보드 내리기(포커스 해제)*/
fun Context.clearFocus(view: View) {
    val imm: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}    layoutInflater: LayoutInflater,
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
