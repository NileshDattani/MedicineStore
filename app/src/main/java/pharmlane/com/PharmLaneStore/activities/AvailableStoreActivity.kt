package pharmlane.com.PharmLaneStore.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.adapters.AvailableStoreAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.StoreList
import pharmlane.com.PharmLaneStore.response.StoreListResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_available_store.*
import org.json.JSONObject
import pharmlane.com.PharmLaneStore.R
import retrofit2.Call
import retrofit2.Callback

class AvailableStoreActivity : AppCompatActivity(), View.OnClickListener {

    private var availableStoreAdapter: AvailableStoreAdapter? = null
    private var recycler_store: RecyclerView? = null
    var storeListArray: List<StoreListResponse.Datum> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_available_store)


        img_back.setOnClickListener(this)

        recycler_store = findViewById(R.id.recycl_store)
        recycler_store!!.setHasFixedSize(true)
        recycler_store!!.layoutManager =
            LinearLayoutManager(this)

        textView26.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                // filter your list from your input
                filter(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })

        storeListApi()

    }

    fun filter(text: String) {
        val temp = arrayListOf<StoreListResponse.Datum>()
        for (d in storeListArray) {
            //or use .equal(text) with you want equal match
            //use .lowercase() for better matches
            if (d.name!!.contains(text, ignoreCase = true) || d.zipcode!!.contains(
                    text,
                    ignoreCase = true
                )
                || d.city!!.contains(text, ignoreCase = true) || d.area!!.contains(
                    text,
                    ignoreCase = true
                )
                || d.merchandiseCategory!!.contains(text, ignoreCase = true)
            ) {
                temp.add(d)
            }
        }
        //update recyclerview
        availableStoreAdapter!!.updateList(temp)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
        }
    }

    private fun storeListApi() {
        progressBar!!.visibility = View.VISIBLE
        val storeList = StoreList()
        storeList.user_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.storelist(storeList)

        call.enqueue(object : Callback<StoreListResponse> {
            override fun onResponse(
                call: Call<StoreListResponse>,
                response: retrofit2.Response<StoreListResponse>
            ) {
                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {

                    storeListArray = arrayListOf()

                    storeListArray = response.body()?.data!!

                    availableStoreAdapter =
                        AvailableStoreAdapter(this@AvailableStoreActivity, storeListArray)
                    recycler_store!!.adapter = availableStoreAdapter

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@AvailableStoreActivity,
                            "" + jObjError.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@AvailableStoreActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreListResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar!!.visibility = View.GONE
            }
        })
    }
}
