package com.ddi.rumahkos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddi.rumahkos.databinding.FragmentLoginBinding
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


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container,false)


        binding.btnlogin.setOnClickListener {
            GlobalScope.launch {
                requireActivity().runOnUiThread {
                    ProgressHelper.showDialog(requireContext(),"Please wait...")
                }
                val x = async {

                    loginUser(binding.txtusername.text.toString(),binding.txtpassword.text.toString())
                }
                x.await()


            }
        }

        return binding.root
    }


    private fun loginUser(nama: String, password: String) {
        val url = Helper.url+"/login.php"

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("nama", nama)
            .add("password", password)
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

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body?.string()
                    val jsonResponse = JSONObject(responseData.toString())
                    val message = jsonResponse.getString("message")
                    val userid = jsonResponse.getString("userid")
                    val username = jsonResponse.getString("nama")
                    val hakakses = jsonResponse.getString("hakakses")

                    if (message == "success") {
                        Helper.updateUserId(requireContext(),userid)
                        Helper.updateUserName(requireContext(),username)

                        if(hakakses == "admin"){
                            requireActivity().supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.slide_in_up,
                                    R.anim.slide_out_down,
                                    R.anim.slide_in_up,
                                    R.anim.slide_out_down
                                )
                                .replace(R.id.fragmentContainer,PengelolaFragment()).commit()
                        }else{
                            requireActivity().supportFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.slide_in_up,
                                    R.anim.slide_out_down,
                                    R.anim.slide_in_up,
                                    R.anim.slide_out_down
                                )
                                .replace(R.id.fragmentContainer,PenyewaFragment()).commit()
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            CustomDialog.showDialog(requireContext(),"login Failed")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                requireActivity().runOnUiThread {
                    ProgressHelper.dismissDialog()
                }
            }

        })
    }

}