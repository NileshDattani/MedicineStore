package pharmlane.com.PharmLaneStore.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.adapters.OrderOfferAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderDetailOffer
import pharmlane.com.PharmLaneStore.response.OrderDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_custom_view_offer.*
import kotlinx.android.synthetic.main.activity_view_offer.*
import kotlinx.android.synthetic.main.activity_view_offer.btn_edt_del_datetime
import kotlinx.android.synthetic.main.activity_view_offer.btn_edt_delivry_prefernce
import kotlinx.android.synthetic.main.activity_view_offer.btn_edt_your_ofer
import kotlinx.android.synthetic.main.activity_view_offer.btn_re_submit_offer
import kotlinx.android.synthetic.main.activity_view_offer.constraint7
import kotlinx.android.synthetic.main.activity_view_offer.constraint_order_for
import kotlinx.android.synthetic.main.activity_view_offer.img2
import kotlinx.android.synthetic.main.activity_view_offer.img_back
import kotlinx.android.synthetic.main.activity_view_offer.txt
import kotlinx.android.synthetic.main.activity_view_offer.txt_ofermade_accepted
import kotlinx.android.synthetic.main.activity_view_offer.txt_title
import kotlinx.android.synthetic.main.activity_view_offer.txt_view_order
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ViewOfferActivity : AppCompatActivity(), View.OnClickListener {

    private var recycler_order_offer: RecyclerView? = null
    private var orderOfferAdapter: OrderOfferAdapter? = null
    var isViewall = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark
        setContentView(R.layout.activity_view_offer)
        recycler_order_offer = findViewById(R.id.recycl_order_offer)
        img_back.setOnClickListener(this)
        txt_view_order.setOnClickListener(this)
        constraint7.setOnClickListener(this)
        btn_re_submit_offer.setOnClickListener(this)

        recycler_order_offer!!.setHasFixedSize(true)
        recycler_order_offer!!.layoutManager =
                LinearLayoutManager(this)


        if (intent != null) {
            if (intent.getStringExtra("reviewfinaloffer") != null) {
                txt_title.setText(R.string.review_final_offer)
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.submit_offer)
            }
            if (intent.getStringExtra("reviewoffer") != null) {
                reviewOffer()
            }
            if (intent.getStringExtra("offersmade") != null) {
                txt_ofermade_accepted.visibility = View.VISIBLE
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.upload_invoice_start_delivery)
            }
            if (intent.getStringExtra("uploadinvoice") != null) {
                txt_ofermade_accepted.visibility = View.VISIBLE
                constraint7.visibility = View.VISIBLE
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.start_delivery)
            }
            if (intent.getStringExtra("handoverinvoice") != null) {
                img2.visibility = View.GONE
                constraint7.isEnabled = false
                txt_ofermade_accepted.visibility = View.VISIBLE
                constraint7.visibility = View.VISIBLE
                btn_re_submit_offer.visibility = View.VISIBLE
                btn_re_submit_offer.setText(R.string.start_delivery)
                txt.setText(R.string.invoice_status1)
            }
            if (intent.getStringExtra("MyOrder") != null) {
                orderDetailApi(intent.getStringExtra("MyOrder") ?: "")
//                myOrderDetail()
            }
        }
    }

    fun orderDetailApi(orderId: String) {

//        progressBar!!.visibility = View.VISIBLE
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

                Log.d("TAsdsG", "------------------Dpre" + response.body()?.data!!.offerDeliveryPreference + "   DpreName   " + response.body()?.data!!.offerDeliveryPreferenceName + "  NAme  " + response.body()?.data!!.deliveryTypeName)
//                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {

                    if (intent.getStringExtra("offersmade") != null || intent.getStringExtra("uploadinvoice") != null
                            || intent.getStringExtra("Accepted") != null) {

                        customOrderdata = response.body()!!.data

                        /* if (customOrderdata!!.products!!.size > 3) {
                             txt_view_all_produts.visibility = View.VISIBLE
                         }*/


                        if (customOrderdata!!.orderType.equals("0")) {

                            orderOfferAdapter = OrderOfferAdapter(
                                    this@ViewOfferActivity,
                                    customOrderdata!!.products, isViewall
                            )
                            recycler_order_offer!!.adapter = orderOfferAdapter

                            orderOfferAdapter!!.notifyDataSetChanged()


                        } else {

                        }

                        // error

                        if (customOrderdata?.totalAmount!!.equals("") && customOrderdata?.delCharge!!.equals("")) {
                            customOrderdata?.netBillAmount = ""
                        } else {
                            customOrderdata?.netBillAmount = (((customOrderdata?.totalAmount!!.toFloat()) - (customOrderdata?.delCharge!!.toFloat())).toString())
                        }

                        if (customOrderdata?.netBillAmount!!.equals("") && customOrderdata?.totalDiscount!!.equals("")) {
                            customOrderdata?.totalMrp = ""
                        } else {
                            customOrderdata?.totalMrp = (customOrderdata?.netBillAmount!!.toFloat() + customOrderdata?.totalDiscount!!.toFloat()).toString()
                        }

//                        customOrderdata?.totalMrp = (customOrderdata?.netBillAmount!!.toFloat() + customOrderdata?.totalDiscount!!.toFloat()).toString()

                    }
                    customOrderdata?.customerPhone = response.body()?.data?.customerPhone
                    customOrderdata?.offerId = response.body()?.data?.offerId
                    AppConstant.setPreferenceText(AppConstant.OFFER_ID, response.body()?.data?.offerId.toString())
                    customOrderdata?.customerId = response.body()?.data?.customerId
                    customOrderdata?.paymentMethod = response.body()?.data?.paymentMethod

                    if (response.body()?.data?.offerIsPrescription.equals("0")) {
                        customOrderdata?.quantiyStatus = "Original Prescription Not Required"
                    } else if (response.body()?.data?.offerIsPrescription.equals("1")) {
                        customOrderdata?.quantiyStatus = "Original Prescription Required"
                    }

//                    setdata()
                } else {


                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@ViewOfferActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@ViewOfferActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
//                progressBar!!.visibility = View.GONE


            }
        })
    }

    fun myOrderDetail() {
        txt_ofermade_accepted.visibility = View.VISIBLE
        constraint_order_for.visibility = View.GONE
        constraint_custmr_details.visibility = View.VISIBLE
        constraint7.visibility = View.VISIBLE
        btn_re_submit_offer.visibility = View.VISIBLE
        //   btn_re_submit_offer.setText(R.string.send_order_reminder_to_buyer)


        txt.setText(R.string.upload_invoice)
        btn_re_submit_offer.setText(R.string.start_delivery)
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
            R.id.img_back -> {
                finish()
            }
            R.id.txt_view_order -> {
//                /* startActivity(
//                     Intent(this@ViewOfferActivity, FinalOrderActivity::class.java)
//                         .putExtra("vieworder", "1"))
//                 //finish()
//                 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
            }
            R.id.btn_re_submit_offer -> {
                if (intent.getStringExtra("offersmade") != null) {
                    startActivity(Intent(this@ViewOfferActivity, UploadInvoiceActivity::class.java))
                } else if (intent.getStringExtra("uploadinvoice") != null || intent.getStringExtra("handoverinvoice") != null) {
                    startActivity(Intent(this@ViewOfferActivity, PaymentActivity::class.java))
                } else if (intent.getStringExtra("reviewfinaloffer") != null) {
                    startActivity(
                            Intent(this@ViewOfferActivity, OrderDetailActivity::class.java)
                                    .putExtra("reviewfinaloffer", "11")
                    )
                } else if (intent.getStringExtra("reviewoffer") != null) {
                    startActivity(
                            Intent(this@ViewOfferActivity, OrderDetailActivity::class.java)
                                    .putExtra("reviewoffer", "11")
                    )
                }

                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.constraint7 -> {
                Log.d("TAG", "CLICKED")
            }
        }
    }
}