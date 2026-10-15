package pharmlane.com.PharmLaneStore.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.DemoActivity
import pharmlane.com.PharmLaneStore.activities.OffersMadeActivity
import pharmlane.com.PharmLaneStore.activities.PendingOrdersActivity
import pharmlane.com.PharmLaneStore.adapters.OffersMadeAdapter
import pharmlane.com.PharmLaneStore.adapters.OrdersAdapter
import pharmlane.com.PharmLaneStore.adapters.PendingAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.GetStoreList
import pharmlane.com.PharmLaneStore.response.GetStoreListResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


class DashBoardFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var ordersAdapter: OrdersAdapter? = null
    private var pendingAdapter: PendingAdapter? = null
    private var offersMadeAdapter: OffersMadeAdapter? = null

    private var progressbar: ProgressBar? = null

    private var recycleDirectorder: RecyclerView? = null
    private var recyclePendingorder: RecyclerView? = null
    private var recycleOffersMade: RecyclerView? = null

    private var txt_direct_ordr_viewall: AppCompatTextView? = null
    private var txt_pending_ordr_viewall: AppCompatTextView? = null
    private var txt_ofers_made_viewall: AppCompatTextView? = null
    private var txt_launch_demo: AppCompatTextView ? = null

    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var directViewall = ""
    private var pendingViewall = ""
    private var offersMadeViewall = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recycleDirectorder = view.findViewById(R.id.recycl_directorders) as RecyclerView?
        recyclePendingorder = view.findViewById(R.id.recycl_pendingorders) as RecyclerView?
        recycleOffersMade = view.findViewById(R.id.recycl_offersmade) as RecyclerView?
        mSwipeRefreshLayout = view.findViewById(R.id.simpleSwipeRefreshLayout)
        progressbar = view.findViewById(R.id.progressBar) as ProgressBar?

        txt_direct_ordr_viewall =
                view.findViewById(R.id.txt_direct_ordr_viewall) as AppCompatTextView?
        txt_pending_ordr_viewall =
                view.findViewById(R.id.txt_pending_ordr_viewall) as AppCompatTextView?
        txt_ofers_made_viewall =
                view.findViewById(R.id.txt_ofers_made_viewall) as AppCompatTextView?
        txt_launch_demo = view.findViewById(R.id.txt_launch_demo) as AppCompatTextView?

        mSwipeRefreshLayout?.setOnRefreshListener(this)
        mSwipeRefreshLayout?.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

        mSwipeRefreshLayout?.post(Runnable {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout!!.isRefreshing = true
            }
            orderListApi()
        })
        txt_direct_ordr_viewall!!.setOnClickListener(this)
        txt_pending_ordr_viewall!!.setOnClickListener(this)
        txt_ofers_made_viewall!!.setOnClickListener(this)
        txt_launch_demo!!.setOnClickListener (this)

        recycleDirectorder!!.setHasFixedSize(true)
        recycleDirectorder!!.layoutManager =
                LinearLayoutManager(activity)
        ordersAdapter = OrdersAdapter(requireActivity(), directOrderdata)
        recycleDirectorder!!.adapter = ordersAdapter


        recyclePendingorder!!.setHasFixedSize(true)
        recyclePendingorder!!.layoutManager =
                LinearLayoutManager(activity)
        pendingAdapter = PendingAdapter(requireActivity(), pendingOrderdata)
        recyclePendingorder!!.adapter = pendingAdapter

        recycleOffersMade!!.setHasFixedSize(true)
        recycleOffersMade!!.layoutManager = LinearLayoutManager(activity)
        offersMadeAdapter = OffersMadeAdapter(requireActivity(), offersmadeOrderdata)
        recycleOffersMade!!.adapter = offersMadeAdapter

        orderListApi()

        // Inflate the layout for this fragment
        return view
    }

    companion object {

        var pendingOrderdata: List<GetStoreListResponse.PendingOrder> = arrayListOf()
        var directOrderdata: List<GetStoreListResponse.DirectOrder> = arrayListOf()
        var offersmadeOrderdata: List<GetStoreListResponse.OffersMade> = arrayListOf()

        @JvmStatic
        fun newInstance() =
                DashBoardFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txt_direct_ordr_viewall -> {
                startActivity(
                        Intent(context, PendingOrdersActivity::class.java)
                                .putExtra("direct", directViewall)
                )
                //finish()
            }
            R.id.txt_pending_ordr_viewall -> {
                startActivity(
                        Intent(context, PendingOrdersActivity::class.java)
                                .putExtra("pending", pendingViewall)
                )
            }
            R.id.txt_ofers_made_viewall -> {
                startActivity(
                        Intent(context, OffersMadeActivity::class.java)
                                .putExtra("offersmade", offersMadeViewall)
                )
            }
            R.id.txt_launch_demo -> {
                startActivity(
                    Intent(
                        context,
                        DemoActivity::class.java
                    )
                )
            }
        }
    }

    fun orderListApi() {

        progressbar!!.visibility = View.VISIBLE
        val getStoreList = GetStoreList()
        getStoreList.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getstoreorderlist(getStoreList)

        call.enqueue(object : Callback<GetStoreListResponse> {
            override fun onResponse(
                    call: Call<GetStoreListResponse>,
                    response: retrofit2.Response<GetStoreListResponse>
            ) {
                progressbar!!.visibility = View.GONE
                if (response.isSuccessful) {
                    mSwipeRefreshLayout!!.isRefreshing = false
                    directOrderdata = arrayListOf()
                    offersmadeOrderdata = arrayListOf()
                    pendingOrderdata = arrayListOf()

                    directOrderdata = response.body()!!.data!!.directOrder!!
                    offersmadeOrderdata = response.body()!!.data!!.offersMade!!
                    pendingOrderdata = response.body()!!.data!!.pendingOrder!!

                    if (response.body()!!.data!!.directOrder!!.size > 0) {

                        if (txt_direct_orders == null)
                            return
                        txt_direct_orders.visibility = View.VISIBLE
                        txt_direct_ordr_viewall!!.visibility = View.VISIBLE
                        recycl_directorders.visibility = View.VISIBLE

                        directViewall =
                                response.body()!!.data!!.directOrder!!.get(0).statusViewAll.toString()
                    }
                    if (response.body()!!.data!!.pendingOrder!!.size > 0) {
                        if (recycl_pendingorders == null)
                            return
                        recycl_pendingorders.visibility = View.VISIBLE
                        txt_pending_ordr_viewall!!.visibility = View.VISIBLE
                        txt_pending_orders.visibility = View.VISIBLE

                        pendingViewall =
                                response.body()!!.data!!.pendingOrder!!.get(0).statusViewAll.toString()
                    }
                    if (response.body()!!.data!!.offersMade!!.size > 0) {
                        if (recycl_offersmade == null)
                            return
                        recycl_offersmade.visibility = View.VISIBLE
                        txt_ofers_made_viewall!!.visibility = View.VISIBLE
                        txt_offers_made.visibility = View.VISIBLE
                        Log.d("TAG", "------11111---" + response.body()!!.data!!.offersMade!!.get(0).offerFinalAmount)
                        offersMadeViewall =
                                response.body()!!.data!!.offersMade!!.get(0).statusViewAll.toString()
                    }

                    if (response.body()!!.data!!.pendingOrder!!.size == 0 &&
                            response.body()!!.data!!.offersMade!!.size == 0 &&
                            response.body()!!.data!!.directOrder!!.size == 0) {

                        constrain_nothing_here.visibility = View.VISIBLE
                        if (constraint == null)
                            return
                        constraint.visibility = View.GONE
                    } else {
                        if (constrain_nothing_here == null)
                            return
                        constrain_nothing_here.visibility = View.GONE
                        constraint.visibility = View.VISIBLE
                    }
                    ordersAdapter = OrdersAdapter(activity!!, directOrderdata)
                    recycleDirectorder!!.adapter = ordersAdapter
                    ordersAdapter!!.notifyDataSetChanged()

                    pendingAdapter = PendingAdapter(activity!!, pendingOrderdata)
                    recyclePendingorder!!.adapter = pendingAdapter
                    pendingAdapter!!.notifyDataSetChanged()

                    offersMadeAdapter = OffersMadeAdapter(activity!!, offersmadeOrderdata)
                    recycleOffersMade!!.adapter = offersMadeAdapter
                    offersMadeAdapter!!.notifyDataSetChanged()

                } else {

                    mSwipeRefreshLayout!!.isRefreshing = false
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                activity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }


            override fun onFailure(call: Call<GetStoreListResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressbar!!.visibility = View.GONE
                mSwipeRefreshLayout!!.isRefreshing = false

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