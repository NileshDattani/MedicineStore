package pharmlane.com.PharmLaneStore.loginactivities

import pharmlane.com.PharmLaneStore.utills.IMAGE_PERMISSIONS
import pharmlane.com.PharmLaneStore.utills.REQUEST_CAMERA_PERMISSION
import android.Manifest
import android.annotation.TargetApi
import androidx.appcompat.app.AppCompatActivity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.InternalStorageContentProvider
import pharmlane.com.PharmLaneStore.utills.ScaleFile
import pharmlane.com.PharmLaneStore.utills.cropper.CropImage
import com.canhub.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_upload_store_photo.*
import kotlinx.android.synthetic.main.activity_upload_store_photo.btn_next
import kotlinx.android.synthetic.main.activity_upload_store_photo.img_back
import kotlinx.android.synthetic.main.activity_upload_store_photo.txt_skip
import pharmlane.com.PharmLaneStore.BuildConfig
import pharmlane.com.PharmLaneStore.utills.Utils
import java.io.File
import java.io.IOException

class UploadStorePhotoActivity : AppCompatActivity(), View.OnClickListener {
    private var mFileTemp: Uri? = null
    private val SELECT_FILE = 1
    var bitmap: Bitmap? = null
    var path = ""
    private val REQUEST_CAMERA = 0
    val REQUEST_CODE_TAKE_PICTURE = 2
    val REQUEST_CODE_GALLERY = 1
    val INITIAL_GALLERY = IMAGE_PERMISSIONS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }//  set status text dark

        setContentView(R.layout.activity_upload_store_photo)



        btn_next.setOnClickListener(this)
        img_back.setOnClickListener(this)
        txt_skip.setOnClickListener(this)
        img_upload_store_photo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.img_back -> {
                finish()
            }
            R.id.txt_skip -> {
                startActivity(
                    Intent(
                        this@UploadStorePhotoActivity,
                        PreferredPaymentActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_next -> {
                startActivity(
                    Intent(
                        this@UploadStorePhotoActivity,
                        PreferredPaymentActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_upload_store_photo -> {
                choice()
                selectFromGallery()
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
                        .setAspectRatio(1, 1)
                    .setInitialCropWindowPaddingRatio(0f)
                        .setActivityTitle(getResources().getString(R.string.my_crop))
                        .start(this)
            }
            if (requestCode == REQUEST_CAMERA) {
                val selectedImageUri = mFileTemp
                CropImage.activity(selectedImageUri)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setAspectRatio(1, 1)
                    .setInitialCropWindowPaddingRatio(0f)
                        .setActivityTitle(getResources().getString(R.string.my_crop))
                        .start(this)
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                val resultUri = result.getUri()
                //imgProfile.setImageURI(resultUri)
                path = resultUri.getPath() ?: ""
                if (resultUri.getPath() == null) {
                    return
                } else {
//                    bitmap = BitmapFactory.decodeFile(path)
                    bitmap = ScaleFile.compressImage(path, this)
                    img_upload_store_photo.setImageBitmap(bitmap)
                    storePhoto   = AppConstant.convert(bitmap!!)
                }
            }
        }
    }
    companion object{
        var storePhoto = ""
    }

}