package pharmlane.com.PharmLaneStore.activities

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.MakeBuyerReview
import pharmlane.com.PharmLaneStore.response.MakeBuyerReviewResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_rate_customer.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class RateCustomerActivity : AppCompatActivity(), View.OnClickListener {

    private var offerid=""
    private var userid=""
    private var rateStar=""
    private val REQUEST_CODE_ASK_PERMISSIONS_CALL_PHONE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_rate_customer)



        if (intent != null) {
            if (intent.getStringExtra("custmrId") != null) {
                userid = intent.getStringExtra("custmrId") ?: ""
            }
            if (intent.getStringExtra("offerId") != null) {
                offerid = intent.getStringExtra("offerId") ?: ""
            }
        }

        txt_customer_name.text = customOrderdata!!.customerName

        img_bad.setOnClickListener(this)
        img_meh.setOnClickListener(this)
        img_nice.setOnClickListener(this)
        img_good.setOnClickListener(this)
        img_great.setOnClickListener(this)
        btn_submit_review.setOnClickListener(this)
        txt_view_ordr.setOnClickListener(this)
        txt_call.setOnClickListener(this)
        img_back.setOnClickListener(this)
    }
    private fun setBadEmojiLayout() {
        img_bad.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_colorbad
            )
        )
        img_meh.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_meh
            )
        )
        img_nice.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_nice
            )
        )
        img_good.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_good
            )
        )
        img_great.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_great
            )
        )

        txt_bad.setTextColor(ContextCompat.getColor(this, R.color.black_txt))
        txt_meh.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_nice.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_good.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_great.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
    }

    private fun setMehEmojiLayout() {
        img_bad.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_bad
            )
        )
        img_meh.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_colormeh
            )
        )
        img_nice.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_nice
            )
        )
        img_good.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_good
            )
        )
        img_great.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_great
            )
        )

        txt_bad.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_meh.setTextColor(ContextCompat.getColor(this, R.color.black_txt))
        txt_nice.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_good.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_great.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
    }

    private fun setNiceEmojiLayout() {
        img_bad.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_bad
            )
        )
        img_meh.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_meh
            )
        )
        img_nice.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_colornice
            )
        )
        img_good.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_good
            )
        )
        img_great.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_great
            )
        )

        txt_bad.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_meh.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_nice.setTextColor(ContextCompat.getColor(this, R.color.black_txt))
        txt_good.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_great.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
    }

    private fun setGoodEmojiLayout() {
        img_bad.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_bad
            )
        )
        img_meh.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_meh
            )
        )
        img_nice.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_nice
            )
        )
        img_good.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_colorgood
            )
        )
        img_great.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_great
            )
        )

        txt_bad.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_meh.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_nice.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_good.setTextColor(ContextCompat.getColor(this, R.color.black_txt))
        txt_great.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
    }

    private fun setGreatEmojiLayout() {
        img_bad.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_bad
            )
        )
        img_meh.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_meh
            )
        )
        img_nice.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_nice
            )
        )
        img_good.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_good
            )
        )
        img_great.setImageDrawable(
            ContextCompat.getDrawable(
                this@RateCustomerActivity,
                R.drawable.rate_customer_colorgreat
            )
        )

        txt_bad.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_meh.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_nice.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_good.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
        txt_great.setTextColor(ContextCompat.getColor(this, R.color.black_txt))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.img_bad -> {
                rateStar= "1"
                setBadEmojiLayout()
            }
            R.id.img_meh -> {
                rateStar= "2"
                setMehEmojiLayout()
            }
            R.id.img_nice -> {
                rateStar= "3"
                setNiceEmojiLayout()
            }
            R.id.img_good -> {
                rateStar= "4"
                setGoodEmojiLayout()
            }
            R.id.img_great -> {
                rateStar= "5"
                setGreatEmojiLayout()
            }
            R.id.btn_submit_review -> {

                val review = edt_box_review.text.toString().trim()

                if (rateStar.equals("")){
                    Toast.makeText(this,"Rate Customer", Toast.LENGTH_LONG)
                        .show()
                } else{
                    rateStoreApi(review)
                }

               /* startActivity(Intent(this@RateCustomerActivity, MyRatingActivity::class.java)
                    .putExtra("offerId", offerid)
                    .putExtra("storeId", storeid))
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
            }
            R.id.txt_view_ordr -> {
              /*  startActivity(
                    Intent(this@RateCustomerActivity, FinalOrderActivity::class.java)
                        .putExtra("vieworder", "1")
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/



                Log.e("HelloOrderNull", "IsBlank   " + customOrderdata!!.orderTypeName)

                if (customOrderdata!!.orderTypeName.equals("Custom", true)) {
                    startActivity(
                            Intent(this@RateCustomerActivity, CustomOrderDetailActivityFromReview::class.java)
                                    .putExtra("orderId", customOrderdata!!.orderId)
                                    .putExtra("vieworder", "1")

                    )
                } else {
                    startActivity(
                            Intent(this@RateCustomerActivity, OrderDetailActivityFromReview::class.java)
                                    .putExtra("orderId", customOrderdata!!.orderId)
                                    .putExtra("vieworder", "1")

                    )
                }
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.txt_call -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    val hasPermission = ContextCompat.checkSelfPermission(this@RateCustomerActivity, Manifest.permission.CALL_PHONE)
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@RateCustomerActivity,
                                        Manifest.permission.CALL_PHONE)) {
                            // Display UI and wait for user interaction
                            getPermissionInfoDialog(this@RateCustomerActivity.getString(R.string.call_phone_permission), this@RateCustomerActivity)?.show()
                        } else {
                            ActivityCompat.requestPermissions(this@RateCustomerActivity, arrayOf(Manifest.permission.CALL_PHONE),
                                    REQUEST_CODE_ASK_PERMISSIONS_CALL_PHONE)
                        }
                        return
                    }
                }

                val builder = AlertDialog.Builder(this@RateCustomerActivity, R.style.AppCompatAlertDialogStyle)
                builder.setTitle(R.string.confirm_call)
                builder.setIcon(R.mipmap.ic_launcher)

                builder.setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel: "+AppConstant.getPreferenceText(AppConstant.BUYER_PHONE))
                    startActivity(callIntent)
                    //Utils.psLog("OK clicked.")
                })
                builder.show()
            }
        }
    }

    fun getPermissionInfoDialog(message: String?, context: Activity?): android.app.AlertDialog.Builder? {
        val alertDialog = android.app.AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.app_name)).setMessage(message)
        alertDialog.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            ActivityCompat.requestPermissions(context!!, arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CODE_ASK_PERMISSIONS_CALL_PHONE)
        }
        return alertDialog
    }
    fun rateStoreApi(review: String) {
        progressBar.visibility = View.VISIBLE
        val makeBuyerReview = MakeBuyerReview()
        makeBuyerReview.offer_id = offerid
        makeBuyerReview.user_id = userid
        makeBuyerReview.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        makeBuyerReview.rate_experience = rateStar
        makeBuyerReview.review =review

        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.makebuyerreview(makeBuyerReview)

        call.enqueue(object : Callback<MakeBuyerReviewResponse> {
            override fun onResponse(
                call: Call<MakeBuyerReviewResponse>,
                response: retrofit2.Response<MakeBuyerReviewResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    startActivity(Intent(this@RateCustomerActivity, MainActivity::class.java))
                   // startActivity(Intent(this@RateCustomerActivity, MainActivity::class.java).putExtra("offerId", offerid))
                    //finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    Toast.makeText(
                            this@RateCustomerActivity,
                            "Review Submitted" ,
                            Toast.LENGTH_SHORT
                    ).show()
                    btn_submit_review.isEnabled = false
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                       // Toast.makeText(this@RateCustomerActivity, AppConstant.serverErrorMessage(jObjError), Toast.LENGTH_SHORT).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@RateCustomerActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }

            override fun onFailure(call: Call<MakeBuyerReviewResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }
}
