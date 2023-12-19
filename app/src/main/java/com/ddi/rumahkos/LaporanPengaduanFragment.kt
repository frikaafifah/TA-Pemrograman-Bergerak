package com.ddi.rumahkos

import LaporanPengaduanModel
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddi.rumahkos.databinding.FragmentLaporanPengaduanBinding
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
import org.json.JSONArray
import org.json.JSONObject
import okio.IOException

class LaporanPengaduanFragment : Fragment() {


    lateinit var binding:FragmentLaporanPengaduanBinding
    var arrayList = ArrayList<LaporanPengaduanModel>()
    lateinit  var mAdapter: LaporanPengaduanAdapter
    lateinit var mRecyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaporanPengaduanBinding.inflate(layoutInflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        })

        binding.btnback.setOnClickListener {
            back()
        }

        mRecyclerView = binding.recyclerview
        mAdapter = LaporanPengaduanAdapter(arrayList)
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
                    listpengaduan()
                }else{
                    ProgressHelper.dismissDialog()
                    CustomDialog.showDialog(requireContext(),"Internet Connection Problem")
                }

            }
            x.await()
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
    fun listpengaduan() {
        val url = Helper.url+"/daftarpengaduan.php"
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
                            val judul = jsonObject.getString("judul")
                            val deskripsi = jsonObject.getString("deskripsi")
                            val nokamar = jsonObject.getString("nokamar")
                            val nama = jsonObject.getString("nama")
                            val tanggal = jsonObject.getString("tanggal")
                            val bukti = Helper.url+"/"+jsonObject.getString("bukti")

                            arrayList.add(
                                LaporanPengaduanModel(
                                    (i+1).toString(),id,userid,judul,deskripsi,tanggal,nama,nokamar,bukti
                                )
                            )

                            requireActivity().runOnUiThread {
                                mAdapter.notifyDataSetChanged()
                            }
                        }

                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                    CustomDialog.showDialog(requireContext(),e.message.toString())}
                }
                ProgressHelper.dismissDialog()
            }
        })


    }

    @OptIn(DelicateCoroutinesApi::class)
    private var clickevent =object: LaporanPengaduanAdapter.OnItemClickListener {

        override fun onItemClick(position: Int) {

            val bundle = Bundle()
            bundle.putParcelableArrayList("pengaduan", arrayList)
            bundle.putInt("position",position)
            val receiverFragment = DetailLaporanFragment()

            receiverFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
                .replace(R.id.fragmentContainer,receiverFragment).commit()
        }


        override fun onItemDelete(position: Int) {
            GlobalScope.launch {
                requireActivity().runOnUiThread {
                    ProgressHelper.showDialog(requireContext(),"Please wait...")
                }
                val x = async {
                    deletelaporan(position)
                }
                x.await()
            }
        }


    }

    fun deletelaporan(position: Int) {
        val url = Helper.url+"/hapuslaporanpengaduan.php"

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("id", arrayList[position].id)
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
                            try{
                                arrayList.removeAt(position)
                            }catch (_:Exception){}
                            mAdapter.notifyDataSetChanged()
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