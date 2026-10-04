package pharmlane.com.PharmLaneStore.activities

import androidx.activity.OnBackPressedCallback
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.adapters.MakeOfferOrderAdapter
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.SubmitOffer
import pharmlane.com.PharmLaneStore.response.GetColorCodeResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.Utils.Companion.changeBitmapColor
import kotlinx.android.synthetic.main.activity_custom_make_offer.*
import kotlinx.android.synthetic.main.activity_make_offer.*
import kotlinx.android.synthetic.main.activity_make_offer.con_toolbar
import kotlinx.android.synthetic.main.activity_make_offer.constraint
import kotlinx.android.synthetic.main.activity_make_offer.constraint_totl_amount
import kotlinx.android.synthetic.main.activity_make_offer.img_back
import kotlinx.android.synthetic.main.activity_make_offer.progressBar
import kotlinx.android.synthetic.main.activity_make_offer.textView26
import kotlinx.android.synthetic.main.activity_make_offer.txt_buying_prefrnce
import kotlinx.android.synthetic.main.activity_make_offer.txt_calc_bil
import kotlinx.android.synthetic.main.activity_make_offer.txt_order_dtntime
import kotlinx.android.synthetic.main.activity_make_offer.txt_order_id
import kotlinx.android.synthetic.main.activity_view_all_products.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt


class MakeOfferActivity : AppCompatActivity(), View.OnClickListener {

    private var recycler_order_offer: RecyclerView? = null
    private lateinit var mNested: NestedScrollView
    private var makeOfferOrderAdapter: MakeOfferOrderAdapter? = null


    var tempArrProduct: ArrayList<String> = arrayListOf()

    var custmProducts = SubmitOffer.Products()

    var listColorCode: List<GetColorCodeResponse.Datum> = arrayListOf()
    var total: Float = 0.0f
    var iv: ImageView? = null

    var direct = ""
    var orderId = ""
    var iNormalFLow = 0

    var hashMap: HashMap<Int, Int> = HashMap<Int, Int>()

    var viewImgWidth = 0
    var viewImgHeight = 0
    var imgAttchmentWidth = 0
    var imgAttchmentHeight = 0
    var imgViewX = 0
    var imgViewY = 0
    var imgXPosi = 0
    var imgYPosi = 0


    override fun onResume() {
        super.onResume()
        if (mNested != null) {
            mNested.scrollTo(0, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // From Android 16 the system no longer calls onBackPressed(); back goes through the dispatcher.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_make_offer)

        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        recycler_order_offer = findViewById(R.id.recycl_code)
        mNested = findViewById(R.id.nested)
        img_back.setOnClickListener(this)
        img_point.setOnClickListener(this)
        txt_calc_bil.setOnClickListener(this)
        constraint_totl_amount.setOnClickListener(this)

        tempArrProduct = arrayListOf()


        if (intent != null) {

            if (intent.getStringExtra("direct") != null && intent.getStringExtra("direct").equals("1")) {
                direct = "1"
            }

            if (intent.getStringExtra("orderId") != null) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }
        }

        if (customOrderdata!!.products!!.size > 0) {
            for (j in 0 until customOrderdata!!.products!!.size) {
                customOrderdata!!.products!!.get(j).productMrp = "0.0"
            }
        }

        recycler_order_offer!!.setHasFixedSize(true)
        recycler_order_offer!!.layoutManager =
                LinearLayoutManager(this)
        makeOfferOrderAdapter = MakeOfferOrderAdapter(this, customOrderdata?.products)
        recycler_order_offer!!.adapter = makeOfferOrderAdapter

        if (customOrderdata?.prescriptionImage != null) {
            val url = URL(customOrderdata?.prescriptionImage)
            val connection: HttpURLConnection
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            bmp = scaleBitmap(myBitmap)
            imageView.setImageBitmap(bmp)
        }

        //   setImage(customOrderdata!!.prescriptionImage)
        /*if (customOrderdata!!.orderPreferenceName.equals("")) {
            txt_buying_prefrnce.text = customOrderdata!!.buyingPreferenceName
        } else {
            txt_buying_prefrnce.text = customOrderdata!!.orderPreferenceName
        }*/
        if(!TextUtils.isEmpty(customOrderdata!!.buyingPreferenceNameNew))
            txt_buying_prefrnce.text = customOrderdata!!.buyingPreferenceNameNew
        txt_order_id.text = "ID: " + customOrderdata!!.orderNo
        txt_order_dtntime.text = customOrderdata?.createdDate!!

        //  getColorCodeListApi()

        mNested.scrollTo(0, 0)
    }


    private fun scaleBitmap(bmp: Bitmap): Bitmap {
        var bm = bmp
        var width = bm.width
        var height = bm.height
        imgAttchmentWidth = width
        imgAttchmentHeight = height
        Log.v("Pictures", "Width and height are $width--$height")
        Log.v("Pictures", "WidthHeight" + con_toolbar.height)

        val rectangle = Rect()
        val window = window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top

        var maxWidth = Resources.getSystem().displayMetrics.widthPixels
        var maxHeight =
                Resources.getSystem().displayMetrics.heightPixels - con_toolbar.height - constraint.height
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

            Log.v("Pictures", "after scaling Width and height are $width--$height")

            bm = Bitmap.createScaledBitmap(bm, width, height, true)
        }

        return bm
    }

