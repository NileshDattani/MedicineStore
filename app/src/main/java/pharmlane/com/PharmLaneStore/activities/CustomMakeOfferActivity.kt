package pharmlane.com.PharmLaneStore.activities

import androidx.activity.OnBackPressedCallback
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.adapters.CustomMakeOfferAdapter
import pharmlane.com.PharmLaneStore.model.SubmitOffer
import kotlinx.android.synthetic.main.activity_custom_make_offer.*
import kotlinx.android.synthetic.main.activity_custom_make_offer.img_back
import kotlinx.android.synthetic.main.activity_custom_make_offer.txt_order_dtntime
import kotlinx.android.synthetic.main.activity_custom_make_offer.txt_order_id
import kotlinx.android.synthetic.main.activity_order_detail.*
import java.util.*

class CustomMakeOfferActivity : AppCompatActivity(), View.OnClickListener,
        CustomMakeOfferAdapter.ClikIntrfc {

    var total: Float = 0.0f
    var direct = ""
    var orderId = ""

    var tempArrProduct = ArrayList<SubmitOffer.Products>()
    var products = SubmitOffer.Products()

    private var customMakeOfferAdapter: CustomMakeOfferAdapter? = null
    private var recycler: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // From Android 16 the system no longer calls onBackPressed(); back goes through the dispatcher.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_custom_make_offer)

        recycler = findViewById(R.id.recycl)
        tempArrProduct = arrayListOf()

        img_back.setOnClickListener(this)
        txt_calc_bil.setOnClickListener(this)
        constraint_totl_amount.setOnClickListener(this)

        if (intent != null) {
            if (intent.getStringExtra("orderId") != null) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }
            if (intent.getStringExtra("direct") != null && intent.getStringExtra("direct").equals("1")) {
                direct = "1"
            }
        }
        /*   Log.d("TAG","111111"+customOrderdata?.products?.size)
           if (customOrderdata?.products!!.isNullOrEmpty()) {
               for (j in 0 until customOrderdata?.products!!.size) {
                   customOrderdata?.products?.get(j)?.productMrp = "0.0"
               }
           }*/
        /*if (customOrderdata!!.orderPreferenceName.equals("")) {
            txt_buying_prefrnce.text = customOrderdata!!.buyingPreferenceName
        } else {
            txt_buying_prefrnce.text = customOrderdata!!.orderPreferenceName
        }*/
        if(!TextUtils.isEmpty(customOrderdata!!.buyingPreferenceNameNew))
            txt_buying_prefrnce.text = customOrderdata!!.buyingPreferenceNameNew
        txt_order_id.text = "ID: " + customOrderdata!!.orderNo

        //val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        // val createdConvertedDate = dateFormat.parse(customOrderdata?.createdDate!!)
        // val date = SimpleDateFormat("dd MMM,yyyy").format(createdConvertedDate)
        //  val time = SimpleDateFormat("hh:mm a").format(createdConvertedDate).lowercase()

        txt_order_dtntime.text = customOrderdata?.createdDate
        //AppConstant.convertdatentimetoOrderTime(customOrderdata!!.createdDate!!).lowercase()
        recycler!!.setHasFixedSize(true)
        recycler!!.layoutManager =
                LinearLayoutManager(this)
        customMakeOfferAdapter = CustomMakeOfferAdapter(
                this, this,
                customOrderdata!!.products
        )
        recycler!!.adapter = customMakeOfferAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {

                if (constraint_totl_amount.isVisible) {
                    checkQty()
                } else {
                    finish()
                }
            }
            R.id.txt_calc_bil -> {
                startActivity(
                        Intent(this@CustomMakeOfferActivity, SetDeliveryActivity::class.java)
                                .putExtra("custom", "1")
                                .putExtra("orderId", orderId)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
            R.id.constraint_totl_amount -> {

                customOrderdata!!.totalMrp = String.format("%.2f", total)
                textView26.text = "₹ " + String.format("%.2f", total)
                customMakeOfferAdapter!!.notifyDataSetChanged()
                startActivity(
                        Intent(this@CustomMakeOfferActivity, SetDeliveryActivity::class.java)
                                .putExtra("custom", "1")
                                .putExtra("orderId", orderId)
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun showdialog(position: Int, quantity: String, mrp: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_two)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.NO_GRAVITY
        //  lp.windowAnimations = R.style.DialogAnimation;
        dialog.window!!.attributes = lp

        val rdiogrp = dialog.findViewById(R.id.radioGroup) as RadioGroup?
        val qtyAvail = dialog.findViewById(R.id.txt_oty_avail) as AppCompatTextView?
        val totalMrp = dialog.findViewById(R.id.total_mrp) as AppCompatTextView?
        val txtRs = dialog.findViewById(R.id.txt_rs) as AppCompatTextView?
        val txtConfrm = dialog.findViewById(R.id.txt_confrm) as AppCompatTextView?
        val edtQtyAvail = dialog.findViewById(R.id.edt_avail_qty) as AppCompatEditText?
        val edtRs = dialog.findViewById(R.id.edt_rs) as AppCompatEditText?
        val imgClose = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        val imgClr = dialog.findViewById(R.id.img_clr) as AppCompatTextView?
        val txtProductid = dialog.findViewById(R.id.txt_product_id) as AppCompatTextView?
        val txtOrdersQnt = dialog.findViewById(R.id.txt_orderd_qty) as AppCompatTextView?

        edtQtyAvail?.setText(quantity)
        edtRs?.setText(mrp)

        var prescStatus = "0"
        Log.d("TAG", "iiiiiiiiiiiiiii==>" + tempArrProduct.size)

        val clr = ("#" + customOrderdata!!.products!!.get(position).color!!)
        val color = Color.parseColor(clr)

        val drawableBg = imgClr!!.background.mutate() as GradientDrawable
        drawableBg.setColor(color)

        txtProductid!!.text = customOrderdata!!.products!![position].productId
        txtOrdersQnt!!.text = customOrderdata!!.products!![position].quantity
//        customOrderdata!!.products!![position].quantityAvail = ""

        fun hideYes() {
            prescStatus = "1"
            totalMrp!!.visibility = View.VISIBLE
            txtRs!!.visibility = View.VISIBLE
            edtRs!!.visibility = View.VISIBLE

            //  edtQtyAvail!!.visibility = View.GONE
            //  qtyAvail!!.visibility = View.GONE
        }

        fun hideNo() {
            prescStatus = "0"
            totalMrp!!.visibility = View.VISIBLE
            txtRs!!.visibility = View.VISIBLE
            edtRs!!.visibility = View.VISIBLE

            edtQtyAvail!!.visibility = View.VISIBLE
            qtyAvail!!.visibility = View.VISIBLE
        }

        /*    if (tempArrProduct.size > 0) {
                Log.d("TAG","DDSDDDSIZE==>")
                for (k in 0 until tempArrProduct.size) {

                    Log.d("TAG","AAAAAAAAAA==>")
                    if (txtProductid.text.toString().equals(tempArrProduct.get(k).order_product_id)) {
                        if (tempArrProduct.get(k).quantity_status.equals("0")) {
                            rdiogrp!!.check(R.id.radio_no)
                            hideNo()
                        } else {
                            rdiogrp!!.check(R.id.radio_yes)
                            hideYes()
                        }
                            edtQtyAvail!!.setText(tempArrProduct.get(k).quantity_avail!!)
                            edtRs!!.setText(tempArrProduct.get(k).productMrp!!)
                    }
                }

            }*/
        txtConfrm!!.setOnClickListener {

            if (prescStatus.equals("1")) {

                val addedQu = edtQtyAvail?.text.toString()

                val finalValue: Int = addedQu.toInt()

                if (finalValue > customOrderdata!!.products!!.get(position).quantity!!.toInt()) {
                    Toast.makeText(this, "Available Quantity cannot more than Ordered Quantity", Toast.LENGTH_SHORT)
                            .show()
                } else if (edtQtyAvail!!.text.toString().equals("0")) {
                    Toast.makeText(this, "Quantity cannot be 0", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    /*if (edtQtyAvail!!.text.toString().equals("") ) {
                        Toast.makeText(this, "Enter Quantity", Toast.LENGTH_SHORT)
                                .show()
                    }else*/ if (edtRs!!.text.toString().equals("")) {
                        Toast.makeText(this, "Enter Total MRP", Toast.LENGTH_SHORT)
                                .show()
                    } else if (edtRs.text.toString().equals("0")) {
                        Toast.makeText(this, "MRP cannot be 0", Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        customOrderdata!!.products!![position].availableProduct = "1"
                        customOrderdata!!.products!![position].qnt_status = "1"
                        customOrderdata!!.products!![position].prescrStatus = prescStatus
                        customOrderdata!!.products!![position].productMrp = edtRs.text.toString()
                        customOrderdata!!.products!!.get(position).storeAvailableQuantity = edtQtyAvail.text.toString()
//                    customOrderdata!!.products!!.get(position).quantityAvail = edtQtyAvail?.text.toString()

                        Log.e("HelloQAvail", "Avialis   " + customOrderdata!!.products!!.get(position).quantityAvail)

//                    customOrderdata!!.products!!.get(parseColor).quantity = edtQtyAvail?.text.toString()
                        customOrderdata!!.products!![position].prescrStatus = "1"
                        textView24.visibility = View.VISIBLE

                        if (total > 1) {
                            total = 0.0F
                            for (i in 0 until customOrderdata!!.products!!.size) {

                                try {
                                    if (customOrderdata!!.products!!.get(i).productMrp!! == null) {

                                    } else {
                                        total = total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                                    }
                                } catch (e: Exception) {
                                    Log.e("TAG", "productMrpEROOR=============" +
                                            "" + String.format("%.2f", total))
                                }

                            }
                        } else {
                            total = total + customOrderdata!!.products!![position].productMrp!!.toFloat()
                        }

                        customOrderdata?.totalMrp = String.format("%.2f", total)
                        textView26.text = "₹ " + String.format("%.2f", total)
                        Log.d("TAG", "productMrp=============" +
                                "" + String.format("%.2f", total))
                        constraint_totl_amount.visibility = View.VISIBLE

//                    customMakeOfferAdapter!!.notifyDataSetChanged()
                        customMakeOfferAdapter!!.notifyItemChanged(position)

                        dialog.dismiss()
                    }
                }

            } else {
                /*if (edtQtyAvail!!.text.toString().equals("") && prescStatus.equals("0")) {
                    Toast.makeText(this, "Enter Quantity", Toast.LENGTH_SHORT)
                        .show()
                } else */if (edtRs!!.text.toString().equals("")) {
                    Toast.makeText(this, "Enter Total MRP", Toast.LENGTH_SHORT)
                            .show()
                } else if (edtRs.text.toString().equals("0")) {
                    Toast.makeText(this, "MRP cannot be 0", Toast.LENGTH_SHORT)
                            .show()
                } else if (edtQtyAvail!!.text.toString().equals("0")) {
                    Toast.makeText(this, "Quantity cannot be 0", Toast.LENGTH_SHORT)
                            .show()
                } else {

                    customOrderdata!!.products!![position].availableProduct = "1"
                    customOrderdata!!.products!![position].qnt_status = "1"
                    customOrderdata!!.products!![position].prescrStatus = prescStatus
                    customOrderdata!!.products!![position].productMrp = edtRs.text.toString()
//                    customOrderdata!!.products!![parseColor].quantity =edtQtyAvail?.text.toString()
//                    customOrderdata!!.products!![position].quantityAvail =
//                            edtQtyAvail?.text.toString()
                    customOrderdata!!.products!![position].storeAvailableQuantity =
                            edtQtyAvail?.text.toString()
                    customOrderdata!!.products!![position].prescrStatus = "0"
                    edtQtyAvail?.setText(quantity)
                    textView24.visibility = View.VISIBLE
                    if (total > 1) {
                        total = 0.0F
                        for (i in 0 until customOrderdata!!.products!!.size) {

                            try {
                                if (customOrderdata!!.products!!.get(i).productMrp!! == null) {

                                } else {
                                    total = total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                                }
                            } catch (e: Exception) {
                                Log.e("TAG", "productMrpEROOR=============" +
                                        "" + String.format("%.2f", total))
                            }

//                            total =
//                                    total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                        }
                    } else {
                        total =
                                total + customOrderdata!!.products!![position].productMrp!!.toFloat()
                    }

                    customOrderdata!!.totalMrp = String.format("%.2f", total)
                    textView26.text = "₹ " + String.format("%.2f", total)

                    Log.d("TAG", "productMrp=============" +
                            "" + String.format("%.2f", total))

                    constraint_totl_amount.visibility = View.VISIBLE

                    /* if (tempArrProduct.size == 0) {


                         Log.d("TAG","CCCCCCCCCCC==>")
                         products = SubmitOffer.Products()
                         products.order_product_id =
                             customOrderdata!!.products!!.get(parseColor).productId
                         products.quantity_status =
                             customOrderdata!!.products!!.get(parseColor).qnt_status
                         products.quantity_avail =
                             customOrderdata!!.products!!.get(parseColor).quantityAvail
                         products.productMrp =
                             customOrderdata!!.products!!.get(parseColor).productMrp
                         tempArrProduct.add(products)
                     }

                     if (tempArrProduct.size > 0) {
                         Log.d("TAG","EEEEEEEEE==>")
                         for (k in 0 until tempArrProduct.size) {
                             if (!txtProductid.text.toString().equals(tempArrProduct.get(k).order_product_id)) {
                                 Log.d("TAG","FFFFFFFFFF==>")
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
                                 Log.d("TAG","GGGGGGGGG==>")

                                 tempArrProduct.get(k).quantity_status =
                                     customOrderdata!!.products!!.get(parseColor).qnt_status
                                 tempArrProduct.get(k).productMrp =
                                     customOrderdata!!.products!!.get(parseColor).productMrp
                             }
                         }
                     }*/


//                    customMakeOfferAdapter!!.notifyDataSetChanged()
                    customMakeOfferAdapter!!.notifyItemChanged(position)

                    dialog.dismiss()
                }
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

            /* if (R.id.radio_less == checkedId) {
                 totalMrp!!.visibility = View.VISIBLE
                 txtRs!!.visibility = View.VISIBLE
                 edtRs!!.visibility = View.VISIBLE

                 edtQtyAvail!!.visibility = View.VISIBLE
                 qtyAvail!!.visibility = View.VISIBLE
             }*/
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun showdialogOne(parseColor: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_three)
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
        val imgClr = dialog.findViewById(R.id.img_clr) as AppCompatTextView?
        val txtConfrm = dialog.findViewById(R.id.txt_confrm) as AppCompatTextView?
        val txtNote = dialog.findViewById(R.id.txt_note) as AppCompatTextView?
        val txtProductid = dialog.findViewById(R.id.txtProductid) as AppCompatTextView?
        val txtOrdersQnt = dialog.findViewById(R.id.txtOrdersQnt) as AppCompatTextView?
        var prescStatus = "0"
        // total = 0.0F

        val clr = ("#" + customOrderdata!!.products!!.get(parseColor).color!!)
        val color = Color.parseColor(clr)

        val drawableBg = imgClr!!.background.mutate() as GradientDrawable
        drawableBg.setColor(color)

        txtProductid!!.text = customOrderdata!!.products!![parseColor].productId
        txtOrdersQnt!!.text = customOrderdata!!.products!![parseColor].quantity

        txtConfrm!!.setOnClickListener {

            if (prescStatus.equals("1")) {

                if (edtRs!!.text.toString().equals("")) {
                    Toast.makeText(this, "Enter Total MRP", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    customOrderdata!!.products!![parseColor].storeAvailableQuantity = customOrderdata!!.products!![parseColor].quantity
                    customOrderdata!!.products!![parseColor].availableProduct = "1"
                    customOrderdata!!.products!![parseColor].qnt_status = "1"
                    customOrderdata!!.products!![parseColor].prescrStatus = prescStatus
                    customOrderdata!!.products!![parseColor].productMrp = edtRs.text.toString()

//                    if (prescStatus.equals("0")) {
//                        customOrderdata!!.products!![parseColor].productMrp = edtRs!!.text.toString()
//
//                    } else {
//                        customOrderdata!!.products!![parseColor].productMrp = "0"
//
//                    }

                    if (total > 1) {
                        total = 0.0F
                        for (i in 0 until customOrderdata!!.products!!.size) {


                            try {
                                if (customOrderdata!!.products!!.get(i).productMrp!! == null) {

                                } else {
                                    total = total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                                }
                            } catch (e: Exception) {
                                Log.e("TAG", "productMrpEROOR=============" +
                                        "" + String.format("%.2f", total))
                            }

//
//                            total =
//                                    total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                        }
                    } else {
                        total =
                                total + customOrderdata!!.products!![parseColor].productMrp!!.toFloat()
                    }

                    customOrderdata!!.totalMrp = String.format("%.2f", total)
                    textView26.text = "₹ " + String.format("%.2f", total)

                    constraint_totl_amount.visibility = View.VISIBLE
                    customMakeOfferAdapter!!.notifyItemChanged(parseColor)
//                customMakeOfferAdapter!!.notifyDataSetChanged()


                    dialog.dismiss()
                }
            } else {
                if (edtRs!!.text.toString().equals("")) {
                    Toast.makeText(this, "Enter Total MRP", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    customOrderdata!!.products!![parseColor].storeAvailableQuantity = customOrderdata!!.products!![parseColor].quantity
                    customOrderdata!!.products!![parseColor].availableProduct = "1"
                    customOrderdata!!.products!![parseColor].qnt_status = "1"
                    customOrderdata!!.products!![parseColor].prescrStatus = prescStatus
                    customOrderdata!!.products!![parseColor].productMrp = edtRs.text.toString()
//                    if (prescStatus.equals("0")) {
//                        customOrderdata!!.products!![parseColor].productMrp = edtRs!!.text.toString()
//                    } else {
//                        customOrderdata!!.products!![parseColor].productMrp = "0"
//
//                    }

                    if (total > 1) {
                        total = 0.0F
                        for (i in 0 until customOrderdata!!.products!!.size) {


                            try {
                                if (customOrderdata!!.products!!.get(i).productMrp!! == null) {

                                } else {
                                    total = total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                                }
                            } catch (e: Exception) {
                                Log.e("TAG", "productMrpEROOR=============" +
                                        "" + String.format("%.2f", total))
                            }


//                            total = total + customOrderdata!!.products!!.get(i).productMrp!!.toFloat()
                        }
                    } else {
                        total =
                                total + customOrderdata!!.products!![parseColor].productMrp!!.toFloat()
                    }

                    customOrderdata!!.totalMrp = String.format("%.2f", total)
                    textView26.text = "₹ " + String.format("%.2f", total)

                    constraint_totl_amount.visibility = View.VISIBLE
                    customMakeOfferAdapter!!.notifyItemChanged(parseColor)
//                customMakeOfferAdapter!!.notifyDataSetChanged()


                    dialog.dismiss()
                }
            }


        }

        imgClose!!.setOnClickListener {

            dialog.dismiss()
        }

        rdiogrp?.setOnCheckedChangeListener { group, checkedId ->

            if (R.id.radio_yes == checkedId) {
                prescStatus = "1"
                txtNote!!.text = resources.getString(R.string.note_txt_1)
                txtNote.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
                totalMrp!!.visibility = View.VISIBLE
                txtRs!!.visibility = View.VISIBLE
                edtRs!!.visibility = View.VISIBLE
//                txtNote!!.setText(resources.getString(R.string.note_txt_2))
//                txtNote.setTextColor(ContextCompat.getColor(this, R.color.offer_lost))
//                totalMrp!!.visibility = View.GONE
//                txtRs!!.visibility = View.GONE
//                edtRs!!.visibility = View.GONE

            }
            if (R.id.radio_no == checkedId) {
                prescStatus = "0"
                txtNote!!.text = resources.getString(R.string.note_txt_1)
                txtNote.setTextColor(ContextCompat.getColor(this, R.color.grey_txt))
                totalMrp!!.visibility = View.VISIBLE
                txtRs!!.visibility = View.VISIBLE
                edtRs!!.visibility = View.VISIBLE
            }
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun openDialog(pos: Int, quantity: String, mrp: String, str: String) {


        Log.e("HelloReOrde", "WhichOne  " + str)

        if (str.equals("rej")) {
            customOrderdata!!.products!![pos].availableProduct = "0"
            customOrderdata!!.products!![pos].qnt_status = "0"

            customOrderdata!!.products!!.get(pos).storeAvailableQuantity = quantity
            customOrderdata!!.products!!.get(pos).quantity = quantity
            customMakeOfferAdapter!!.notifyItemChanged(pos)

            /* products = SubmitOffer.Products()
             products.order_product_id = customOrderdata!!.products!!.get(pos).productId
             products.quantity_status = customOrderdata!!.products!!.get(pos).prescrStatus
             products.quantity_avail = customOrderdata!!.products!!.get(pos).quantityAvail
             products.productMrp = customOrderdata!!.products!!.get(pos).productMrp
             tempArrProduct.add(products)*/

        } else if (direct.equals("1")) {
            val isEmtys = customOrderdata!!.products!![pos].storeAvailableQuantity
            if (!isEmtys.equals("", true) && isEmtys != null) {
                showdialog(pos, customOrderdata!!.products!![pos].storeAvailableQuantity!!, mrp)
            } else {
                customOrderdata!!.products!![pos].storeAvailableQuantity = ""
                showdialog(pos, quantity, mrp)
            }
        } else {
            showdialogOne(pos)
        }

    }

    companion object {
        var arrProduct = ArrayList<SubmitOffer.Products>()
    }

    private fun handleBack() {


        checkQty()


    }

    private fun checkQty() {
        if (constraint_totl_amount.isVisible) {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to back?")
            builder.setPositiveButton("Yes") { dialog, which ->

                startActivity(
                        Intent(
                                this@CustomMakeOfferActivity,
                                MainActivity::class.java
                        )
                )
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->

                dialog.dismiss()
            }

            val dialog: androidx.appcompat.app.AlertDialog = builder.create()

            dialog.show()

        } else {
            finish()
        }


    }

}