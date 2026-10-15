package pharmlane.com.PharmLaneStore.loginactivities

import pharmlane.com.PharmLaneStore.utills.firstAddress
import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.GetAllDropDown
import pharmlane.com.PharmLaneStore.response.GetAllDropdownResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import kotlinx.android.synthetic.main.activity_edit_store_details.*
import kotlinx.android.synthetic.main.activity_store_info.*
import kotlinx.android.synthetic.main.activity_store_info.edt_area
import kotlinx.android.synthetic.main.activity_store_info.edt_building
import kotlinx.android.synthetic.main.activity_store_info.edt_city
import kotlinx.android.synthetic.main.activity_store_info.edt_est_since
import kotlinx.android.synthetic.main.activity_store_info.edt_geo_location
import kotlinx.android.synthetic.main.activity_store_info.edt_landmark
import kotlinx.android.synthetic.main.activity_store_info.edt_office_shop_no
import kotlinx.android.synthetic.main.activity_store_info.edt_store_name
import kotlinx.android.synthetic.main.activity_store_info.edt_street_road
import kotlinx.android.synthetic.main.activity_store_info.edt_zip_code
import kotlinx.android.synthetic.main.activity_store_info.img_back
import kotlinx.android.synthetic.main.activity_store_info.progressBar
import kotlinx.android.synthetic.main.activity_store_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class StoreInfoActivity : AppCompatActivity(), View.OnClickListener, LocationListener {

    private var arrMList = ArrayList<String>()
    private var arrMIDList = ArrayList<String>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    val PERMISSION_ID = 42
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val MERCI_CATEGORY = 101
    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*  getWindow().setFlags(
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
          )*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //  window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }//  set status text dark
        setContentView(R.layout.activity_store_info)

        edt_merch_categry.isFocusable = false
        edt_merch_categry.isClickable = true

        edt_geo_location.isFocusable = false
        edt_geo_location.isClickable = true

        img_back.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        edt_merch_categry.setOnClickListener(this)
        edt_geo_location.setOnClickListener(this)
        // Initialize Places.
        Places.initialize(applicationContext, "AIzaSyBNaw-vL6x2-VDXDMEdiuvl2ZWxESHBq0U")
        // requestPermission()


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this@StoreInfoActivity, Locale.US)

        getdropdownApi()
        /*radiogroup.setOnCheckedChangeListener { group, checkedId ->
            //group.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.textview_selector))
            var text = "You selected: "
            text += if (R.id.radio_retailer == checkedId) resources.getString(R.string.retailer) else resources.getString(
                R.string.wholeseller
            )

            txt_type =
                if (R.id.radio_retailer == checkedId) resources.getString(R.string.retailer) else resources.getString(
                    R.string.wholeseller
                )
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
        }*/
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                startActivity(Intent(this@StoreInfoActivity, PersonalInfoActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_next -> {

                storeName = edt_store_name.text.toString().trim()
                gstNo = edt_gst_no.text.toString().trim()
                drugLicno = edt_drug_licnc_no.text.toString().trim()
                val merchCatgrye = edt_merch_categry.text.toString().trim()
                streetRoad = edt_street_road.text.toString().trim()
                area = edt_area.text.toString().trim()
                city = edt_city.text.toString().trim()
                zipCode = edt_zip_code.text.toString().trim()
                geoLocation = edt_geo_location.text.toString().trim()
                estSince = edt_est_since.text.toString().trim()
                shopNo = edt_office_shop_no.text.toString().trim()
                landMark = edt_landmark.text.toString().trim()
                building = edt_building.text.toString().trim()

                pharmacist_name = edt_pharmacist_name.text.toString().trim()
                if (storeName.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_store_name), Toast.LENGTH_SHORT)
                            .show()
                } else if (txt_type.equals("")) {
                    Toast.makeText(this, getString(R.string.select_type), Toast.LENGTH_SHORT).show()
                } else if (gstNo.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_gst_no), Toast.LENGTH_SHORT)
                            .show()
                } else if (drugLicno.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_drug_lic_no), Toast.LENGTH_SHORT)
                            .show()
                } else if (pharmacist_name.equals("")) {
                    Toast.makeText(this, "Enter Pharmacist Name", Toast.LENGTH_SHORT)
                            .show()
                } else if (merchCatgrye.equals("")) {
                    Toast.makeText(
                            this,
                            getString(R.string.enter_merch_categry),
                            Toast.LENGTH_SHORT
                    ).show()
                } else if (streetRoad.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_street_road), Toast.LENGTH_SHORT)
                            .show()
                } else if (area.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_area), Toast.LENGTH_SHORT).show()
                } else if (city.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_city), Toast.LENGTH_SHORT).show()
                } else if (zipCode.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_zip_code), Toast.LENGTH_SHORT)
                            .show()
                } else if (geoLocation.equals("")) {
                    Toast.makeText(this, getString(R.string.enter_geo_location), Toast.LENGTH_SHORT)
                            .show()
                } else {
                    startActivity(
                            Intent(
                                    this@StoreInfoActivity,
                                    UploadGstCertiActivity::class.java
                            )
                    )
                    // finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                //  }
            }
            R.id.edt_merch_categry -> {
                AppConstant.setPreferenceText(AppConstant.MERCHANDISE, "1")
                arrMList = arrayListOf()
                startActivityForResult(
                        Intent(
                                this@StoreInfoActivity,
                                SelectMerchActivity::class.java
                        ).putExtra("selectedCat", ""), MERCI_CATEGORY
                )
                // finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.edt_geo_location -> {

//                val fields = listOf(Place.Field.ADDRESS, Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//                // Start the autocomplete intent.
//                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(this)
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)


                getLastLocation()
            }
        }
    }


    /*  override fun onRequestPermissionsResult(
          requestCode: Int,
          permissions: Array<out String>,
          grantResults: IntArray
      ) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)


          if (requestCode == 100) {
              if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  setLocation()
              }
          }
      }*/

    private fun setLocation() {

        if (ActivityCompat.checkSelfPermission(
                        this@StoreInfoActivity,
                        ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        statusCheck()
        mFusedLocationClient!!.lastLocation
                .addOnSuccessListener(this@StoreInfoActivity) {
                    if (it != null) {
                        progressBar.visibility = View.GONE
                        val latitude = it.latitude
                        val longitude = it.longitude

                        try {

                            val addressLine1 = geocoder!!.firstAddress(latitude, longitude)?.getAddressLine(0)
                            if (addressLine1 != null) edt_geo_location.text = addressLine1
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e("MainActivity", e.toString())
                        }

                    }

                }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.e("OnEDCLICK", "getLastLocation")

        if (checkPermissions()) {

            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return
                }
                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        store_latitude = location.latitude.toString()
                        store_longitude = location.longitude.toString()

                        Log.e("ehlodsd", "latitude   " + store_latitude + "   longitude   " + store_longitude)

                        val addressLine1 = geocoder!!.firstAddress(location.latitude, location.longitude)?.getAddressLine(0)

                        if (addressLine1 != null) edt_geo_location.text = addressLine1
                        //  findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        //   findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

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

            store_latitude = mLastLocation.latitude.toString()
            store_longitude = mLastLocation.longitude.toString()
            val addressLine1 = geocoder!!.firstAddress(mLastLocation.latitude, mLastLocation.longitude)?.getAddressLine(0)

            if (addressLine1 != null) edt_geo_location.text = addressLine1

        }
    }

    private fun isLocationEnabled(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun getdropdownApi() {
        progressBar.visibility = View.VISIBLE
        val apiService = AppConstant.getClient()!!.create(RetrofitInterface::class.java)
        val getAllDropDown = GetAllDropDown()
        getAllDropDown.order_type = ""
        val call = apiService.getalldropdown(getAllDropDown)

        call.enqueue(object : Callback<GetAllDropdownResponse> {
            override fun onResponse(
                    call: Call<GetAllDropdownResponse>,
                    response: Response<GetAllDropdownResponse>
            ) {

                progressBar.visibility = View.GONE
                if (response.isSuccessful) {


                    merchCategoryArray = arrayListOf()
                    storeTypeArray = arrayListOf()
                    paymentArray = arrayListOf()

                    merchCategoryArray = response.body()!!.data!!.merchandiseCategory!!
                    storeTypeArray = response.body()!!.data!!.storeType!!
                    paymentArray = response.body()!!.data!!.paymentMethods!!


                    radiogroup.removeAllViews()
                    // int count = mGroup.getChildCount();
                    for (i in 0 until response.body()!!.data!!.storeType!!.size) {
                        /* if (mGroup.getChildAt(i) instanceof RadioButton)
                            ((RadioButton) mGroup.getChildAt(i)).setText(response.body().getData().getComesWith().get(i).getDetail());*/
                        val rbn = RadioButton(this@StoreInfoActivity)
                        rbn.id = response.body()!!.data!!.storeType!!.get(i).id!!
                        rbn.text = response.body()!!.data!!.storeType!!.get(i).name

                        if (txt_type.equals(
                                        response.body()!!.data!!.storeType!!.get(i).name,
                                        ignoreCase = true
                                )
                        ) {
                            storeTypeId =
                                    response.body()!!.data!!.storeType!!.get(i).id!!.toString()
                            rbn.isChecked = true
                        }

                        radiogroup.addView(rbn)
                    }

                    radiogroup.setOnCheckedChangeListener { group, checkedId ->
                        if (storeTypeId.equals(checkedId)) {
                            radiogroup.check(storeTypeId.toInt())
                        } else {
                            val radioButton = findViewById<RadioButton>(checkedId)
                            txt_type = radioButton.text.toString()
                        }
                        for (i in 0 until response.body()!!.data!!.storeType!!.size) {
                            if (txt_type.equals(
                                            response.body()!!.data!!.storeType!!.get(i).name,
                                            ignoreCase = true
                                    )
                            ) {
                                storeTypeId =
                                        response.body()!!.data!!.storeType!!.get(i).id!!.toString()
                            }
                        }

                    }

                }
            }

            override fun onFailure(call: Call<GetAllDropdownResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })
    }

    fun statusCheck() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled: Boolean = false
        var network_enabled: Boolean = false


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            buildAlertMessageNoGps()
        }

    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.gps_disable_txt)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        DialogInterface.OnClickListener { dialog, id ->
                            startActivity(
                                    Intent(
                                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                    )
                            )
                        })
                .setNegativeButton(R.string.no,
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)

                        val queriedLocation: LatLng = place.latLng!!
                        store_latitude = queriedLocation.latitude.toString()
                        store_longitude = queriedLocation.longitude.toString()
                        edt_geo_location.text = place.address
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                    }
                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        } else if (requestCode == MERCI_CATEGORY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                arrMList.addAll(data!!.getStringArrayListExtra("list")!!)
                arrMIDList.addAll(data.getStringArrayListExtra("list_1")!!)
                merchCatgry = Arrays.toString(arrMIDList.toArray()).replace('[', ' ').replace(
                        ']',
                        ' '
                )
                edt_merch_categry.setText(
                        Arrays.toString(arrMList.toArray()).replace('[', ' ').replace(
                                ']',
                                ' '
                        ).trim()
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), 100)
    }

    companion object {

        var storeName = ""
        var gstNo = ""
        var drugLicno = ""
        var merchCatgry = ""
        var streetRoad = ""
        var area = ""
        var city = ""
        var zipCode = ""
        var geoLocation = ""
        var txt_type = ""
        var storeTypeId = ""
        var estSince = ""
        var shopNo = ""
        var landMark = ""
        var building = ""
        var pharmacist_name = ""
        var store_latitude = ""
        var store_longitude = ""


        var merchCategoryArray: List<GetAllDropdownResponse.MerchandiseCategory> = arrayListOf()
        var paymentArray: List<GetAllDropdownResponse.PaymentMethods> = arrayListOf()
        var storeTypeArray: List<GetAllDropdownResponse.StoreType> = arrayListOf()
    }

    override fun onLocationChanged(location: Location) {
        store_latitude = location!!.latitude.toString()
        store_longitude = location.longitude.toString()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
}