    fun urlToBitmap(prescriptionImage: String) {
        val url = URL(prescriptionImage)
        val connection: HttpURLConnection
        connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        var myBitmap = BitmapFactory.decodeStream(input)
        // imgAttchmentHeight = myBitmap.height
        //  imgAttchmentWidth = myBitmap.width

        Log.d("Medical", "imgWidth=>" + imgAttchmentHeight)
        Log.d("Medical", "imgHeight=>" + imgAttchmentWidth)

        //  myBitmap = getScaledBitMapBaseOnScreenSize(myBitmap)

        if (myBitmap != null) {
            // bmp = scaleBitmap(myBitmap)
            imageView.setImageBitmap(scaleBitmap(myBitmap))
        }

        // imageView.setImageBitmap(myBitmap)

        //  photoFinishBitmap = myBitmap
    }

    fun setImage(prescriptionImage: String?) {

        // Picasso.with(this).load(prescriptionImage).into(imageView)

        // if (!prescriptionImage.equals(""))
        urlToBitmap(prescriptionImage!!)


    }


    private fun showdialog(parseColor: Int, i: Int, rupee: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.NO_GRAVITY
        //  lp.windowAnimations = R.style.DialogAnimation;
        dialog.window!!.attributes = lp


        val rdiogrp = dialog.findViewById(R.id.radioGroup) as RadioGroup?
        val qtyAvail = dialog.findViewById(R.id.qty_avail) as AppCompatTextView?
        val totalMrp = dialog.findViewById(R.id.total_mrp) as AppCompatTextView?
        val txtRs = dialog.findViewById(R.id.txt_rs) as AppCompatTextView?
        val txtConfrm = dialog.findViewById(R.id.txt_confrm) as AppCompatTextView?
        val txtQnt = dialog.findViewById(R.id.txt_qty) as AppCompatTextView?
        val txtProductId = dialog.findViewById(R.id.txt_productid) as AppCompatTextView?
        val edtQtyAvail = dialog.findViewById(R.id.edt_qty_avail) as AppCompatEditText?
        val edtRs = dialog.findViewById(R.id.edt_rs) as AppCompatEditText?
        val imgClose = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        val imgClr = dialog.findViewById(R.id.img_clr) as AppCompatImageView?

        var isAvail = "1"
        var isAvailable = "1"

        txtProductId!!.text = customOrderdata!!.products!!.get(i).productId

        txtQnt!!.text = customOrderdata!!.products!!.get(i).quantity

        if (customOrderdata!!.products!!.get(i).quantityType.equals("0")) {
            txtQnt.text = "Qty: Full"
        } else {
            txtQnt.text = customOrderdata!!.products!!.get(i).quantity
        }

        edtRs!!.setText(rupee)
        imgClr!!.setColorFilter(parseColor)
        fun hideYes() {
            isAvail = "1"
            isAvailable = "1"
            totalMrp!!.visibility = View.VISIBLE
            txtRs!!.visibility = View.VISIBLE
            edtRs.visibility = View.VISIBLE

            edtQtyAvail!!.visibility = View.GONE
            qtyAvail!!.visibility = View.GONE
        }

        fun hideNo() {
            isAvail = "0"
            isAvailable = "0"
            totalMrp!!.visibility = View.GONE
            txtRs!!.visibility = View.GONE
            edtRs.visibility = View.GONE

            edtQtyAvail!!.visibility = View.GONE
            qtyAvail!!.visibility = View.GONE
        }

        fun hideLess() {
            isAvail = "2"
            isAvailable = "1"
            totalMrp!!.visibility = View.VISIBLE
            txtRs!!.visibility = View.VISIBLE
            edtRs.visibility = View.VISIBLE

            edtQtyAvail!!.visibility = View.VISIBLE
            qtyAvail!!.visibility = View.VISIBLE
        }
        /* if (tempArrProduct.size > 0) {

             for (k in 0 until tempArrProduct.size) {
                 if (txtProductId.text.toString().equals(tempArrProduct.get(k).order_product_id)) {
                     if (tempArrProduct.get(k).quantity_status.equals("0")) {
                         rdiogrp!!.check(R.id.radio_no)
                         hideNo()
                     } else if (tempArrProduct.get(k).quantity_status.equals("1")) {
                         rdiogrp!!.check(R.id.radio_yes)
                         hideYes()
                     } else {
                         rdiogrp!!.check(R.id.radio_less)
                         hideLess()
                     }

                     edtQtyAvail!!.setText(tempArrProduct.get(k).quantity_avail!!)
                     edtRs!!.setText(tempArrProduct.get(k).productMrp!!)
                 }
             }

         }*/



        txtConfrm!!.setOnClickListener {

            if (edtRs.text.toString().equals("") && isAvail.equals("1")) {
                Toast.makeText(this, "Enter MRP", Toast.LENGTH_SHORT)
                        .show()
            } else if (edtRs.text.toString().equals("0") && isAvail.equals("1")) {
                Toast.makeText(this, "MRP cannot be 0", Toast.LENGTH_SHORT)
                        .show()
            } else if (edtRs.text.toString().equals("") && isAvail.equals("2")) {
                Toast.makeText(this, "Enter MRP", Toast.LENGTH_SHORT)
                        .show()
            } else if (edtQtyAvail!!.text.toString().equals("") && isAvail.equals("2")) {
                Toast.makeText(this, "Enter Quantity", Toast.LENGTH_SHORT)
                        .show()
            } else if (edtQtyAvail.text.toString().equals("0") && isAvail.equals("2")) {
                Toast.makeText(this, "Quantity cannot be 0", Toast.LENGTH_SHORT)
                        .show()
            } else if (edtRs.text.toString().equals("0") && isAvail.equals("2")) {
                Toast.makeText(this, "MRP cannot be 0", Toast.LENGTH_SHORT)
                        .show()
            } else {

                customOrderdata!!.products!!.get(i).quantityAvail = ""
                customOrderdata!!.products!!.get(i).availableProduct = isAvailable
                customOrderdata!!.products!!.get(i).qnt_status = isAvail

                if (isAvail.equals("0")) {
                    customOrderdata!!.products!!.get(i).productMrp = "0.0"
                } else if (isAvail.equals("2")) {


                    val addedQu = edtQtyAvail.text.toString()

                    val finalValue: Int = addedQu.toInt()

                    if (finalValue > customOrderdata!!.products!!.get(i).quantity!!.toInt()) {
                        if(customOrderdata!!.products!!.get(i).quantityType.equals("0")){
                            customOrderdata!!.products!!.get(i).quantityType = "1"
                            customOrderdata!!.products!!.get(i).productMrp = edtRs.text.toString().trim()
                            customOrderdata!!.products!!.get(i).quantityAvail = edtQtyAvail.text.toString().trim()
                            customOrderdata!!.products!!.get(i).storeAvailableQuantity = edtQtyAvail.text.toString().trim()

                            arrProduct = arrayListOf()

                            for (i in 0 until customOrderdata!!.products!!.size) {
                                custmProducts = SubmitOffer.Products()

                                custmProducts.quantity_avail = customOrderdata!!.products!![i].quantityAvail
                                custmProducts.storeAvailableQuantity = customOrderdata!!.products!![i].storeAvailableQuantity
                                custmProducts.quantity_type = customOrderdata!!.products!![i].quantityType
                                custmProducts.order_product_id = customOrderdata!!.products!![i].id
                                custmProducts.product_available = customOrderdata!!.products!![i].availableProduct
                                custmProducts.product_price = customOrderdata!!.products!![i].productMrp
                                custmProducts.quantity_status = customOrderdata!!.products!![i].qnt_status
                                custmProducts.quantity = customOrderdata!!.products!![i].quantity

                                Log.d("TAG", "1----" + customOrderdata!!.products!![i].productMrp)
                                Log.d("TAG", "2----" + customOrderdata!!.products!![i].quantity)
                                arrProduct.add(custmProducts)

                            }
                        }else{
                            Toast.makeText(this, "Available Quantity cannot more than Ordered Quantity", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnClickListener
                        }


                    } else {
                        customOrderdata!!.products!!.get(i).quantityType = "1"
                        customOrderdata!!.products!!.get(i).productMrp = edtRs.text.toString().trim()
                        customOrderdata!!.products!!.get(i).quantityAvail = edtQtyAvail.text.toString().trim()
                        customOrderdata!!.products!!.get(i).storeAvailableQuantity = edtQtyAvail.text.toString().trim()

                        arrProduct = arrayListOf()

                        for (i in 0 until customOrderdata!!.products!!.size) {
                            custmProducts = SubmitOffer.Products()

                            custmProducts.quantity_avail = customOrderdata!!.products!![i].quantityAvail
                            custmProducts.storeAvailableQuantity = customOrderdata!!.products!![i].storeAvailableQuantity
                            custmProducts.quantity_type = customOrderdata!!.products!![i].quantityType
                            custmProducts.order_product_id = customOrderdata!!.products!![i].id
                            custmProducts.product_available = customOrderdata!!.products!![i].availableProduct
                            custmProducts.product_price = customOrderdata!!.products!![i].productMrp
                            custmProducts.quantity_status = customOrderdata!!.products!![i].qnt_status
                            custmProducts.quantity = customOrderdata!!.products!![i].quantity

                            Log.d("TAG", "1----" + customOrderdata!!.products!![i].productMrp)
                            Log.d("TAG", "2----" + customOrderdata!!.products!![i].quantity)
                            arrProduct.add(custmProducts)

                        }
                    }


                } else {
                    customOrderdata!!.products!!.get(i).productMrp = edtRs.text.toString().trim()

                }

                if (total > 1) {
                    total = 0.0F
                    for (j in 0 until customOrderdata!!.products!!.size) {
                        total =
                                total + customOrderdata!!.products!!.get(j).productMrp!!.toFloat()


                    }
                } else {
                    total =
                            total + customOrderdata!!.products!![i].productMrp!!.toFloat()
                }

                customOrderdata!!.totalMrp = String.format("%.2f", total)
                textView26.text = "₹ " + String.format("%.2f", total)

                if (total > 0) {
                    constraint_totl_amount.visibility = View.VISIBLE
                } else {
                    constraint_totl_amount.visibility = View.INVISIBLE
                }
                /*if (tempArrProduct.size == 0) {

                    products = SubmitOffer.Products()
                    products.order_product_id = customOrderdata!!.products!!.get(i).productId
                    products.quantity_status = customOrderdata!!.products!!.get(i).qnt_status
                    products.quantity_avail = customOrderdata!!.products!!.get(i).quantityAvail
                    products.productMrp = customOrderdata!!.products!!.get(i).productMrp
                    tempArrProduct.add(products)
                }*/

                /* if (tempArrProduct.size > 0) {

                     for (k in 0 until tempArrProduct.size) {
                         if (!txtProductId.text.toString().equals(tempArrProduct.get(k).order_product_id)) {

                             products = SubmitOffer.Products()
                             products.order_product_id =
                                 customOrderdata!!.products!!.get(k).productId
                             products.quantity_status =
                                 customOrderdata!!.products!!.get(k).qnt_status
                             products.quantity_avail =
                                 customOrderdata!!.products!!.get(k).quantityAvail
                             products.productMrp = customOrderdata!!.products!!.get(k).productMrp
                             tempArrProduct.add(products)
                         }else{
                             tempArrProduct.get(k).quantity_status = customOrderdata!!.products!!.get(i).qnt_status
                             tempArrProduct.get(k).quantity_avail = customOrderdata!!.products!!.get(i).quantityAvail
                             tempArrProduct.get(k).productMrp = customOrderdata!!.products!!.get(i).productMrp
                         }
                     }
                 }*/


                makeOfferOrderAdapter!!.notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        imgClose!!.setOnClickListener {

            dialog.dismiss()
        }



        rdiogrp?.setOnCheckedChangeListener { group, checkedId ->

            if (R.id.radio_yes == checkedId) {
                hideYes()
            }
            if (R.id.radio_no == checkedId) {
                hideNo()
            }

            if (R.id.radio_less == checkedId) {
                hideLess()
            }


        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun showdialogOne(parseColor: Int, i: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_one)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.NO_GRAVITY
        //  lp.windowAnimations = R.style.DialogAnimation;
        dialog.window!!.attributes = lp


        val rdiogrp = dialog.findViewById(R.id.radioGroup) as RadioGroup?
        val txtRs = dialog.findViewById(R.id.txt_rs) as AppCompatTextView?
        val totalMrp = dialog.findViewById(R.id.total_mrp) as AppCompatTextView?
        val edtRs = dialog.findViewById(R.id.edt_rs) as AppCompatEditText?
        val imgClose = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        val imgClr = dialog.findViewById(R.id.img_clr) as AppCompatImageView?
        val txtConfrm = dialog.findViewById(R.id.txt_confrm) as AppCompatTextView?
        val txtProductId = dialog.findViewById(R.id.txt_productid) as AppCompatTextView?
        val txtQtyOrdered = dialog.findViewById(R.id.txt_qty_ordered) as AppCompatTextView?
        val txt_note = dialog.findViewById(R.id.txt_note) as AppCompatTextView?

        txt_note!!.text = resources.getString(R.string.pri_note_txt_1)
        var isAvail = true
        imgClr!!.setColorFilter(parseColor)


        fun hideYes() {
            isAvail = true
            totalMrp!!.visibility = View.VISIBLE
            txtRs!!.visibility = View.VISIBLE
            edtRs!!.visibility = View.VISIBLE
            txt_note.text = resources.getString(R.string.pri_note_txt_1)
        }

        fun hideNo() {
            isAvail = false
            totalMrp!!.visibility = View.GONE
            txtRs!!.visibility = View.GONE
            edtRs!!.visibility = View.GONE
            txt_note.text = resources.getString(R.string.pri_note_txt_2)
        }

        txtProductId!!.text = customOrderdata!!.products!!.get(i).productId
        txtQtyOrdered!!.text = customOrderdata!!.products!!.get(i).quantity
        if (customOrderdata!!.products!!.get(i).quantityType.equals("0")) {
            txtQtyOrdered.text = "Qty: Full"
        } else {
            txtQtyOrdered.text = customOrderdata!!.products!!.get(i).quantity
        }

        /* if (tempArrProduct.size > 0) {

             for (k in 0 until tempArrProduct.size) {
                 if (txtProductId.text.toString().equals(tempArrProduct.get(k).order_product_id)) {
                     if (tempArrProduct.get(k).quantity_status.equals("0")) {
                         rdiogrp!!.check(R.id.radio_no)
                         hideNo()
                     } else {
                         rdiogrp!!.check(R.id.radio_yes)
                         hideYes()
                     }
                     edtRs!!.setText(tempArrProduct.get(k).productMrp!!)
                 }
             }

         }*/

        txtConfrm!!.setOnClickListener {

            if (edtRs!!.text.toString().equals("") && isAvail) {
                Toast.makeText(this, "Enter MRP", Toast.LENGTH_SHORT)
                        .show()
            } else {
                customOrderdata!!.products!!.get(i).quantityAvail = ""
                if (isAvail) {
                    customOrderdata!!.products!!.get(i).availableProduct = "1"
                    customOrderdata!!.products!!.get(i).qnt_status = "1"
                    customOrderdata!!.products!!.get(i).productMrp = edtRs.text.toString().trim()
                } else {
                    customOrderdata!!.products!!.get(i).availableProduct = "0"
                    customOrderdata!!.products!!.get(i).qnt_status = "0"
                    customOrderdata!!.products!!.get(i).productMrp = "0.0"
                }


                if (total > 1) {
                    total = 0.0F
                    for (j in 0 until customOrderdata!!.products!!.size) {

                        total =
                                total + customOrderdata!!.products!!.get(j).productMrp!!.toFloat()
                    }
                } else {
                    total =
                            total + customOrderdata!!.products!![i].productMrp!!.toFloat()
                }

                customOrderdata!!.totalMrp = String.format("%.2f", total)
                textView26.text = "₹ " + String.format("%.2f", total)


                if (total > 0) {
                    constraint_totl_amount.visibility = View.VISIBLE
                } else {
                    constraint_totl_amount.visibility = View.INVISIBLE
                }

                /*  if (tempArrProduct.size == 0) {

                      products = SubmitOffer.Products()
                      products.order_product_id = customOrderdata!!.products!!.get(i).productId
                      products.quantity_status = customOrderdata!!.products!!.get(i).qnt_status
                      products.quantity_avail = customOrderdata!!.products!!.get(i).quantityAvail
                      products.productMrp = customOrderdata!!.products!!.get(i).productMrp
                      tempArrProduct.add(products)
                  }*/

                /*     if (tempArrProduct.size > 0) {

                         for (k in 0 until tempArrProduct.size) {
                             if (!txtProductId.text.toString().equals(tempArrProduct.get(k).order_product_id)) {

                                 products = SubmitOffer.Products()
                                 products.order_product_id =
                                     customOrderdata!!.products!!.get(k).productId
                                 products.quantity_status =
                                     customOrderdata!!.products!!.get(k).qnt_status
                                 products.quantity_avail =
                                     customOrderdata!!.products!!.get(k).quantityAvail
                                 products.productMrp = customOrderdata!!.products!!.get(k).productMrp
                                 tempArrProduct.add(products)
                             } else {
                                 tempArrProduct.get(k).quantity_status =
                                     customOrderdata!!.products!!.get(i).qnt_status
                                 tempArrProduct.get(k).productMrp =
                                     customOrderdata!!.products!!.get(i).productMrp
                             }
                         }
                     }
     */
                makeOfferOrderAdapter!!.notifyDataSetChanged()


                dialog.dismiss()
            }

        }

        imgClose!!.setOnClickListener {

            dialog.dismiss()
        }

        rdiogrp?.setOnCheckedChangeListener { group, checkedId ->

            if (R.id.radio_yes == checkedId) {
                hideYes()

            }
            if (R.id.radio_no == checkedId) {
                hideNo()
            }
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
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

                // iv!!.setPadding(7, 7, 7, 7)
                val params = RelativeLayout.LayoutParams(bm.width, bm.height)


                /*imgAttchmentWidth = imageView!!.width
                imgAttchmentHeight = imageView!!.height*/

                //   childcount = rel.getChildCount()

                /* params.leftMargin =
                     customOrderdata!!.products!!.get(i).productXPosition!!.toFloat().toInt()
                 params.topMargin =
                     customOrderdata!!.products!!.get(i).productYPosition!!.toFloat().toInt()*/


                imgXPosi =
                        customOrderdata!!.products!!.get(i).productXPosition!!.toFloat().toInt()
                imgYPosi =
                        customOrderdata!!.products!!.get(i).productYPosition!!.toFloat().toInt()





                params.leftMargin = (viewImgWidth * imgXPosi) / imgAttchmentWidth
                params.topMargin = (viewImgHeight * imgYPosi) / imgAttchmentHeight


                Log.d("TAG", "ID--viewWidth--> $viewImgWidth && $viewImgHeight")
                Log.d("TAG", "ID--iviewWidth--> $imgAttchmentWidth && $imgAttchmentHeight")
                Log.d("TAG", "ID--OriginviewWidth-->" + params.leftMargin)
                Log.d("TAG", "ID--OriginviewHeight-->" + params.topMargin)
                Log.d("TAG", "ID--OiginviewWidth--> $imgXPosi && $imgYPosi")

                hashMap.put(
                        customOrderdata!!.products!!.get(i).productXPosition!!.toFloat().roundToInt(),
                        customOrderdata!!.products!!.get(i).productYPosition!!.toFloat().roundToInt()
                )

                rel.addView(iv, params)

                // for (i in 0 until childcount) {
                val v = rel.getChildAt(i)

                changeBitmapColor(
                        bm,
                        iv!!,
                        Color.parseColor("#" + customOrderdata!!.products!!.get(i).color!!)
                )
                // }

                iv!!.setOnClickListener {

                    Log.e("onDialogOpen", "hii   " + direct)

//                    val myMrp: String = makeOfferOrderAdapter!!.getrupee2(customOrderdata!!.products!!.get(i))
                    var myMrp: String = customOrderdata!!.products!!.get(i).productMrp!!
                    if (myMrp.equals("0.0", true)) {
                        myMrp = ""
                    }

                    if (direct.equals("1")) {
                        showdialog(Color.parseColor("#" + customOrderdata!!.products!!.get(i).color!!), i, myMrp)
                    } else {
                        showdialogOne(Color.parseColor("#" + customOrderdata!!.products!!.get(i).color!!), i)
                    }

                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        viewImgWidth = rel.width
        viewImgHeight = rel.height

        if (!viewImgWidth.equals("0")) {
            addTags()
        }
    }


    private fun handleBack() {
        if (iNormalFLow == 1) {
            startActivityForResult(Intent(this, CustomViewOfferActivity::class.java)
                    .putExtra("reviewfinaloffer", "1")
                    .putExtra("normal", "1")
                    .putExtra("orderId", customOrderdata!!.orderId)
                    .putExtra("custom", ""), 1000
            )
            iNormalFLow = 0
        } else {
            finish()
        }
//        super.onBackPressed()
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.img_back -> {
                handleBack()


            }
            R.id.img_point -> {
                if (bmp != null) {

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
                            Intent(this@MakeOfferActivity, ImgActivity::class.java)
                    )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

            }
            //finish()
            R.id.txt_calc_bil -> {
                // if (customOrderdata!!.products!!.size.equals(tempArrProduct.size)) {

                startActivityForResult(
                        Intent(this@MakeOfferActivity, SetDeliveryActivity::class.java)
                                .putExtra("normal", "1")
                                .putExtra("orderId", orderId), 101
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                // }
            }
            R.id.constraint_totl_amount -> {

                // if (customOrderdata!!.products!!.size.equals(tempArrProduct.size)) {


                startActivityForResult(Intent(this@MakeOfferActivity, SetDeliveryActivity::class.java)
                        .putExtra("normal", "1")
                        .putExtra("orderId", orderId), 101)
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                //  }


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val isNormal = data!!.getIntExtra("normal", 0)
                Log.e("IsNoewew", "HIi Normal   " + isNormal)
                if (isNormal == 1) {
                    iNormalFLow = 1
                } else {
                    iNormalFLow = 0
                }
            }
        }
    }

    private fun getColorCodeListApi() {
        progressBar.visibility = View.VISIBLE
        val apiService = AppConstant.getClient()!!.create(RetrofitInterface::class.java)

        val call = apiService.getcolorcode()

        call.enqueue(object : Callback<GetColorCodeResponse> {
            override fun onResponse(
                    call: Call<GetColorCodeResponse>,
                    response: Response<GetColorCodeResponse>
            ) {

                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    listColorCode = arrayListOf()

                    listColorCode = response.body()!!.data!!



                    for (i in 0 until listColorCode.size) {
                        //  var list = listPhoneCode.get(i).code
                        colorList?.add("#" + listColorCode.get(i).code.toString())
                    }

                    addTags()
                }
            }

            override fun onFailure(call: Call<GetColorCodeResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })
    }

    companion object {
        var colorList: ArrayList<String>? = arrayListOf()
        var bmp: Bitmap? = null
        var arrProduct: ArrayList<SubmitOffer.Products> = arrayListOf()

    }
}