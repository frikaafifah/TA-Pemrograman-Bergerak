package com.ddi.rumahkos

import LaporanPengaduanModel
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.ddi.rumahkos.databinding.FragmentDetailLaporanBinding
import com.ddi.rumahkos.databinding.FragmentLaporanPengaduanBinding
import com.squareup.picasso.Picasso


class DetailLaporanFragment : Fragment() {

    lateinit var binding:FragmentDetailLaporanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailLaporanBinding.inflate(layoutInflater,container,false)


        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        })

        binding.btnback.setOnClickListener {
            back()
        }

        val daftarpenyewa : ArrayList<LaporanPengaduanModel>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("pengaduan",LaporanPengaduanModel::class.java)
        } else {
            arguments?.getParcelableArrayList("pengaduan" )
        }
        val posistion = arguments?.getInt("position")

        if (daftarpenyewa != null && posistion != null) {
            val list = daftarpenyewa[posistion]
            binding.judul.setText(list.judul)
            binding.nokamar.setText(list.nokamar)
            binding.deskripsi.setText(list.deskripsi)
            binding.nama.setText(list.nama)
            binding.tanggal.setText(list.tanggal)

            Picasso.get().load(list.bukti).into(binding.gambar)

            if(list.bukti.isNotEmpty())
                binding.gambar.setBackgroundResource(0)

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
}