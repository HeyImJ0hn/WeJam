package dam.a47471.wejam.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import dam.a47471.wejam.R

class LoadingDialog(context: Context) {
    private val dialog = Dialog(context)

    fun show() {
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

}