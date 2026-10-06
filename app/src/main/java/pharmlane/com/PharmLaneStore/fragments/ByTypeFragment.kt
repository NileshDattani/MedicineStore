package pharmlane.com.PharmLaneStore.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.ByTypeAdapter
import pharmlane.com.PharmLaneStore.interfaces.FiltersInterFace
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Filter
import pharmlane.com.PharmLaneStore.response.FilterResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback


class ByTypeFragment(val filterListener: FiltersInterFace) : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var byTypeAdapter: ByTypeAdapter? = null
    private var orderTypes: List<FilterResponse.OrderType> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_by_type, container, false)
        init(view)
        commonFilter()

        return view
    }


    @SuppressLint("WrongConstant")
    private fun init(view: View) {

        recyclerView = view.findViewById(R.id.recycl)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

    }

//    companion object {
//
//        @JvmStatic
//        fun newInstance() =
//            ByTypeFragment().apply {
//                arguments = Bundle().apply {
//
//                }
//            }
//    }

    fun commonFilter() {

        progressBar?.visibility = View.VISIBLE
        val filter = Filter()
        filter.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.common_filter(filter)

        call.enqueue(object : Callback<FilterResponse> {
            override fun onResponse(
                    call: Call<FilterResponse>,
                    response: retrofit2.Response<FilterResponse>
            ) {
                progressBar?.visibility = View.GONE
                if (response.isSuccessful) {

                    orderTypes = response.body()?.getData()?.orderType!!
                    byTypeAdapter = ByTypeAdapter(activity!!, orderTypes, filterListener)
                    recyclerView?.adapter = byTypeAdapter

                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                context,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }

                }
            }


            override fun onFailure(call: Call<FilterResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar?.visibility = View.GONE


            }
        })
    }

}
