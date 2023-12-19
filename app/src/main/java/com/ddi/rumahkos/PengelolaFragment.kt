package com.ddi.rumahkos

import PengelolaModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddi.rumahkos.databinding.FragmentPengelolaBinding
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
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log


class PengelolaFragment : Fragment() {

    lateinit var binding:FragmentPengelolaBinding
    var arrayList = ArrayList<PengelolaModel>()
    lateinit  var mAdapter: PengelolaAdapter
    lateinit var mRecyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPengelolaBinding.inflate(layoutInflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
        binding.btnPengaduan.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
                .replace(R.id.fragmentContainer,LaporanPengaduanFragment()).commit()
        }

        binding.btntambah.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
                .replace(R.id.fragmentContainer,TambahPenyewaFragment()).commit()
        }

        binding.btnProfile.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
                .replace(R.id.fragmentContainer,ProfileFragment()).commit()
        }


        mRecyclerView = binding.recyclerview
        mAdapter = PengelolaAdapter(arrayList)
        linearLayoutManager= LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener(clickevent)

        GlobalScope.launch {

            requireActivity().runOnUiThread {
                arrayList.clear()
                mRecyclerView.recycledViewPool.clear()
                mAdapter.notifyDataSetChanged()
                ProgressHelper.showDialog(requireContext(),"Please wait...")
            }
            val x = async {
                if(Helper.isInternetAvailable()){
                    listpenyewa()
                }else{
                    ProgressHelper.dismissDialog()
                    CustomDialog.showDialog(requireContext(),"Internet Connection Problem")
                }

            }
            x.await()
        }


        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    fun listpenyewa() {
        val url = Helper.url+"/daftarpenyewa.php"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
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
                try {
                    val res: Response = client.newCall(request).execute()
                    if (res.isSuccessful) {
                        val jsonData = res.body?.string()
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {

                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val id = jsonObject.getString("id")
                            val userid = jsonObject.getString("userid")
                            val nama = jsonObject.getString("nama")
                            val alamat = jsonObject.getString("alamat")
                            val telepon = jsonObject.getString("telepon")
                            val password = jsonObject.getString("password")
                            val email = jsonObject.getString("email")
                            val tanggallahir = jsonObject.getString("tanggallahir")
                            val sewa = jsonObject.getString("biayasewa")
                            val tanggalmasuk = jsonObject.getString("tanggalmasuk")
                            val tanggalkeluar = jsonObject.getString("tanggalkeluar")
                            val statusbayar = jsonObject.getString("statusbayar")
                            val catatan = jsonObject.getString("catatan")
                            val nokamar = jsonObject.getString("nokamar")
                            val kontakdarurat = jsonObject.getString("kontakdarurat")
                            val gambar = Helper.url+"/"+jsonObject.getString("gambar")


                            arrayList.add(
                                PengelolaModel(
                                    (i+1).toString(),id,userid,nama,password,tanggallahir,email,alamat,nokamar,sewa,telepon,kontakdarurat,tanggalmasuk,tanggalkeluar,statusbayar,catatan,gambar

                                )
                            )
                            requireActivity().runOnUiThread {
                                mAdapter.notifyDataSetChanged()
                            }
                        }

                    }
                } catch (e: Exception) {

                    CustomDialog.showDialog(requireContext(),e.message.toString())
                }
                ProgressHelper.dismissDialog()
            }
        })


    }

    @OptIn(DelicateCoroutinesApi::class)
    private var clickevent =object: PengelolaAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            val bundle = Bundle()
            bundle.putParcelableArrayList("penyewa", arrayList)
            bundle.putInt("position",position)
            val receiverFragment = DetailPenyewaFragment()

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

        override fun onItemEdit(position: Int) {


            val bundle = Bundle()
            bundle.putParcelableArrayList("penyewa", arrayList)
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

        override fun onItemDelete(position: Int) {
            GlobalScope.launch {
                requireActivity().runOnUiThread {
                    ProgressHelper.showDialog(requireContext(),"Please wait...")
                }
                val x = async {
                    deletepenyewa(position)
                }
                x.await()
            }
        }


    }

    fun deletepenyewa(position: Int) {
        val url = Helper.url+"/hapuspenyewa.php"

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("userid", arrayList[position].userid)
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
                            try {
                                val iterator = arrayList.iterator()
                                while (iterator.hasNext()) {
                                    val element = iterator.next()
                                    if (element.userid == arrayList[position].userid) {
                                        iterator.remove()
                                    }
                                }
                            }catch (e:Exception){
                                arrayList.removeAt(position)
                            }
                            mAdapter.notifyDataSetChanged()
                            CustomDialog.showDialog(requireContext(),message)
                        } else {
                            CustomDialog.showDialog(requireContext(),message)
                        }}
                } catch (e: Exception) {
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        CustomDialog.showDialog(requireContext(),responseData.toString())
                    }
                }
                requireActivity().runOnUiThread {
                    ProgressHelper.dismissDialog()}

            }
        })
    }

}