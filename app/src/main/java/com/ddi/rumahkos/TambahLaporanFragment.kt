package com.ddi.rumahkos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.ddi.rumahkos.databinding.FragmentTambahLaporanBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.log

class TambahLaporanFragment : Fragment() {

    var fileNameToUpload:String=""
    lateinit var fileToUpload: File
    lateinit var binding:FragmentTambahLaporanBinding
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTambahLaporanBinding.inflate(layoutInflater,container,false)
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        })

        binding.btnback.setOnClickListener {
            back()
        }

        Log.d("TAG", "onCreateView: "+Helper.getUserId(requireContext()).toString())

        binding.btnkirim.setOnClickListener {
            if(binding.nama.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "nama kosong")
            }else if(binding.judul.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "judul kosong")
            }else if(binding.nokamar.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "no kamar kosong")
            }else if(binding.deskripsi.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "deskripsi kosong")
            }else if(binding.tanggal.text.isNullOrEmpty()){
                CustomDialog.showDialog(requireContext(), "tanggal kosong")
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

        binding.btnAddPhoto.setOnClickListener{
            openFilePicker()
        }

        return binding.root
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*" // You can specify the MIME type(s) of files you want to allow

        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val selectedFileUri: Uri? = data.data

                if (selectedFileUri != null) {
                    val contentResolver = requireActivity().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(selectedFileUri, takeFlags)

                    fileNameToUpload = getFileNameFromUri(selectedFileUri).toString()
                    binding.previewPhoto.setImageURI(selectedFileUri)
                    binding.previewPhoto.setBackgroundResource(0)
                    // binding.txtfilename.text = fileNameToUpload



                    val inputStream = contentResolver.openInputStream(selectedFileUri)

                    if (inputStream != null) {
                        fileToUpload = saveInputStreamToFile(inputStream)!!

                    } else {

                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            displayName
        }
    }
    private fun saveInputStreamToFile(inputStream: InputStream): File? {
        // Create a temporary file to store the content
        val tempDir = requireContext().cacheDir
        val tempFile = File(tempDir, "temp_file")
        return try {
            val outputStream = FileOutputStream(tempFile)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            inputStream.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    fun save() {
        val url = Helper.url+"/tambahpengaduan.php"
        val client = OkHttpClient()
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("userid", Helper.getUserId(requireContext()).toString())
            .addFormDataPart("nama", binding.nama.text.toString())
            .addFormDataPart("judul", binding.judul.text.toString())
            .addFormDataPart("nokamar", binding.nokamar.text.toString())
            .addFormDataPart("tanggal", binding.tanggal.text.toString())
            .addFormDataPart("deskripsi", binding.deskripsi.text.toString())
            .addFormDataPart("file", fileNameToUpload, fileToUpload.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
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
                            binding.judul.setText("")
                            binding.nokamar.setText("")
                            binding.tanggal.setText("")
                            binding.deskripsi.setText("")
                            binding.previewPhoto.setBackgroundResource(0)
                            binding.previewPhoto.setImageURI(null)
                            binding.previewPhoto.setImageDrawable(null)
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