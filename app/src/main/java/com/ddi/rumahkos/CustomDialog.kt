package com.ddi.rumahkos

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop

object CustomDialog {

    private var dialog: AlertDialog? = null

    @SuppressLint("SetTextI18n")
    fun showDialog(context: Context, message: String) {
        if (dialog == null) {
            val llPadding = 30
            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.VERTICAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.CENTER
            var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            ll.layoutParams = llParam

            llParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            val tvText = TextView(context)
            tvText.text = message
            //tvText.setTextColor(Color.parseColor("#000000"))
           // tvText.setTextColor(ContextCompat.getColor(context, R.color.green))
            tvText.textSize = 20f
            tvText.layoutParams = llParam

            val closeButton = Button(context)
            closeButton.text = "Close"
            //closeButton.setTextColor(ContextCompat.getColor(context, R.color.dark))
            //closeButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            closeButton.setOnClickListener { dismissDialog() }


            llParam.setMargins(16, 16, 16, 16) // left, top, right, bottom


            closeButton.layoutParams = llParam

            ll.addView(tvText)
            ll.addView(closeButton)

            val builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setView(ll)
            dialog = builder.create()
            dialog!!.show()

            val window = dialog!!.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog!!.window!!.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog!!.window!!.attributes = layoutParams
            }
        }
    }

    val isDialogVisible: Boolean
        get() = dialog?.isShowing ?: false

    fun dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }
}
