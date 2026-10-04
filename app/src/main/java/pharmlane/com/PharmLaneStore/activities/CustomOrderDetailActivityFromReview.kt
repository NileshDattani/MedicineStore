package pharmlane.com.PharmLaneStore.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.adapters.ProductsOrederedAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderDetail
import pharmlane.com.PharmLaneStore.response.OrderDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
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

class CustomOrderDetailActivityFromReview : AppCompatActivity(), View.OnClickListener {
    var orderId = ""
    var offerId = ""
    var isViewall = "0"
    var moreHour = "0"
    var timer = ""
    var direct = ""
    var isCustom = "Custom"
    var latitude = ""
    var longitude = ""

    private var productsOrederedAdapter: ProductsOrederedAdapter? = null
    private var recycler: RecyclerView? = null

    lateinit var img_add_photo:AppCompatImageView

//    var customOrderdataReview: OrderDetailResponse.Data? = null


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
                moreHour = intent.getStringExtra("moreHour")?: ""

                if (moreHour.equals("1")) {
                    constraint_top.visibility = View.GONE
                }
            }

            if (intent.getStringExtra("offerId") != null) {
                offerId = intent.getStringExtra("offerId")?: ""
            }
            if (intent.getStringExtra("timer") != null) {
                timer = intent.getStringExtra("timer")?: ""
            } else {
                timer = customOrderdata?.createdDate!!
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
        img_back.setOnClickListener(this)
        img_menu.setOnClickListener(this)
        btn_offer.setOnClickListener(this)
        img_add_photo.setOnClickListener(this)
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
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }

            R.id.img_add_photo -> {

                startActivity(Intent(this@CustomOrderDetailActivityFromReview, ViewPhotoIdActivity::class.java)
                        .putExtra("photo_id",customOrderdataReview?.photoId))

            }
            R.id.txt_geo_location -> {
                val currentlat  =  AppConstant.getPreferenceText(AppConstant.STORELATITUDE)
                val currentlong =  AppConstant.getPreferenceText(AppConstant.STORELONGITUDE)


//                val currentlat = MainActivity.current_latitude
//                val currentlong = MainActivity.current_longitude

                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentlat + "," + currentlong + "&daddr=" + latitude + "," + longitude))
                startActivity(intent)
            }
            R.id.btn_offer -> {

                if (intent.getStringExtra("reviewfinaloffer") != null) {
                    startActivity(
                            Intent(this@CustomOrderDetailActivityFromReview, CustomViewOfferActivity::class.java)
                                    .putExtra("reviewoffer", "1")
                                    .putExtra("custom", isCustom)
                    )
                    //  finish()
                } else {
                    startActivity(
                            Intent(
                                    this@CustomOrderDetailActivityFromReview,
                                    CustomViewOfferActivity::class.java
                            ).putExtra("custom", isCustom)
                    )
                    //  finish()
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_offer_1 -> {

                startActivity(Intent(this@CustomOrderDetailActivityFromReview, CustomViewOfferActivity::class.java)
                        .putExtra("custom", isCustom))
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_view_all_produts -> {
                startActivity(
                        Intent(
                                this@CustomOrderDetailActivityFromReview,
                                ViewAllProductsActivity::class.java
                        ).putExtra("statusviewall", direct)
                )
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_make_offer -> {

                Log.e("HelloReOrde", "direct   "+direct+"   orderId   "+orderId)
                startActivity(
                        Intent(
                                this@CustomOrderDetailActivityFromReview,
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


        Log.e("HelloReOrde", "OFFER_ID   "+AppConstant.getPreferenceText(AppConstant.OFFER_ID)+"   STORE_ID   "+AppConstant.getPreferenceText(AppConstant.STORE_ID))

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
                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {

                    customOrderdataReview = response.body()!!.data

                    txt_order_id.text = "ID: " + customOrderdataReview!!.orderNo
                    txt_order_dtntime.text = (customOrderdataReview?.createdDate!!)
                    //  AppConstant.convertdatentimetoOrderTime(customOrderdataReview!!.createdDate!!).lowercase()

                    /* if (customOrderdataReview!!.products!!.size>3){
                         txt_view_all_produts.visibility = View.VISIBLE
                     }*/



                    productsOrederedAdapter = ProductsOrederedAdapter(
                            this@CustomOrderDetailActivityFromReview,
                            customOrderdataReview!!.products,
                            isViewall
                    )
                    recycler!!.adapter = productsOrederedAdapter

                    if (customOrderdataReview!!.requestStatus.equals(">24 hrs", ignoreCase = true)) {
                        constraint_top.visibility = View.GONE
                    } else {
                        txt_date.text = timer
                    }

                    Log.e("HelloReOrde", "orderDetailApi   " + customOrderdataReview?.todateFromdate)

                    latitude = customOrderdataReview?.orderLatitude.toString()
                    longitude = customOrderdataReview?.orderLongitude.toString()


                    /* coverrtdate(
                             customOrderdataReview!!.deliveryDate.toString(),
                             customOrderdataReview!!.startTime,
                             customOrderdataReview!!.endTime
                     )*/

                    if (!customOrderdataReview?.todateFromdate.equals("")) {

                        txt_del_date.text = customOrderdataReview?.todateFromdate
                    }


                    txt_del_pref_type.text = customOrderdataReview!!.deliveryTypeName
                    if (customOrderdataReview!!.deliveryType.equals("0")) {
                        textView51.text = resources.getString(R.string.pickup_date)
                        textView52.text = resources.getString(R.string.pickup_time)
                        txt_del_pref_add.visibility = View.GONE
                        img_add_photo.visibility = View.GONE

                    } else if (customOrderdataReview!!.deliveryType.equals("1")) {
                        txt_del_pref_type.text = customOrderdata!!.deliveryTypeName + " in "+customOrderdata!!.deliveryCity
                        val locaTion =
                                customOrderdataReview!!.blockNo + ", " + customOrderdataReview!!.buildingName
                        txt_del_pref_add.text = locaTion
                        txt_geo_location.visibility = View.VISIBLE
                        txt_geo_location.text = customOrderdataReview!!.location
                        if (!customOrderdataReview?.photoId.equals("")) {
                            img_add_photo.visibility = View.VISIBLE
                        } else {
                            img_add_photo.visibility = View.GONE
                        }
                    } else {
                        txt_del_pref_type.text = customOrderdata!!.deliveryTypeName + " in "+customOrderdata!!.deliveryCity
                        val locaTion =
                                customOrderdataReview!!.blockNo + ", " + customOrderdataReview!!.buildingName + ", " + customOrderdataReview!!.street + ", " +
                                        customOrderdataReview!!.area + ", " + customOrderdataReview!!.landmark + ", " + customOrderdataReview!!.zipcode
                        txt_del_pref_add.text = locaTion
                        img_add_photo.visibility = View.GONE
                    }


                    if (customOrderdataReview?.customerName!!.length > 0) {
                        constrain_customer.visibility = View.VISIBLE
                        txt_cusmrname.text = customOrderdataReview!!.customerName
                    } else {
                        constrain_customer.visibility = View.GONE
                    }
                    // txt_del_pref_type.text = customOrderdataReview!!.deliveryTypeName
                    /*if (customOrderdataReview!!.orderPreferenceName.equals("")) {
                        txt_buy_pref.text = customOrderdataReview!!.buyingPreferenceName
                    } else {
                        txt_buy_pref.text = customOrderdataReview!!.orderPreferenceName
                    }*/
                    if(!TextUtils.isEmpty(customOrderdataReview!!.buyingPreferenceNameNew))
                        txt_buy_pref.text = customOrderdataReview!!.buyingPreferenceNameNew

                    txt_order_for.text = customOrderdataReview!!.orderForName
                } else {


                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@CustomOrderDetailActivityFromReview,
                                "" + jObjError.getString("message"),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@CustomOrderDetailActivityFromReview, e.message, Toast.LENGTH_LONG)
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
        var customOrderdataReview: OrderDetailResponse.Data? = null

    }
}
