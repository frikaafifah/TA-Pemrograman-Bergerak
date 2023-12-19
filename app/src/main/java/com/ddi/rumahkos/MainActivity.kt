package com.ddi.rumahkos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ddi.rumahkos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,LoginFragment()).commit()

        setContentView(binding.root)

    }
}