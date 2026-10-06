package pharmlane.com.PharmLaneStore.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.loginactivities.*
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.model.StoreDetail
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.AppConstant.MERCHANDISE
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_store_detail.*
import kotlinx.android.synthetic.main.activity_store_profile.*
import kotlinx.android.synthetic.main.activity_store_profile.img_back
import kotlinx.android.synthetic.main.activity_store_profile.txt_drug_licens_no
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class StoreProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var arrMList = ArrayList<String>()
    private var arrPayList = ArrayList<String>()
    private var pharmacist_name = ""

    private var arrPayListtt = ArrayList<StoreDetailResponse.Data.PaymentMethodArray>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // set status text dark

        setContentView(R.layout.activity_store_profile)

        edt_img_my_employee.setOnClickListener(this)
        img_back.setOnClickListener(this)
        img_edit_store_detail.setOnClickListener(this)
        img_edit_business_type.setOnClickListener(this)
        img_gst_no.setOnClickListener(this)
        img_drug_licns_no.setOnClickListener(this)
        img_edit_merch_categry.setOnClickListener(this)
        img_edit_payment_method.setOnClickListener(this)
        imageView4.setOnClickListener(this)
        img_edit_registered_name.setOnClickListener(this)

        if (!AppConstant.getPreferenceText(AppConstant.LOGINFROM)
                .equals(AppConstant.STORE_OWNER, true)
        ) {
            edt_img_my_employee.visibility = View.INVISIBLE
            img_edit_store_detail.visibility = View.INVISIBLE
            img_edit_business_type.visibility = View.INVISIBLE
            img_edit_registered_name.visibility = View.INVISIBLE
            imageView4.visibility = View.INVISIBLE
            edt_registered_pharmacist_name.isClickable = false
            edt_registered_pharmacist_name.isEnabled = false
            img_gst_no.isClickable = false
            img_drug_licns_no.isClickable = false
            img_edit_merch_categry.visibility = View.INVISIBLE
            img_edit_payment_method.visibility = View.INVISIBLE
        }

        storeDetailApi()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.img_edit_registered_name -> {
                pharmacist_name = edt_registered_pharmacist_name.text.toString().trim()

                if (pharmacist_name.length < 1) {
                    Toast.makeText(
                        this@StoreProfileActivity,
                        "Enter Registered Pharmacist Name",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    update_storeDetail()
                }
            }
            R.id.imageView4 -> {
                startActivity(
                    Intent(
                        this@StoreProfileActivity,
                        UpdateStorePhotoActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.edt_img_my_employee -> {
                startActivity(
                    Intent(this@StoreProfileActivity, EditEmployeesActivity::class.java)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_edit_store_detail -> {

                startActivity(
                    Intent(this@StoreProfileActivity, EditStoreDetailsActivity::class.java)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_edit_business_type -> {

                startActivity(
                    Intent(this@StoreProfileActivity, EditBusinessTypeActivity::class.java)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_gst_no -> {
                startActivity(
                    Intent(this@StoreProfileActivity, GstNumberActivity::class.java)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_drug_licns_no -> {
                startActivity(
                    Intent(this@StoreProfileActivity, DrugLicenseNumberActivity::class.java)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_edit_merch_categry -> {

                AppConstant.setPreferenceText(MERCHANDISE, "edt")
                arrMList = arrayListOf()
                startActivityForResult(
                    Intent(
                        this@StoreProfileActivity,
                        SelectMerchActivity::class.java
                    ).putExtra("selectedCat", txt_merch_cat.text.toString()), 101
                )
            }
            R.id.img_edit_payment_method -> {

                AppConstant.setPreferenceText(AppConstant.QR_CODE, "")

                arrPayList = arrayListOf()
                startActivityForResult(
                    Intent(
                        this@StoreProfileActivity,
                        EditPaymentMethodActivity::class.java
                    ), 103
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                arrMList.addAll(data!!.getStringArrayListExtra("list")!!)
                txt_merch_cat.text = Arrays.toString(arrMList.toArray()).replace('[', ' ').replace(
                    ']',
                    ' '
                ).trim()
            }
        }
        if (requestCode == 103) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                arrPayList.addAll(data!!.getStringArrayListExtra("list")!!)
                txt_paymnt_method.text = Arrays.toString(arrPayList.toArray()).replace('[', ' ').replace(
                    ']',
                    ' '
                ).trim()
            }
        }
    }

    fun storeDetailApi() {

        Progressbar.visibility = View.VISIBLE
        val storeDetail = StoreDetail()

//        if(AppConstant.getPreferenceText(AppConstant.LOGINFROM).equals("Employee1",true)){
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.EMP1_MOBILE)
//        }else if (AppConstant.getPreferenceText(AppConstant.LOGINFROM).equals("Employee2",true)){
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.EMP2_MOBILE)
//        }else{
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.PHONE)
//        }

        storeDetail.phone = AppConstant.getPreferenceText(AppConstant.PHONE)


        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.storedetail(storeDetail)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                call: Call<StoreDetailResponse>,
                response: retrofit2.Response<StoreDetailResponse>
            ) {
                Progressbar.visibility = View.GONE
                if (response.isSuccessful) {


                    val locaTion =
                        response.body()!!.data!!.shopNo + ", " + response.body()!!.data!!.building + ", " + response.body()!!.data!!.street + ", " + response.body()!!.data!!.area + ", " + response.body()!!.data!!.landmark + ", " + response.body()!!.data!!.city + ", " + response.body()!!.data!!.zipcode
                    txt_store_owner_name.text = response.body()?.data?.ownerName
                    AppConstant.setPreferenceText(
                        AppConstant.OWNER_NAME,
                        response.body()?.data?.ownerName.toString()
                    )
                    txt_stor_no.text = response.body()?.data?.phone
                    AppConstant.setPreferenceText(
                        AppConstant.OWNER_PHONE,
                        response.body()?.data?.phone.toString()
                    )
                    textView105.text = response.body()?.data?.employee1Name
                    AppConstant.setPreferenceText(
                        AppConstant.EMP1_NAME,
                        response.body()?.data?.employee1Name.toString()
                    )
                    textView106.text = response.body()?.data?.employee1Mobile
                    AppConstant.setPreferenceText(
                        AppConstant.EMP1_MOBILE,
                        response.body()?.data?.employee1Mobile.toString()
                    )
                    textView107.text = response.body()?.data?.employee2Name
                    AppConstant.setPreferenceText(
                        AppConstant.EMP2_NAME,
                        response.body()?.data?.employee2Name.toString()
                    )
                    textView108.text = response.body()?.data?.employee2Mobile
                    AppConstant.setPreferenceText(
                        AppConstant.EMP2_MOBILE,
                        response.body()?.data?.employee2Mobile.toString()
                    )
                    txt_store_name.text = response.body()?.data?.name
                    AppConstant.setPreferenceText(
                        AppConstant.STORE_NAME,
                        response.body()?.data?.name.toString()
                    )
                    txt_since.text = "Est. Since: " + response.body()?.data?.establishedSince
                    AppConstant.setPreferenceText(
                        AppConstant.SINCE,
                        response.body()?.data?.establishedSince.toString()
                    )
                    AppConstant.setPreferenceText(AppConstant.LOCATION, locaTion)
                    txt_store_address.text = locaTion
                    AppConstant.setPreferenceText(
                        AppConstant.SHOP_NO,
                        response.body()?.data?.shopNo.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.STREET_ROAD,
                        response.body()?.data?.street.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.AREA,
                        response.body()?.data?.area.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.BUILDING,
                        response.body()?.data?.building.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.LANDMARK,
                        response.body()?.data?.landmark.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.CITY,
                        response.body()?.data?.city.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.ZIPCODE,
                        response.body()?.data?.zipcode.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.GEOLOCATION,
                        response.body()?.data?.geoLocation.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.STORELATITUDE,
                        response.body()?.data?.latitude.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.STORELONGITUDE,
                        response.body()?.data?.longitude.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.BUSINESS_TYPE,
                        response.body()?.data?.type.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.GSET_NUMBER,
                        response.body()?.data?.gstNumber.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.DRUG_LICENCE,
                        response.body()?.data?.drugLicenseNumber.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.MERCHANDISE_CATEGORY,
                        response.body()?.data?.merchandiseCategory.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.PAYMENT_METHOD,
                        response.body()?.data?.paymentMethod.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.STORE_PHOTO,
                        response.body()?.data?.storePhoto.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.GST_CERTIFICATE,
                        response.body()?.data?.gstCertificate.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.GST_STATUS,
                        response.body()?.data?.gstVerifyStatus.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.DRUG_LICENCE_IMG,
                        response.body()?.data?.drugLicenseImage.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.DRUG_LICENCE_STATUS,
                        response.body()?.data?.dlCerifyStatus.toString()
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.REGISTERED_PHARMACIST_NAME,
                        response.body()?.data?.registeredPharmacistName.toString()
                    )

                    if (response.body()?.data?.dlCerifyStatus.equals("1")) {
                        txt_drug_verified.visibility = View.VISIBLE
                        txt_drug_unverified.visibility = View.GONE
                    } else {
                        txt_drug_verified.visibility = View.GONE
                        txt_drug_unverified.visibility = View.VISIBLE
                    }

                    if (response.body()?.data?.gstVerifyStatus.equals("1")) {
                        txt_verified.visibility = View.VISIBLE
                        txt_unverified.visibility = View.GONE
                    } else {
                        txt_verified.visibility = View.GONE
                        txt_unverified.visibility = View.VISIBLE
                    }


                    txt_business_type.text = response.body()?.data?.type
                    textView110.text = response.body()?.data?.gstNumber
                    txt_drug_licens_no.text = response.body()?.data?.drugLicenseNumber
                    txt_merch_cat.text = (response.body()?.data?.merchandiseCategory)
                    txt_paymnt_method.text = response.body()?.data?.paymentMethod
                    textView112.text = response.body()?.data?.type
                    edt_registered_pharmacist_name.setText(response.body()?.data?.registeredPharmacistName.toString())

                    Glide.with(applicationContext)  //2
                        .load(response.body()?.data?.gstCertificate) //3
                        .centerCrop() //4
                        .placeholder(R.drawable.ic_launcher_background) //5
                        .error(R.drawable.ic_launcher_background) //6
                        .fallback(R.drawable.ic_launcher_background) //7
                        .placeholder(R.drawable.ic_launcher_background) //5
                        .into(img_gst_no) //8

                    Glide.with(applicationContext)  //2
                        .load(response.body()?.data?.drugLicenseImage) //3
                        .centerCrop() //4
                        .placeholder(R.drawable.ic_launcher_background) //5
                        .error(R.drawable.ic_launcher_background) //6
                        .fallback(R.drawable.ic_launcher_background) //7
                        .placeholder(R.drawable.ic_launcher_background) //5
                        .into(img_drug_licns_no) //8


                    Glide.with(applicationContext)  //2
                        .load(response.body()?.data?.storePhoto) //3
                        .centerCrop() //4
                        .placeholder(R.drawable.ic_launcher_background) //5
                        .error(R.drawable.ic_launcher_background) //6
                        .fallback(R.drawable.ic_launcher_background) //7
                        .placeholder(R.drawable.ic_launcher_background) //5
                        .into(imageView3) //8


                    if (!AppConstant.getPreferenceText(AppConstant.LOGINFROM)
                            .equals("Store_owner", true)
                    ) {
                        edt_img_my_employee.visibility = View.INVISIBLE
                        img_edit_store_detail.visibility = View.INVISIBLE
                        img_edit_business_type.visibility = View.INVISIBLE
                        img_edit_registered_name.visibility = View.INVISIBLE
                        imageView4.visibility = View.INVISIBLE

                        edt_registered_pharmacist_name.isClickable = false
                        edt_registered_pharmacist_name.isEnabled = false

                        img_gst_no.isClickable = false
                        img_drug_licns_no.isClickable = false
                        img_edit_merch_categry.visibility = View.INVISIBLE
                        img_edit_payment_method.visibility = View.INVISIBLE
                    }

                    /*
                                       startActivity(
                                           Intent(this@LoginActivity, VerifyActivity::class.java).putExtra(
                                               CONSTANT_NUMBER,
                                               number
                                           )
                                       )
                                       finish()
                                       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@StoreProfileActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@StoreProfileActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                Progressbar.visibility = View.GONE
            }
        })
    }

    override fun onPostResume() {
        super.onPostResume()

        storeDetailApi()
    }

    private fun update_storeDetail() {
        Progressbar.visibility = View.VISIBLE

        val register = Register()

        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        register.registered_pharmacist_name = pharmacist_name

        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.update_store_detail(register)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                call: Call<StoreDetailResponse>,
                response: retrofit2.Response<StoreDetailResponse>
            ) {
                Progressbar.visibility = View.GONE
                if (response.isSuccessful) {

                    storeDetailApi()

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@StoreProfileActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@StoreProfileActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                Progressbar.visibility = View.GONE
            }
        })
    }
}
