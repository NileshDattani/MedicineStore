package pharmlane.com.PharmLaneStore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.ByAreaAdapter
import pharmlane.com.PharmLaneStore.interfaces.FiltersInterFace
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Filter
import pharmlane.com.PharmLaneStore.response.FilterResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_login.progressBar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ByAreaFragment(val filterListener: FiltersInterFace) : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var byAreaAdapter: ByAreaAdapter? = null
    private var areaList:List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.fragment_by_area, container, false)


        commonFilter()
        init(view)

        // Inflate the layout for this fragment
        return view
    }

//    companion object {
//
//        @JvmStatic
//        fun newInstance() =
//            ByAreaFragment().apply {
//                arguments = Bundle().apply {
//
//                }
//            }
//    }

    private fun init(view: View) {

        recyclerView = view.findViewById(R.id.recycl)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)


    }

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
                    areaList = ArrayList()
                    areaList= response.body()?.getData()?.area!!
                    byAreaAdapter = ByAreaAdapter(activity!!, areaList,filterListener)
                    recyclerView?.adapter = byAreaAdapter


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
