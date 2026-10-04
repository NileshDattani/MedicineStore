package pharmlane.com.PharmLaneStore.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.ProductsOrederedAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderDetail
import pharmlane.com.PharmLaneStore.response.OrderDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.Utils.Companion.GettingMiliSeconds
import kotlinx.android.synthetic.main.activity_custom_order_detail.*
import kotlinx.android.synthetic.main.activity_custom_order_detail.btn_make_offer
import kotlinx.android.synthetic.main.activity_custom_order_detail.btn_offer
import kotlinx.android.synthetic.main.activity_custom_order_detail.btn_offer_1
import kotlinx.android.synthetic.main.activity_custom_order_detail.con_bottom
import kotlinx.android.synthetic.main.activity_custom_order_detail.constrain_customer
import kotlinx.android.synthetic.main.activity_custom_order_detail.constrain_delivry_time_out
import kotlinx.android.synthetic.main.activity_custom_order_detail.constraint_top
import kotlinx.android.synthetic.main.activity_custom_order_detail.img_back
import kotlinx.android.synthetic.main.activity_custom_order_detail.img_menu
import kotlinx.android.synthetic.main.activity_custom_order_detail.progressBar

import kotlinx.android.synthetic.main.activity_custom_order_detail.textView51
import kotlinx.android.synthetic.main.activity_custom_order_detail.textView52
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_buy_pref
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_cusmrname

