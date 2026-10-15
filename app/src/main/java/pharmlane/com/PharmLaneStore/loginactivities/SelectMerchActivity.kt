package pharmlane.com.PharmLaneStore.loginactivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.MerchCategoryAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.loginactivities.StoreInfoActivity.Companion.merchCategoryArray
import pharmlane.com.PharmLaneStore.model.GetAllDropDown
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.GetAllDropdownResponse
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_select_merch.*
import kotlinx.android.synthetic.main.activity_select_merch.btn_next
import kotlinx.android.synthetic.main.activity_select_merch.img_back
import kotlinx.android.synthetic.main.activity_select_merch.textView12
import kotlinx.android.synthetic.main.activity_select_merch.txt_title
import kotlinx.android.synthetic.main.activity_store_info.*
import kotlinx.android.synthetic.main.activity_store_profile.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SelectMerchActivity : AppCompatActivity(), View.OnClickListener,
        MerchCategoryAdapter.ArraylistOfCategory {


    private var recyclerView: RecyclerView? = null
    private var arrMerchList = ArrayList<String>()
    private var arrMList = ArrayList<String>()
    private var merchCategoryAdapter: MerchCategoryAdapter? = null
    private var merchCategoryList = ArrayList<String>()
    private var merchCategoryIdList = ArrayList<String>()

    var merchCatgry = ""
    var message = ""

    private var edtMerch: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_select_merch)

        val bundle = intent.extras
        message = bundle?.getString("selectedCat")!!

        if ((AppConstant.getPreferenceText(AppConstant.MERCHANDISE)).equals("edt")) {
            textView12.visibility = View.GONE
            txt_title.text = resources.getString(R.string.edt_merch_categry)
            btn_next.text = resources.getString(R.string.save)

            getdropdownApi()
        }

        for (i in 0 until merchCategoryArray.size) {
            merchCategoryList.add(merchCategoryArray[i].name!!)
            merchCategoryIdList.add(merchCategoryArray[i].id!!)
        }




        recyclerView = findViewById(R.id.recycl_merch_category)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager =
                LinearLayoutManager(this) as RecyclerView.LayoutManager?
        merchCategoryAdapter = MerchCategoryAdapter(this, merchCategoryArray, this,message)
        recyclerView!!.adapter = merchCategoryAdapter

        btn_next.setOnClickListener(this)
        img_back.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.btn_next -> {

                if ((AppConstant.getPreferenceText(AppConstant.MERCHANDISE)).equals("edt")) {
                    update_storeDetail(merchCatgry)
                } else {
                    val returnIntent = Intent()
                    returnIntent.putExtra("list", arrMList)
                    returnIntent.putExtra("list_1", arrMerchList)
                    setResult(AppCompatActivity.RESULT_OK, returnIntent)
                    finish()
                }

            }
        }
    }

    override fun add(position: String, position1: String) {

        if(!arrMList.contains(position1)){
            arrMerchList.add(position)
            arrMList.add(position1)
            merchCatgry = Arrays.toString(arrMerchList.toArray()).replace('[', ' ').replace(
                    ']',
                    ' '
            )
        }


    }

    override fun remove(position: String, position1: String) {

        if(arrMList.contains(position1)){
            arrMerchList.remove(position)
            arrMList.remove(position1)
            merchCatgry = Arrays.toString(arrMerchList.toArray()).replace('[', ' ').replace(
                ']',
                ' '
            )
        }



    }

    private fun getdropdownApi() {
        //   progressBar_merch.visibility = View.VISIBLE
        val apiService = AppConstant.getClient()!!.create(RetrofitInterface::class.java)
        val getAllDropDown = GetAllDropDown()
        getAllDropDown.order_type = ""
        val call = apiService.getalldropdown(getAllDropDown)

        call.enqueue(object : Callback<GetAllDropdownResponse> {
            override fun onResponse(
                    call: Call<GetAllDropdownResponse>,
                    response: Response<GetAllDropdownResponse>
            ) {

                // progressBar_merch.visibility = View.GONE
                if (response.isSuccessful()) {

                    merchCategoryArray = arrayListOf()
                    StoreInfoActivity.storeTypeArray = arrayListOf()
                    StoreInfoActivity.paymentArray = arrayListOf()

                    merchCategoryArray = response.body()!!.data!!.merchandiseCategory!!

                    merchCategoryAdapter = MerchCategoryAdapter(this@SelectMerchActivity, merchCategoryArray, this@SelectMerchActivity,message)
                    recyclerView!!.adapter = merchCategoryAdapter

                }
            }

            override fun onFailure(call: Call<GetAllDropdownResponse>, t: Throwable) {
                // progressBar_merch.visibility = View.GONE
            }
        })
    }

    private fun update_storeDetail(merchCatgry: String) {
        progressBar_merch.visibility = View.VISIBLE
        val register = Register()
        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        register.merchandise_category = merchCatgry
        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.update_store_detail(register)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                progressBar_merch.visibility = View.GONE
                if (response.isSuccessful) {
//                    startActivity(
//                            Intent(this@SelectMerchActivity, StoreProfileActivity::class.java))
                    finish()

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@SelectMerchActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@SelectMerchActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }

            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar_merch.visibility = View.GONE
            }
        })
    }

}