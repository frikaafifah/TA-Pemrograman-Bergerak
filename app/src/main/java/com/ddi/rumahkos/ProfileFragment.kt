package com.ddi.rumahkos

import PengelolaModel
import ProfileModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.ddi.rumahkos.databinding.FragmentDetailPenyewaBinding
import com.ddi.rumahkos.databinding.FragmentPengelolaBinding
import com.ddi.rumahkos.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@Suppress("UNREACHABLE_CODE")
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentProfileBinding

    @OptIn(DelicateCoroutinesApi::class)

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//
//
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

//        val daftarpengelola: ArrayList<ProfileModel>? =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                arguments?.getParcelableArrayList("pengelola", ProfileModel::class.java)
//            } else {
//                arguments?.getParcelableArrayList("pengelola")
//            }
//        val position = arguments?.getInt("position")
//
//        if (daftarpengelola != null && position != null) {
//            val list = daftarpengelola[position]
//
//            binding.nama.setText(list.nama)
//            binding.alamat.setText(list.alamat)
//            binding.telepon.setText(list.telepon)
//            binding.email.setText(list.email)
//            binding.tgllahir.setText(list.tanggallahir)
//            // binding.gambar.setImageURI(Uri.parse(list.gambar))
//            Picasso.get().load(list.gambar).into(binding.gambar)
//            if (list.gambar.isNotEmpty())
//                binding.gambar.setBackgroundResource(0)


            binding.btnKeluar.setOnClickListener {
                activity?.finish()

            }


//


//        }



    }

//    private fun back() {
//        TODO("Not yet implemented")
//    }
}