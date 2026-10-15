package pharmlane.com.PharmLaneStore.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.loginactivities.*
import pharmlane.com.PharmLaneStore.model.StoreDetail
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_store_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class StoreDetailActivity : AppCompatActivity(), View.OnClickListener {

    var latitude = ""
    var longitude = ""
    var currentLatitude = ""
    var currentLongitude = ""
    val PERMISSION_ID = 42
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_store_detail)

        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        img_back.setOnClickListener(this)
        txt_store_add.setOnClickListener(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this@StoreDetailActivity, Locale.US)
        getLastLocation()

        storeDetailApi()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.img_back -> {
                finish()
            }
            R.id.txt_store_add -> {
                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?&daddr=" + latitude + "," + longitude))
                startActivity(intent)
            }
        }
    }

    fun storeDetailApi() {

        progressBar.visibility = View.VISIBLE
        val storeDetail = StoreDetail()

//        if(AppConstant.getPreferenceText(AppConstant.LOGINFROM).equals("Employee1",true)){
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.EMP1_MOBILE)
//        }else if (AppConstant.getPreferenceText(AppConstant.LOGINFROM).equals("Employee2",true)){
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.EMP2_MOBILE)
//        }else{
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.PHONE)
//        }

        storeDetail.phone = phoneNumber

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.storedetail(storeDetail)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {


                    val locaTion =
                            response.body()!!.data!!.shopNo + ", " + response.body()!!.data!!.building + ", " + response.body()!!.data!!.street + ", " + response.body()!!.data!!.area + ", " + response.body()!!.data!!.landmark + ", " + response.body()!!.data!!.city + ", " + response.body()!!.data!!.zipcode

                    txt_stor_nm.setText(response.body()!!.data!!.name)
                    txt_store_type.setText(response.body()!!.data!!.type)
                    txt_geo_location.setText(locaTion)
                    txt_ownr_name.setText(response.body()!!.data!!.ownerName)
                    edt_gst_no.setText(response.body()!!.data!!.gstNumber)
                    txt_drug_licens_no.setText(response.body()!!.data!!.drugLicenseNumber)
                    txt_establis_since.setText(response.body()!!.data!!.establishedSince)
                    txt_store_add.setText(response.body()!!.data!!.geoLocation)
                    txt_pharmacist_name.setText(response.body()!!.data?.registeredPharmacistName)
                    latitude = response.body()?.data?.latitude.toString()
                    longitude = response.body()?.data?.longitude.toString()

                    Log.e("HelloLogsss", "enqueue   " + latitude + "   Longi   " + longitude)

                    val input: String = response.body()?.data?.paymentMethod!!
                    val builder = StringBuilder()
                    val items = input.split("\\p{Punct}".toRegex()).toTypedArray()
                    for (details in items) {
                        builder.append(details + "\n")
                    }
                    txt_prefrd_paymnt.setText(builder.toString())

                    val category: String = response.body()?.data?.merchandiseCategory!!
                    val builderr = StringBuilder()
                    val merchandise = category.split("\\p{Punct}".toRegex()).toTypedArray()
                    for (details in merchandise) {
                        builderr.append(details + "\n")
                    }
                    txt_mrchandiz_category.setText(builderr.toString())

                    Glide.with(applicationContext)  //2
                            .load(response.body()!!.data!!.storePhoto) //3
                            .centerCrop() //4
                            .placeholder(R.drawable.ic_launcher_background) //5
                            .error(R.drawable.ic_launcher_background) //6
                            .fallback(R.drawable.ic_launcher_background) //7
                            .placeholder(R.drawable.ic_launcher_background) //5
                            .into(img_store) //8

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
                                this@StoreDetailActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@StoreDetailActivity, e.message, Toast.LENGTH_LONG)
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {


                        currentLatitude = location.getLatitude().toString()
                        currentLongitude = location.getLongitude().toString()

                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation ?: return


            currentLatitude = mLastLocation.getLatitude().toString()
            currentLongitude = mLastLocation.getLongitude().toString()

        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

}