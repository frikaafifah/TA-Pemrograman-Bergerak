package com.ddi.rumahkos

import PenyewaModel2
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.ddi.rumahkos.databinding.FragmentDetailPenyewaForUserBinding
import com.squareup.picasso.Picasso


class DetailPenyewaForUserFragment : Fragment() {

    lateinit var binding:FragmentDetailPenyewaForUserBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailPenyewaForUserBinding.inflate(layoutInflater,container,false)


        val daftarpenyewa : ArrayList<PenyewaModel2>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("penyewa",PenyewaModel2::class.java)
        } else {
            arguments?.getParcelableArrayList("penyewa" )
        }



        if (daftarpenyewa != null) {
            val list = daftarpenyewa[0]

            binding.nama.setText(list.nama)
            binding.nokamar.setText(list.nokamar)
            binding.alamat.setText(list.alamat)
            binding.telepon.setText(list.telepon)
            binding.email.setText(list.email)
            binding.tgllahir.setText(list.tgllahir)

            Picasso.get().load(list.gambar).into(binding.gambar)
            if(list.gambar.isNotEmpty())
                binding.gambar.setBackgroundResource(0)
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
                ).replace(R.id.fragmentContainer,PenyewaFragment()).commit()
        }catch (_:Exception){}
    }

}