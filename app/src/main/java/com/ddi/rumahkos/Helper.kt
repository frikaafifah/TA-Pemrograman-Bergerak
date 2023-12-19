package com.ddi.rumahkos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.DatePicker
import java.net.InetAddress
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Helper {



    var url = "https://websiteafifah.000webhostapp.com/"

    val calendar = Calendar.getInstance()

    fun formatRupiah(number: Long): String {
        val formatRupiah = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatRp = DecimalFormatSymbols.getInstance()
        formatRp.currencySymbol = "Rp "
        formatRp.monetaryDecimalSeparator = ','
        formatRp.groupingSeparator = '.'
        formatRupiah.maximumFractionDigits = 0
        formatRupiah.decimalFormatSymbols = formatRp
        return formatRupiah.format(number).replace("Rp", "Rp ")
    }

    fun updateUserName(context: Context, newUserName: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("d", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", newUserName)
        editor.apply()
    }

    fun updateUserId(context: Context, newUserId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("d", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", newUserId)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("d", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }

    fun getUserName(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("d", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", "")
    }

    fun showDatePickerDialog(context: Context, callback: (String) -> Unit) {
        val dateListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val formattedDate = updateDateInView()
            callback(formattedDate)
        }

        val datePickerDialog = DatePickerDialog(
            context,
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    @SuppressLint("WeekBasedYear")
    fun updateDateInView(dateFormat:String="dd-MMM-yyyy"):String {
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return sdf.format(calendar.time)
    }

    fun isInternetAvailable(): Boolean {
        return try {
            val address = InetAddress.getByName("www.google.com")
            !address.equals("")
        } catch (e: Exception) {
            false
        }
    }
}