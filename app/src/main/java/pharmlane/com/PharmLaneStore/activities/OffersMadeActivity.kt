package pharmlane.com.PharmLaneStore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.ViewallOffermadeAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.ViewallStoreOrder
import pharmlane.com.PharmLaneStore.response.ViewAllStoreOrderResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_offers_made.*
import kotlinx.android.synthetic.main.activity_offers_made.btn_sortby
import kotlinx.android.synthetic.main.activity_offers_made.img_back
import kotlinx.android.synthetic.main.activity_offers_made.progressBar
import kotlinx.android.synthetic.main.activity_offers_made.simpleSwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_pending_orders.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class OffersMadeActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    var Orderdata: List<ViewAllStoreOrderResponse.Datum> = arrayListOf()


    private var recycler_order_offer: RecyclerView? = null
    private var offersMadeAdapter: ViewallOffermadeAdapter? = null

    var statusViewall = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_offers_made)



        if (intent != null) {
            if (intent.getStringExtra("offersmade") != null) {
                statusViewall = intent.getStringExtra("offersmade") ?: ""
            }
        }
        recycler_order_offer = findViewById(R.id.recycl_ofer_made)
        img_back.setOnClickListener(this)
        btn_sortby.setOnClickListener(this)

        recycler_order_offer!!.setHasFixedSize(true)
        recycler_order_offer!!.layoutManager =
            LinearLayoutManager(this)

        simpleSwipeRefreshLayout.setOnRefreshListener(this)
        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

        simpleSwipeRefreshLayout.post(Runnable {
            if (simpleSwipeRefreshLayout != null) {
                simpleSwipeRefreshLayout!!.isRefreshing = true
            }
            orderListApi()
        })

     //   orderListApi()
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.btn_sortby -> {
                /* startActivity(
                     Intent(this@ViewOfferActivity, FinalOrderActivity::class.java)
                         .putExtra("vieworder", "1"))
                 //finish()
                 overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
            }
        }
    }

    fun orderListApi() {

        progressBar!!.visibility = View.VISIBLE
        val viewallStoreOrder = ViewallStoreOrder()
        viewallStoreOrder.status_view_all = statusViewall
        viewallStoreOrder.store_id =AppConstant.getPreferenceText(AppConstant.STORE_ID)


        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.viewallstorelist(viewallStoreOrder)

        call.enqueue(object : Callback<ViewAllStoreOrderResponse> {
            override fun onResponse(
                    call: Call<ViewAllStoreOrderResponse>,
                    response: retrofit2.Response<ViewAllStoreOrderResponse>
            ) {
                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {
                    simpleSwipeRefreshLayout.isRefreshing = false
                    Orderdata = arrayListOf()

                    Orderdata = response.body()!!.data!!

                    offersMadeAdapter = ViewallOffermadeAdapter(this@OffersMadeActivity, Orderdata,statusViewall)
                    recycler_order_offer!!.adapter = offersMadeAdapter

                    offersMadeAdapter!!.notifyDataSetChanged()



                } else {
                    simpleSwipeRefreshLayout.isRefreshing = false

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@OffersMadeActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@OffersMadeActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                }
            }


            override fun onFailure(call: Call<ViewAllStoreOrderResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar!!.visibility = View.GONE
                simpleSwipeRefreshLayout.isRefreshing = false

            }
        })
    }
    override fun onResume() {
        super.onResume()
        orderListApi()
    }

    override fun onRefresh() {
        orderListApi()
    }
}