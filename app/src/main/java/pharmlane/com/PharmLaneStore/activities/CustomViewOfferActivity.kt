package pharmlane.com.PharmLaneStore.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.adapters.CustomViewOfferAdapter
import pharmlane.com.PharmLaneStore.adapters.OrderOfferAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderDetailOffer
import pharmlane.com.PharmLaneStore.model.SubmitOffer
import pharmlane.com.PharmLaneStore.response.OrderDetailResponse
import pharmlane.com.PharmLaneStore.response.SubmitOfferResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_custom_order_detail.*
import kotlinx.android.synthetic.main.activity_custom_order_detail.txt_geo_location
import kotlinx.android.synthetic.main.activity_custom_view_offer.*
import kotlinx.android.synthetic.main.activity_custom_view_offer.img_back
import kotlinx.android.synthetic.main.activity_custom_view_offer.progressBar
import kotlinx.android.synthetic.main.activity_custom_view_offer.txt_order_for
import kotlinx.android.synthetic.main.activity_custom_view_offer.txt_order_id
import kotlinx.android.synthetic.main.activity_custom_view_offer.txt_view_all_produts
import kotlinx.android.synthetic.main.activity_make_offer.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CustomViewOfferActivity : AppCompatActivity(), View.OnClickListener {

    private var recycler_order_offer: RecyclerView? = null
    private var customViewOfferAdapter: CustomViewOfferAdapter? = null
    private var orderOfferAdapter: OrderOfferAdapter? = null
    var isViewall = "0"
    var isMoreHour = "0"
    var isCustom = ""
    var latitude = ""
    var longitude = ""
    var total = ""
    var status = ""
    var custmProducts = SubmitOffer.Products()
    var isNormal = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_custom_view_offer)

        recycler_order_offer = findViewById(R.id.recycl_order_offer)

        img_back.setOnClickListener(this)
        txt_view_order.setOnClickListener(this)
        constraint7.setOnClickListener(this)
        btn_re_submit_offer.setOnClickListener(this)
        txt_view_all_produts.setOnClickListener(this)
        btn_edt_your_ofer.setOnClickListener(this)
        btn_edt_delivry_prefernce.setOnClickListener(this)
        btn_edt_del_datetime.setOnClickListener(this)
        txt_geo_location.setOnClickListener(this)

        recycler_order_offer!!.setHasFixedSize(true)
        recycler_order_offer!!.layoutManager =
            LinearLayoutManager(this)


        if (intent != null) {
            if (intent.getStringExtra("custom") != null) {
                if (intent.getStringExtra("custom").equals("Custom", ignoreCase = true)) {
                    isCustom = "Custom"
                }
            }

            if (intent.getStringExtra("normal") != null) {
                isNormal = intent.getStringExtra("normal")!!.toInt()
            }
            if (intent.getStringExtra("reviewfinaloffer") != null) {
                txt_title.setText(R.string.review_final_offer)
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_edt_your_ofer.visibility = View.VISIBLE
                btn_edt_delivry_prefernce.visibility = View.VISIBLE
                btn_edt_del_datetime.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.submit_offer)
            }
            if (intent.getStringExtra("reviewoffer") != null) {
                reviewOffer()
            }
            if (intent.getStringExtra("Accepted") != null) {
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.upload_invoice_start_delivery)
            }
            if (intent.getStringExtra("offersmade") != null) {

                btn_re_submit_offer.visibility = View.GONE

            }
            Log.e("yORDERID", "IDD   " + intent.getStringExtra("orderId"))

            if (intent.getStringExtra("orderId") != null) {
                orderDetailApi(intent.getStringExtra("orderId")?: "")
            } else {
                setdata()
            }





            if (intent.getStringExtra("uploadinvoice") != null) {
                constraint7.visibility = View.VISIBLE
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.start_delivery)
                if (intent.getStringExtra("invoiceStatus") != null) {
                    customOrderdata!!.orderStatus = intent.getStringExtra("invoiceStatus")
                }

            }
            if (intent.getStringExtra("handoverinvoice") != null) {
                img2.visibility = View.GONE
                constraint7.isEnabled = false
                constraint7.visibility = View.VISIBLE
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.start_delivery)
                txt.setText(R.string.invoice_status1)
                if (intent.getStringExtra("invoiceStatus") != null) {
                    customOrderdata!!.orderStatus = intent.getStringExtra("invoiceStatus")
                }
            }


        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("yORDERID", "RESUME IDD   " + intent.getStringExtra("orderId"))
        if (intent.getStringExtra("orderId") != null) {
            orderDetailApi(intent.getStringExtra("orderId")?: "")
        }
    }

    private fun setdata() {

        /*  if (customOrderdata!!.products!!.size > 3) {
              txt_view_all_produts.visibility = View.VISIBLE
          }*/
        if (isCustom.equals("Custom")) {
            customViewOfferAdapter = CustomViewOfferAdapter(
                this,
                customOrderdata!!.products, isViewall
            )
            recycler_order_offer!!.adapter = customViewOfferAdapter

        } else {
            orderOfferAdapter = OrderOfferAdapter(
                this,
                customOrderdata!!.products, isViewall
            )
            recycler_order_offer!!.adapter = orderOfferAdapter
        }

        txt_order_id.text = "ID: " + customOrderdata!!.orderNo

        txt_status.text = customOrderdata!!.orderStatus
        txt_status1.text = customOrderdata!!.requestStatus

        DrawableCompat.setTint(
            txt_status.background,
            ContextCompat.getColor(
                this,
                AppConstant.setsStatus(customOrderdata!!.orderStatus!!)
            )
        )
        DrawableCompat.setTint(
            txt_status1.background,
            ContextCompat.getColor(
                this,
                AppConstant.setsStatus(customOrderdata!!.requestStatus!!)
            )
        )
        if (customOrderdata!!.orderStatus.equals(
                "Accepted",
                ignoreCase = true && customOrderdata!!.requestStatus.equals(
                    ">24 hrs",
                    ignoreCase = true
                )
            )
        ) {
            txt_time_elpsed.visibility = View.GONE
            txt_ofermade_accepted.visibility = View.VISIBLE
        } else if (customOrderdata!!.orderStatus.equals("Accepted", ignoreCase = true)) {
            txt_time_elpsed.visibility = View.GONE
            txt_ofermade_accepted.visibility = View.VISIBLE
        } else if (customOrderdata!!.requestStatus.equals(">24 hrs", ignoreCase = true)) {
            txt_time_elpsed.visibility = View.VISIBLE
            txt_ofermade_accepted.visibility = View.GONE
        } else {
            txt_time_elpsed.visibility = View.VISIBLE
            txt_ofermade_accepted.visibility = View.GONE
        }

        if (customOrderdata!!.requestStatus.equals(">24 hrs", ignoreCase = true)) {

            isMoreHour = "1"
        }

        timeElapsed(customOrderdata!!.createdDate)

        /*if (customOrderdata?.orderPreferenceName.equals("")) {
            txt_buying_pref.text = customOrderdata?.buyingPreferenceName
        } else {
            txt_buying_pref.text = customOrderdata?.orderPreferenceName
        }*/
        if(!TextUtils.isEmpty(customOrderdata!!.buyingPreferenceNameNew))
            txt_buying_pref.text = customOrderdata!!.buyingPreferenceNameNew

        txt_order_for.text = customOrderdata!!.orderForName

        if (customOrderdata!!.offerDeliveryPreference != null && !customOrderdata!!.offerDeliveryPreference.equals(
                ""
            )
        ) {


            if (customOrderdata!!.offerDeliveryPreference.equals("0")) {
                textView76.text = resources.getString(R.string.pickup_date)
                textView77.text = resources.getString(R.string.pickup_time)
                textView78.text = resources.getString(R.string.pickupdatetime)
                txt_del_pref.text = resources.getString(R.string.only_store_pickup)



                if (customOrderdata!!.quantiyStatus != null && !customOrderdata!!.quantiyStatus.equals(
                        ""
                    )
                ) {
                    txt_del_add.text = customOrderdata!!.quantiyStatus
                    txt_del_add.visibility = View.VISIBLE
                } else {
                    txt_del_add.visibility = View.GONE
                }
            } else if (customOrderdata!!.offerDeliveryPreference.equals("1")) {

                // txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName
                textView76.text = resources.getString(R.string.delivery_date)
                textView77.text = resources.getString(R.string.delivery_time)
                textView78.text = resources.getString(R.string.delivery_date_time)
                txt_del_pref.text =
                    resources.getString(R.string.location_delivry) + " in " + customOrderdata!!.deliveryCity
                val locaTion =
                    customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
                txt_del_add.text = locaTion
                txt_geo_location.visibility = View.VISIBLE
                txt_geo_location.text = customOrderdata!!.location
                latitude = customOrderdata?.orderLatitude.toString()
                longitude = customOrderdata?.orderLongitude.toString()
            } else {
                txt_del_pref.text =
                    resources.getString(R.string.zip_address_delivry) + " in " + customOrderdata!!.deliveryCity
//                txt_del_pref.text = customOrderdata!!.deliveryTypeName
                val locaTion =
                    customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
                            customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
                txt_del_add.text = locaTion
            }

            //Comment by Rahul RB
//            if (customOrderdata!!.deliveryType.equals("0")) {
//                textView76.text = resources.getString(R.string.pickup_date)
//                textView77.text = resources.getString(R.string.pickup_time)
//                textView78.text = resources.getString(R.string.pickupdatetime)
//                txt_del_pref.text = resources.getString(R.string.only_store_pickup)
//                if (customOrderdata!!.quantiyStatus != null && !customOrderdata!!.quantiyStatus.equals("")) {
//                    txt_del_add.text = customOrderdata!!.quantiyStatus
//                    txt_del_add.visibility = View.GONE
//                } else {
//                    txt_del_add.visibility = View.GONE
//                }
//            } else if (customOrderdata!!.deliveryType.equals("1")) {
//               // txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName
//                textView76.text = resources.getString(R.string.delivery_date)
//                textView77.text = resources.getString(R.string.delivery_time)
//                textView78.text = resources.getString(R.string.delivery_date_time)
//                txt_del_pref.text = resources.getString(R.string.location_delivry)
//                val locaTion =
//                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
//                txt_del_add.text = locaTion
//                txt_geo_location.visibility = View.VISIBLE
//                txt_geo_location.text = customOrderdata!!.location
//                latitude = customOrderdata?.orderLatitude.toString()
//                longitude = customOrderdata?.orderLongitude.toString()
//            } else {
//                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName // Rahul RB
////                txt_del_pref.text = customOrderdata!!.deliveryTypeName
//                val locaTion =
//                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
//                                customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
//                txt_del_add.text = locaTion
//            }
//

        } else {


            if (customOrderdata!!.offerDeliveryPreference.equals("0")) {
                textView76.text = resources.getString(R.string.pickup_date)
                textView77.text = resources.getString(R.string.pickup_time)
                textView78.text = resources.getString(R.string.pickupdatetime)
                txt_del_pref.text = resources.getString(R.string.only_store_pickup)
                if (customOrderdata!!.quantiyStatus != null && !customOrderdata!!.quantiyStatus.equals(
                        ""
                    )
                ) {
                    txt_del_add.text = customOrderdata!!.quantiyStatus
                    txt_del_add.visibility = View.GONE
                } else {
                    txt_del_add.visibility = View.GONE
                }
            } else if (customOrderdata!!.offerDeliveryPreference.equals("1")) {
                textView76.text = resources.getString(R.string.delivery_date)
                textView77.text = resources.getString(R.string.delivery_time)
                textView78.text = resources.getString(R.string.delivery_date_time)
//                txt_del_pref.text = resources.getString(R.string.location_delivry)
                txt_del_pref.text =
                    resources.getString(R.string.location_delivry) + " in " + customOrderdata!!.deliveryCity

                //txt_del_pref.text = customOrderdata!!.deliveryTypeName
                val locaTion =
                    customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
                txt_del_add.text = locaTion
                txt_geo_location.visibility = View.VISIBLE
                txt_geo_location.text = customOrderdata!!.location
                latitude = customOrderdata?.deliveryLatitude.toString()
                longitude = customOrderdata?.deliveryLongitude.toString()
            } else {
                txt_del_pref.text =
                    resources.getString(R.string.zip_address_delivry) + " in " + customOrderdata!!.deliveryCity
//                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName // Rahul RB
//                txt_del_pref.text = customOrderdata!!.deliveryTypeName
                val locaTion =
                    customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
                            customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
                txt_del_add.text = locaTion
            }


            // COmment By RAHUL RB
//            if (customOrderdata!!.deliveryType.equals("0")) {
//                textView76.text = resources.getString(R.string.pickup_date)
//                textView77.text = resources.getString(R.string.pickup_time)
//                textView78.text = resources.getString(R.string.pickupdatetime)
//                txt_del_pref.text = resources.getString(R.string.only_store_pickup)
//                if (customOrderdata!!.quantiyStatus != null && !customOrderdata!!.quantiyStatus.equals("")) {
//                   txt_del_add.text = customOrderdata!!.quantiyStatus
//                    txt_del_add.visibility = View.GONE
//                } else {
//                    txt_del_add.visibility = View.GONE
//                }
//            } else if (customOrderdata!!.deliveryType.equals("1")) {
//                textView76.text = resources.getString(R.string.delivery_date)
//                textView77.text = resources.getString(R.string.delivery_time)
//                textView78.text = resources.getString(R.string.delivery_date_time)
//                txt_del_pref.text = resources.getString(R.string.location_delivry)
//
//                //txt_del_pref.text = customOrderdata!!.deliveryTypeName
//                val locaTion =
//                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
//                txt_del_add.text = locaTion
//                txt_geo_location.visibility = View.VISIBLE
//                txt_geo_location.text = customOrderdata!!.location
//                latitude = customOrderdata?.deliveryLatitude.toString()
//                longitude = customOrderdata?.deliveryLongitude.toString()
//            } else {
//                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName // Rahul RB
////                txt_del_pref.text = customOrderdata!!.deliveryTypeName
//                val locaTion =
//                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
//                                customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
//                txt_del_add.text = locaTion
//            }

        }

        Log.d("TAG", "111111111111" + customOrderdata!!.offerTodateFromdate)
        Log.d("TAG", "222222222222" + customOrderdata!!.todateFromdate)
        if (!customOrderdata!!.offerTodateFromdate.equals(null) && !customOrderdata!!.offerTodateFromdate.equals(
                ""
            )
        ) {

            /*  val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

              val start_time= dateFormat.parse(customOrderdata!!.offerDeliveryTimeStart.toString())
              val end_time= dateFormat.parse(customOrderdata!!.offerDeliveryTimeEnd.toString())


              txt_delivery_time.text =AppConstant.convertFromtoTime(SimpleDateFormat("hh:mm a").format(start_time).lowercase()+" to ",
                      SimpleDateFormat("hh:mm a").format(end_time).lowercase())

  */
            txt_delivery_date.text = customOrderdata?.offerTodateFromdate!!

            // txt_delivery_time.text = customOrderdata?.offerDeliveryTimeStart.toString()+" "+ customOrderdata?.offerDeliveryTimeEnd.toString()

        } else {

            /* val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

             val start_time= dateFormat.parse(customOrderdata!!.startTime.toString())
             val end_time= dateFormat.parse(customOrderdata!!.endTime.toString())


             txt_delivery_time.text =AppConstant.convertFromtoTime(SimpleDateFormat("hh:mm a").format(start_time).lowercase()+" to ",
                     SimpleDateFormat("hh:mm a").format(end_time).lowercase())
 */

            txt_delivery_date.text = customOrderdata?.todateFromdate!!
            // txt_delivery_date.text = (customOrderdata?.deliveryDate!!)

            // txt_delivery_time.text = AppConstant.convertFromtoTime(customOrderdata!!.startTime.toString(), customOrderdata!!.endTime.toString())
        }
        /* if (!customOrderdata!!.startTime.equals("") || !customOrderdata!!.endTime.equals("")) {
             txt_delivery_time.text = AppConstant.convertFromtoTime(
                     customOrderdata!!.startTime.toString(),
                     customOrderdata!!.endTime.toString()
             )
         }*/


        txt_bill_amount.text = "₹ " + customOrderdata?.totalMrp

        txt_discount_perc.text =
            "Discount (" + customOrderdata?.billDiscount + "%)"
        txt_discount_total.text = "₹ " + customOrderdata?.totalDiscount
        // val df = DecimalFormat("#.##")
        // df.roundingMode = RoundingMode.CEILING
        //  Log.d("TAG","-----------"+df.format(customOrderdata?.netBillAmount!!.toInt()))
        txt_netbill_amount.text = (customOrderdata?.netBillAmount!!)
        if (!customOrderdata?.delCharge!!.equals("")) {
            txt_del_charge.text = "₹ " + customOrderdata?.delCharge
        }


        var str = customOrderdata?.totalAmount

        var numeric = true

        numeric = str!!.matches("-?\\d+(\\.\\d+)?".toRegex())

        if (numeric) {
            txt_total.text = "₹ " + customOrderdata?.totalAmount
        } else {
            txt_total.text = customOrderdata?.totalAmount
        }

        if (customOrderdata!!.requestStatus!!.equals("Timed out", true)) {
            if (customOrderdata!!.orderStatus!!.equals("Offer Made", true)) {
                          ForAcceptedDeliveryTimeOut()
            } else if (customOrderdata!!.orderStatus!!.equals("Accepted", true)) {
//                ForAcceptedDeliveryTimeOut()
            } else if (customOrderdata!!.orderStatus!!.equals("Invoiced", true)) {
                ForAcceptedDeliveryTimeOut()
            } else if (customOrderdata!!.orderStatus!!.equals("Delivered", true)) {
                ForDelivered()
            }

        } else if (customOrderdata!!.requestStatus!!.equals("Active", true)) {
            if (customOrderdata!!.orderStatus!!.equals("Offer Made", true)) {
                ForOfferMadeActive()
            } else if (customOrderdata!!.orderStatus!!.equals("Accepted", true)) {
//                ForOfferMadeActive()
            } else if (customOrderdata!!.orderStatus!!.equals("Invoiced", true)) {
                ForOfferMadeActive()
            } else if (customOrderdata!!.orderStatus!!.equals("Delivered", true)) {
                ForDelivered()
            }
        }
    }


    fun ForDelivered() {

        const_buttons.visibility = View.GONE
    }

    fun ForAcceptedDeliveryTimeOut() {
        btn_re_submit_offer.visibility = View.GONE
        constrain_delivry_time_out_OM.visibility = View.VISIBLE
        textViewbottom.text = resources.getString(R.string.you_cant_make_delivery_now)
    }

    fun ForOfferMadeActive() {
        btn_re_submit_offer.visibility = View.GONE
        constrain_delivry_time_out_OM.visibility = View.GONE
    }

    fun timeElapsed(datenTime: String?) {

        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)

        txt_datentime.text = datenTime.toString()
        //SimpleDateFormat("dd MMM,yyyy hh:mm a").format(createdConvertedDate).lowercase()

        val cal1 = Calendar.getInstance()
        val currDate1 = dateFormat.format(cal1.time)
        cal1.time = dateFormat.parse(datenTime)
        cal1.get(Calendar.HOUR_OF_DAY)
        val convertedDate1 = dateFormat.format(cal1.time)

        val stopTime1 = dateFormat.parse(convertedDate1)
        val startTime1 = dateFormat.parse(currDate1)

        val difference1 = startTime1.time - stopTime1.time


        val InSec1 = TimeUnit.MILLISECONDS.toSeconds(difference1) % 60
        val day1 = TimeUnit.MILLISECONDS.toDays(difference1)
        val hh1 = (TimeUnit.MILLISECONDS.toHours(difference1) - TimeUnit.DAYS.toHours(day1))
        val mm1 = (TimeUnit.MILLISECONDS.toMinutes(difference1) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(difference1)
        ))

        //`txt_time_elpsed.setText("Time Elapsed: " + day1 + "d " + hh1 + "h " + mm1 + "m " + InSec1 + "s")
        txt_time_elpsed.text = "Time Elapsed: " + day1 + "d " + hh1 + "h " + mm1 + "m "
    }

    fun reviewOffer() {
        btn_edt_your_ofer.visibility = View.VISIBLE
        btn_edt_delivry_prefernce.visibility = View.VISIBLE
        btn_edt_del_datetime.visibility = View.VISIBLE
        btn_re_submit_offer.visibility = View.VISIBLE
        txt_title.text = resources.getString(R.string.review_offer)
        btn_re_submit_offer.text = resources.getString(R.string.re_submit_offer)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txt_geo_location -> {

                val currentlat = AppConstant.getPreferenceText(AppConstant.STORELATITUDE)
                val currentlong = AppConstant.getPreferenceText(AppConstant.STORELONGITUDE)

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + currentlat + "," + currentlong + "&daddr=" + latitude + "," + longitude)
                )
                startActivity(intent)
            }

            R.id.img_back -> {
                finish()
            }


            R.id.txt_view_order -> {
                Log.e("HElloVIewOrder", "Hii   " + isCustom)
                if (isCustom.equals("Custom")) {
                    startActivity(
                        Intent(
                            this@CustomViewOfferActivity,
                            CustomOrderDetailActivityFromReview::class.java
                        )
                            .putExtra("vieworder", "1")
                            .putExtra("orderId", customOrderdata!!.orderId)
                            .putExtra("offerId", customOrderdata!!.offerId)
                            .putExtra("moreHour", isMoreHour)
                    )
                } else {
                    startActivity(
                        Intent(
                            this@CustomViewOfferActivity,
                            OrderDetailActivityFromReview::class.java
                        )
                            .putExtra("vieworder", "1")
                            .putExtra("orderId", customOrderdata!!.orderId)
                            .putExtra("offerId", customOrderdata!!.offerId)
                            .putExtra("moreHour", isMoreHour)
                    )
                }

                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_re_submit_offer -> {
                if (intent.getStringExtra("Accepted") != null) {
                    startActivity(
                        Intent(
                            this@CustomViewOfferActivity,
                            UploadInvoiceActivity::class.java
                        ).putExtra("custom", isCustom)
                    )
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else if (intent.getStringExtra("uploadinvoice") != null || intent.getStringExtra("handoverinvoice") != null) {

                    startActivity(
                        Intent(
                            this@CustomViewOfferActivity,
                            PaymentActivity::class.java
                        ).putExtra("orderId", customOrderdata!!.orderId)
                    )
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                } else if (intent.getStringExtra("reviewfinaloffer") != null) {
                    Log.d("HElloPAiss", "Hii   Caliin")
                    submitOfferApi()


                    /*  startActivity(
                          Intent(this@CustomViewOfferActivity, CustomOrderDetailActivity::class.java)
                              .putExtra("reviewfinaloffer", "11")
                      )*/
                } else if (intent.getStringExtra("reviewoffer") != null) {
                    if (isCustom.equals("Custom")) {
                        startActivity(
                            Intent(
                                this@CustomViewOfferActivity,
                                CustomOrderDetailActivity::class.java
                            )
                                .putExtra("reviewoffer", "11")
                                .putExtra("offerId", customOrderdata!!.offerId)
                        )
                    } else {
                        startActivity(
                            Intent(this@CustomViewOfferActivity, OrderDetailActivity::class.java)
                                .putExtra("reviewoffer", "11")
                                .putExtra("offerId", customOrderdata!!.offerId)
                        )
                    }
                    /* startActivity(
                             Intent(this@CustomViewOfferActivity, CustomOrderDetailActivity::class.java)
                                     .putExtra("reviewoffer", "11")
                                     .putExtra("offerId", customOrderdata!!.offerId)
                     )*/
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }

            R.id.btn_edt_your_ofer -> {
                val returnIntent = Intent()
                returnIntent.putExtra("result", "Hey, I received your intent!")
                returnIntent.putExtra("normal", isNormal)
                setResult(AppCompatActivity.RESULT_OK, returnIntent)
                finish()
//                if(isNormal != 1){
//                    finish()
//                }
            }
            R.id.btn_edt_delivry_prefernce -> {

                /*startActivity(
                        Intent(this@CustomViewOfferActivity, SetDeliveryActivity::class.java)
                                .putExtra("PREFERENCE", "1")
                                .putExtra("custom", "Custom"))*/
                finish()

            }
            R.id.btn_edt_del_datetime -> {

                finish()

            }
            R.id.constraint7 -> {
                //   Log.d("TAG", "CLICKED")
            }
            R.id.txt_view_all_produts -> {


                startActivity(
                    Intent(this@CustomViewOfferActivity, ViewAllProductsActivity::class.java)
                        .putExtra("youroffer", "1")
                        .putExtra("custom", isCustom)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }


    fun submitOfferApi() {

        progressBar.visibility = View.VISIBLE
        //  Log.d("TAG", "PERCNTAGE===>" + customOrderdata!!.billDiscount)
        val submitOffer = SubmitOffer()

        if (!customOrderdata?.startTime.equals("") || !customOrderdata?.endTime!!.equals("")) {
            submitOffer.delivery_time_end = customOrderdata!!.endTime
            submitOffer.delivery_time_start = customOrderdata!!.startTime
        } else {
            submitOffer.delivery_time_end = ""
            submitOffer.delivery_time_start = ""
        }
        if (customOrderdata?.quantiyStatus != null) {
            if (customOrderdata?.quantiyStatus!!.equals(
                    "Original Prescription Required",
                    ignoreCase = false
                )
            ) {
                submitOffer.is_prescription = "1"
            } else if (customOrderdata?.quantiyStatus!!.equals(
                    "Original Prescription Not Required",
                    ignoreCase = false
                )
            ) {
                submitOffer.is_prescription = "0"
            } else {
                submitOffer.is_prescription = ""
            }
        } else {
            submitOffer.is_prescription = ""
        }

        submitOffer.delivery_charges = customOrderdata?.delCharge.toString()
        submitOffer.discount_percentage = customOrderdata?.billDiscount
        submitOffer.discount_price = customOrderdata?.totalDiscount

        var str = customOrderdata?.totalAmount

        val sb = StringBuilder()
        sb.append(str)

        if (sb.contains("₹")) {
            sb.deleteCharAt(0)
            str = sb.toString()
            total = str.trim()
        } else {
            total = customOrderdata?.totalAmount.toString()
        }

        Log.e("HEllodsdSIze", "Size    " + customOrderdata!!.products!!.size)
        Log.e("HEllodsdSIze", "arrProductSize    " + arrProduct.size)

        for (i in 0 until customOrderdata!!.products!!.size) {
            custmProducts = SubmitOffer.Products()

            custmProducts.quantity_avail = customOrderdata!!.products!![i].storeAvailableQuantity
            custmProducts.storeAvailableQuantity =
                customOrderdata!!.products!![i].storeAvailableQuantity
            custmProducts.quantity_type = customOrderdata!!.products!![i].quantityType
            custmProducts.order_product_id = customOrderdata!!.products!![i].id
            custmProducts.product_available = customOrderdata!!.products!![i].availableProduct
            custmProducts.product_price = customOrderdata!!.products!![i].productMrp
            custmProducts.quantity_status = customOrderdata!!.products!![i].qnt_status
            custmProducts.prescr_status = customOrderdata!!.products!![i].prescrStatus
//            custmProducts.quantity = customOrderdata!!.products!![i].quantityAvail
            custmProducts.quantity = customOrderdata!!.products!![i].quantity
            arrProduct.add(custmProducts)

        }

        submitOffer.final_amount = total
        submitOffer.order_id = customOrderdata?.orderId
        Log.e("HElloDeliveryPreff", "Hiii   " + customOrderdata?.offerDeliveryPreference)
        submitOffer.delivery_type = customOrderdata?.offerDeliveryPreference
        submitOffer.products = arrProduct
        if (!customOrderdata?.deliveryDate!!.equals("") && !customOrderdata?.toDeliveryDate!!.equals(
                ""
            )
        ) {

            customOrderdata?.deliveryDate =
                AppConstant.submitofferDate(customOrderdata?.deliveryDate!!)
            customOrderdata?.toDeliveryDate =
                AppConstant.submitofferDate(customOrderdata?.toDeliveryDate!!)
            submitOffer.delivery_date = (customOrderdata?.deliveryDate!!)
            submitOffer.to_delivery_date = (customOrderdata?.toDeliveryDate!!)
            //  AppConstant.convertDelDateApi(customOrderdata?.deliveryDate!!)
        } else {
            submitOffer.delivery_date = ""
            submitOffer.to_delivery_date = ""
        }
        submitOffer.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.submitoffer(submitOffer)

        call.enqueue(object : Callback<SubmitOfferResponse> {
            override fun onResponse(
                call: Call<SubmitOfferResponse>,
                response: retrofit2.Response<SubmitOfferResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

                    Log.d("HElloPAiss", "Hii   Caliin Api  " + response.body())
                    startActivity(
                        Intent(this@CustomViewOfferActivity, MainActivity::class.java)
                            .putExtra("fromplatform", "1")
                    )

                    val customOrderdatas: OrderDetailResponse.Data? = null
                    customOrderdata = customOrderdatas
                    custmProducts = SubmitOffer.Products()
                    arrProduct = ArrayList<SubmitOffer.Products>()
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@CustomViewOfferActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@CustomViewOfferActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }

            override fun onFailure(call: Call<SubmitOfferResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }


    fun orderDetailApi(orderId: String) {

        progressBar!!.visibility = View.VISIBLE
        val orderDetailOffer = OrderDetailOffer()
        orderDetailOffer.order_id = orderId
        orderDetailOffer.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.orderdetailoffer(orderDetailOffer)

        call.enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(
                call: Call<OrderDetailResponse>,
                response: retrofit2.Response<OrderDetailResponse>
            ) {

                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {

                    if (intent.getStringExtra("offersmade") != null || intent.getStringExtra("uploadinvoice") != null
                        || intent.getStringExtra("Accepted") != null || intent.getStringExtra("Delivered") != null
                    ) {

                        customOrderdata = response.body()!!.data

                        /* if (customOrderdata!!.products!!.size > 3) {
                             txt_view_all_produts.visibility = View.VISIBLE
                         }*/


                        if (customOrderdata!!.orderType.equals("0")) {
                            orderOfferAdapter = OrderOfferAdapter(
                                this@CustomViewOfferActivity,
                                customOrderdata!!.products, isViewall
                            )
                            recycler_order_offer!!.adapter = orderOfferAdapter

                            orderOfferAdapter!!.notifyDataSetChanged()
                        } else {

                            isCustom = "Custom"

                            customViewOfferAdapter = CustomViewOfferAdapter(
                                this@CustomViewOfferActivity,
                                customOrderdata!!.products, isViewall
                            )
                            recycler_order_offer!!.adapter = customViewOfferAdapter

                        }
                        // error
//                        customOrderdata?.netBillAmount = (((customOrderdata?.totalAmount!!.toFloat()) - (customOrderdata?.delCharge!!.toFloat())).toString())

                    }

                    Log.e(
                        "HelloLohssas",
                        "TotalAmount   " + customOrderdata?.totalAmount + "   DelCHarege   " + customOrderdata?.delCharge
                    )
                    Log.e(
                        "HelloLohssas",
                        "netBillAmount   " + customOrderdata?.netBillAmount + "   DelCHarege   " + customOrderdata?.totalDiscount
                    )
                    if (customOrderdata?.totalAmount!!.equals("") && customOrderdata?.delCharge!!.equals(
                            ""
                        )
                    ) {
                        customOrderdata?.netBillAmount = ""
                    } else {
                        if (customOrderdata?.delCharge!!.equals("")) {
                            customOrderdata?.netBillAmount =
                                customOrderdata?.totalAmount!!.toString()

                        } else {
                            customOrderdata?.netBillAmount =
                                (((customOrderdata?.totalAmount!!.toFloat()) - (customOrderdata?.delCharge!!.toFloat())).toString())

                        }
                    }

                    if (customOrderdata?.netBillAmount!!.equals("") && customOrderdata?.totalDiscount!!.equals(
                            ""
                        )
                    ) {
                        customOrderdata?.totalMrp = ""
                    } else {
                        customOrderdata?.totalMrp =
                            (customOrderdata?.netBillAmount!!.toFloat() + customOrderdata?.totalDiscount!!.toFloat()).toString()
                    }

                    customOrderdata?.customerPhone = response.body()?.data?.customerPhone
                    customOrderdata?.offerId = response.body()?.data?.offerId
                    AppConstant.setPreferenceText(
                        AppConstant.OFFER_ID,
                        response.body()?.data?.offerId.toString()
                    )
                    customOrderdata?.customerId = response.body()?.data?.customerId
                    customOrderdata?.paymentMethod = response.body()?.data?.paymentMethod

                    if (response.body()?.data?.offerIsPrescription.equals("0")) {
                        customOrderdata?.quantiyStatus = "Original Prescription Not Required"
                    } else if (response.body()?.data?.offerIsPrescription.equals("1")) {
                        customOrderdata?.quantiyStatus = "Original Prescription Required"
                    }

                    setdata()
                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@CustomViewOfferActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@CustomViewOfferActivity, e.message, Toast.LENGTH_LONG)
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

    companion object {
        var arrProduct = ArrayList<SubmitOffer.Products>()
    }
}