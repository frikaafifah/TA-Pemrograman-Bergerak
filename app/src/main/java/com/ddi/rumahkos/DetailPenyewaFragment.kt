package com.ddi.rumahkos

import PengelolaModel
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.ddi.rumahkos.databinding.FragmentDetailPenyewaBinding
import com.squareup.picasso.Picasso
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


class DetailPenyewaFragment : Fragment() {


    lateinit var binding:FragmentDetailPenyewaBinding
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentDetailPenyewaBinding.inflate(layoutInflater,container,false)


        val daftarpenyewa : ArrayList<PengelolaModel>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("penyewa",PengelolaModel::class.java)
        } else {
            arguments?.getParcelableArrayList("penyewa" )
        }
        val position = arguments?.getInt("position")


        if (daftarpenyewa != null && position != null) {
            val list = daftarpenyewa[position]

            binding.nama.setText(list.nama)
            binding.alamat.setText(list.alamat)
            binding.nokamar.setText(list.nokamar)
            binding.tgllahir.setText(list.tanggallahir)
            binding.email.setText(list.email)
            binding.sewa.setText(list.sewa)
            binding.telepon.setText(list.telepon)
            binding.kontakdarurat.setText(list.kontakdarurat)
            binding.tglmasuk.setText(list.tanggalmasuk)
            binding.tglkeluar.setText(list.tanggalkeluar)
            binding.statusbayar.setText(list.statusbayar)
            binding.catatan.setText(list.catatan)
           // binding.gambar.setImageURI(Uri.parse(list.gambar))
            Picasso.get().load(list.gambar).into(binding.gambar)
            if(list.gambar.isNotEmpty())
                binding.gambar.setBackgroundResource(0)

            binding.btnhapus.setOnClickListener {
                GlobalScope.launch {
                    requireActivity().runOnUiThread {
                        ProgressHelper.showDialog(requireContext(),"Please wait...")
                    }
                    val x = async {
                        deletepenyewa(list.userid)
                    }
                    x.await()
                }
            }

            binding.btnEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelableArrayList("penyewa", daftarpenyewa)
                bundle.putInt("position",position)
                val receiverFragment = EditPenyewaFragment()

                receiverFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down,
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    )
                    .replace(R.id.fragmentContainer, receiverFragment)
                    .commit()
            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        })

        binding.btnback.setOnClickListener {
            back()
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

    fun deletepenyewa(userid: String) {
        val url = Helper.url+"/hapuspenyewa.php"

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("userid", userid)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    ProgressHelper.dismissDialog()
                    CustomDialog.showDialog(requireContext(),"Network Problem")
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                try {
                    val jsonResponse = JSONObject(responseData.toString())
                    val message = jsonResponse.getString("message")

                    requireActivity().runOnUiThread {
                        if (message == "success") {
                            CustomDialog.showDialog(requireContext(),message)
                        } else {
                            CustomDialog.showDialog(requireContext(),message)
                        }}
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                requireActivity().runOnUiThread {
                    ProgressHelper.dismissDialog()}

            }
        })
    }
}