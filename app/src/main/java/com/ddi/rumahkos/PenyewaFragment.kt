package com.ddi.rumahkos

import PenyewaModel
import PenyewaModel2
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddi.rumahkos.databinding.FragmentPenyewaBinding
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
import org.json.JSONArray
import org.json.JSONObject


class PenyewaFragment : Fragment() {

    lateinit var binding:FragmentPenyewaBinding
    var arrayList = ArrayList<PenyewaModel>()
    var arrayList2 = ArrayList<PenyewaModel2>()
    lateinit  var mAdapter: PenyewaAdapter
    lateinit var mRecyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    @SuppressLint("NotifyDataSetChanged")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentPenyewaBinding.inflate(layoutInflater,container,false)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        binding.btnBuatPengaduan.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down,
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
                .replace(R.id.fragmentContainer,TambahLaporanFragment()).commit()
        }


        mRecyclerView = binding.recyclerview
        mAdapter = PenyewaAdapter(arrayList)
        linearLayoutManager= LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener(clickevent)


        requireActivity().runOnUiThread {
            arrayList.clear()
            mRecyclerView.recycledViewPool.clear()
            mAdapter.notifyDataSetChanged()
            ProgressHelper.showDialog(requireContext(),"Please wait...")
        }
        GlobalScope.launch {


            val x = async {
                if(Helper.isInternetAvailable()){
                    ambilpenyewa()
                    listPengaduan()
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
    fun listPengaduan() {
        val url = Helper.url+"/daftarpengaduan.php"
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("userid", Helper.getUserId(requireContext()).toString())
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
                val res: Response = client.newCall(request).execute()
                try {

                    if (res.isSuccessful) {
                        val jsonData = res.body?.string()
                        val jsonArray = JSONArray(jsonData)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val id = jsonObject.getString("id")
                            val userid = jsonObject.getString("userid")
                            val tanggal = jsonObject.getString("tanggal")
                            val judul = jsonObject.getString("judul")
                            val deskripsi = jsonObject.getString("deskripsi")
                            val nokamar = jsonObject.getString("nokamar")
                            val nama = jsonObject.getString("nama")
                            val bukti = jsonObject.getString("bukti")

                            arrayList.add(
                                PenyewaModel(
                                    (i+1).toString(),id,userid,nama,judul, deskripsi,tanggal,nokamar,bukti

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

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    fun ambilpenyewa() {
        val url = Helper.url+"/ambilpenyewa.php"
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("userid", Helper.getUserId(requireContext()).toString())
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

            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
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
                            val nokamar = jsonObject.getString("nokamar")
                            val alamat = jsonObject.getString("alamat")
                            val telepon = jsonObject.getString("telepon")
                            val email = jsonObject.getString("email")
                            val tgllahir = jsonObject.getString("tanggallahir")
                            val gambar = Helper.url+"/"+jsonObject.getString("gambar")




                            arrayList2.add(
                                PenyewaModel2(
                                    (i+1).toString(),id,userid,nama,tgllahir,alamat,telepon,email,nokamar,gambar

                                )
                            )


                            requireActivity().runOnUiThread {
                               binding.nama.text = nama
                                binding.nokamar.text = "Kamar No. $nokamar"
                                binding.alamat.text = alamat

                                Picasso.get().load(gambar).into(binding.foto)
                                if(gambar.isNotEmpty())
                                    binding.foto.setBackgroundResource(0)
                            }
                        }

                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                    CustomDialog.showDialog(requireContext(),e.message.toString())
                    }
                }
                ProgressHelper.dismissDialog()
            }
        })

        binding.btnProfile.setOnClickListener {
            try{
                val bundle = Bundle()
                bundle.putParcelableArrayList("penyewa", arrayList2)
                val receiverFragment = DetailPenyewaForUserFragment()

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
            }catch (_:Exception){}
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private var clickevent =object: PenyewaAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
//            val bundle = Bundle()
//            bundle.putParcelableArrayList("pengaduan", arrayList2)
//            val receiverFragment = DetailLaporanFragment()
//
//            receiverFragment.arguments = bundle
//            requireActivity().supportFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.anim.slide_in_up,
//                    R.anim.slide_out_down,
//                    R.anim.slide_in_up,
//                    R.anim.slide_out_down
//                )
//                .replace(R.id.fragmentContainer, receiverFragment)
//                .commit()
        }

        override fun onItemDelete(position: Int) {
            GlobalScope.launch {
                requireActivity().runOnUiThread {
                    ProgressHelper.showDialog(requireContext(),"Please wait...")
                }
                val x = async {
                    deletepengaduan(position)
                }
                x.await()
            }
        }


    }

    fun deletepengaduan(position: Int) {
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
                            try {
                                val iterator = arrayList.iterator()
                                while (iterator.hasNext()) {
                                    val element = iterator.next()
                                    if (element.id == arrayList[position].id) {
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
                }
                requireActivity().runOnUiThread {
                    ProgressHelper.dismissDialog()}

            }
        })
    }

}