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
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.StorePaymntAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Filter
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.GetPaymentMethodsResponse
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.InternalStorageContentProvider
import pharmlane.com.PharmLaneStore.utills.ScaleFile
import pharmlane.com.PharmLaneStore.utills.cropper.CropImage
import com.canhub.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_payment_method.*
import kotlinx.android.synthetic.main.activity_edit_payment_method.img_back
import kotlinx.android.synthetic.main.activity_preferred_payment.*
import kotlinx.android.synthetic.main.activity_store_profile.*
import kotlinx.android.synthetic.main.activity_upload_photo.*
import org.json.JSONObject
import pharmlane.com.PharmLaneStore.BuildConfig
import pharmlane.com.PharmLaneStore.utills.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class EditPaymentMethodActivity : AppCompatActivity(), View.OnClickListener, StorePaymntAdapter.arrayPayment, StorePaymntAdapter.addQRCode, StorePaymntAdapter.isProgress {

    private var mFileTemp: Uri? = null
    private val SELECT_FILE = 1
    var bitmap: Bitmap? = null
    var path = ""
    var whichQR = ""
    private val REQUEST_CAMERA = 0
    val REQUEST_CODE_TAKE_PICTURE = 2
    val REQUEST_CODE_GALLERY = 1
    val INITIAL_GALLERY = IMAGE_PERMISSIONS
    private var img_qr: AppCompatImageView? = null
    private var chb_qr: CheckBox? = null

    var arrayPayment = ArrayList<Register.payment>()
    var paymentArray: List<GetPaymentMethodsResponse.Data> = arrayListOf()
    private var paymntAdapter: StorePaymntAdapter? = null
    private var recycler: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_edit_payment_method)

        recycler = findViewById(R.id.recyl_paymnt)

        img_back.setOnClickListener(this)
        btn.setOnClickListener(this)

        recycler!!.setHasFixedSize(true)
        recycler!!.layoutManager =
                LinearLayoutManager(this)
        AppConstant.setPreferenceText(AppConstant.GPAY_QR_CODE, "")
        AppConstant.setPreferenceText(AppConstant.PHONEPAY_QR_CODE, "")
        AppConstant.setPreferenceText(AppConstant.PAYTM_QR_CODE, "")
        getdropdownApi()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.btn -> {
                Log.e("okhttp", "btn   ")

                for (i in 0 until  arrayPayment.size){
                    Log.e("myisSelected","update_storeDetail_upi_id   "+arrayPayment.get(i).payment_method_id)
                    Log.e("myisSelected","update_storeDetail_qr_img   "+arrayPayment.get(i).upi_id)
                    Log.e("myisSelected","update_storeDetail_PAy ID   "+arrayPayment.get(i).qr_img)
                }


                update_storeDetail(arrayPayment)
            }
        }

    }

    private fun getdropdownApi() {
        progressBar_payment.visibility = View.VISIBLE
        //   progressBar.visibility = View.VISIBLE
        val apiService = AppConstant.getClient()!!.create(RetrofitInterface::class.java)
        val filter = Filter()
        filter.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        val call = apiService.getqrcodes(filter)

        call.enqueue(object : Callback<GetPaymentMethodsResponse> {
            override fun onResponse(
                    call: Call<GetPaymentMethodsResponse>,
                    response: Response<GetPaymentMethodsResponse>
            ) {

                Log.e("Hllodsd","Res[ponse   "+response.toString())

                // progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    progressBar_payment.visibility = View.GONE

                    paymentArray = arrayListOf()

                    paymentArray = response.body()?.data!!

                    paymntAdapter = StorePaymntAdapter(this@EditPaymentMethodActivity, paymentArray, this@EditPaymentMethodActivity, this@EditPaymentMethodActivity,this@EditPaymentMethodActivity)
                    recycler!!.adapter = paymntAdapter

                }else{
                    progressBar_payment.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<GetPaymentMethodsResponse>, t: Throwable) {
                // progressBar.visibility = View.GONE
            }
        })
    }

    private fun update_storeDetail(paymentArray: ArrayList<Register.payment>) {
        progressBar_payment.visibility = View.VISIBLE


        for (i in paymentArray.indices){
            Log.e("myisSelected","update_storeDetail_upi_id   "+paymentArray.get(i).payment_method_id)
            Log.e("myisSelected","update_storeDetail_qr_img   "+paymentArray.get(i).upi_id)
            Log.e("myisSelected","update_storeDetail_PAy ID   "+paymentArray.get(i).qr_img)
        }

        val register = Register()
        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        register.payment_method_array = paymentArray

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.update_store_detail(register)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                progressBar_payment.visibility = View.GONE
                if (response.isSuccessful) {
                    AppConstant.setPreferenceText(AppConstant.GPAY_QR_CODE, "")
                    AppConstant.setPreferenceText(AppConstant.PHONEPAY_QR_CODE, "")
                    AppConstant.setPreferenceText(AppConstant.PAYTM_QR_CODE, "")
                    finish()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@EditPaymentMethodActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@EditPaymentMethodActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }

            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar_payment.visibility = View.GONE
            }
        })
    }

    override fun add(array: ArrayList<Register.payment>) {
        arrayPayment = arrayListOf()
        arrayPayment = array

    }

    override fun qr_codee(img: AppCompatImageView, checks: CheckBox, name: String) {
        whichQR = name
        img_qr = img
        chb_qr = checks

        choiceAlertView()
    }

    private fun choiceAlertView() {
        var fileTemporary: File? = null
        try {
            fileTemporary = Utils.createTempImageFile(this)
        } catch (exception: IOException) {
            Log.d(javaClass.simpleName + " openCamera", exception.toString())
        }
        mFileTemp = FileProvider.getUriForFile(this,
            BuildConfig.APPLICATION_ID.toString() + ".provider", fileTemporary!!)
        val items = arrayOf("Take from Camera", "Select from Gallery")
        val builderSingle = AlertDialog.Builder(this)
        builderSingle.setTitle("Make your selection")
        builderSingle.setItems(items) { dialog, which ->
            if (which == 0) {
                openCamera()
            } else if (which == 1) {
                selectFromGallery()
            }
            chb_qr!!.isChecked = false
        }.create().show()
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
                        .setActivityTitle(resources.getString(R.string.my_crop))
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
                    qr_img = AppConstant.convert(bitmap!!)

                    if(whichQR.equals("Google Pay")){
                        AppConstant.setPreferenceText(AppConstant.GPAY_QR_CODE, qr_img)
                    }else if (whichQR.equals("Paytm")){
                        AppConstant.setPreferenceText(AppConstant.PAYTM_QR_CODE, qr_img)
                    }else if (whichQR.equals("Phone Pe")){
                        AppConstant.setPreferenceText(AppConstant.PHONEPAY_QR_CODE, qr_img)
                    }



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
                    img_qr?.setImageBitmap(bitmap)
//                    paymntAdapter!!.notifyDataSetChanged()
                    //  }
                    /*  btn_next?.setAlpha(1F)
                      btn_next?.setEnabled(true)*/
                }
            }
        }
    }

    companion object {
        var qr_img = ""
    }

    override fun showProgress(show: Boolean) {
        if(show){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar_payment.visibility = View.VISIBLE
        }else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar_payment.visibility = View.GONE
        }
    }

}