import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_date
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_del_date
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_del_pref_add
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_del_pref_type
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_del_time
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_geo_location
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_order_dtntime
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_order_for
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_order_id
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_status_offr
import kotlinx.android.synthetic.main.activity_order_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CustomOrderDetailActivity : AppCompatActivity(), View.OnClickListener {
    var orderId = ""
    var offerId = ""
    var isViewall = "0"
    var moreHour = "0"
    var timer = ""
    var direct = ""
    var isCustom = "Custom"
    var latitude = ""
    var longitude = ""
    var cTimer: CountDownTimer? = null

    lateinit var  img_add_photo:AppCompatImageView

    private var productsOrederedAdapter: ProductsOrederedAdapter? = null
    private var recycler: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_custom_order_detail)

        img_add_photo = findViewById(R.id.img_add_photo)

        if (intent != null) {

            if (intent.getStringExtra("orderId") != null) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }
            if (intent.getStringExtra("direct") != null && intent.getStringExtra("direct").equals("1")) {
                direct = "1"
            }

            if (intent.getStringExtra("moreHour") != null) {
                moreHour = intent.getStringExtra("moreHour") ?: ""

                if (moreHour.equals("1")) {
                    constraint_top.visibility = View.GONE
                }
            }

            if (intent.getStringExtra("offerId") != null) {
                offerId = intent.getStringExtra("offerId") ?: ""
            }
            if (intent.getStringExtra("timer") != null) {
                timer = intent.getStringExtra("timer") ?: ""
            } else {
                try {
                    if(customOrderdata?.createdDate!! != null){
                        timer = customOrderdata?.createdDate!!
                    }
                }catch (e:Exception){
                    timer = ""
                }

            }

            if (intent.getStringExtra("reviewfinaloffer") != null) {
                reviewFinalLayout()
            }
            if (intent.getStringExtra("reviewoffer") != null) {
                viewOffer()
            }
            if (intent.getStringExtra("vieworder") != null) {
                constraint_top.visibility = View.GONE
                con_bottom.visibility = View.GONE
            }
        }

        recycler = findViewById(R.id.recycl_products_ordered)

        txt_view_all_produts.setOnClickListener(this)
        img_add_photo.setOnClickListener(this)
        img_back.setOnClickListener(this)
        img_menu.setOnClickListener(this)
        btn_offer.setOnClickListener(this)
        btn_offer_1.setOnClickListener(this)
        btn_make_offer.setOnClickListener(this)
        txt_status_offr.setOnClickListener(this)
        txt_geo_location.setOnClickListener(this)

        recycler!!.setHasFixedSize(true)
        recycler!!.layoutManager =
                LinearLayoutManager(this) as RecyclerView.LayoutManager?

        orderDetailApi()
    }

    fun makeOffer() {
        btn_make_offer.visibility = View.VISIBLE
    }

    fun viewOffer() {
        btn_make_offer.visibility = View.GONE
        constraint_top.visibility = View.GONE

        txt_status_offr.visibility = View.VISIBLE
        btn_offer.visibility = View.VISIBLE
    }

    fun reviewFinalLayout() {
        btn_make_offer.visibility = View.GONE

        txt_status_offr.visibility = View.VISIBLE
        btn_offer.visibility = View.VISIBLE

        txt_status_offr.setText("5th in Q")
        txt_status_offr.setTextColor(ContextCompat.getColor(this, R.color.grey_btn))

        btn_offer.setText(resources.getString(R.string.review_offer))
    }

    fun ForDeliveryTimeOut() {
        btn_make_offer.visibility = View.GONE
        constraint_top.visibility = View.GONE

        txt_status_offr.visibility = View.GONE
        btn_offer.visibility = View.GONE

        constrain_delivry_time_out.visibility = View.VISIBLE
        btn_offer_1.visibility = View.VISIBLE
    }

    fun ForDeliveryTimeOutOnlyTxt() {
        btn_make_offer.visibility = View.GONE
        constraint_top.visibility = View.GONE

        txt_status_offr.visibility = View.GONE
        btn_offer.visibility = View.GONE

        constrain_delivry_time_out.visibility = View.VISIBLE
        btn_offer_1.visibility = View.GONE
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }

            R.id.img_add_photo -> {

                Log.d("TAG","Click Custom")

                startActivity(Intent(this@CustomOrderDetailActivity, ViewPhotoIdActivity::class.java)
                        .putExtra("photo_id",customOrderdata?.photoId))

            }
            R.id.txt_geo_location -> {
                Log.e("HelloLoc", "hiiiCustom clcick   latitude   " + latitude + "   longitude   " + longitude)


                val currentlat =  AppConstant.getPreferenceText(AppConstant.STORELATITUDE)
                val currentlong = AppConstant.getPreferenceText(AppConstant.STORELONGITUDE)

                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentlat + "," + currentlong + "&daddr=" + latitude + "," + longitude))
                startActivity(intent)
            }
            R.id.btn_offer -> {

                if (intent.getStringExtra("reviewfinaloffer") != null) {
                    startActivity(
                            Intent(this@CustomOrderDetailActivity, CustomViewOfferActivity::class.java)
                                    .putExtra("reviewoffer", "1")
                                    .putExtra("custom", isCustom)
                    )
                    //  finish()
                } else {
                    startActivity(
                            Intent(
                                    this@CustomOrderDetailActivity,
                                    CustomViewOfferActivity::class.java
                            ).putExtra("custom", isCustom)
                    )
                    //  finish()
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_offer_1 -> {

                startActivity(Intent(this@CustomOrderDetailActivity, CustomViewOfferActivity::class.java)
                        .putExtra("custom", isCustom))
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_view_all_produts -> {
                startActivity(
                        Intent(
                                this@CustomOrderDetailActivity,
                                ViewAllProductsActivity::class.java
                        ).putExtra("statusviewall", direct)
                )
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_make_offer -> {

                Log.e("HelloReOrde", "direct   " + direct + "   orderId   " + orderId)
                startActivity(
                        Intent(
                                this@CustomOrderDetailActivity,
                                CustomMakeOfferActivity::class.java
                        )
                                .putExtra("direct", direct)
                                .putExtra("orderId", orderId)
                )
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }


    fun orderDetailApi() {


        Log.e("HelloReOrde", "orderDetailApi   ")

        progressBar!!.visibility = View.VISIBLE
        val orderDetail = OrderDetail()
        orderDetail.order_id = orderId
        orderDetail.offer_id = AppConstant.getPreferenceText(AppConstant.OFFER_ID)
        orderDetail.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.orderdetail(orderDetail)

        call.enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(
                    call: Call<OrderDetailResponse>,
                    response: retrofit2.Response<OrderDetailResponse>
            ) {
                if (response.isSuccessful) {
                    progressBar!!.visibility = View.GONE

                    customOrderdata = response.body()!!.data

                    txt_order_id.text = "ID: " + customOrderdata!!.orderNo
                    txt_order_dtntime.text = (customOrderdata?.createdDate!!)
                    //  AppConstant.convertdatentimetoOrderTime(customOrderdata!!.createdDate!!).lowercase()

                    /* if (customOrderdata!!.products!!.size>3){
                         txt_view_all_produts.visibility = View.VISIBLE
                     }*/

                    productsOrederedAdapter = ProductsOrederedAdapter(
                            this@CustomOrderDetailActivity,
                            customOrderdata!!.products,
                            isViewall
                    )
                    recycler!!.adapter = productsOrederedAdapter

                    if (customOrderdata!!.requestStatus.equals(">24 hrs", ignoreCase = true)) {
                        constraint_top.visibility = View.GONE
                    } else {

                        if (!customOrderdata!!.requestStatus.equals("Timed out", ignoreCase = true)) {
                            val date = Date()
                            //This method returns the time in millis
                            val timeMilli = date.time
                            val wastedTime: Long = GettingMiliSeconds(response.body()?.data!!.toDeliveryDate!! + " " + response.body()?.data!!.endTime!!)!! - timeMilli
                            if (cTimer != null) {
                                cTimer!!.cancel();
                                cTimer = null;
                            }
                            try {
                                cTimer = object : CountDownTimer(wastedTime, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {

                                        val day1 = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                                        val hh1 = (TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                                        val mm1 = (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))
                                        val ss = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

                                        timer = ("" + hh1 + "h : " + mm1 + "m : " + ss + "s")
                                        txt_date.text = timer
                                    }

                                    override fun onFinish() {
                                        cancelTimer()
                                        orderDetailApi()
                                    }
                                }
                                (cTimer as CountDownTimer).start()
                            }catch (e:java.lang.Exception){
                                Log.e("HelloTimerDown","Hiiii   "+e)
                            }

                        }

                    }

                    latitude = customOrderdata?.orderLatitude.toString()
                    longitude = customOrderdata?.orderLongitude.toString()


                    /* coverrtdate(
                             customOrderdata!!.deliveryDate.toString(),
                             customOrderdata!!.startTime,
                             customOrderdata!!.endTime
                     )*/

                    if (!customOrderdata?.todateFromdate.equals("")) {

                        txt_del_date.text = customOrderdata?.todateFromdate
                    }


                    txt_del_pref_type.text = customOrderdata!!.deliveryTypeName
                    if (customOrderdata!!.deliveryType.equals("0")) {
                        textView51.text = resources.getString(R.string.pickup_date)
                        textView52.text = resources.getString(R.string.pickup_time)
                        txt_del_pref_add.visibility = View.GONE
                        img_add_photo.visibility = View.GONE

                    } else if (customOrderdata!!.deliveryType.equals("1")) {
                        txt_del_pref_type.text = customOrderdata!!.deliveryTypeName + " in "+customOrderdata!!.deliveryCity
                        val locaTion =
                                customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
                        txt_del_pref_add.text = locaTion
                        txt_geo_location.visibility = View.VISIBLE
                        txt_geo_location.text = customOrderdata!!.location
                        if (!customOrderdata?.photoId.equals("")) {
                            img_add_photo.visibility = View.VISIBLE
                        } else {
                            img_add_photo.visibility = View.GONE
                        }
                    } else {
                        txt_del_pref_type.text = customOrderdata!!.deliveryTypeName + " in "+customOrderdata!!.deliveryCity
                        val locaTion =
                                customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
                                        customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
                        txt_del_pref_add.text = locaTion
                        img_add_photo.visibility = View.GONE
                    }


                    if (customOrderdata?.customerName!!.length > 0) {
                        constrain_customer.visibility = View.VISIBLE
                        txt_cusmrname.text = customOrderdata!!.customerName
                    } else {
                        constrain_customer.visibility = View.GONE
                    }
                    // txt_del_pref_type.text = customOrderdata!!.deliveryTypeName
                    /*if (!TextUtils.isEmpty(customOrderdata!!.buyingPreference)) {
                        txt_buy_pref.text = customOrderdata!!.buyingPreferenceName
                    } else if (!TextUtils.isEmpty(customOrderdata!!.orderPreference)) {
                        txt_buy_pref.text = customOrderdata!!.orderPreferenceName
                    }*/

                    if(!TextUtils.isEmpty(customOrderdata!!.buyingPreferenceNameNew))
                        txt_buy_pref.text = customOrderdata!!.buyingPreferenceNameNew

                    txt_order_for.text = customOrderdata!!.orderForName



                    if (customOrderdata!!.requestStatus!!.equals("Timed out", true)) {
                        if (customOrderdata!!.orderStatus!!.equals("Offer Made", true)) {
                            Log.e("HelloOrder", "Cust IFF requestStatus   " + customOrderdata!!.requestStatus + "   orderStatus   " + customOrderdata!!.orderStatus)

                            ForDeliveryTimeOut()

                        } else {
                            Log.e("HelloOrder", "Cust ELSEE requestStatus   " + customOrderdata!!.requestStatus + "   orderStatus   " + customOrderdata!!.orderStatus)
                            ForDeliveryTimeOutOnlyTxt()

                        }
                    }

                } else {


                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@CustomOrderDetailActivity,
                                "" + jObjError.getString("message"),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@CustomOrderDetailActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar!!.visibility = View.GONE


            }
        })
    }

    //cancel timer
    fun cancelTimer() {
        if (cTimer != null) {
            cTimer!!.cancel()
            cTimer = null
        }
    }

    private fun coverrtdate(
            from_date: String,
            startTime: String?,
            endTime: String?) {
        val dateFormat1 = SimpleDateFormat("HH:mm:ss", Locale.US)
        if (!startTime.equals("") || !endTime.equals("")) {
            val createdConvertedTime = dateFormat1.parse(startTime)
            val createdConvertedTime1 = dateFormat1.parse(endTime)
            txt_del_time.text =
                    SimpleDateFormat("hh:mm a",Locale.US).format(createdConvertedTime).lowercase() + " to " + SimpleDateFormat(
                            "hh:mm a"
                    ).format(
                            createdConvertedTime1
                    ).lowercase()
        }
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        if (!from_date.equals("")) {
            val createdConvertedDate = dateFormat.parse(from_date)
            txt_del_date.text =
                    SimpleDateFormat("dd MMM, yyyy",Locale.US).format(createdConvertedDate)
        }
    }

    companion object {
        var customOrderdata: OrderDetailResponse.Data? = null

    }
}
