package pharmlane.com.PharmLaneStore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_edit_business_type.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class EditBusinessTypeActivity : AppCompatActivity(), View.OnClickListener {


    var txt_type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_edit_business_type)

        img_back.setOnClickListener(this)

        txt_type=AppConstant.getPreferenceText(AppConstant.BUSINESS_TYPE)


        if(txt_type.equals("Retailer"))
        {
            txt_type="0"
//            update_storeDetail()
            radio_retailer.isChecked=true
        }
        else
        {
            txt_type="1"
//            update_storeDetail()
            radio_wholeseller.isChecked=true
        }



        radiogroup.setOnCheckedChangeListener { group, checkedId ->
            //group.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.textview_selector))
            var text = ""
            text += if (R.id.radio_retailer == checkedId) resources.getString(R.string.retailer) else resources.getString(
                    R.string.wholeseller
            )

            txt_type = text

            if(txt_type.equals("Retailer"))
            {
                txt_type="0"
                update_storeDetail()
            }
            else
            {
                txt_type="1"
                update_storeDetail()
            }

          //  Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()


        }



    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.img_back -> {

//                startActivity(Intent(this@EditBusinessTypeActivity, StoreProfileActivity::class.java))
                finish()

            }
        }

    }

    private fun update_storeDetail() {
        progressBar.visibility = View.VISIBLE
        val register = Register()
        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        register.type = txt_type
        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.update_store_detail(register)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

                    /*startActivity(
                            Intent(this@EditBusinessTypeActivity, StoreProfileActivity::class.java))
                    finish()*/

                    finish()

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@EditBusinessTypeActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@EditBusinessTypeActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }
}
