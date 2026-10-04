package pharmlane.com.PharmLaneStore.activities

import pharmlane.com.PharmLaneStore.utills.firstAddress
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import kotlinx.android.synthetic.main.activity_edit_store_details.*
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_area
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_city
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_est_since
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_geo_location
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_landmark
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_building
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_office_shop_no
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_store_name
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_street_road
import kotlinx.android.synthetic.main.activity_edit_store_details.edt_zip_code
import kotlinx.android.synthetic.main.activity_edit_store_details.img_back
import kotlinx.android.synthetic.main.activity_store_info.progressBar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class EditStoreDetailsActivity : AppCompatActivity(), View.OnClickListener, LocationListener {

    private var store_name = ""
    private var since = ""
    private var area = ""
    private var office_shop_no = ""
    private var street_road = ""
    private var landmark = ""
    private var building = ""
    private var city = ""
    private var zipcode = ""
    private var geo_location = ""
    private var latitude = ""
    private var longitude = ""
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    val PERMISSION_ID = 42
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark


        setContentView(R.layout.activity_edit_store_details)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this@EditStoreDetailsActivity, Locale.US)

        img_back.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        edt_geo_location.setOnClickListener(this)

        edt_store_name.setText(AppConstant.getPreferenceText(AppConstant.STORE_NAME))
        edt_est_since.setText(AppConstant.getPreferenceText(AppConstant.SINCE))
        edt_office_shop_no.setText(AppConstant.getPreferenceText(AppConstant.SHOP_NO))
        edt_street_road.setText(AppConstant.getPreferenceText(AppConstant.STREET_ROAD))
        edt_area.setText(AppConstant.getPreferenceText(AppConstant.AREA))
        edt_landmark.setText(AppConstant.getPreferenceText(AppConstant.LANDMARK))
        edt_building.setText(AppConstant.getPreferenceText(AppConstant.BUILDING))
        edt_city.setText(AppConstant.getPreferenceText(AppConstant.CITY))
        edt_zip_code.setText(AppConstant.getPreferenceText(AppConstant.ZIPCODE))
        edt_geo_location.setText(AppConstant.getPreferenceText(AppConstant.GEOLOCATION))


        // Initialize Places.
        Places.initialize(applicationContext, "AIzaSyBNaw-vL6x2-VDXDMEdiuvl2ZWxESHBq0U")


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save -> {

                store_name = edt_store_name.text.toString().trim()
                since = edt_est_since.text.toString().trim()
                office_shop_no = edt_office_shop_no.text.toString().trim()
                street_road = edt_street_road.text.toString().trim()
                area = edt_area.text.toString().trim()
                landmark = edt_landmark.text.toString().trim()
                building = edt_building.text.toString().trim()
                city = edt_city.text.toString().trim()
                zipcode = edt_zip_code.text.toString().trim()
                geo_location = edt_geo_location.text.toString().trim()

                if (store_name.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter store name", Toast.LENGTH_LONG).show()
                } else if (since.length < 4) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter since", Toast.LENGTH_LONG).show()
                } else if (office_shop_no.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter shop number", Toast.LENGTH_LONG).show()
                } else if (street_road.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter street", Toast.LENGTH_LONG).show()
                } else if (area.length < 1) {

                    Toast.makeText(this@EditStoreDetailsActivity, "please enter area", Toast.LENGTH_LONG).show()
                } else if (landmark.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter landmark", Toast.LENGTH_LONG).show()
                } else if (building.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter building", Toast.LENGTH_LONG).show()
                } else if (city.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter city", Toast.LENGTH_LONG).show()
                } else if (geo_location.length < 1) {
                    Toast.makeText(this@EditStoreDetailsActivity, "please enter geolocation", Toast.LENGTH_LONG).show()
                } else {


                    update_storeDetail(store_name, since, office_shop_no, street_road, area, landmark, building, city, zipcode, geo_location)

                }

            }
            R.id.edt_geo_location -> {
                getLastLocation()

                //Working lat-long with google place selection
//                val fields = listOf(Place.Field.ADDRESS, Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//                // Start the autocomplete intent.
//                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(this)
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

            }
            R.id.img_back -> {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        edt_geo_location.setText(place.address)

                        val queriedLocation: LatLng = place.latLng!!
                        latitude = queriedLocation.latitude.toString()
                        longitude = queriedLocation.longitude.toString()

                        Log.e("HelloBroad", "latitude   " + latitude + "  longitude   " + longitude)

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
        }
        super.onActivityResult(requestCode, resultCode, data)
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

                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()

                        Log.e("ehlodsd", "latitude   " + latitude + "   longitude   " + longitude)

                        val addressLine1 = geocoder!!.firstAddress(location.latitude, location.longitude)?.getAddressLine(0)

                        if (addressLine1 != null) edt_geo_location.setText(addressLine1)
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
        val mLocationRequest = LocationRequest()
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


            val addressLine1 = geocoder!!.firstAddress(mLastLocation.latitude, mLastLocation.longitude)?.getAddressLine(0)

            if (addressLine1 != null) edt_geo_location.setText(addressLine1)
            //  findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            // findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
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

    private fun update_storeDetail(store_name: String, since: String, office_shop_no: String, street_road: String, area: String, landmark: String, building: String, city: String, zipcode: String, geo_location: String) {
        progressBar.visibility = View.VISIBLE

        val register = Register()
        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        register.established_since = since
        register.area = area
        register.landmark = landmark
        register.building = building
        register.city = city
        register.zipcode = zipcode
        register.geo_location = geo_location
        register.shop_no = office_shop_no
        register.store_name = store_name
        register.street = street_road
        register.latitude = latitude
        register.longitude = longitude


        Log.e("HelloBroad", "register.store_latitude   " + register.latitude + "  register.store_longitude   " + register.longitude)


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

                    //startActivity(Intent(this@EditStoreDetailsActivity, StoreProfileActivity::class.java))
                    finish()

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@EditStoreDetailsActivity,
                                "" + jObjError.getString("message"),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@EditStoreDetailsActivity, e.message, Toast.LENGTH_LONG)
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

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude.toString()
        longitude = location.longitude.toString()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String) {
        TODO("Not yet implemented")
    }

}
