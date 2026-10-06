package pharmlane.com.PharmLaneStore.activities

import pharmlane.com.PharmLaneStore.utills.IMAGE_PERMISSIONS
import pharmlane.com.PharmLaneStore.utills.REQUEST_CAMERA_PERMISSION
import android.Manifest
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.InternalStorageContentProvider
import pharmlane.com.PharmLaneStore.utills.ScaleFile
import pharmlane.com.PharmLaneStore.utills.Utils
import com.bumptech.glide.Glide
import pharmlane.com.PharmLaneStore.utills.cropper.CropImage
import com.canhub.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_gst_number.*
import kotlinx.android.synthetic.main.activity_gst_number.img_back
import kotlinx.android.synthetic.main.activity_store_profile.*
import org.json.JSONObject
import pharmlane.com.PharmLaneStore.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException

class GstNumberActivity : AppCompatActivity(), View.OnClickListener {

    private var mFileTemp: Uri? = null
    private val SELECT_FILE = 1
    var bitmap: Bitmap? = null
    var path = ""
    var gst_number = ""
    var storePhoto = ""
    private val REQUEST_CAMERA = 0
    val REQUEST_CODE_TAKE_PICTURE = 2
    val REQUEST_CODE_GALLERY = 1
    val INITIAL_GALLERY = IMAGE_PERMISSIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_gst_number)

        img_back.setOnClickListener(this)
        img_upload_picture.setOnClickListener(this)
        btn_download_gst.setOnClickListener(this)
        btn.setOnClickListener(this)

        edt_gst_no.setText(AppConstant.getPreferenceText(AppConstant.GSET_NUMBER))

        Glide.with(applicationContext)  //2
                .load(AppConstant.getPreferenceText(AppConstant.GST_CERTIFICATE)) //3
                .centerCrop() //4
                .placeholder(R.drawable.ic_launcher_background) //5
                .error(R.drawable.ic_launcher_background) //6
                .fallback(R.drawable.ic_launcher_background) //7
                .placeholder(R.drawable.ic_launcher_background) //5
                .into(img_upload_picture) //8
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.btn -> {
                gst_number = edt_gst_no.text.toString().trim()
                if (gst_number.length < 1) {
                    Toast.makeText(this, "Enter GST Number", Toast.LENGTH_LONG).show()
                } else {
                    update_storeDetail()
                }
            }
            R.id.img_upload_picture -> {
                choice()

                selectFromGallery()
            }
            R.id.btn_download_gst -> {
                val imageName = AppConstant.getPreferenceText(AppConstant.GST_CERTIFICATE).substring(AppConstant.getPreferenceText(AppConstant.GST_CERTIFICATE).lastIndexOf('/') + 1)
                if (Utils.downloadImageNew(imageName, AppConstant.getPreferenceText(AppConstant.GST_CERTIFICATE), applicationContext)) {
                    Toast.makeText(applicationContext, "GST Certificate saved in your Pictures folder.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "GST Certificate download failed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun choice() {
        var fileTemporary: File? = null
        try {
            fileTemporary = Utils.createTempImageFile(this)
        } catch (exception: IOException) {
            Log.d(javaClass.simpleName + " openCamera", exception.toString())
        }
        mFileTemp = FileProvider.getUriForFile(this,
            BuildConfig.APPLICATION_ID.toString() + ".provider", fileTemporary!!)
    }

    private fun openCamera() {
        // Declaring CAMERA means ACTION_IMAGE_CAPTURE throws unless it is granted.
        if (!hasPermission(Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return
        }
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    mFileTemp
            )
            intent.putExtra("return-data", true)
            startActivityForResult(intent, REQUEST_CAMERA)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun selectFromGallery() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!canAccessCamera()) {
                requestPermissions(INITIAL_GALLERY, 1340)
            } else {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
            }
        } else {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
        }
    }

    private fun canAccessCamera(): Boolean {
        return IMAGE_PERMISSIONS.all { hasPermission(it) }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun hasPermission(perm: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm)
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1340) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
            } else {
                Toast.makeText(this, "Premission Required", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    intent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            mFileTemp
                    )
                    intent.putExtra("return-data", true)
                    startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                val selectedImageUri = data?.data
                CropImage.activity(selectedImageUri)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setAspectRatio(3, 4)
                    .setInitialCropWindowPaddingRatio(0f)
                        .setActivityTitle(resources.getString(R.string.my_crop))
                        .start(this)
            }
            if (requestCode == REQUEST_CAMERA) {
                val selectedImageUri = mFileTemp
                CropImage.activity(selectedImageUri)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setAspectRatio(3, 4)
                    .setInitialCropWindowPaddingRatio(0f)
                        .setActivityTitle(resources.getString(R.string.my_crop))
                        .start(this)
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                val resultUri = result.uri
                //imgProfile.setImageURI(resultUri)
                path = resultUri.path ?: ""
                if (resultUri.path == null) {
                    return
                } else {

//                    bitmap = BitmapFactory.decodeFile(path)
                    bitmap = ScaleFile.compressImage(path, this)
                    img_upload_picture.setImageBitmap(bitmap)
                    storePhoto = AppConstant.convert(bitmap!!)

                    btn?.alpha = 1F
                    btn?.isEnabled = true

                }
            }
        }
    }

    private fun update_storeDetail() {
        progressBar.visibility = View.VISIBLE

        val register = Register()
        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        register.gst_certificate = storePhoto
        register.gst_number = gst_number

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.update_store_detail(register)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

//                    startActivity(Intent(this@GstNumberActivity, StoreProfileActivity::class.java))
                    finish()

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@GstNumberActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@GstNumberActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }
}
