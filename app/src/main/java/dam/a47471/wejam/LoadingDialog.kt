package dam.a47471.wejam

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class LoadingDialog(context: Context) {
    private val dialog = Dialog(context)

    fun show() {
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

}