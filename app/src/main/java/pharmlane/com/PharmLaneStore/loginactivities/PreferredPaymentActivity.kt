package pharmlane.com.PharmLaneStore.loginactivities

import pharmlane.com.PharmLaneStore.utills.IMAGE_PERMISSIONS
import android.Manifest
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.PaymntAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.loginactivities.PersonalInfoActivity.Companion.emponeMobile
import pharmlane.com.PharmLaneStore.loginactivities.PersonalInfoActivity.Companion.emponeName
import pharmlane.com.PharmLaneStore.loginactivities.PersonalInfoActivity.Companion.emptwoMobile
import pharmlane.com.PharmLaneStore.loginactivities.PersonalInfoActivity.Companion.emptwoName
import pharmlane.com.PharmLaneStore.loginactivities.PersonalInfoActivity.Companion.ownerMobile
import pharmlane.com.PharmLaneStore.loginactivities.PersonalInfoActivity.Companion.ownerName
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.area
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.building
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.city
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.drugLicno
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.estSince
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.geoLocation
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.gstNo
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.landMark
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.merchCatgry
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.paymentArray
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.pharmacist_name
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.shopNo
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.storeName
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.storeTypeId
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.store_latitude
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.store_longitude
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.streetRoad
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.zipCode
import pharmlane.com.PharmLaneStore.loginactivities.UploadDrugLicenseActivity.Companion.drugLicense
import pharmlane.com.PharmLaneStore.loginactivities.UploadGstCertiActivity.Companion.gst_certificate
import pharmlane.com.PharmLaneStore.loginactivities.UploadStorePhotoActivity.Companion.storePhoto
import pharmlane.com.PharmLaneStore.model.AddToken
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.CommonResponse
import pharmlane.com.PharmLaneStore.response.RegisterResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.InternalStorageContentProvider
import pharmlane.com.PharmLaneStore.utills.ScaleFile
import pharmlane.com.PharmLaneStore.utills.cropper.CropImage
import com.canhub.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_preferred_payment.*
import kotlinx.android.synthetic.main.activity_preferred_payment.progressBar
import kotlinx.android.synthetic.main.activity_verify.*
import org.json.JSONObject
import pharmlane.com.PharmLaneStore.BuildConfig
import pharmlane.com.PharmLaneStore.utills.Utils.Companion.createTempImageFile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class PreferredPaymentActivity : AppCompatActivity(), View.OnClickListener,
    PaymntAdapter.arrayPayment,PaymntAdapter.addQRCode {

    private var mFileTemp: Uri? = null
    private val SELECT_FILE = 1
    var bitmap: Bitmap? = null
    var path = ""
    private val REQUEST_CAMERA = 0
    val REQUEST_CODE_TAKE_PICTURE = 2
    val REQUEST_CODE_GALLERY = 1
    val INITIAL_GALLERY = IMAGE_PERMISSIONS

    private var img_qr:AppCompatImageView?=null

    private var paymntAdapter: PaymntAdapter? = null
    private var recycler: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //  window.statusBarColor = Color.TRANSPARENT
        }
        /* getWindow().setFlags(
             WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
             WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
         )*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }//  set status text dark

        setContentView(R.layout.activity_preferred_payment)

        val termsOfCondition = "<font color=#389bf1><u>Terms & Conditions.</u></font>"


        val result: Spanned =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    "By registering, you agree to the $termsOfCondition",
                    Html.FROM_HTML_MODE_LEGACY
                )
            } else {
                Html.fromHtml("By registering, you agree to the $termsOfCondition")
            }

        /* val text =
             Html.fromHtml("By Register, you agree with our $termsOfCondition$commma")*/

        txt_terms_condition.setText(result)

        recycler = findViewById(R.id.recyl_paymnt)

        arrayPayment = arrayListOf()

        img_back.setOnClickListener(this)
        btn_registr.setOnClickListener(this)
        txt_terms_condition.setOnClickListener(this)

        recycler!!.setHasFixedSize(true)
        recycler!!.layoutManager =
            LinearLayoutManager(this) as RecyclerView.LayoutManager?
        paymntAdapter = PaymntAdapter(this, paymentArray, this@PreferredPaymentActivity,this@PreferredPaymentActivity)
        recycler!!.adapter = paymntAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.btn_registr -> {
                /*if (arrayPayment.size == 0){

                }else{*/
                    signup()
                //}
            }
            R.id.txt_terms_condition -> {
                /* startActivity(Intent(this@PreferredPaymentActivity, ThankYouActivity::class.java))
                 // finish()
                 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
            }
        }
    }


    private fun signup() {

        progressBar.setVisibility(View.VISIBLE)
        val register = Register()
        register.established_since = estSince
        register.phone = ownerMobile
        register.area = area
        register.drug_license_image = drugLicense
        register.employee1_mobile = emponeMobile
        register.employee2_mobile = emptwoMobile
        register.landmark = landMark
        register.building = building
        register.city = city
        register.zipcode = zipCode
        register.employee1_name = emponeName
        register.employee2_name = emptwoName
        register.geo_location = geoLocation
        register.gst_certificate = gst_certificate
        register.gst_number = gstNo
        register.merchandise_category = merchCatgry
        register.owner_name = ownerName
        register.shop_no = shopNo
        register.store_name = storeName
        register.store_photo = storePhoto
        register.type = storeTypeId
        register.street = streetRoad
        register.store_latitude= store_latitude
        register.store_longitude= store_longitude
        register.latitude = store_latitude
        register.longitude = store_longitude
        register.drug_license_number = drugLicno
        register.registered_pharmacist_name=pharmacist_name
        register.payment_method_array = arrayPayment


        val apiService = AppConstant.getClient()!!.create(RetrofitInterface::class.java)
        val call = apiService.register(register)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {

                Log.d("TAG", "RESPONSE")
                progressBar.setVisibility(View.GONE)
                if (response.isSuccessful()) {
                    AppConstant.setPreferenceText(
                        AppConstant.STORE_ID,
                        response.body()?.data!!.storeId!!.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.PHONE,
                        response.body()?.data!!.phone!!
                    )

                    addTokenApi()


                    /*  AppConstant.setPreferenceText(
                          AppConstant.PREF_USER_PROFILE_IMAGE,
                          response.body()!!.data!!.profilePic!!
                      )
                      AppConstant.setPreferenceText(
                          AppConstant.PREF_USER_ID,
                          response.body()!!.data!!.userid!!
                      )

                      AppConstant.setPreferenceText(
                          AppConstant.PREF_USER_NAME,
                          response.body()!!.data!!.fullname!!
                      )
                      AppConstant.setPreferenceText(
                          AppConstant.PREF_USER_PHONE,
                          response.body()!!.data!!.phone!!
                      )


                      startActivity(Intent(this@Register2Activity, MainActivity::class.java))
                      finish()
                      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@PreferredPaymentActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PreferredPaymentActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                progressBar.setVisibility(View.GONE)

            }
        })
    }

    override fun add(array: ArrayList<Register.payment>) {
        arrayPayment= arrayListOf()

        arrayPayment = array


        Log.d("array","finalarray-->$arrayPayment")

    }


    override fun qr_codee(img: AppCompatImageView) {
        img_qr=img
        choice()
        selectFromGallery(img)
    }

    fun choice() {
        var fileTemporary: File? = null
        try {
            fileTemporary = createTempImageFile(this)
        } catch (exception: IOException) {
            Log.d(javaClass.simpleName + " openCamera", exception.toString())
        }
        mFileTemp = FileProvider.getUriForFile(this,
            BuildConfig.APPLICATION_ID.toString() + ".provider", fileTemporary!!)
    }

    private fun selectFromGallery(img: AppCompatImageView) {
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
                    qr_img = AppConstant.convert(bitmap!!)
                    AppConstant.setPreferenceText(AppConstant.QR_CODE, qr_img)


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
                    //  }
                    /*  btn_next?.setAlpha(1F)
                      btn_next?.setEnabled(true)*/
                }
            }
        }
    }

    companion object {
        // var storeName = ""
        var arrayPayment = ArrayList<Register.payment>()
        var qr_img = ""
    }


    fun addTokenApi() {

        progressBar.visibility = View.VISIBLE
        val addToken = AddToken()
        addToken.device_token_id = AppConstant.getPreferenceText(AppConstant.PREF_FCM_TOKEN)
        addToken.user_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        addToken.user_type = "1"
        addToken.device_type = "1"

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.addToken(addToken)

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                    call: Call<CommonResponse>,
                    response: retrofit2.Response<CommonResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    startActivity(Intent(this@PreferredPaymentActivity, ThankYouActivity::class.java))
                    // finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PreferredPaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: java.lang.Exception) {
                        Toast.makeText(this@PreferredPaymentActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                }
            }


            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }
}
