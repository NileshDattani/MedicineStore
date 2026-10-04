package pharmlane.com.PharmLaneStore.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.activities.MakeOfferActivity.Companion.bmp
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderDetail
import pharmlane.com.PharmLaneStore.response.OrderDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.Utils.Companion.changeBitmapColor
import pharmlane.com.PharmLaneStore.utills.afterMeasured
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_custom_order_detail.*
import kotlinx.android.synthetic.main.activity_make_offer.*
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.activity_order_detail.btn_make_offer
import kotlinx.android.synthetic.main.activity_order_detail.btn_offer
import kotlinx.android.synthetic.main.activity_order_detail.btn_offer_1
import kotlinx.android.synthetic.main.activity_order_detail.con_bottom
import kotlinx.android.synthetic.main.activity_order_detail.con_toolbar
import kotlinx.android.synthetic.main.activity_order_detail.constrain_customer
import kotlinx.android.synthetic.main.activity_order_detail.constrain_delivry_time_out
import kotlinx.android.synthetic.main.activity_order_detail.constraint_top
import kotlinx.android.synthetic.main.activity_order_detail.img_back
import kotlinx.android.synthetic.main.activity_order_detail.img_menu
import kotlinx.android.synthetic.main.activity_order_detail.progressBar
import kotlinx.android.synthetic.main.activity_order_detail.textView51
import kotlinx.android.synthetic.main.activity_order_detail.textView52
import kotlinx.android.synthetic.main.activity_order_detail.txt_buy_pref
import kotlinx.android.synthetic.main.activity_order_detail.txt_cusmrname
import kotlinx.android.synthetic.main.activity_order_detail.txt_date
import kotlinx.android.synthetic.main.activity_order_detail.txt_del_date
import kotlinx.android.synthetic.main.activity_order_detail.txt_del_pref_add
import kotlinx.android.synthetic.main.activity_order_detail.txt_del_pref_type
import kotlinx.android.synthetic.main.activity_order_detail.txt_del_time
import kotlinx.android.synthetic.main.activity_order_detail.txt_geo_location
import kotlinx.android.synthetic.main.activity_order_detail.txt_order_dtntime
import kotlinx.android.synthetic.main.activity_order_detail.txt_order_for
import kotlinx.android.synthetic.main.activity_order_detail.txt_order_id
import kotlinx.android.synthetic.main.activity_order_detail.txt_status_offr
import kotlinx.android.synthetic.main.activity_view_all_products.*
import kotlinx.android.synthetic.main.activity_view_photo_id.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class OrderDetailActivityFromReview : AppCompatActivity(), View.OnClickListener {

    var orderId = ""
    var offerId = ""
    var timer = ""
    var direct = ""
    var latitude = ""
    var longitude = ""
    var imgAttchmentWidth = 0
    var imgAttchmentHeight = 0
    var moreHour = "0"
    var customOrderdataReview: OrderDetailResponse.Data? = null

    var viewImgWidth = 0
    var viewImgHeight = 0
    var iv: ImageView? = null
    var imgXPosi = 0
    var imgYPosi = 0
    var hashMap: HashMap<Int, Int> = HashMap<Int, Int>()

    lateinit var img_add_img: AppCompatImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_order_detail)

        img_add_img = findViewById(R.id.img_add_img)


        val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        img_back.setOnClickListener(this)
        img_menu.setOnClickListener(this)
        btn_offer.setOnClickListener(this)
        btn_offer_1.setOnClickListener(this)
        btn_make_offer.setOnClickListener(this)
        txt_status_offr.setOnClickListener(this)
        txt_geo_location.setOnClickListener(this)
        img_prescri.setOnClickListener(this)
        img_add_img.setOnClickListener(this)

        progressBar!!.visibility = View.GONE

        if (intent != null) {
            if (intent.getStringExtra("reviewfinaloffer") != null) {
                reviewFinalLayout()
            }
            if (intent.getStringExtra("datentime") != null) {
                datenTime = intent.getStringExtra("datentime") ?: ""
            }
            if (intent.getStringExtra("offerId") != null) {
                offerId = intent.getStringExtra("offerId") ?: ""
            }
            if (intent.getStringExtra("moreHour") != null) {
                moreHour = intent.getStringExtra("moreHour") ?: ""

                if (moreHour.equals("1")) {
                    constraint_top.visibility = View.GONE
                }
            }
            if (intent.getStringExtra("direct") != null && intent.getStringExtra("direct")
                            .equals("1")
            ) {
                direct = "1"
            }
            if (intent.getStringExtra("reviewoffer") != null) {
                viewOffer()
            }
            if (intent.getStringExtra("timer") != null) {
                timer = intent.getStringExtra("timer") ?: ""
            } else {
                timer = customOrderdata?.createdDate!!
            }
            if (intent.getStringExtra("vieworder") != null) {
                constraint_top.visibility = View.GONE
                con_bottom.visibility = View.GONE
            }
            if (intent.getStringExtra("orderId") != null) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }
        }

        orderDetailApi()
    }

    fun makeOffer() {
        btn_make_offer.visibility = VISIBLE
    }

    fun viewOffer() {
        btn_make_offer.visibility = View.GONE
        constraint_top.visibility = View.GONE

        txt_status_offr.visibility = VISIBLE
        btn_offer.visibility = VISIBLE
    }

    fun reviewFinalLayout() {
        btn_make_offer.visibility = View.GONE

        txt_status_offr.visibility = VISIBLE
        btn_offer.visibility = VISIBLE

        txt_status_offr.setText("5th in Q")
        txt_status_offr.setTextColor(ContextCompat.getColor(this, R.color.grey_btn))

        btn_offer.setText(resources.getString(R.string.review_offer))
    }

    fun ForDeliveryTimeOut() {
        btn_make_offer.visibility = View.GONE
        constraint_top.visibility = View.GONE

        txt_status_offr.visibility = View.GONE
        btn_offer.visibility = View.GONE

        constrain_delivry_time_out.visibility = VISIBLE
        btn_offer_1.visibility = VISIBLE
    }

    fun ForDeliveryTimeOutOnlyTxt() {
        btn_make_offer.visibility = View.GONE
        constraint_top.visibility = View.GONE

        txt_status_offr.visibility = View.GONE
        btn_offer.visibility = View.GONE

        constrain_delivry_time_out.visibility = VISIBLE
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.txt_geo_location -> {
//                val currentlat= MainActivity.current_latitude
//                val currentlong= MainActivity.current_longitude

                val currentlat = AppConstant.getPreferenceText(AppConstant.STORELATITUDE)
                val currentlong = AppConstant.getPreferenceText(AppConstant.STORELONGITUDE)

                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentlat + "," + currentlong + "&daddr=" + latitude + "," + longitude))
                startActivity(intent)
            }

            R.id.img_back -> {

                finish()
            }

            R.id.img_add_img -> {

                startActivity(Intent(this@OrderDetailActivityFromReview, ViewPhotoIdActivity::class.java)
                        .putExtra("photo_id", customOrderdataReview?.photoId))

            }
            R.id.img_menu -> {

                //   finish()
            }
            R.id.img_prescri -> {

                startActivity(
                        Intent(this@OrderDetailActivityFromReview, ViewAllProductsOrderActivity::class.java)
                                .putExtra("youroffer", "1")
                                .putExtra("custom", "")
                                .putExtra("orderId", orderId)
                                .putExtra("offerId", offerId)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_offer -> {

                if (intent.getStringExtra("reviewfinaloffer") != null) {
                    startActivity(
                            Intent(this@OrderDetailActivityFromReview, CustomViewOfferActivity::class.java)
                                    .putExtra("reviewoffer", "1")
                                    .putExtra("orderId", orderId)
                    )
                    //  finish()
                } else {
                    startActivity(
                            Intent(this@OrderDetailActivityFromReview, CustomViewOfferActivity::class.java)
                                    .putExtra("orderId", orderId)
                    )
                    //  finish()
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_offer_1 -> {

                startActivity(
                        Intent(this@OrderDetailActivityFromReview, CustomViewOfferActivity::class.java)
                                .putExtra("orderId", orderId)
                )
                //  finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_make_offer -> {

                Handler().postDelayed({
                    progressBar.visibility = VISIBLE
                    startActivity(Intent(this@OrderDetailActivityFromReview, MakeOfferActivity::class.java)
                            .putExtra("direct", direct)
                            .putExtra("orderId", orderId))
                    //  finish()


                }, 4000)

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                progressBar.visibility = View.GONE
            }
            R.id.txt_status_offr -> {

                //   finish()
            }
        }
    }


    fun orderDetailApi() {

        progressBar!!.visibility = VISIBLE
        val orderDetail = OrderDetail()
        orderDetail.order_id = orderId
        orderDetail.offer_id = offerId
        orderDetail.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.orderdetail(orderDetail)

        call.enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(
                    call: Call<OrderDetailResponse>,
                    response: retrofit2.Response<OrderDetailResponse>
            ) {

                if (response.isSuccessful) {


                    customOrderdataReview = response.body()!!.data

                    txt_order_id.text = "ID: " + customOrderdataReview!!.orderNo
                    txt_order_dtntime.text = (customOrderdataReview?.createdDate!!)

                    if (customOrderdataReview!!.requestStatus.equals(">24 hrs", ignoreCase = true)) {
                        constraint_top.visibility = View.GONE
                    } else {
                        txt_date.text = timer
                    }

                    txt_cusmrname.text = customOrderdataReview!!.orderForName


                    /*   coverrtdate(
                               customOrderdataReview!!.deliveryDate.toString(),
                               customOrderdataReview!!.startTime,
                               customOrderdataReview!!.endTime
                       )
   */
                    txt_del_date.text = customOrderdataReview?.todateFromdate

                    txt_del_pref_type.text = customOrderdataReview!!.deliveryTypeName
                    if (customOrderdataReview!!.deliveryType.equals("0")) {
                        textView51.text = resources.getString(R.string.pickup_date)
                        textView52.text = resources.getString(R.string.pickup_time)
                        txt_del_pref_add.visibility = View.GONE
                        img_add_img.visibility = View.GONE

                    } else if (customOrderdataReview!!.deliveryType.equals("1")) {
                        txt_del_pref_type.text = customOrderdata!!.deliveryTypeName + " in "+customOrderdata!!.deliveryCity
                        val locaTion =
                                customOrderdataReview!!.blockNo + ", " + customOrderdataReview!!.buildingName
                        txt_del_pref_add.text = locaTion
                        txt_geo_location.visibility = VISIBLE
                        txt_geo_location.text = customOrderdataReview!!.location
                        latitude = customOrderdataReview?.deliveryLatitude.toString()
                        longitude = customOrderdataReview?.deliveryLongitude.toString()
                        if (!customOrderdataReview?.photoId.equals("")) {
                            img_add_img.visibility = VISIBLE
                        } else {
                            img_add_img.visibility = View.GONE
                        }

                    } else {
                        txt_del_pref_type.text = customOrderdata!!.deliveryTypeName + " in "+customOrderdata!!.deliveryCity
                        val locaTion =
                                customOrderdataReview!!.blockNo + ", " + customOrderdataReview!!.buildingName + ", " + customOrderdataReview!!.street + ", " +
                                        customOrderdataReview!!.area + ", " + customOrderdataReview!!.landmark + ", " + customOrderdataReview!!.zipcode
                        txt_del_pref_add.text = locaTion
                        img_add_img.visibility = View.GONE
                    }

                    if (customOrderdataReview?.customerName!!.length > 0) {
                        constrain_customer.visibility = VISIBLE
                        txt_cusmrname.text = customOrderdataReview!!.customerName

                    } else {
                        constrain_customer.visibility = View.GONE
                    }
                    // txt_del_pref_type.text = customOrderdataReview!!.deliveryTypeName
                    /*if (customOrderdataReview?.orderPreferenceName.equals("")) {
                        txt_buy_pref.text = customOrderdataReview?.buyingPreferenceName
                    } else {
                        txt_buy_pref.text = customOrderdataReview?.orderPreferenceName
                    }*/

                    if(!TextUtils.isEmpty(customOrderdata!!.buyingPreferenceNameNew))
                        txt_buy_pref.text = customOrderdata!!.buyingPreferenceNameNew


                    txt_order_for.text = customOrderdataReview!!.orderForName
                    progressBar.visibility = VISIBLE



                    if (customOrderdata?.prescriptionImage != null) {
                        if (!customOrderdata?.prescriptionImage.equals("")) {

                            try {
                                Glide.with(this@OrderDetailActivityFromReview)
                                        .load(customOrderdataReview!!.prescriptionImage)
                                        .listener(object : RequestListener<Drawable?> {
                                            override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                                                progressBar!!.visibility = View.GONE
                                                return false
                                            }

                                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                                progressBar!!.visibility = View.GONE
                                                return false
                                            }
                                        })
                                        .into(img_prescri)
                            }catch (e:Exception){
                                Log.e("Priscridfnsj","Erroor   "+e)
                            }

                            LoadImageWithDots(customOrderdata?.prescriptionImage!!).execute()

                        } else {
                            rel_image_view.visibility = View.GONE
                        }
                    }


                } else {


                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@OrderDetailActivityFromReview,
                                "" + jObjError.getString("message"),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@OrderDetailActivityFromReview, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();VISIBLE
                progressBar!!.visibility = View.GONE


            }
        })
    }


    @SuppressLint("StaticFieldLeak")
    inner class LoadImageWithDots(var imageSrc: String) : AsyncTask<Bitmap?, Bitmap?, Bitmap>() {


        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Bitmap) {
            progressBar.visibility = View.GONE
            SetBits(result)

        }

        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            return urlToBitmap(imageSrc)
        }
    }

    fun urlToBitmap(prescriptionImage: String): Bitmap {
        val url = URL(prescriptionImage)
        val connection: HttpURLConnection
        connection = url.openConnection() as HttpURLConnection
        connection.setDoInput(true)
        connection.connect()
        val input = connection.getInputStream()
        val myBitmap = BitmapFactory.decodeStream(input)
        return myBitmap
    }

    fun SetBits(result: Bitmap) {
        if (result != null) {
            bmp = scaleBitmap(result)
            img_prescri!!.setImageDrawable(BitmapDrawable(resources, bmp))
            rel_image_view.afterMeasured {
                Log.e("HelloTags", "hii")
                viewImgWidth = rel_image_view.width
                viewImgHeight = rel_image_view.height
                if (viewImgWidth != 0) {
                    addTags()
                }
            }
        } else {
            rel_image_view.visibility = View.GONE
        }
    }


    fun addTags() {

        for (i in 0 until customOrderdata!!.products!!.size) {
            iv = ImageView(this)


            val bm: Bitmap?

            bm = BitmapFactory.decodeResource(resources, R.drawable.pin)

            if (bm == null) {
                Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show()
            } else {
                iv!!.setImageBitmap(bm)

                val params = RelativeLayout.LayoutParams(bm.width, bm.height)

                imgXPosi =
                        customOrderdata!!.products!!.get(i).productXPosition!!.toFloat().toInt()
                imgYPosi =
                        customOrderdata!!.products!!.get(i).productYPosition!!.toFloat().toInt()


                params.leftMargin = (viewImgWidth * imgXPosi) / imgAttchmentWidth
                params.topMargin = (viewImgHeight * imgYPosi) / imgAttchmentHeight

                hashMap.put(
                        customOrderdata!!.products!!.get(i).productXPosition!!.toFloat().roundToInt(),
                        customOrderdata!!.products!!.get(i).productYPosition!!.toFloat().roundToInt()
                )

                rel_image_view.addView(iv, params)

                // for (i in 0 until childcount) {
                val v = rel_image_view.getChildAt(i)
                changeBitmapColor(
                        bm,
                        iv!!,
                        Color.parseColor("#" + customOrderdata!!.products!!.get(i).color!!)
                )
                // }

//                iv!!.setOnClickListener {
//
//                    if (direct.equals("1")) {
//                        showdialog(
//                                Color.parseColor("#" + customOrderdata!!.products!!.get(i).color!!),
//                                i
//                        )
//                    } else {
//                        showdialogOne(
//                                Color.parseColor("#" + customOrderdata!!.products!!.get(i).color!!),
//                                i
//                        )
//                    }
//
//                }
            }
        }

        progressBar.visibility = View.GONE
    }


    private fun scaleBitmap(bmp: Bitmap): Bitmap {
        var bm = bmp
        var width = bm.width
        var height = bm.height
        imgAttchmentWidth = width
        imgAttchmentHeight = height
        Log.v("Pictures", "Width and height are $width--$height")
        Log.v("Pictures", "WidthHeight" + con_toolbar.height)

        return bm
    }

    private fun coverrtdate(
            from_date: String,
            startTime: String?,
            endTime: String?
    ) {
        val dateFormat1 = SimpleDateFormat("HH:mm:ss", Locale.US)

        if (!startTime.equals("") || !endTime.equals("")) {
            txt_del_time.text = AppConstant.convertFromtoTime(startTime.toString(), endTime.toString())


        }


        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        if (!from_date.equals("")) {
            val createdConvertedDate = dateFormat.parse(from_date)
            txt_del_date.text =
                    SimpleDateFormat("dd MMM, yyyy",Locale.US).format(createdConvertedDate)
        }

    }

    companion object {
        var datenTime = ""
        var bitmap: Bitmap? = null
    }


}