package pharmlane.com.PharmLaneStore.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.*
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderListByFilter
import pharmlane.com.PharmLaneStore.response.ViewAllStoreOrderResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class MyOrdersActivity : AppCompatActivity(), View.OnClickListener, SortByAdapter.sortBy {

    private var recyclerView: RecyclerView? = null
    private var myOrdersAdapter: MyOrdersAdapter? = null
    var myOrder: List<ViewAllStoreOrderResponse.Datum> = arrayListOf()
    private lateinit var mBroadcastManager: LocalBroadcastManager

    private var mysort_value = 1
    private var type = ""
    private var status = ""
    private var customerName = ""
    private var city = ""
    private var area = ""
    private var zipcode = ""

    private var dialog: Dialog? = null
    var language = arrayOf(
            "By Date",
            "By Invoice Value"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark
        setContentView(R.layout.activity_my_orders)

        recyclerView = findViewById(R.id.recycl)

        img_back.setOnClickListener(this)
        txt_sortby.setOnClickListener(this)
        txt_filtr.setOnClickListener(this)

        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
                LinearLayoutManager(this)

        mBroadcastManager = LocalBroadcastManager.getInstance(this)
        mBroadcastManager.registerReceiver(mReceiverFilterResult, IntentFilter(resources.getString(R.string.broadcastFilterResult)))
        orderListApi("", "", "", "", "", "", 1)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.txt_sortby -> {
                showDialog()
            }
            R.id.txt_filtr -> {
                startActivity(Intent(this@MyOrdersActivity, FilterActivity::class.java))
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    fun showDialog() {
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialog_sort_by)
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.white)
        //  dialog .setCancelable(false)
        val lv = dialog!!.findViewById(R.id.recycl) as RecyclerView
        val sortByAdapter: SortByAdapter

        lv.layoutManager = LinearLayoutManager(this)

        sortByAdapter = SortByAdapter(this, language, this)
        lv.adapter = sortByAdapter
        /*close_icon.setOnClickListener {
            dialog.dismiss()
        }*/

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        lp.windowAnimations = R.style.DialogAnimation
        dialog!!.window!!.attributes = lp
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()

    }

    override fun sortby(pos: Int) {
        mysort_value = pos
        orderListApi(type, status, customerName, city, area, zipcode, mysort_value)
        dialog!!.dismiss()
    }

    fun orderListApi(type: String, status: String, customerName: String, city: String, area: String, zipcode: String, sorts: Int) {

        progressBarForMyOrder.visibility = View.VISIBLE
        val getStoreList = OrderListByFilter()
        getStoreList.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        getStoreList.delivery_type = type
        getStoreList.status = status
        getStoreList.user_id = customerName
        getStoreList.store_criteria_city = city
        getStoreList.store_criteria_area = area
        getStoreList.store_criteria_zipcode = zipcode
        getStoreList.sort_value = sorts

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getallstoreorderlist(getStoreList)

        call.enqueue(object : Callback<ViewAllStoreOrderResponse> {
            override fun onResponse(
                    call: Call<ViewAllStoreOrderResponse>,
                    response: retrofit2.Response<ViewAllStoreOrderResponse>
            ) {
                progressBarForMyOrder.visibility = View.GONE
                if (response.isSuccessful) {
                    // mSwipeRefreshLayout!!.isRefreshing = false
                    myOrder = arrayListOf()
                    myOrder = response.body()?.data!!
                    myOrdersAdapter = MyOrdersAdapter(this@MyOrdersActivity, myOrder)
                    recyclerView!!.adapter = myOrdersAdapter
                    myOrdersAdapter!!.notifyDataSetChanged()

                } else {

                    // mSwipeRefreshLayout!!.isRefreshing = false
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@MyOrdersActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MyOrdersActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ViewAllStoreOrderResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBarForMyOrder.visibility = View.GONE
                //mSwipeRefreshLayout!!.isRefreshing = false
            }
        })
    }

    /*
    *  Receiver which get Filters
    */
    private val mReceiverFilterResult: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            type = intent.getStringExtra(resources.getString(R.string.filterType)) ?: ""
            status = intent.getStringExtra(resources.getString(R.string.filterStatus)) ?: ""
            customerName = intent.getStringExtra(resources.getString(R.string.filterCustomerName)) ?: ""
            city = intent.getStringExtra(resources.getString(R.string.filterCity)) ?: ""
            area = intent.getStringExtra(resources.getString(R.string.filterArea)) ?: ""
            zipcode = intent.getStringExtra(resources.getString(R.string.filterZipcode)) ?: ""
            orderListApi(type, status, customerName, city, area, zipcode, mysort_value)
        }
    }
}
