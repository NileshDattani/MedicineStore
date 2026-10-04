package pharmlane.com.PharmLaneStore.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomMakeOfferActivity.Companion.arrProduct
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.model.SubmitOffer
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_set_delivery.*
import kotlinx.android.synthetic.main.dialog_set_delivry_date.*
import java.text.SimpleDateFormat
import java.util.*


class SetDeliveryActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
        View.OnClickListener {


    private var myCalendar = Calendar.getInstance()
    private var from_date = ""
    private var to_date = ""
    private var fromTime = ""
    private var toTime = ""
    private var ftoTime = ""
    private var ffromTime = ""
    private var date_time = ""

    private var isDelPref: Boolean = false
    private var isDelDate: Boolean = false
    private var isDelTime: Boolean = false
    private var Date: Boolean = false


    private var txtFromTime: AppCompatTextView? = null
    private var txtToTime: AppCompatTextView? = null
    private var txtDate: AppCompatTextView? = null
    private var txtToDate: AppCompatTextView? = null

    var custmProducts = SubmitOffer.Products()
    var totlaMrp = "0"
    var delCharge = "0"
    var netBillAmount: Float = 0.0f
    var TotalAmount: Float = 0.0f
    var totalDiscount: Float = 0.0f

    var isCustom = "0"
    var orderId = ""

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_set_delivery)
        init()
    }

    fun init() {
        img_back.setOnClickListener(this)
        img_pref_edit.setOnClickListener(this)
        img_date_edit.setOnClickListener(this)
        img_time_edit.setOnClickListener(this)
        btn_review_final_offer.setOnClickListener(this)
        img_pref_ok.setOnClickListener(this)
        img_date_ok.setOnClickListener(this)
        img_time_ok.setOnClickListener(this)
        img_map.setOnClickListener(this)
        edt_del_time.setOnClickListener(this)
        edt_del_pref.setOnClickListener(this)
        edt_del_date.setOnClickListener(this)
        edt_custm_del_pref.setOnClickListener(this)


        if (intent != null) {
            if (intent.getStringExtra("orderId") != null) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }
            if (intent.getStringExtra("custom") != null) {
                isCustom = "Custom"
            }
        }
        customSetData()

        edt_discount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                // filter your list from your input
                discountCal(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })

        txt_delivery_charges.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                // filter your list from your input
                delChargeCal(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })
    }

    private fun customSetData() {

        if (customOrderdata?.totalMrp != null) {
            totlaMrp = customOrderdata?.totalMrp.toString()
        }

        txt_total_bill_amount.text = "₹ " + totlaMrp
        txt_net_bill_amount.text = (totlaMrp)
        txt_total_amount_payable.text = ("₹ " + totlaMrp)



        txt_order_id.text = "ID: " + customOrderdata!!.orderNo
        txt_order_dtntime.text = customOrderdata?.createdDate!!

        arrProduct = arrayListOf()

        for (i in 0 until customOrderdata!!.products!!.size) {
            custmProducts = SubmitOffer.Products()

            custmProducts.quantity_avail = customOrderdata!!.products!![i].quantityAvail
            custmProducts.quantity_type = customOrderdata!!.products!![i].quantityType
            custmProducts.order_product_id = customOrderdata!!.products!![i].id
            custmProducts.product_available = customOrderdata!!.products!![i].availableProduct
            custmProducts.product_price = customOrderdata!!.products!![i].productMrp
            custmProducts.quantity_status = customOrderdata!!.products!![i].qnt_status
            custmProducts.quantity = customOrderdata!!.products!![i].quantity
            arrProduct.add(custmProducts)
        }

        customOrderdata!!.offerDeliveryPreference = customOrderdata!!.deliveryType
        if (CustomOrderDetailActivity.customOrderdata!!.deliveryType.equals("0")) {
            txt_buying_del_pref.text = customOrderdata?.deliveryTypeName
            /* isDelDate = true
             isDelTime = true*/
            img_map.visibility = View.GONE
            txt_del_add.visibility = View.GONE
            textView60.visibility = View.GONE
            // constraint_third.visibility = View.GONE
            // constraint_four.visibility = View.GONE
        } else if (CustomOrderDetailActivity.customOrderdata!!.deliveryType.equals("1")) {
            textView60.text = CustomOrderDetailActivity.customOrderdata!!.location
            val loc = customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
            if (!loc.equals("")) {
                txt_del_add.text = loc
            }
            txt_buying_del_pref.text = customOrderdata?.deliveryTypeName + " in " + customOrderdata?.deliveryCity
            img_map.visibility = View.VISIBLE
            txt_del_add.visibility = View.VISIBLE
            textView60.visibility = View.VISIBLE
        } else {
            txt_buying_del_pref.text = customOrderdata?.deliveryTypeName + " in " + customOrderdata?.deliveryCity
            txt_del_add.visibility = View.VISIBLE
            val locaTion =
                    customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
                            customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
            txt_del_add.text = locaTion
            img_map.visibility = View.GONE
            textView60.visibility = View.GONE
        }
        customOrderdata!!.deliveryDate = customOrderdata!!.deliveryDate!!
        customOrderdata!!.startTime = customOrderdata!!.startTime!!
        customOrderdata!!.endTime = customOrderdata!!.endTime!!

        if (!CustomOrderDetailActivity.customOrderdata?.toDeliveryDate!!.equals("") || !CustomOrderDetailActivity.customOrderdata?.endTime!!.equals("")) {

            txt_del_time.text = "To " + (CustomOrderDetailActivity.customOrderdata?.toDeliveryDate!! + " " + CustomOrderDetailActivity.customOrderdata?.endTime!!)
            to_date = CustomOrderDetailActivity.customOrderdata?.toDeliveryDate!!
            toTime = CustomOrderDetailActivity.customOrderdata?.endTime!!
        }

        if (!CustomOrderDetailActivity.customOrderdata?.deliveryDate!!.equals("") || CustomOrderDetailActivity.customOrderdata?.startTime!!.equals("")) {
            // val createdConvertedDate = dateFormat.parse(from_datee)
            from_date = CustomOrderDetailActivity.customOrderdata?.deliveryDate!!
            fromTime = CustomOrderDetailActivity.customOrderdata?.startTime!!
            txt_del_date.text = "From " + CustomOrderDetailActivity.customOrderdata?.deliveryDate!! + " " + CustomOrderDetailActivity.customOrderdata?.startTime!!

        }

        val bill = txt_net_bill_amount.text.toString()

        netBillAmount = bill.toFloat()
    }

    private fun delChargeCal(chargeDel: String) {
        if (!chargeDel.equals(""))
            TotalAmount = chargeDel.toFloat() + netBillAmount.toFloat()
        else
            TotalAmount = netBillAmount.toFloat()

        txt_total_amount_payable.text = String.format("%.2f", TotalAmount)
    }

    private fun discountCal(discnt: String) {

        if (discnt > "1") {
            totalDiscount = (discnt.toInt() * totlaMrp.toFloat()) / 100
            netBillAmount = totlaMrp.toFloat() - totalDiscount

            /*int k = (int)(value*(percentage/100.0f));*/
        } else {
            netBillAmount = totlaMrp.toFloat()
        }
        txt_net_bill_amount.text = String.format("%.2f", netBillAmount)

        if (!txt_delivery_charges.text.toString().trim().isEmpty()) {
            TotalAmount = delCharge.toFloat() + netBillAmount
        } else {
            TotalAmount = netBillAmount
        }
        txt_total_amount_payable.text = String.format("%.2f", TotalAmount)
    }

    fun urDelPrefrnc() {
        constraint_del_prfernc.visibility = View.VISIBLE
        txt_prf_acc.visibility = View.GONE
        img_pref_ok.visibility = View.GONE
        img_pref_edit.visibility = View.GONE
    }

    fun urCustmDelPrefrnc() {
        constraint_custm_del_prfernc.visibility = View.VISIBLE
        txt_prf_acc.visibility = View.GONE
        img_pref_ok.visibility = View.GONE
        img_pref_edit.visibility = View.GONE
    }

    fun urDelDate() {
        constraint_del_date.visibility = View.VISIBLE
        txt_dat_acc.visibility = View.GONE
        img_date_ok.visibility = View.GONE
        img_date_edit.visibility = View.GONE
    }

    fun urDelTime() {
        constraint_del_time.visibility = View.VISIBLE
        txt_tim_acc.visibility = View.GONE
        img_time_ok.visibility = View.GONE
        img_time_edit.visibility = View.GONE
    }

    fun showAlert() {
        var delivery_type = "0"
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_set_delivery_prefrnce)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = lp

        val txtDone = dialog.findViewById(R.id.txt_done) as AppCompatTextView?
        val img_close = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        val rdiogrp = dialog.findViewById(R.id.radioGroup) as RadioGroup?
        val radio_location_delivery = dialog.findViewById(R.id.radio_location_delivery) as RadioButton?
        val radio_store_picup = dialog.findViewById(R.id.radio_store_picup) as RadioButton?


        if (customOrderdata?.deliveryTypeName!!.equals("Zip Address Delivery")) {
            radio_location_delivery?.setText(R.string.zip_address_delivry)
            customOrderdata?.deliveryTypeName = "Zip Address Delivery"
        } else if (customOrderdata?.deliveryTypeName!!.equals("Location Delivery")) {
            radio_location_delivery?.setText(R.string.location_delivry)
            customOrderdata?.deliveryTypeName = "Location Delivery"
        } else {
            radio_location_delivery?.setText(R.string.only_store_pickup)
            customOrderdata?.deliveryTypeName = "Store Pickup"
        }

        Log.e("MyDPreff", "PreffIs   " + customOrderdata?.deliveryType!!)
        Log.e("MyDPreff", "PreffIs   " + customOrderdata?.deliveryTypeName!!)
        if (customOrderdata?.deliveryTypeName!!.equals("Store Pickup")) {
            radio_location_delivery!!.visibility = View.GONE
            radio_store_picup!!.isEnabled = false
            radio_store_picup.setTextColor(resources.getColor(R.color.colorGreyText))
        }

        var orgPresc = resources.getString(R.string.only_store_pickup)
        rdiogrp?.setOnCheckedChangeListener { group, checkedId ->

            if (R.id.radio_store_picup == checkedId) {
                orgPresc = resources.getString(R.string.only_store_pickup)
                delivery_type = "0"
            }
            if (R.id.radio_location_delivery == checkedId) {


                if (customOrderdata?.deliveryTypeName!!.equals("Location Delivery")) {
                    orgPresc = resources.getString(R.string.location_delivry)
                    delivery_type = "1"
                } else {
                    orgPresc = resources.getString(R.string.zip_address_delivry)
                    delivery_type = "2"
                }
            }
        }
        img_close!!.setOnClickListener {
            dialog.dismiss()
        }
        txtDone!!.setOnClickListener {

            customOrderdata!!.offerDeliveryPreference = delivery_type
            customOrderdata!!.quantiyStatus = orgPresc
            Log.d("TAG", "orgPresc" + orgPresc)
            txt_ur_del_prefrnc1.text = orgPresc
            urDelPrefrnc()
            isDelPref = true

            accLayout()
            dialog.dismiss()

        }

        /*radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            //group.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.textview_selector))

            //var text = ""
            *//*   text += if (R.id.radio_prescri_require == checkedId) resources.getString(R.string.orignl_prescrption_requird) else resources.getString(
                     R.string.orignl_prescrption_not_requird
                 )*//*

            if (R.id.radiogroup == checkedId) {
                txtDelPref = resources.getString(R.string.orignl_prescrption_requird)
            } else {
                txtDelPref = resources.getString(R.string.orignl_prescrption_not_requird)
            }
            // Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
        }*/



        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun showCustomAlert() {
        var delivery_type = "0"
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custm_set_del_pref)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        //  lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp

        val txtDone = dialog.findViewById(R.id.txt_done) as AppCompatTextView?
        val img_close = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        val rdiogrp1 = dialog.findViewById(R.id.radioGroup1) as RadioGroup?
        var orgOrder = resources.getString(R.string.only_store_pickup)
        var orgPresc = resources.getString(R.string.orignl_prescrption_requird)


        val rdiogrp = dialog.findViewById(R.id.radioGroup) as RadioGroup?
        val radio_location_delivery = dialog.findViewById(R.id.radio_location_delivery) as RadioButton?
        val radio_store_picup = dialog.findViewById(R.id.radio_store_picup) as RadioButton?


        val radio_org_prcr = dialog.findViewById(R.id.radio_org_prcr) as RadioButton?
        val radio_org_prcr_not = dialog.findViewById(R.id.radio_org_prcr_not) as RadioButton?

        if (customOrderdata?.deliveryTypeName!!.equals("Store Pickup")) {
            radio_location_delivery!!.visibility = View.GONE
            radio_store_picup!!.isEnabled = false
            radio_store_picup.setTextColor(resources.getColor(R.color.colorGreyText))
        }

        if (customOrderdata?.deliveryTypeName!!.equals("Zip Address Delivery")) {

            radio_location_delivery?.setText(R.string.zip_address_delivry)
            customOrderdata?.deliveryTypeName = "Zip Address Delivery"
        } else if (customOrderdata?.deliveryTypeName!!.equals("Location Delivery")) {
            radio_location_delivery?.setText(R.string.location_delivry)
            customOrderdata?.deliveryTypeName = "Location Delivery"
        } else {
            radio_location_delivery?.setText(R.string.only_store_pickup)
            customOrderdata?.deliveryTypeName = "Store Pickup"
        }

        rdiogrp?.setOnCheckedChangeListener { group, checkedId ->
            if (R.id.radio_store_picup == checkedId) {
                orgOrder = resources.getString(R.string.only_store_pickup)
                delivery_type = "0"
                radio_org_prcr!!.isEnabled = true
                radio_org_prcr_not!!.isEnabled = true
            }
            if (R.id.radio_location_delivery == checkedId) {

                radio_org_prcr!!.isEnabled = false
                radio_org_prcr_not!!.isEnabled = false

                radio_org_prcr!!.isChecked = false
                radio_org_prcr_not!!.isChecked = false
                if (customOrderdata?.deliveryTypeName!!.equals("Location Delivery")) {

                    orgOrder = resources.getString(R.string.location_delivry)
                    delivery_type = "1"
                } else {
                    orgOrder = resources.getString(R.string.zip_address_delivry)
                    delivery_type = "2"
                }
            }

        }

        rdiogrp1?.setOnCheckedChangeListener { group, checkedId ->

            if (R.id.radio_org_prcr == checkedId) {
                orgPresc = resources.getString(R.string.orignl_prescrption_requird)
            }
            if (R.id.radio_org_prcr_not == checkedId) {
                orgPresc = resources.getString(R.string.orignl_prescrption_not_requird)
            }
        }

        img_close!!.setOnClickListener {
            dialog.dismiss()
        }
        txtDone!!.setOnClickListener {

            if(radio_location_delivery!!.isChecked){
                txt_ur_custm_del_prefrnc1.visibility = View.VISIBLE
                txt_presc_requirmnt.visibility = View.GONE

//                txt_presc_requirmnt.text = ""
//                txt_ur_custm_del_prefrnc1.text = ""
            }else {
                txt_ur_custm_del_prefrnc1.visibility = View.VISIBLE
                txt_presc_requirmnt.visibility = View.VISIBLE
            }
//            txt_presc_requirmnt.text = orgPresc



            txt_presc_requirmnt.text = orgPresc
            txt_ur_custm_del_prefrnc1.text = orgOrder
            customOrderdata!!.offerDeliveryPreference = delivery_type
            customOrderdata!!.quantiyStatus = orgPresc

            urCustmDelPrefrnc()
            isDelPref = true

            accLayout()
            dialog.dismiss()
//
//            customOrderdata!!.deliveryType = delivery_type
//            customOrderdata!!.quantiyStatus = orgPresc
//            Log.d("TAG", "orgPresc" + orgPresc)
//            txt_ur_del_prefrnc1.setText(orgPresc)
//            urDelPrefrnc()


        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun showDeliveryDate() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_set_delivry_date)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        //  lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp

        val txtDone = dialog.findViewById(R.id.txt_done) as AppCompatTextView?
        val img_close = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        txtDate = dialog.findViewById(R.id.txt_from_date) as AppCompatTextView?
        txtFromTime = dialog.findViewById<AppCompatTextView>(R.id.txt_from_time)

        txtDate?.text = CustomOrderDetailActivity.customOrderdata?.deliveryDate!!
        txtFromTime?.text = CustomOrderDetailActivity.customOrderdata?.startTime!!

        txt_ur_del_date.text = "From " + (CustomOrderDetailActivity.customOrderdata?.deliveryDate!! + " " + CustomOrderDetailActivity.customOrderdata?.startTime!!)

        img_close!!.setOnClickListener {
            dialog.dismiss()
        }
        txtDone!!.setOnClickListener {

            if (from_date.equals("") || from_date.isEmpty()) {

                Toast.makeText(applicationContext, "Select From Date", Toast.LENGTH_LONG).show()
            } else if (fromTime.equals("") || fromTime.isEmpty()) {

                Toast.makeText(applicationContext, "Select From Time", Toast.LENGTH_LONG).show()
            } else {

                val myFormat = "dd/MM/yyyy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat,Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("GMT")

                val d1: Date = sdf.parse(from_date)
                val d2: Date = sdf.parse(CustomOrderDetailActivity.customOrderdata!!.toDeliveryDate)

                if (d1.before(d2) || d1.equals(d2)) {
                    txt_ur_del_date.text = "From " + (from_date + " " + fromTime)
                    urDelDate()

                    customOrderdata?.deliveryDate = from_date
                    customOrderdata?.startTime = fromTime

                    isDelDate = true
                    accLayout()
                } else {
                    Toast.makeText(this@SetDeliveryActivity, "Invalid Date", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()


            }

        }
        txtDate?.setOnClickListener {
            Date = true
            showCalender()
        }

        txtFromTime?.setOnClickListener {

            timepicker()
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun showDeliveryTime() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_set_delivery_time)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        //  lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp

        val txtDone = dialog.findViewById(R.id.txt_done) as AppCompatTextView?
        val img_close = dialog.findViewById(R.id.img_close) as AppCompatImageView?
        txtToDate = dialog.findViewById(R.id.txt_to_date) as AppCompatTextView?
        txtToTime = dialog.findViewById(R.id.txt_to_time) as AppCompatTextView?


        if (!CustomOrderDetailActivity.customOrderdata?.toDeliveryDate!!.equals(""))
            txtToDate!!.text = CustomOrderDetailActivity.customOrderdata?.toDeliveryDate!!

        if (!CustomOrderDetailActivity.customOrderdata?.endTime!!.equals(""))
            txtToTime!!.text = CustomOrderDetailActivity.customOrderdata?.endTime!!

        img_close!!.setOnClickListener {
            dialog.dismiss()
        }
        txtDone!!.setOnClickListener {

            if (to_date.equals("") || to_date.isEmpty()) {

                Toast.makeText(applicationContext, "Select To Date", Toast.LENGTH_LONG).show()
            } else if (toTime.equals("") || toTime.isEmpty()) {

                Toast.makeText(applicationContext, "Select To Time", Toast.LENGTH_LONG).show()
            } else {

                val myFormat = "dd/MM/yyyy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat,Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("GMT")

                val d1: Date = sdf.parse(customOrderdata?.deliveryDate)
                val d2: Date = sdf.parse(to_date)

                if (d1.before(d2) || d1.equals(d2)) {
                    txt_ur_del_time.text = "To " + (to_date + " " + toTime)
                    CustomOrderDetailActivity.customOrderdata!!.toDeliveryDate = (to_date)
                    CustomOrderDetailActivity.customOrderdata!!.endTime = (toTime)

                    isDelTime = true
                    urDelTime()
                    accLayout()
                } else {
                    Toast.makeText(this@SetDeliveryActivity, "Invalid Date", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }

        }
        txtToDate!!.setOnClickListener {
            Date = false
            showCalender()
        }
        txtToTime!!.setOnClickListener {
            timepicker1()
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun showCalender() {
        val today = Date()
        myCalendar!!.time = today

        val minDate = myCalendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
                this@SetDeliveryActivity, this,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.setTitle("")
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myCalendar!!.set(Calendar.YEAR, year)
        myCalendar!!.set(Calendar.MONTH, month)
        myCalendar!!.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateLabel()
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat,Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val tz = myCalendar!!.timeZone

        from_date = sdf.format(myCalendar.time)
        if (Date) {

            txtDate?.text = from_date

        } else {
            to_date = sdf.format(myCalendar.time)
            txtToDate?.text = to_date

        }


    }

    private fun timepicker() {
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            /* if (currdate.equals(from_date) && (hour <= hourr) &&
                     (minute <= minutee)) {
                 Toast.makeText(this, "Your start time must be more than present time",
                         Toast.LENGTH_SHORT).show();
             } else {*/
            // from_time = cal.getTimeInMillis()
            txtFromTime?.text = SimpleDateFormat("hh:mm a",Locale.US).format(cal.time).lowercase()
            fromTime = SimpleDateFormat("hh:mm a",Locale.US).format(cal.time).lowercase()
            customOrderdata?.startTime = fromTime
            //txtToTime!!.setText("")
            //txtToTime!!.setHint(R.string.to_time)
            //  }
        }

        TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
        ).show()
    }

    private fun timepicker1() {
        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            txtToTime!!.text = SimpleDateFormat("hh:mm a",Locale.US).format(cal.time).lowercase()
            toTime = SimpleDateFormat("hh:mm a",Locale.US).format(cal.time).lowercase()
            customOrderdata?.endTime = toTime

            /* if (from_time != null && cal.getTimeInMillis() >= from_time!!) {
                 //it's after current
                 txtToTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time).lowercase()
                 toTime = SimpleDateFormat("hh:mm a").format(cal.time).lowercase()
                 customOrderdata?.startTime = toTime
                 //  txt_ur_del_time.text = (fromTime + " to " + toTime)

             } else {
                 //it's before current'
                 Toast.makeText(applicationContext, "Invalid Time", Toast.LENGTH_LONG).show()
             }
 */
            //   txt_to_time!!.text=SimpleDateFormat("hh:mm a").format(cal.time)
        }
        TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
        ).show()
    }


    fun accLayout() {
        if (isDelPref && isDelDate && isDelTime) {
//            btn_review_final_offer.isEnabled = true
            btn_review_final_offer.alpha = 1F
            btn_review_final_offer.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
            btn_review_final_offer.setBackgroundColor(
                    ContextCompat.getColor(
                            this,
                            R.color.colorAccent
                    )
            )
        } else {
            btn_review_final_offer.alpha = 0.5F
        }
    }

//    fun accLayout() {
//        if (isDelPref && isDelDate && isDelTime) {
////            btn_review_final_offer.isEnabled = true
//            btn_review_final_offer.alpha = 1F
//            btn_review_final_offer.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
//            btn_review_final_offer.setBackgroundColor(
//                    ContextCompat.getColor(
//                            this,
//                            R.color.colorAccent
//                    )
//            )
//        } else {
//            btn_review_final_offer.alpha = 0.5F
//            Toast.makeText(this,"Attend to all conditions",Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun accLayout_withoutToast() {
//        if (isDelPref && isDelDate && isDelTime) {
////            btn_review_final_offer.isEnabled = true
//            btn_review_final_offer.alpha = 1F
//            btn_review_final_offer.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
//            btn_review_final_offer.setBackgroundColor(
//                    ContextCompat.getColor(
//                            this,
//                            R.color.colorAccent
//                    )
//            )
//        } else {
//            btn_review_final_offer.alpha = 0.5F
//        }
//    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.img_back -> {
                finish()
            }
            R.id.img_pref_edit -> {

                if (intent != null && intent.getStringExtra("custom") != null) {
                    showAlert()
                } else {
                    showCustomAlert()
                }
            }
            R.id.img_date_edit -> {
                showDeliveryDate()
            }
            R.id.img_time_edit -> {
                showDeliveryTime()
            }
            R.id.btn_review_final_offer -> {
                if (isDelPref && isDelDate && isDelTime) {

                    CustomOrderDetailActivity.customOrderdata!!.netBillAmount =
                            txt_net_bill_amount.text.toString()
                    CustomOrderDetailActivity.customOrderdata!!.totalDiscount =
                            String.format("%.2f", totalDiscount)
                    Log.d("TAG", "-----total-----" + txt_total_amount_payable.text.toString())

                    var total = ""
                    val sb = StringBuilder()
                    sb.append(txt_total_amount_payable.text.toString())
                    if (sb.contains("₹")) {
                        sb.deleteCharAt(0)
                        txt_total_amount_payable.text = sb.toString()
                        total =  txt_total_amount_payable.text.toString().trim()
                    } else {
                        total = txt_total_amount_payable.text.toString().trim()
                    }

                    CustomOrderDetailActivity.customOrderdata!!.totalAmount = total
                    CustomOrderDetailActivity.customOrderdata!!.billDiscount =
                            edt_discount.text.toString()
                    CustomOrderDetailActivity.customOrderdata!!.delCharge =
                            txt_delivery_charges.text.toString()

                    date_time = customOrderdata?.deliveryDate + " " + customOrderdata?.startTime + " to " + customOrderdata?.toDeliveryDate + " " + customOrderdata?.endTime

                    customOrderdata?.offerTodateFromdate = date_time
                    customOrderdata?.todateFromdate = date_time
//                customOrderdata?.offerDeliveryPreference = customOrderdata?.deliveryType
//                customOrderdata?.offerDeliveryPreferenceName = customOrderdata?.deliveryTypeName
                    startActivityForResult(Intent(this@SetDeliveryActivity, CustomViewOfferActivity::class.java)
                            .putExtra("reviewfinaloffer", "1")
                            .putExtra("normal", "1")
                            .putExtra("orderId", customOrderdata!!.orderId)
                            .putExtra("custom", isCustom), 1000
                    )


//                startActivity(Intent(this@SetDeliveryActivity, CustomViewOfferActivity::class.java)
//                        .putExtra("reviewfinaloffer", "1")
//                        .putExtra("orderId", customOrderdata!!.orderId)
//                        .putExtra("custom", isCustom)
//                )

                    //finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                } else {

                    Toast.makeText(this, "Attend to all conditions", Toast.LENGTH_SHORT).show()

                }


            }
            R.id.img_pref_ok -> {

                isDelPref = true
                img_pref_ok.visibility = View.GONE
                txt_prf_acc.visibility = View.VISIBLE

                accLayout()

            }
            R.id.img_date_ok -> {
                isDelDate = true
                img_date_ok.visibility = View.GONE
                txt_dat_acc.visibility = View.VISIBLE

                accLayout()
            }
            R.id.img_time_ok -> {
                isDelTime = true
                img_time_ok.visibility = View.GONE
                txt_tim_acc.visibility = View.VISIBLE

                accLayout()
            }

            R.id.img_map -> {
//                val currentlat = MainActivity.current_latitude
//                val currentlong = MainActivity.current_longitude

                val currentlat = AppConstant.getPreferenceText(AppConstant.STORELATITUDE)
                val currentlong = AppConstant.getPreferenceText(AppConstant.STORELONGITUDE)


                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentlat + "," + currentlong + "&daddr=" + customOrderdata!!.deliveryLatitude + "," + customOrderdata!!.deliveryLongitude))
                startActivity(intent)
            }
            R.id.edt_del_time -> {
                showDeliveryTime()
            }
            R.id.edt_del_date -> {
                showDeliveryDate()
            }
            R.id.edt_del_pref -> {
                if (intent != null && intent.getStringExtra("custom") != null) {
                    showAlert()
                } else {
                    showCustomAlert()
                }
            }
            R.id.edt_custm_del_pref -> {
                if (intent != null && intent.getStringExtra("custom") != null) {
                    showAlert()
                } else {
                    showCustomAlert()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val isNormal = data!!.getIntExtra("normal", 0)
                if (isNormal == 1) {
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", "Hey, I received your intent!")
                    returnIntent.putExtra("normal", isNormal)
                    setResult(AppCompatActivity.RESULT_OK, returnIntent)
                    finish()
                } else {
                    finish()
                }

            }
        }
    }


}