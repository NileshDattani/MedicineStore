package pharmlane.com.PharmLaneStore.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.adapters.CustomViewOfferAdapter
import pharmlane.com.PharmLaneStore.adapters.ProductsOrederedAdapter
import pharmlane.com.PharmLaneStore.adapters.ViewAllProductOrderAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.OrderDetail
import pharmlane.com.PharmLaneStore.response.OrderDetailResponse
import pharmlane.com.PharmLaneStore.response.ViewAllStoreOrderResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.Utils.Companion.changeBitmapColor
import pharmlane.com.PharmLaneStore.utills.afterMeasured
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.activity_view_all_products.*
import kotlinx.android.synthetic.main.activity_view_all_products.img_back
import kotlinx.android.synthetic.main.activity_view_all_products.progressBar
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class ViewAllProductsOrderActivity : AppCompatActivity(),View.OnClickListener {

    private var productsOrederedAdapter: ProductsOrederedAdapter? = null
    private var customViewOfferAdapter: CustomViewOfferAdapter? = null
    private var viewAllProductOrderAdapter: ViewAllProductOrderAdapter? = null
    private var recycl_view_order: RecyclerView? = null
    private var img_order: AppCompatImageView? = null
    var Orderdata: List<ViewAllStoreOrderResponse.Datum> = arrayListOf()

    var statusViewall = ""
    var isCustom = ""
    var isViewall = "1"

    var hashMap: HashMap<Int, Int> = HashMap<Int, Int>()

    var orderId = ""
    var offerId = ""
    var viewImgWidth = 0
    var viewImgHeight = 0
    var imgAttchmentWidth = 0
    var imgAttchmentHeight = 0
    var imgXPosi = 0
    var imgYPosi = 0
    var iv: ImageView? = null
    var customOrderdatas: OrderDetailResponse.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        setContentView(R.layout.activity_view_all_products_order)

        recycl_view_order = findViewById(R.id.recycl_view_order)
        img_order = findViewById(R.id.img_order) as AppCompatImageView
        img_order!!.setImageBitmap(MakeOfferActivity.bmp)



        img_back.setOnClickListener(this)
        img_point_view_all.setOnClickListener(this)

        if (intent != null) {
            if (intent.getStringExtra("custom") != null) {
                if (intent.getStringExtra("custom").equals("Custom", ignoreCase = true)) {
                    isCustom = "Custom"
                }
            }

            if (intent.getStringExtra("statusviewall") != null) {
                statusViewall = intent.getStringExtra("statusviewall") ?: ""
            }
            if (intent.getStringExtra("youroffer") != null) {
                txt.text = resources.getString(R.string.your_offer)

                if (intent.getStringExtra("orderId") != null) {
                    orderId = intent.getStringExtra("orderId") ?: ""
                }

                if (intent.getStringExtra("offerId") != null) {
                    offerId = intent.getStringExtra("offerId") ?: ""
                }

                recycl_view_order!!.setHasFixedSize(true)
                recycl_view_order!!.layoutManager =
                        LinearLayoutManager(this) as RecyclerView.LayoutManager?

                orderDetailApi()



            } else {
                Log.e("HELLOCLICKS","Adapater ELSE")
                recycl_view_order!!.setHasFixedSize(true)
                recycl_view_order!!.layoutManager =
                        LinearLayoutManager(this) as RecyclerView.LayoutManager?
                productsOrederedAdapter = ProductsOrederedAdapter(
                        this, customOrderdatas!!.products, isViewall)
                recycl_view_order!!.adapter = productsOrederedAdapter
            }
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

    @SuppressLint("StaticFieldLeak")
    inner class LoadImageWithDots(var imageSrc: String) : AsyncTask<Bitmap?, Bitmap?, Bitmap>() {


        override fun onPreExecute() {
            super.onPreExecute()
            progressBar_image_all.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Bitmap) {
            Log.e("HelloTags", "onPostExecute")
            progressBar_image_all.visibility = View.GONE
            SetBits(result)

        }

        override fun doInBackground(vararg params: Bitmap?): Bitmap? {
            val btmp = urlToBitmap(imageSrc)

            return btmp
        }
    }

    fun SetBits(result: Bitmap) {
        if (result != null) {
            MakeOfferActivity.bmp = scaleBitmap(result)
            img_order!!.setImageDrawable(BitmapDrawable(resources, MakeOfferActivity.bmp))

            Log.e("HelloTags", "SetBits")

            rel_image.afterMeasured {
                Log.e("HelloTags", "hii")
                viewImgWidth = rel_image.width
                viewImgHeight = rel_image.height
                if (viewImgWidth != 0) {
                    addTags()
                }
            }
        } else {
            rel_image.visibility = View.GONE
        }
    }

    private fun scaleBitmap(bmp: Bitmap): Bitmap {
        var bm = bmp
        var width = bm.width
        var height = bm.height
        imgAttchmentWidth = width
        imgAttchmentHeight = height

        val rectangle = Rect()
        val window = getWindow()
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top

        val maxWidth = Resources.getSystem().getDisplayMetrics().widthPixels
        val maxHeight = Resources.getSystem().getDisplayMetrics().heightPixels - con_toolbars.height - txt.height
        if (maxWidth <= width) {
            if (width > height) {
                // landscape
                val ratio = width.toFloat() / maxWidth
                width = maxWidth
                height = (height / ratio).toInt()
            } else if (height > width) {
                // portrait
                val ratio = height.toFloat() / maxHeight
                height = maxHeight
                width = (width / ratio).toInt()
            } else {
                // square
                height = maxHeight
                width = maxWidth
            }

            bm = Bitmap.createScaledBitmap(bm, width, height, true)
        }
        return bm
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }  R.id.img_point_view_all -> {
            if (MakeOfferActivity.bmp != null) {

/*

                    val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this@MakeOfferActivity)
                    val mView: View = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
                    val photoView: PhotoView = mView.findViewById(R.id.imageView)
                    photoView.setImageResource(R.drawable.nature)
                    mBuilder.setView(mView)
                    val mDialog: AlertDialog = mBuilder.create()
                    mDialog.show()
*/


                startActivity(
                        Intent(this, ImgActivity::class.java)
                )
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
        }
    }

    fun addTags() {

        for (i in 0 until customOrderdatas!!.products!!.size) {
            iv = ImageView(this)


            val bm: Bitmap?

            bm = BitmapFactory.decodeResource(resources, R.drawable.pin)

            if (bm == null) {
                Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show()
            } else {
                iv!!.setImageBitmap(bm)

                val params = RelativeLayout.LayoutParams(bm.width, bm.height)

                imgXPosi =
                        customOrderdatas!!.products!!.get(i).productXPosition!!.toFloat().toInt()
                imgYPosi =
                        customOrderdatas!!.products!!.get(i).productYPosition!!.toFloat().toInt()


                params.leftMargin = (viewImgWidth * imgXPosi) / imgAttchmentWidth
                params.topMargin = (viewImgHeight * imgYPosi) / imgAttchmentHeight

                hashMap.put(
                        customOrderdatas!!.products!!.get(i).productXPosition!!.toFloat().roundToInt(),
                        customOrderdatas!!.products!!.get(i).productYPosition!!.toFloat().roundToInt()
                )

                rel_image.addView(iv, params)

                // for (i in 0 until childcount) {
                val v = rel_image.getChildAt(i)
                changeBitmapColor(
                        bm,
                        iv!!,
                        Color.parseColor("#" + customOrderdatas!!.products!!.get(i).color!!)
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

//        progressBar_image_all.visibility = View.GONE
    }


    fun orderDetailApi() {

        progressBar!!.visibility = View.VISIBLE
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
                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {


                    customOrderdatas = response.body()!!.data
                    if (isCustom.equals("Custom")) {

                        Log.e("HELLOCLICKS","Adapater Custom")
                        customViewOfferAdapter = CustomViewOfferAdapter(
                                this@ViewAllProductsOrderActivity,
                                customOrderdatas!!.products,
                                isViewall
                        )
                        recycl_view_order!!.adapter = customViewOfferAdapter

                    } else {
                        Log.e("HELLOCLICKS","Adapater Not Custom")
                        viewAllProductOrderAdapter = ViewAllProductOrderAdapter(
                                this@ViewAllProductsOrderActivity,
                                customOrderdatas!!.products, isViewall
                        )
                        recycl_view_order!!.adapter = viewAllProductOrderAdapter
                    }


                    if (customOrderdatas?.prescriptionImage != null) {
                        Log.e("Hellobros","Hii   "+ customOrderdatas?.prescriptionImage)
                        if (!customOrderdatas?.prescriptionImage.equals("")) {

                            LoadImageWithDots(customOrderdatas?.prescriptionImage!!).execute()

                        } else {
                            rel_image.visibility = View.GONE
                            img_point_view_all .visibility = View.GONE
                        }
                    }

                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@ViewAllProductsOrderActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@ViewAllProductsOrderActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }

            override fun onFailure(call: Call<OrderDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar!!.visibility = View.GONE


            }
        })
    }
}