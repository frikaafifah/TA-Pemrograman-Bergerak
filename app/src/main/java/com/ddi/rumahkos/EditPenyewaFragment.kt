package com.ddi.rumahkos

import PengelolaModel
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.ddi.rumahkos.databinding.FragmentEditPenyewaBinding
import com.squareup.picasso.Picasso
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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Locale
import kotlin.math.log

class EditPenyewaFragment : Fragment() {

    var fileNameToUpload:String=""
    lateinit var fileToUpload: File

    lateinit var binding:FragmentEditPenyewaBinding
    val decimalFormat = DecimalFormat("###,###", DecimalFormatSymbols(Locale.getDefault()))
    var currentJumlah = ""
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentEditPenyewaBinding.inflate(layoutInflater,container,false)
        val daftarpenyewa : ArrayList<PengelolaModel>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("penyewa",PengelolaModel::class.java)
        } else {
            arguments?.getParcelableArrayList("penyewa" )
        }
        val posistion = arguments?.getInt("position")

        if (daftarpenyewa != null && posistion != null) {
            val list = daftarpenyewa[posistion]

            binding.nama.setText(list.nama)
            binding.password.setText(list.password)
            binding.alamat.setText(list.alamat)
            binding.nokamar.setText(list.nokamar)
            binding.tgllahir.setText(list.tanggallahir)
            binding.email.setText(list.email)
            binding.biayasewa.setText(list.sewa)
            binding.telepon.setText(list.telepon)
            binding.kontakdarurat.setText(list.kontakdarurat)
            binding.tglmasuk.setText(list.tanggalmasuk)
            binding.tglkeluar.setText(list.tanggalkeluar)
            binding.statusbayar.setText(list.statusbayar)
            binding.catatan.setText(list.catatan)
            Picasso.get().load(list.gambar).into(binding.foto)
            if(list.gambar.isNotEmpty())
                binding.foto.setBackgroundResource(0)

            binding.btnSave.setOnClickListener {
                GlobalScope.launch {
                    requireActivity().runOnUiThread {
                        ProgressHelper.showDialog(requireContext(),"Please wait...")
                    }
                    val x = async {
                        save(list.userid)
                    }
                    x.await()
                }
            }
        }


        binding.biayasewa.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {

                    if (editable.toString() != currentJumlah) {
                        binding.biayasewa.removeTextChangedListener(this)
                        val cleanString = editable.toString().replace("[^\\d]".toRegex(), "")
                        try {
                            val parsed = decimalFormat.parse(cleanString)?.toDouble() ?: 0.0
                            val formatted = decimalFormat.format(parsed)
                            currentJumlah = formatted
                            binding.biayasewa.setText(formatted)
                            binding.biayasewa.setSelection(formatted.length)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        binding.biayasewa.addTextChangedListener(this)
                    }


                }
            }
        )

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity() , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        })

        binding.btnback.setOnClickListener {
            back()
        }


        binding.btnFilePick.setOnClickListener{
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
                    binding.foto.setImageURI(selectedFileUri)
                    binding.foto.setBackgroundResource(0)

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
                ).replace(R.id.fragmentContainer,PengelolaFragment()).commit()
        }catch (_:Exception){}
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    fun save(userid:String) {
        val url = Helper.url+"/editpenyewa.php"
        val client = OkHttpClient()
        val formBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("userid", userid)
            .addFormDataPart("nama", binding.nama.text.toString())
            .addFormDataPart("password", binding.password.text.toString())
            .addFormDataPart("nokamar", binding.nokamar.text.toString())
            .addFormDataPart("alamat", binding.alamat.text.toString())
            .addFormDataPart("telepon", binding.telepon.text.toString())
            .addFormDataPart("tanggallahir", binding.tgllahir.text.toString())
            .addFormDataPart("kontakdarurat", binding.kontakdarurat.text.toString())
            .addFormDataPart("email", binding.email.text.toString())
            .addFormDataPart("tanggalmasuk", binding.tglmasuk.text.toString())
            .addFormDataPart("tanggalkeluar", binding.tglkeluar.text.toString())
            .addFormDataPart("statusbayar", binding.statusbayar.text.toString())
            .addFormDataPart("catatan", binding.catatan.text.toString())
            .addFormDataPart("sewa", binding.biayasewa.text.toString().replace("[^\\d]".toRegex(), ""))
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

//                            binding.nama.setText("")
//                            binding.password.setText("")
//                            binding.nokamar.setText("")
//                            binding.alamat.setText("")
//                            binding.telepon.setText("")
//                            binding.tgllahir.setText("")
//                            binding.kontakdarurat.setText("")
//                            binding.email.setText("")
//                            binding.tglmasuk.setText("")
//                            binding.tglkeluar.setText("")
//                            binding.statusbayar.setText("")
//                            binding.catatan.setText("")
//                            binding.biayasewa.setText("")
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