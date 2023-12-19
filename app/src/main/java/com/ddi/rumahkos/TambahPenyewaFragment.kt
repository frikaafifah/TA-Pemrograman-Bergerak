package com.ddi.rumahkos

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.ddi.rumahkos.databinding.FragmentTambahPenyewaBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Locale


class TambahPenyewaFragment : Fragment() {

    val decimalFormat = DecimalFormat("###,###", DecimalFormatSymbols(Locale.getDefault()))
    var currentJumlah = ""
    lateinit var binding:FragmentTambahPenyewaBinding
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahPenyewaBinding.inflate(layoutInflater,container,false)




        binding.tglmasuk.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.tglmasuk.right - binding.tglmasuk.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    Helper.showDatePickerDialog(requireContext()) { formattedDate ->
                        binding.tglmasuk.setText(formattedDate)
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        binding.tglkeluar.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.tglkeluar.right - binding.tglkeluar.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    Helper.showDatePickerDialog(requireContext()) { formattedDate ->
                        binding.tglkeluar.setText(formattedDate)
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        binding.sewa.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {

                    if (editable.toString() != currentJumlah) {
                        binding.sewa.removeTextChangedListener(this)
                        val cleanString = editable.toString().replace("[^\\d]".toRegex(), "")
                        try {
                            val parsed = decimalFormat.parse(cleanString)?.toDouble() ?: 0.0
                            val formatted = decimalFormat.format(parsed)
                            currentJumlah = formatted
                            binding.sewa.setText(formatted)
                            binding.sewa.setSelection(formatted.length)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        binding.sewa.addTextChangedListener(this)
                    }


                }
            }
        )

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        })

        binding.btnback.setOnClickListener {
            back()
        }

        binding.btnSimpan.setOnClickListener {
            if(binding.nama.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "nama kosong")
            }else if(binding.password.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "password kosong")
            }else if(binding.nokamar.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "Nomor kamar kosong")
            }else if(binding.alamat.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "alamat kosong")
            }else if(binding.telepon.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "telepon kosong")
            }else if(binding.email.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "email kosong")
            }else if(binding.tgllahir.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "tempat / tgl lahir kosong")
            }else if(binding.kontakdarurat.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "kontak darurat kosong")
            }else if(binding.sewa.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "sewa kosong")
            }else if(binding.tglmasuk.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "tanggal masuk")
            }else if(binding.tglkeluar.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "tanggal keluar")
            }else if(binding.statusbayar.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "status bayar")
            }else if(binding.catatan.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "catatan")
            }
            else{
                GlobalScope.launch {
                    requireActivity().runOnUiThread {
                        ProgressHelper.showDialog(requireContext(),"Please wait...")
                    }
                    val x = async {
                        save()
                    }
                    x.await()
                }
            }
        }

        return binding.root
    }

    fun back(){
        try{
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this).commit()

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                ).replace(R.id.fragmentContainer,PengelolaFragment()).commit()
        }catch (_:Exception){}
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    fun save() {
        val url = Helper.url+"/tambahpenyewa.php"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("nama", binding.nama.text.toString())
            .add("password", binding.password.text.toString())
            .add("nokamar", binding.nokamar.text.toString())
            .add("alamat", binding.alamat.text.toString())
            .add("telepon", binding.telepon.text.toString())
            .add("tanggallahir", binding.tgllahir.text.toString())
            .add("kontakdarurat", binding.kontakdarurat.text.toString())
            .add("email", binding.email.text.toString())
            .add("tanggalmasuk", binding.tglmasuk.text.toString())
            .add("tanggalkeluar", binding.tglkeluar.text.toString())
            .add("statusbayar", binding.statusbayar.text.toString())
            .add("catatan", binding.catatan.text.toString())
            .add("sewa", binding.sewa.text.toString().replace("[^\\d]".toRegex(), ""))
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    ProgressHelper.dismissDialog()
                    CustomDialog.showDialog(requireContext(),e.message.toString())}
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()


                try {
                    val jsonResponse = JSONObject(responseData.toString())
                    val message = jsonResponse.getString("message")
                    requireActivity().runOnUiThread {
                        if (message == "success") {

                            binding.nama.setText("")
                            binding.password.setText("")
                            binding.nokamar.setText("")
                            binding.alamat.setText("")
                            binding.telepon.setText("")
                            binding.tgllahir.setText("")
                            binding.kontakdarurat.setText("")
                            binding.email.setText("")
                            binding.tglmasuk.setText("")
                            binding.tglkeluar.setText("")
                            binding.statusbayar.setText("")
                            binding.catatan.setText("")
                            binding.sewa.setText("")
                        }
                        CustomDialog.showDialog(requireContext(),message)}
                } catch (e: Exception) {
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        CustomDialog.showDialog(requireContext(),e.message.toString())
                    }
                }

                requireActivity().runOnUiThread { ProgressHelper.dismissDialog() }

            }
        })

    }
}