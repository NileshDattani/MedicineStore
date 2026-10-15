package pharmlane.com.PharmLaneStore.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.PaymentMethodAdapter
import pharmlane.com.PharmLaneStore.adapters.PlansAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Filter
import pharmlane.com.PharmLaneStore.model.MakeSubPayment
import pharmlane.com.PharmLaneStore.response.AllStoreSubPaymentResponse
import pharmlane.com.PharmLaneStore.response.AllStoreSubPlanResponse
import pharmlane.com.PharmLaneStore.response.VerifySubPaymentResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.Utils
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.activity_my_subscription.*
import kotlinx.android.synthetic.main.activity_my_subscription.img_back
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class MySubscriptionActivity : AppCompatActivity(), View.OnClickListener, PlansAdapter.clickedPlan, PaymentMethodAdapter.Paymentmode {

    private var recyclerView: RecyclerView? = null
    private var recycl_methods: RecyclerView? = null
    var isTrue: Boolean = false
    var selectedUPIId: String = ""
    var selectedUPINAME: String = ""
    var selectedPlanId: String = ""
    var selectedPlanName: String = ""
    var selectedPlanPrice: String = ""
    var selectedPlanDesc: String = ""
    var mySubs: List<AllStoreSubPlanResponse.Datum> = arrayListOf()
    private var mySubsAdapter: PlansAdapter? = null
    private var paymentMethodAdapter: PaymentMethodAdapter? = null
    var paymentListArray: List<AllStoreSubPaymentResponse.Datum> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark
        setContentView(R.layout.activity_my_subscription)


        recyclerView = findViewById(R.id.recycl_plans)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
                LinearLayoutManager(this)

        img_back.setOnClickListener(this)
        btn_make_payment.setOnClickListener(this)
        con_current_plan.setOnClickListener(this)

        //recycl

        recycl_methods = findViewById(R.id.recycl_methods)
        recycl_methods!!.setHasFixedSize(true)
        recycl_methods!!.layoutManager =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        subListApi()
        paymentMethodListApi()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                if (isTrue) {
                    nested_location_price.visibility = View.VISIBLE
                    con_my_plan.visibility = View.GONE

                    isTrue = false
                } else {
                    finish()
                }

            }
            R.id.con_current_plan -> {
                isTrue = true
                nested_location_price.visibility = View.GONE
                con_my_plan.visibility = View.VISIBLE
            }
            R.id.btn_make_payment -> {
                //need to call api of make payments
                submitPayment()
            }
        }
    }


    fun subListApi() {

        progressBarForMySubs.visibility = View.VISIBLE

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getallstoresubplanlist()

        call.enqueue(object : Callback<AllStoreSubPlanResponse> {
            override fun onResponse(
                    call: Call<AllStoreSubPlanResponse>,
                    response: retrofit2.Response<AllStoreSubPlanResponse>
            ) {
                progressBarForMySubs.visibility = View.GONE
                if (response.isSuccessful) {
                    // mSwipeRefreshLayout!!.isRefreshing = false
                    mySubs = arrayListOf()
                    mySubs = response.body()?.data!!
                    mySubsAdapter = PlansAdapter(this@MySubscriptionActivity, mySubs, this@MySubscriptionActivity)
                    recyclerView!!.adapter = mySubsAdapter
                    mySubsAdapter!!.notifyDataSetChanged()

                } else {

                    // mSwipeRefreshLayout!!.isRefreshing = false
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@MySubscriptionActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MySubscriptionActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<AllStoreSubPlanResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBarForMySubs.visibility = View.GONE
                //mSwipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    override fun my_plan(planId: String, planName: String, planPrice: String, planDesc: String) {
        isTrue = true
        nested_location_price.visibility = View.GONE
        con_my_plan.visibility = View.VISIBLE

        selectedPlanId = planId
        selectedPlanName = planName
        selectedPlanPrice = planPrice
        selectedPlanDesc = planDesc

        txt_selected_plan.text = selectedPlanName
        txt_plan_price.text = "Rs." + selectedPlanPrice
        txt_plan_desc.text = selectedPlanDesc
    }


    fun paymentMethodListApi() {

        progressBarForMySubs2.visibility = View.VISIBLE

        val filter = Filter()
        filter.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getallstoresubmethodlist(filter)

        call.enqueue(object : Callback<AllStoreSubPaymentResponse> {
            override fun onResponse(
                    call: Call<AllStoreSubPaymentResponse>,
                    response: retrofit2.Response<AllStoreSubPaymentResponse>
            ) {
                progressBarForMySubs2.visibility = View.GONE
                if (response.isSuccessful) {

                    if(response.body()?.currentPlan == null){
                        con_current_plan.visibility = View.GONE
                    }else{
                        con_current_plan.visibility = View.VISIBLE
                        txt_current_plan_text.text = response.body()?.currentPlan!!.get(0).plan
                        txt_current_plan_expire.text = response.body()?.currentPlan!!.get(0).expireDate
                        txt_current_plan_price.text = "Rs."+response.body()?.currentPlan!!.get(0).price

                    }

                    paymentListArray = response.body()?.data!!

                    paymentMethodAdapter = PaymentMethodAdapter(
                            this@MySubscriptionActivity,
                            paymentListArray,
                            this@MySubscriptionActivity)

                    recycl_methods!!.adapter = paymentMethodAdapter

                    paymentMethodAdapter!!.notifyDataSetChanged()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@MySubscriptionActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@MySubscriptionActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }
                }

            }

            override fun onFailure(call: Call<AllStoreSubPaymentResponse>, t: Throwable) {
                progressBarForMySubs2.visibility = View.GONE
            }
        })
    }

    fun submitPayment() {

        progressBarForMySubs.visibility = View.VISIBLE

        val verifyPayment = MakeSubPayment()
        verifyPayment.Pmethod_name = selectedUPINAME
        verifyPayment.upi_id = selectedUPIId
        verifyPayment.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        verifyPayment.subscription_plan_id = selectedPlanId


        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.makesubpayment(verifyPayment)

        call.enqueue(object : Callback<VerifySubPaymentResponse> {
            override fun onResponse(
                    call: Call<VerifySubPaymentResponse>,
                    response: retrofit2.Response<VerifySubPaymentResponse>
            ) {
                progressBarForMySubs.visibility = View.GONE
                if (response.isSuccessful) {
                    val intent = Intent()
                    intent.putExtra("date", "")
                    setResult(1001, intent)
                    finish()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@MySubscriptionActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@MySubscriptionActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }
                }

            }

            override fun onFailure(call: Call<VerifySubPaymentResponse>, t: Throwable) {
                progressBarForMySubs.visibility = View.GONE
            }
        })
    }

    override fun getId(name: String, id: String) {

        selectedUPIId = id
        selectedUPINAME = name
        if (name.equals("Google Pay")) {

            Utils.startNewActivity(this@MySubscriptionActivity,"com.google.android.apps.nbu.paisa.user")

        } else if (name.equals("Paytm")) {

            Utils.startNewActivity(this@MySubscriptionActivity,"net.one97.paytm")

        } else if (name.equals("Phone pe")) {
            Utils.startNewActivity(this@MySubscriptionActivity,"com.phonepe.app")
        } else {

        }



    }


}
