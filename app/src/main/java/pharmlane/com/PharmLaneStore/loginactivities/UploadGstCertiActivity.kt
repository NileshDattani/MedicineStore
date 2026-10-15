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
import android.util.DisplayMetrics
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
import kotlinx.android.synthetic.main.activity_upload_photo.*
import pharmlane.com.PharmLaneStore.BuildConfig
import pharmlane.com.PharmLaneStore.utills.Utils
import java.io.File
import java.io.IOException

class UploadGstCertiActivity : AppCompatActivity(), View.OnClickListener {

    private var mFileTemp: Uri? = null
    private val SELECT_FILE = 1
    var bitmap: Bitmap? = null
    var path = ""
    private val REQUEST_CAMERA = 0
    val REQUEST_CODE_TAKE_PICTURE = 2
    val REQUEST_CODE_GALLERY = 1
    val INITIAL_GALLERY = IMAGE_PERMISSIONS


    private var isGstCerti = false
    private var isDrugLic = false
    private var isStorePhoto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )*/
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
        setContentView(R.layout.activity_upload_photo)

        btn_next.setOnClickListener(this)
        img_back.setOnClickListener(this)
        txt_skip3.setOnClickListener(this)
        txt_skip2.setOnClickListener(this)
        txt_skip.setOnClickListener(this)

        img_upload_gst_certi.setOnClickListener(this)
        img_upload_drug_lic.setOnClickListener(this)
        img_upload_store_photo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.img_back -> {

               /* if (isDrugLic) {
                    isDrugLic = false
                    constraint2.visibility = View.VISIBLE
                    constraint3.visibility = View.GONE
                } else if (isGstCerti) {
                    isGstCerti = false
                    constraint2.visibility = View.GONE
                    constraint1.visibility = View.VISIBLE

                } else {*/
                    finish()
              //  }
            }
            R.id.txt_skip3 -> {
                startActivity(
                    Intent(
                        this@UploadGstCertiActivity,
                        PreferredPaymentActivity::class.java
                    )
                )
                // finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_skip2 -> {
                isDrugLic = true
                constraint2.visibility = View.GONE
                constraint3.visibility = View.VISIBLE
            }
            R.id.txt_skip -> {
             /*   isGstCerti = true
                constraint1.visibility = View.GONE
                constraint2.visibility = View.VISIBLE*/
                startActivity(
                    Intent(
                        this@UploadGstCertiActivity,
                        UploadDrugLicenseActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_next -> {

              //  if (isDrugLic) {
                    startActivity(
                        Intent(
                            this@UploadGstCertiActivity,
                            UploadDrugLicenseActivity::class.java
                        )
                    )
                    // finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
               /* } else if (isGstCerti) {
                    isDrugLic = true
                    constraint2.visibility = View.GONE
                    constraint3.visibility = View.VISIBLE

                } else {
                    isGstCerti = true
                    constraint1.visibility = View.GONE
                    constraint2.visibility = View.VISIBLE
                }*/
            }
            R.id.img_upload_gst_certi -> {
                choice()
                selectFromGallery()
            }
            R.id.img_upload_drug_lic -> {
                choice()
                selectFromGallery()
            }
            R.id.img_upload_store_photo -> {
                choice()
                selectFromGallery()
            }
        }
    }
/*
    override fun onBackPressed() {
        // super.onBackPressed()
        if (isDrugLic) {
            isDrugLic = false
            constraint2.visibility = View.VISIBLE
            constraint3.visibility = View.GONE
        } else if (isGstCerti) {
            isGstCerti = false
            constraint2.visibility = View.GONE
            constraint1.visibility = View.VISIBLE
        } else {
            finish()
        }
    }*/

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
                        .setActivityTitle(getResources().getString(R.string.my_crop))
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
                    gst_certificate = AppConstant.convert(bitmap!!)
                   /* val resizedBitmap = resizeBitmap(bitmap!!, 100, 100)
                    Toast.makeText(
                        this,
                        "width===>  ${resizedBitmap.width} * height=====> ${resizedBitmap.height}",
                        Toast.LENGTH_SHORT
                    ).show()

                    println("density: ${determineScreenDensityCode()}")*/

                  /*  if (isDrugLic) {
                        img_upload_store_photo.setImageBitmap(bitmap)
                    } else if (isGstCerti) {
                        img_upload_drug_lic.setImageBitmap(bitmap)
                    } else {*/
                        img_upload_gst_certi.setImageBitmap(bitmap)
                  //  }
                    /*  btn_next?.setAlpha(1F)
                      btn_next?.setEnabled(true)*/
                }
            }
        }
    }

    // Method to resize a bitmap programmatically
    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        /*

            *** reference source developer.android.com ***
            Bitmap createScaledBitmap (Bitmap src, int dstWidth, int dstHeight, boolean filter)
                Creates a new bitmap, scaled from an existing bitmap, when possible. If the specified
                width and height are the same as the current width and height of the source bitmap,
                the source bitmap is returned and no new bitmap is created.

            Parameters
                src Bitmap : The source bitmap.
                    This value must never be null.

            dstWidth int : The new bitmap's desired width.
            dstHeight int : The new bitmap's desired height.
            filter boolean : true if the source should be filtered.

            Returns
                Bitmap : The new scaled bitmap or the source bitmap if no scaling is required.

            Throws
                IllegalArgumentException : if width is <= 0, or height is <= 0
        */
        return Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            false
        )
    }

    fun determineScreenDensityCode(): String {
        return when (resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "ldpi"
            DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
            DisplayMetrics.DENSITY_HIGH -> "hdpi"
            DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_280 -> "xhdpi"
            DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_360, DisplayMetrics.DENSITY_400, DisplayMetrics.DENSITY_420 -> "xxhdpi"
            DisplayMetrics.DENSITY_XXXHIGH, DisplayMetrics.DENSITY_560 -> "xxxhdpi"
            else -> "Unknown code ${resources.displayMetrics.densityDpi}"
        }
    }


    companion object{
        var gst_certificate = ""
    }
}