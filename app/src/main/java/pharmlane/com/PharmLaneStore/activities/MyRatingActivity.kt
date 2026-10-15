package pharmlane.com.PharmLaneStore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.StoreRating
import pharmlane.com.PharmLaneStore.response.StoreRatingResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_my_rating.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class MyRatingActivity : AppCompatActivity(), View.OnClickListener {

    var offerid=""
    var userid=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_my_rating)



        if (intent != null) {

            if (intent.getStringExtra("custmrId") != null) {
                userid = intent.getStringExtra("custmrId") ?: ""
            }
            if (intent.getStringExtra("offerId") != null) {
                offerid = intent.getStringExtra("offerId") ?: ""
            }
        }

        img_back.setOnClickListener(this)
      //  txt_customer_name.text = customOrderdata!!.customerName
        getRateStoreApi()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
        }
    }

    fun getRateStoreApi() {
        progressBar.visibility = View.VISIBLE
        val storeRating = StoreRating()
        storeRating.offer_id = offerid
        storeRating.user_id = userid
        storeRating.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getstorerating(storeRating)

        call.enqueue(object : Callback<StoreRatingResponse> {
            override fun onResponse(
                call: Call<StoreRatingResponse>,
                response: retrofit2.Response<StoreRatingResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {


                    txt_created_date.text=response.body()!!.data!!.createdDate
                    txt_review.text=response.body()!!.data!!.review
                    txt_customer_name.text=response.body()!!.data!!.custoName
                    setEmoji(response.body()!!.data!!.rateExperience)

                } else {
                    noDataFound()
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@MyRatingActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@MyRatingActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreRatingResponse>, t: Throwable) {

                // progressBarHandler.hide();
                progressBar.visibility = View.GONE


            }
        })
    }
    fun setEmoji(rateExperience: String?) {
        if (rateExperience.equals("1")) {
            img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyRatingActivity,
                    R.drawable.rate_customer_colorbad
                )
            )
            txt_first.text = resources.getString(R.string.bad)
        }else if (rateExperience.equals("2")) {
            img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyRatingActivity,
                    R.drawable.rate_customer_colormeh
                )
            )
            txt_first.text = resources.getString(R.string.meh)
        }else if (rateExperience.equals("3")) {
            img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyRatingActivity,
                    R.drawable.rate_customer_colornice
                )
            )
            txt_first.text = resources.getString(R.string.nice)
        }else if (rateExperience.equals("4")) {
            img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyRatingActivity,
                    R.drawable.rate_customer_colorgood
                )
            )
            txt_first.text = resources.getString(R.string.good)
        }else if (rateExperience.equals("5")) {
            img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MyRatingActivity,
                    R.drawable.rate_customer_colorgreat
                )
            )
            txt_first.text = resources.getString(R.string.great)
        }
    }
    fun noDataFound() {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        //builder.setTitle("App background color")

        // Display a message on alert dialog
        builder.setMessage("You did not get any review from buyer.")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok") { dialog, which ->
            // Do something when user press the positive button

            finish()
        }

        // Display a negative button on alert dialog


        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        // Display the alert dialog on app interface
        dialog.show()

    }
}
