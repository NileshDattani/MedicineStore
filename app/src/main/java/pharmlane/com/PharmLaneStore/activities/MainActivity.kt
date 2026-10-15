package pharmlane.com.PharmLaneStore.activities

import android.os.Build
import androidx.activity.OnBackPressedCallback
import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.fragments.DashBoardFragment
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.loginactivities.LoginActivity
import pharmlane.com.PharmLaneStore.model.StoreDetail
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.AppConstant.MAINACTIVITY_LAT
import pharmlane.com.PharmLaneStore.utills.AppConstant.MAINACTIVITY_LONG
import pharmlane.com.PharmLaneStore.utills.Utils
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_rate_customer.*
import kotlinx.android.synthetic.main.activity_store_profile.*
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var doubleBackToExitOnce: Boolean = false
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var geocoder: Geocoder? = null
    val PERMISSION_ID = 42
//    private var current_latitude=""
//    private var current_longitude=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // From Android 16 the system no longer calls onBackPressed(); back goes through the dispatcher.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this@MainActivity, Locale.US)



        getLastLocation()
        if (checkPermissions() && needsNotificationPermission()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_ID + 1)
        }
        storeDetailApi()

        AppConstant.setPreferenceText(AppConstant.OFFER_ID, "")

        img_menu.setOnClickListener(this)
        closeDrawer.setOnClickListener(this)
        img_notification.setOnClickListener(this)
        txt_notfcation.setOnClickListener(this)
        txt_store_profile.setOnClickListener(this)
        txt_check_avail_stores.setOnClickListener(this)
        txt_my_orders.setOnClickListener(this)
        txt_rate_us.setOnClickListener(this)
        txt_refer_app_to_partner_store.setOnClickListener(this)
        txt_invit_custmr_to_app.setOnClickListener(this)
        txt_send_feedback.setOnClickListener(this)
        txt_contact_us.setOnClickListener(this)
        txt_terms_conditions.setOnClickListener(this)
        txt_store_presc.setOnClickListener(this)
        txt_buy_for_my_store.setOnClickListener(this)
        txt_seek_finance.setOnClickListener(this)
        txt_home.setOnClickListener(this)
        txt_logout.setOnClickListener(this)
        txt_demo.setOnClickListener(this)
        txt_privacy_policy.setOnClickListener(this)
        clickDashboard()

        Glide.with(applicationContext)  //2
                .load(AppConstant.getPreferenceText(AppConstant.STORE_PHOTO)) //3
                .placeholder(R.drawable.ic_launcher_background) //5
                .error(R.drawable.ic_launcher_background) //6
                .fallback(R.drawable.ic_launcher_background) //7
                .into(img_shop)

        txt_name.text = AppConstant.getPreferenceText(AppConstant.NAME)
        txt_phone.text = "+91 " + AppConstant.getPreferenceText(AppConstant.PHONE)


    }

    private fun clickDashboard() {

        val newFragment = DashBoardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_menu -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
            R.id.closeDrawer -> {
                drawerLayout!!.closeDrawer(GravityCompat.START)
            }
            R.id.img_notification -> {
                callNotificationScreen()
            }
            R.id.txt_notfcation -> {
                callNotificationScreen()
            }
            R.id.txt_store_profile -> {
                startActivity(Intent(this@MainActivity, StoreProfileActivity::class.java))
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_check_avail_stores -> {
                startActivity(Intent(this@MainActivity, AvailableStoreActivity::class.java))
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_my_orders -> {
                startActivity(Intent(this@MainActivity, MyOrdersActivity::class.java))
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_rate_us -> {
                drawerLayout!!.closeDrawer(GravityCompat.START)
                showPopup()
            }
            R.id.txt_refer_app_to_partner_store -> {
                drawerLayout!!.closeDrawer(GravityCompat.START)
                shareLink()
            }
            R.id.txt_invit_custmr_to_app -> {
                drawerLayout!!.closeDrawer(GravityCompat.START)
                shareLinkForStore()
            }
            R.id.txt_send_feedback -> {
                startActivity(
                        Intent(
                                this@MainActivity,
                                SendFeedbackActivity::class.java
                        )
                )
            }
            R.id.txt_demo -> {
                startActivity(
                    Intent(
                        this@MainActivity,
                        DemoActivity::class.java
                    )
                )
            }
            R.id.txt_contact_us -> {
                startActivity(
                        Intent(
                                this@MainActivity,
                                ContactUsActivity::class.java
                        )
                )
            }
            R.id.txt_terms_conditions -> {
                startActivity(
                        Intent(
                                this@MainActivity,
                                TermsConditionsActivity::class.java
                        )
                )
            }
            R.id.txt_privacy_policy -> {
                startActivity(
                    Intent(
                        this@MainActivity,
                        PrivacyPolicyActivity::class.java
                    )
                )
            }
            R.id.txt_store_presc -> {

                startActivity(
                        Intent(
                                this@MainActivity,
                                SubscriptionHistoryActivity::class.java
                        )
                )

//                startActivity(
//                        Intent(
//                                this@MainActivity,
//                                SubscriptionHistoryActivity::class.java
//                        )
//                )

            }
            R.id.txt_buy_for_my_store -> {

                Utils.startNewActivity(this@MainActivity, "pharmlane.com.PharmLane")


            }
            R.id.txt_seek_finance -> {
                startActivity(
                        Intent(
                                this@MainActivity,
                                SeekFinanceActivity::class.java
                        )
                )
            }
            R.id.txt_home -> {
                startActivity(
                        Intent(
                                this@MainActivity,
                                MainActivity::class.java
                        )
                )
            }
            R.id.txt_logout -> {
                logOut()
            }
        }
    }

    fun callNotificationScreen() {
        startActivity(
                Intent(
                        this@MainActivity,
                        NotificationsActivity::class.java
                )
        )
    }

    fun noDataAvail() {
        img_notification.visibility = View.GONE
        constraint.visibility = View.GONE
        constrain_nothing_here.visibility = View.VISIBLE
    }


    fun shareLink() {
        val share = Intent(android.content.Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Refer App to Partner Stor")
        share.putExtra(Intent.EXTRA_TEXT, "Download PharmLane Store app - https://play.google.com/store/apps/details?id=pharmlane.com.PharmLaneStore\n\nReceive orders on your mobile from all corners of your city.")
        startActivity(Intent.createChooser(share, "Refer App to Partner Store"))
    }

    fun shareLinkForStore() {
        val share = Intent(android.content.Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Invite Customer to App")
        share.putExtra(Intent.EXTRA_TEXT, "Download PharmLane app - https://play.google.com/store/apps/details?id=pharmlane.com.PharmLane\n\nOrder on your mobile from your favorite Pharmacy Store.")
        startActivity(Intent.createChooser(share, "Invite Customer to App"))
    }

    fun showPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_rate_app)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.NO_GRAVITY
        //  lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp
        val img_close = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        val btn = dialog.findViewById(R.id.btn) as AppCompatButton?
        val rBar = dialog.findViewById(R.id.rBar) as RatingBar?

        img_close!!.setOnClickListener {
            dialog.dismiss()
        }
        btn!!.setOnClickListener {

            dialog.dismiss()
            val msg = rBar!!.rating.toString()
            Toast.makeText(
                    this@MainActivity,
                    "Rating is: " + msg, Toast.LENGTH_SHORT
            ).show()

        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }

    fun logOut() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        //builder.setTitle("App background color")

        // Display a message on alert dialog
        builder.setMessage("Do You want to Logout from this app?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Yes") { dialog, which ->
            // Do something when user press the positive button
            clearPref()
            startActivity(
                    Intent(
                            this@MainActivity,
                            LoginActivity::class.java
                    )
            )
            finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Do something when user press the positive button
            dialog.dismiss()
        }

        // Display a negative button on alert dialog


        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

    }

    fun clearPref() {
        AppConstant.setPreferenceBoolean(AppConstant.IS_LOGIN, false)
        AppConstant.remove(AppConstant.STORE_ID)
    }

    private fun handleBack() {
        if (doubleBackToExitOnce) {
            // super.onBackPressed()
            moveTaskToBack(true)
        }

        this.doubleBackToExitOnce = true

        //displays a toast message when user clicks exit button
        //toast("please press again to exit ").show()

        Toast.makeText(this, "please press again to exit", Toast.LENGTH_SHORT).show()

        //displays the toast message for a while
        Handler().postDelayed({
            kotlin.run { doubleBackToExitOnce = false }
        }, 2000)

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
//                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
//                    var location: Location? = task.result
//                    if (location == null) {
//                        requestNewLocationData()
//                    } else {
//
////                        current_latitude = location.latitude.toString()
////                        current_longitude = location.longitude.toString()
//
//                        MAINACTIVITY_LAT = location.latitude.toString()
//                        MAINACTIVITY_LONG = location.longitude.toString()
//
//                    }
//                }


                mFusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->

                    MAINACTIVITY_LAT = location?.latitude.toString()
                    MAINACTIVITY_LONG = location?.longitude.toString()

                    // Got last known location. In some rare situations this can be null.
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

            MAINACTIVITY_LAT = mLastLocation.latitude.toString()
            MAINACTIVITY_LONG = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        val permissions = arrayListOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (needsNotificationPermission()) permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_ID)
    }

    // Android 13+ keeps order notifications off until the user allows them.
    private fun needsNotificationPermission(): Boolean =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }


    fun storeDetailApi() {

        val storeDetail = StoreDetail()
        storeDetail.phone = AppConstant.getPreferenceText(AppConstant.PHONE)

//        if(AppConstant.getPreferenceText(AppConstant.LOGINFROM).equals("Employee1",true)){
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.EMP1_MOBILE)
//        }else if (AppConstant.getPreferenceText(AppConstant.LOGINFROM).equals("Employee2",true)){
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.EMP2_MOBILE)
//        }else{
//            storeDetail.phone = AppConstant.getPreferenceText(AppConstant.PHONE)
//        }

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.storedetail(storeDetail)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                if (response.isSuccessful) {


                    AppConstant.setPreferenceText(AppConstant.STORELATITUDE, response.body()?.data?.latitude.toString())
                    AppConstant.setPreferenceText(AppConstant.STORELONGITUDE, response.body()?.data?.longitude.toString())

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())

                    } catch (e: Exception) {

                    }

                }
            }


            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                Log.e("HelloLogsss", "onFailure   ")

            }
        })
    }

}