package pharmlane.com.PharmLaneStore.activities

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity.Companion.customOrderdata
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.*
import pharmlane.com.PharmLaneStore.response.*
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.AppConstant.isEmpty
import kotlinx.android.synthetic.main.activity_payment.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.util.*

class PaymentActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    var isMobileNo = true
    var isOtp: Boolean = false
    var isPaymnt: Boolean = false
    var paymentFlag =""
    var isBuyerReview = ""
    private val REQUEST_CODE_ASK_PERMISSIONS_CALL_PHONE = 100

    var amountReceived = ""
    var otp = ""
    var generatedOtp = ""

    var List: List<OrderDetailResponse.Payment> = arrayListOf()
    private var payList = ArrayList<String>()
    private var payIdList = ArrayList<String>()
    var pay = ""
    var payId = ""
    var offerid = ""
    var orderId = ""
    var userid = ""
    var phone = ""
    var latitude = ""
    var longitude = ""

    var offersmadeOrderdata: List<GetStoreListResponse.OffersMade> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_payment)

        img_back.setOnClickListener(this)
        btn_verify.setOnClickListener(this)
        txt_store_detail.setOnClickListener(this)
        txt_skip.setOnClickListener(this)
        btn_down_invoice.setOnClickListener(this)
        txt_call.setOnClickListener(this)
        txt_geo_location.setOnClickListener(this)
        txt_view_order.setOnClickListener(this)

        if (intent != null) {

            if (intent.getStringExtra("orderId") != null) {
                orderId = intent.getStringExtra("orderId") ?: ""
            }

            /*if (intent.getStringExtra("orderDeliveredDate") != null && !intent.getStringExtra("orderDeliveredDate").equals("")) {
                orderDeliveredDate = intent.getStringExtra("orderDeliveredDate")
                txt_del_time.visibility= View.VISIBLE
                txt_del_time.text = AppConstant.convertdatentimetoOrderTime2(orderDeliveredDate)
                       // AppConstant.convertdatentimetoOrderTime2(orderDeliveredDate).lowercase()
                if (intent.getStringExtra("paymentVerifyDate") != null && !intent.getStringExtra("paymentVerifyDate").equals("")) {

                    txt_paymnt_time.text = AppConstant.convertdatentimetoOrderTime2(intent.getStringExtra("paymentVerifyDate"))
                    if(List.isEmpty())
                    {
                        txt_verification_payment.text="Skip: ₹ "+AppConstant.getPreferenceText(AppConstant.FINAL_AMOUNT)
                    }
                    else
                    {
                        txt_verification_payment.text="₹ "+AppConstant.getPreferenceText(AppConstant.FINAL_AMOUNT)
                    }


                }
                receiverVerifyDate = intent.getStringExtra("receiverVerifyDate")
                txt_verification_number.text=AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)

                if (receiverVerifyDate.equals("Skip", ignoreCase = true)) {

                    txt_verification_time.text = (receiverVerifyDate)
                } else {

                    txt_verification_time.text =AppConstant.convertdatentimetoOrderTime2(receiverVerifyDate)
                           // AppConstant.convertdatentimetoOrderTime1(receiverVerifyDate).lowercase()
                }
                deliverLayout()
            } else if (intent.getStringExtra("paymentVerifyDate") != null && !intent.getStringExtra("paymentVerifyDate").equals("")) {
                paymentVerifyDate = intent.getStringExtra("paymentVerifyDate")

                txt_paymnt_time.text = AppConstant.convertdatentimetoOrderTime2(paymentVerifyDate)
                if(List.isEmpty())
                {
                    txt_verification_payment.text="Skip: ₹ "+AppConstant.getPreferenceText(AppConstant.FINAL_AMOUNT)
                }
                else
                {
                    txt_verification_payment.text="₹ "+AppConstant.getPreferenceText(AppConstant.FINAL_AMOUNT)
                }
                receiverVerifyDate = intent.getStringExtra("receiverVerifyDate")
                txt_verification_number.text=AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                if (receiverVerifyDate.equals("Skip", ignoreCase = true)) {

                    txt_verification_time.text =(receiverVerifyDate)
                } else {

                    txt_verification_time.text =AppConstant.convertdatentimetoOrderTime2(receiverVerifyDate)
                          //  AppConstant.convertdatentimetoOrderTime1(receiverVerifyDate).lowercase()
                }
                paymentLayout()

            } else if (intent.getStringExtra("receiverVerifyDate") != null && !intent.getStringExtra("receiverVerifyDate").equals("")) {
                receiverVerifyDate = intent.getStringExtra("receiverVerifyDate")
                txt_verification_number.text=AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)

                if (receiverVerifyDate.equals("Skip", ignoreCase = true)) {

                    txt_verification_time.text = (receiverVerifyDate)

                } else {

                    txt_verification_time.text =AppConstant.convertdatentimetoOrderTime2(receiverVerifyDate)
                           // AppConstant.convertdatentimetoOrderTime1(receiverVerifyDate).lowercase()
                }
                skipLayout()

            }
            if (intent.getStringExtra("orderId") != null) {

                orderDetailApi(intent.getStringExtra("orderId"))
            } else {
                setData()//Skip,2020-03-19 17:17:49
            }*/
        }


        simpleSwipeRefreshLayout.setOnRefreshListener(this)
        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)

        simpleSwipeRefreshLayout?.post(Runnable {
            if (simpleSwipeRefreshLayout != null) {
                simpleSwipeRefreshLayout!!.isRefreshing = true
            }
            orderListApi()

        })
        moveIndexes()

    }


    fun setData() {
        if (customOrderdata?.orderFor.equals("1")) {
            txt_store_detail.visibility = View.VISIBLE
        }

        //Log.d("TAG","customOrderdata!!.createdDate-----!!"+customOrderdata?.createdDate!!)

        txt_orderid.text = "ID: " + customOrderdata?.orderNo
        txt_orderdatentime.text = customOrderdata?.createdDate!!

        DrawableCompat.setTint(
                txt_status.getBackground(),
                ContextCompat.getColor(
                        this,
                        AppConstant.setsStatus(customOrderdata!!.orderStatus!!)
                )
        )
        DrawableCompat.setTint(
                txt_status1.getBackground(),
                ContextCompat.getColor(
                        this,
                        AppConstant.setsStatus(customOrderdata!!.requestStatus!!)
                )
        )

        txt_status.text = customOrderdata!!.orderStatus
        txt_status1.text = customOrderdata!!.requestStatus
        txt_custmername.text = customOrderdata!!.customerName
        txt_custmernumbr.text = customOrderdata!!.customerPhone
        txt_order_for.text = customOrderdata!!.orderForName
        phone = customOrderdata?.customerPhone.toString()

        if (customOrderdata!!.offerDeliveryPreference != null && !customOrderdata!!.offerDeliveryPreference.equals("")) {
            if (customOrderdata!!.offerDeliveryPreference.equals("0")) {
                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName
               // txt_del_prefadd.text = customOrderdata!!.location



//                if (customOrderdata!!.quantiyStatus != null && !customOrderdata!!.quantiyStatus.equals("")) {
//                    txt_del_prefadd.text = customOrderdata!!.quantiyStatus
//                } else {
//                    txt_del_prefadd.visibility = View.GONE
//                }
            } else if (customOrderdata!!.offerDeliveryPreference.equals("1")) {
                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName + " in " + customOrderdata!!.deliveryCity
//                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName
                val locaTion =
                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
                txt_del_prefadd.text = locaTion
                txt_geo_location.visibility = View.VISIBLE
                txt_geo_location.text = customOrderdata!!.location
                latitude = customOrderdata?.deliveryLatitude.toString()
                longitude = customOrderdata?.deliveryLongitude.toString()
            } else {
                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName + " in " + customOrderdata!!.deliveryCity
//                txt_del_pref.text = customOrderdata!!.offerDeliveryPreferenceName
                val locaTion =
                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
                                customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
                txt_del_prefadd.text = locaTion
            }
        } else {

            if (customOrderdata!!.deliveryType.equals("0")) {
                txt_del_pref.text = customOrderdata!!.deliveryTypeName
//                if (customOrderdata!!.quantiyStatus != null && !customOrderdata!!.quantiyStatus.equals("")) {
//                    txt_del_prefadd.text = customOrderdata!!.quantiyStatus
//                } else {
//                    txt_del_prefadd.visibility = View.GONE
//                }
            } else if (customOrderdata!!.deliveryType.equals("1")) {
//                txt_del_pref.text = customOrderdata!!.deliveryTypeName
                txt_del_pref.text = customOrderdata!!.deliveryTypeName + " in " + customOrderdata!!.deliveryCity
                val locaTion =
                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName
                txt_del_prefadd.text = locaTion
                txt_geo_location.visibility = View.VISIBLE
                txt_geo_location.text = customOrderdata!!.location
                latitude = customOrderdata?.deliveryLatitude.toString()
                longitude = customOrderdata?.deliveryLongitude.toString()
            } else {
                txt_del_pref.text = customOrderdata!!.deliveryTypeName + " in " + customOrderdata!!.deliveryCity
//                txt_del_pref.text = customOrderdata!!.deliveryTypeName
                val locaTion =
                        customOrderdata!!.blockNo + ", " + customOrderdata!!.buildingName + ", " + customOrderdata!!.street + ", " +
                                customOrderdata!!.area + ", " + customOrderdata!!.landmark + ", " + customOrderdata!!.zipcode
                txt_del_prefadd.text = locaTion
            }
        }
        amountReceived = customOrderdata?.totalAmount.toString()

        val myamount = amountReceived

        val amountReceivedtotal =  application.resources.getString(
                R.string.txt_amount_received,
                myamount
        )
        txt_amount_received.setText(amountReceivedtotal)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txt_call -> {
                if (SDK_INT >= 23) {
                    val hasPermission = ContextCompat.checkSelfPermission(this@PaymentActivity, Manifest.permission.CALL_PHONE)
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@PaymentActivity,
                                        Manifest.permission.CALL_PHONE)) {
                            // Display UI and wait for user interaction
                            getPermissionInfoDialog(this@PaymentActivity.getString(R.string.call_phone_permission), this@PaymentActivity)?.show()
                        } else {
                            ActivityCompat.requestPermissions(this@PaymentActivity, arrayOf(Manifest.permission.CALL_PHONE),
                                    REQUEST_CODE_ASK_PERMISSIONS_CALL_PHONE)
                        }
                        return
                    }
                }

                val builder = AlertDialog.Builder(this@PaymentActivity, R.style.AppCompatAlertDialogStyle)
                builder.setTitle(R.string.confirm_call)
                builder.setIcon(R.mipmap.ic_launcher)

                builder.setPositiveButton(R.string.OK, DialogInterface.OnClickListener { dialog, which ->
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel: " + phone)
                    startActivity(callIntent)
                    //Utils.psLog("OK clicked.")
                })
                builder.show()

            }
            R.id.txt_geo_location -> {
//                val currentlat= MainActivity.current_latitude
//                val currentlong= MainActivity.current_longitude
                val currentlat  =  AppConstant.getPreferenceText(AppConstant.STORELATITUDE)
                val currentlong =  AppConstant.getPreferenceText(AppConstant.STORELONGITUDE)


                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+currentlat+","+currentlong+"&daddr="+latitude+","+longitude))
                startActivity(intent)
            }
            R.id.txt_view_order -> {

                val myOrderStatus = customOrderdata!!.orderStatus


                if(myOrderStatus.equals("Delivered",true)){
                    startActivity(
                        Intent(this@PaymentActivity, CustomViewOfferActivity::class.java)
                            .putExtra("orderId", orderId)
                            .putExtra("Delivered", myOrderStatus)
                            .putExtra("custom", customOrderdata!!.orderTypeName)
                    )
                }else{
                    if(myOrderStatus.equals("Invoiced")){
                        startActivity(
                            Intent(this@PaymentActivity, CustomViewOfferActivity::class.java)
                                .putExtra("orderId", orderId)
                                .putExtra("custom", customOrderdata!!.orderTypeName)
                                .putExtra("uploadinvoice", "1")
                        )
                    }else{
                        startActivity(
                            Intent(this@PaymentActivity, CustomViewOfferActivity::class.java)
                                .putExtra("orderId", orderId)
                                .putExtra("custom", customOrderdata!!.orderTypeName)
                        )
                    }




                }



                Log.e("HelloOrderNull","IsBlank   "+customOrderdata!!.orderTypeName)

//                if(customOrderdata!!.orderTypeName.equals("Custom",true)){
//                    startActivity(
//                            Intent(this@PaymentActivity, CustomOrderDetailActivityFromReview::class.java)
//                                    .putExtra("orderId", customOrderdata!!.orderId)
//                                    .putExtra("vieworder", "1")
//
//                    )
//                }else{
//                    startActivity(
//                            Intent(this@PaymentActivity, OrderDetailActivityFromReview::class.java)
//                                    .putExtra("orderId", customOrderdata!!.orderId)
//                                    .putExtra("vieworder", "1")
//
//                    )
//                }


                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.img_back -> {
                finish()
            }
            R.id.txt_store_detail -> {
                startActivity(
                        Intent(this@PaymentActivity, StoreDetailActivity::class.java)
                            .putExtra("phoneNumber", AppConstant.getPreferenceText(AppConstant.PHONE))
                )
                //finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.btn_down_invoice -> {
                if (isStoragePermissionGranted()) {
                    downloadInvoiceApi()
                }
            }
            R.id.txt_skip -> {
                skipVerifyOtpApi()
                /*if (isMobileNo) {
                    constraint_otp.visibility = View.VISIBLE
                    ll_verify_no.visibility = View.GONE
                    btn_verify.text = resources.getString(R.string.verify)

                    isOtp = true
                    isMobileNo = false
                } else if (isOtp) {*/

                //   }
            }
            R.id.btn_verify -> {

                if (isBuyerReview.equals("1")) {
                    startActivity(
                            Intent(this@PaymentActivity, MyRatingActivity::class.java)
                                    .putExtra("offerId", customOrderdata!!.offerId)
                                    .putExtra("custmrId", customOrderdata!!.customerId)
                    )
                    //finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {

                    if (isMobileNo) {
                        val number = text_num.text.toString().trim()

                        when {
                            isEmpty(number) -> {
                                Toast.makeText(
                                        this,
                                        getString(R.string.enter_mobilenumber),
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                            number.length < 10 -> {
                                Toast.makeText(
                                        this,
                                        getString(R.string.enter_valid_number),
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                generateOtpApi(number)
                            }
                        }
                    } else if (isOtp) {
                        val stringBuilder = StringBuilder()
                        stringBuilder.append(edt_otp_1.text)
                        stringBuilder.append(edt_otp_2.text)
                        stringBuilder.append(edt_otp_3.text)
                        stringBuilder.append(edt_otp_4.text)
                        stringBuilder.append(edt_otp_5.text)
                        otp = stringBuilder.toString()


                        if (stringBuilder.toString().length < 5 || !generatedOtp.equals(otp)) {

                            Toast.makeText(
                                    this,
                                    getString(R.string.enter_valid_otp),
                                    Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            verifyOtpApi()

                        }
                    } else if (isPaymnt) {


                        val separated = txt_amount_received.text.toString().trim().split(" ".toRegex()).toTypedArray()
                        amountReceived = separated[1]
                        if (amountReceived.equals("")) {
                            Toast.makeText(
                                    this,
                                    getString(R.string.enter_amount_received),
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        } else {
                            verifyPaymentApi()
                        }


                    } else {
                        startActivity(
                                Intent(this@PaymentActivity, RateCustomerActivity::class.java)
                                        .putExtra("offerId", customOrderdata!!.offerId)
                                        .putExtra("custmrId", customOrderdata!!.customerId)
                        )
                        //finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }
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

    fun moveIndexes() {
        edt_otp_1.requestFocus()
        edt_otp_1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                edt_otp_1.inputType = InputType.TYPE_CLASS_NUMBER

                //    edt_otp_1.transformationMethod = PasswordTransformationMethod.getInstance()


                if (s?.length == 1) {
                    edt_otp_2.requestFocus()
                    edt_otp_1.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_selected)
                } else {
                    edt_otp_1.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_empty)
                }

            }

            override fun afterTextChanged(editable: Editable) {

            }
        })


        edt_otp_2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edt_otp_2.inputType = InputType.TYPE_CLASS_NUMBER
                //  edt_otp_2.transformationMethod = PasswordTransformationMethod.getInstance()


                if (s?.length == 1) {
                    edt_otp_3.requestFocus()
                    edt_otp_2.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_selected)
                } else {
                    edt_otp_1.requestFocus()
                    edt_otp_2.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_empty)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        edt_otp_3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edt_otp_3.inputType = InputType.TYPE_CLASS_NUMBER
                //   edt_otp_3.transformationMethod = PasswordTransformationMethod.getInstance()


                if (s?.length == 1) {
                    edt_otp_4.requestFocus()
                    edt_otp_3.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_selected)
                } else {
                    edt_otp_2.requestFocus()
                    edt_otp_3.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_empty)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        edt_otp_4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edt_otp_4.inputType = InputType.TYPE_CLASS_NUMBER
                //   edt_otp_4.transformationMethod = PasswordTransformationMethod.getInstance()


                if (s?.length == 1) {
                    edt_otp_5.requestFocus()
                    edt_otp_4.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_selected)
                } else {
                    edt_otp_3.requestFocus()
                    edt_otp_4.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_empty)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        edt_otp_5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edt_otp_5.inputType = InputType.TYPE_CLASS_NUMBER
                //   edt_otp_5.transformationMethod = PasswordTransformationMethod.getInstance()


                if (s?.length == 0) {
                    edt_otp_4.requestFocus()
                    edt_otp_5.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_empty)
                } else {

                    edt_otp_5.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.otp_box_selected)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun setSpinPaymnt() {

        payIdList.clear()
        payList.clear()

        Log.d("TAG","payment"+ customOrderdata?.payment?.size)
        if(customOrderdata?.payment.isNullOrEmpty())
            return
        for (i in customOrderdata?.payment!!.indices) {

                payList.add(customOrderdata?.payment!!.get(i).paymentNames.toString())

                val distinct = payList.toSet().toList();

                payIdList.add(customOrderdata?.payment?.get(i)?.id.toString())

                val arrayAdapter = ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1, distinct
                )

                spin_paymnt.adapter = arrayAdapter
            }


        spin_paymnt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pay = payList[position]
                payId = payIdList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    fun verifyPaymentApi() {

        progressBar.visibility = View.VISIBLE
        val verifyPayment = VerifyPayment()
        verifyPayment.order_id = customOrderdata!!.orderId
        verifyPayment.offer_id = customOrderdata!!.offerId
        verifyPayment.amount_received = amountReceived
        verifyPayment.mode_of_payment = payId

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.verifypayment(verifyPayment)

        call.enqueue(object : Callback<VerifyPaymentResponse> {
            override fun onResponse(
                    call: Call<VerifyPaymentResponse>,
                    response: retrofit2.Response<VerifyPaymentResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

                    txt_paymnt_time.text = (response.body()?.data?.paymentVerifyDate!!)
                    txt_verification_payment.text = "₹ " + amountReceived

                    orderListApi()
                    AppConstant.setPreferenceText(AppConstant.FINAL_AMOUNT, amountReceived)
                    //AppConstant.convertdatentimetoOrderTime1(response.body()!!.data!!.paymentVerifyDate!!).lowercase()
                    // txt_del_time.setText(AppConstant.convertdatentimetoOrderTime1(response.body()!!.data!!.paymentVerifyDate!!))
                    paymentLayout()


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<VerifyPaymentResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }


    fun downloadInvoiceApi() {

        progressBar.visibility = View.VISIBLE
        val getInvoice = GetInvoice()
        getInvoice.offer_id = customOrderdata!!.offerId

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getinvoice(getInvoice)

        call.enqueue(object : Callback<GetInvoiceResponse> {
            override fun onResponse(
                    call: Call<GetInvoiceResponse>,
                    response: retrofit2.Response<GetInvoiceResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {


                    if (response.body()!!.data!!.offerInvoice!!.contains(".png")) {
                        // DownloadAndSaveImageTask(this@PaymentActivity).execute(response.body()!!.data!!.offerInvoice)
                        val img_qr: String = response.body()?.data?.offerInvoice.toString()
                        val imageName = img_qr.substring(img_qr.lastIndexOf('/') + 1)

                        downloadImageNew(imageName, img_qr)
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<GetInvoiceResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun downloadImageNew(filename: String, downloadUrlOfImage: String) {
        try {
            val dm: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(downloadUrlOfImage)
            val request: DownloadManager.Request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator.toString() + filename + ".jpg")
            dm.enqueue(request)
            Toast.makeText(this, "Download Invoice.", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Download Invoice Failed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateOtpApi(number: String) {

        progressBar.visibility = View.VISIBLE
        val generateOtp = GenerateOtp()
        generateOtp.order_id = customOrderdata!!.orderId
        generateOtp.offer_id = customOrderdata!!.offerId
        generateOtp.phone = number

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.generateotp(generateOtp)

        call.enqueue(object : Callback<GenerateOtpResponse> {
            override fun onResponse(
                    call: Call<GenerateOtpResponse>,
                    response: retrofit2.Response<GenerateOtpResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    constraint_otp.visibility = View.VISIBLE
                    ll_verify_no.visibility = View.GONE
                    btn_verify.text = resources.getString(R.string.verify_rece)
                    txt_verification_time.visibility = View.VISIBLE

                    isOtp = true
                    isMobileNo = false

                    generatedOtp = response.body()!!.data!!.receiverOpt.toString()
                    AppConstant.setPreferenceText(AppConstant.BUYER_PHONE, response.body()?.data?.receiverOptPhone.toString())
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<GenerateOtpResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun verifyOtpApi() {

        progressBar.visibility = View.VISIBLE
        val verifyotp = VerifyOtp()
        verifyotp.order_id = customOrderdata!!.orderId
        verifyotp.offer_id = customOrderdata!!.offerId
        verifyotp.otp = otp

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.verifyotp(verifyotp)

        call.enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(
                    call: Call<VerifyOtpResponse>,
                    response: retrofit2.Response<VerifyOtpResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

                    constraint_otp.visibility = View.GONE
                    txt_skip.visibility = View.GONE
                    constraint_verify_payment.visibility = View.VISIBLE

                    txt_verification_time.text = (response.body()?.data?.receiverOptDate.toString())
                    txt_verification_number.text = AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                    txt_verify_receivr.text = resources.getString(R.string.verify_paymnt)
                    btn_verify.text = resources.getString(R.string.verify_paymnt)
                    btn_verify.alpha = 0.5f
                    btn_verify.isClickable = false

                    txt_first.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
                    txt_secnd.background =
                            ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker_active)
                    view12.setBackgroundColor(
                            ContextCompat.getColor(
                                    this@PaymentActivity,
                                    R.color.colorAccent
                            )
                    )
                    txt_first.setTextColor(
                            ContextCompat.getColor(
                                    this@PaymentActivity,
                                    R.color.colorWhite
                            )
                    )
                    txt_secnd.setTextColor(
                            ContextCompat.getColor(
                                    this@PaymentActivity,
                                    R.color.colorAccent
                            )
                    )
                    textView96.setTextColor(
                            ContextCompat.getColor(
                                    this@PaymentActivity,
                                    R.color.colorAccent
                            )
                    )

                    isPaymnt = true
                    isOtp = false

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }

    fun skipVerifyOtpApi() {

        progressBar.visibility = View.VISIBLE
        val skipVerify = SkipVerify()
        skipVerify.order_id = customOrderdata!!.orderId
        skipVerify.offer_id = customOrderdata!!.offerId

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.skipverify(skipVerify)

        call.enqueue(object : Callback<SkipVerifyResponse> {
            override fun onResponse(
                    call: Call<SkipVerifyResponse>,
                    response: retrofit2.Response<SkipVerifyResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

                    btn_verify.alpha = 0.5f
                    btn_verify.isClickable = false
                    txt_verification_time.text = (response.body()?.data?.receiverOptDate.toString())
                    setPaymentMethod()
                    if (response.body()?.data?.isSkipReceiverOptDate == 1) {
                        txt_verification_number.text = "Skip"
                    } else {
                        txt_verification_number.text = AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                    }

                    skipLayout("")

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<SkipVerifyResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }

    fun paymentLayout() {
        constraint_otp.visibility = View.GONE
        ll_verify_no.visibility = View.GONE
        txt_verify_receivr.visibility = View.GONE
        constraint_verify_payment.visibility = View.GONE
        btn_verify.visibility = View.GONE
        txt_skip.visibility = View.GONE
        constraint_order_wait.visibility = View.VISIBLE

        txt_first.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        txt_secnd.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        view12.setBackgroundColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        txt_first.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
        txt_secnd.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
        textView96.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        isMobileNo = false
        isPaymnt = true
        isOtp = false
    }

    fun deliverLayout() {
        isMobileNo = false
        isPaymnt = false
        isOtp = false
        constraint_otp.visibility = View.GONE
        ll_verify_no.visibility = View.GONE
        txt_verify_receivr.visibility = View.GONE
        constraint_verify_payment.visibility = View.GONE
        txt_skip.visibility = View.GONE
        btn_verify.visibility = View.VISIBLE
        constraint_order_wait.visibility = View.GONE
        constraint_order_success.visibility = View.VISIBLE
        btn_verify.text = resources.getString(R.string.rate_custmr)
        btn_verify.alpha = 1f
        txt_first.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        txt_first.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
        txt_secnd.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        txt_secnd.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
        txt_third.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        txt_third.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
        textView96.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        txt_delivrd.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        view_2.setBackgroundColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )




        isPaymnt = false
    }

    fun skipLayout(payment:String) {


        if(payment.equals("Skip"))
        {
            constraint_verify_payment.visibility = View.GONE
        }
        else
        {
            constraint_verify_payment.visibility = View.VISIBLE
        }
        constraint_otp.visibility = View.GONE
        ll_verify_no.visibility = View.GONE
        txt_skip.visibility = View.GONE
        btn_verify.text = resources.getString(R.string.verify_paymnt)
        txt_verification_time.visibility = View.VISIBLE
        txt_verify_receivr.text = resources.getString(R.string.verify_paymnt)

        txt_first.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        txt_secnd.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker_active)
        view12.setBackgroundColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        txt_first.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
        txt_secnd.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        textView96.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorAccent
                )
        )
        isMobileNo = false
        isPaymnt = true
        isOtp = false
    }

    fun paymntLayout() {
        txt_secnd.background =
                ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_marker)
        txt_secnd.setTextColor(
                ContextCompat.getColor(
                        this@PaymentActivity,
                        R.color.colorWhite
                )
        )
    }

    fun orderDetailApi(orderId: String) {

        progressBar!!.visibility = View.VISIBLE
        val orderDetailOffer = OrderDetailOffer()
        orderDetailOffer.order_id = orderId
        orderDetailOffer.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.orderdetailoffer(orderDetailOffer)

        call.enqueue(object : Callback<OrderDetailResponse> {
            override fun onResponse(
                    call: Call<OrderDetailResponse>,
                    response: retrofit2.Response<OrderDetailResponse>
            ) {
                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {
                    customOrderdata = response.body()!!.data

                    isBuyerReview = response.body()?.data?.isBuyerReview.toString()
                    if (isBuyerReview.equals("1")) {
                        btn_verify.text = resources.getString(R.string.view_customrs_rating_for_you)
                    }
                    if (response.body()!!.data!!.offerIsPrescription!!.equals("")) {
                        customOrderdata!!.quantiyStatus = "Original Prescription Not Required"
                    } else if (response.body()!!.data!!.offerIsPrescription!!.equals("1")) {
                        customOrderdata!!.quantiyStatus = "Original Prescription Required"
                    }

                    if (response.body()?.data!!.offerInvoiceImage.equals("") || response.body()?.data!!.offerInvoiceImage.equals(null)) {
                        btn_down_invoice.visibility = View.GONE
                    } else {
                        btn_down_invoice.visibility = View.VISIBLE
                    }
                    customOrderdata?.payment
                    customOrderdata?.paymentMethod
                    /* Log.d("TAG","is....."+customOrderdata)
                     if(!customOrderdata?.payment!!.isEmpty())
                     {
                         List= customOrderdata?.paymentMethod!!
                         Log.d("TAG","aaa....."+List)
                     }
                    setSpinPaymnt()*/
                    AppConstant.setPreferenceText(AppConstant.BUYER_PHONE, response.body()?.data?.customerPhone.toString())

                    setData()

                    if(paymentFlag == "2")
                    {
                        setSpinPaymnt()
                    }else
                    {
                       setPaymentMethod()
                    }

                } else {


                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG)
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

    fun isStoragePermissionGranted(): Boolean {
        // DownloadManager needs no storage permission from Android 10, and WRITE_EXTERNAL_STORAGE
        // can no longer be granted there, so asking for it blocked invoice downloads.
        if (SDK_INT in 23..28) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED) {
                return true
            } else {

                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                )
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }


    fun orderListApi() {

        progressBar!!.visibility = View.VISIBLE
        val getStoreList = GetStoreList()
        getStoreList.order_id = orderId

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getstoreordertimeline(getStoreList)

        call.enqueue(object : Callback<GetStoreListResponse> {
            override fun onResponse(
                    call: Call<GetStoreListResponse>,
                    response: retrofit2.Response<GetStoreListResponse>
            ) {
                progressBar!!.visibility = View.GONE
                if (response.isSuccessful) {
                    simpleSwipeRefreshLayout!!.isRefreshing = false
                    offersmadeOrderdata = arrayListOf()
                    offersmadeOrderdata = response.body()?.data?.offersMade!!

                    for (i in offersmadeOrderdata.indices) {

                        paymentFlag=(offersmadeOrderdata[i].timelineFlag.toString())


                        if (offersmadeOrderdata[i].paymentName.equals(""))
                            orderDetailApi(offersmadeOrderdata.get(i).orderId.toString())


                        if (offersmadeOrderdata.get(i).timelineFlag.equals("1")) {
                            btn_verify.text = resources.getString(R.string.verify_paymnt)
                            btn_verify.alpha = 0.5f
                            btn_verify.isClickable = false
                            txt_verification_time.text = (offersmadeOrderdata.get(i).receiverVerifyDate.toString())

                            if (offersmadeOrderdata.get(i).isSkipReceiverOptDate.equals("1")) {
                                txt_verification_number.text = "Skip"
                            } else {
                                txt_verification_number.text = AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                            }
                            skipLayout("")
                        } else if (offersmadeOrderdata.get(i).timelineFlag.equals("2")) {

                            btn_verify.text = resources.getString(R.string.verify_paymnt)
                            btn_verify.alpha = 1f
                            btn_verify.isClickable = true
                            txt_verification_time.text = (offersmadeOrderdata.get(i).receiverVerifyDate.toString())
                            setSpinPaymnt()

                            if (offersmadeOrderdata.get(i).isSkipReceiverOptDate.equals("1")) {
                                txt_verification_number.text = "Skip"
                            } else {
                                txt_verification_number.text = AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                            }

                            if(offersmadeOrderdata.get(i).paymentName.equals("Skip"))
                            {
                                constraint_order_wait.visibility= View.VISIBLE
                                txt_verify_receivr.visibility=View.GONE
                                txt_order_waiting.text="Customer has skipped the\npayment of ₹ "+offersmadeOrderdata.get(i).offerFinalAmount

                            }
                            skipLayout(offersmadeOrderdata.get(i).paymentName.toString())




                        } else if (offersmadeOrderdata.get(i).timelineFlag.equals("3")) {

                            txt_paymnt_time.text = (offersmadeOrderdata.get(i).paymentVerifyDate.toString())
                            txt_verification_time.text = (offersmadeOrderdata.get(i).receiverVerifyDate.toString())
                            txt_order_waiting.text="Waiting For Delivery to Customer!"

                            if (offersmadeOrderdata.get(i).offerPriceChangeByStore.isNullOrBlank() || offersmadeOrderdata.get(i).offerPriceChangeByStore.equals("0.00")) {
                                txt_verification_payment.text = offersmadeOrderdata.get(i).paymentName + "\n₹ " + offersmadeOrderdata.get(i).offerFinalAmount
                            } else {
                                txt_verification_payment.text = offersmadeOrderdata.get(i).paymentName + "\n₹ " + offersmadeOrderdata.get(i).offerPriceChangeByStore
                            }

                            if (offersmadeOrderdata.get(i).isSkipReceiverOptDate.equals("1")) {
                                txt_verification_number.text = "Skip"
                            } else {
                                txt_verification_number.text = AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                            }

                            paymentLayout()


                        } else if (offersmadeOrderdata.get(i).timelineFlag.equals("4")) {
                            txt_del_time.visibility = View.VISIBLE
                            txt_del_time.text = (offersmadeOrderdata.get(i).orderDelivered.toString())
                            txt_paymnt_time.text = (offersmadeOrderdata.get(i).paymentVerifyDate.toString())

                            if (offersmadeOrderdata.get(i).offerPriceChangeByStore.isNullOrBlank() || offersmadeOrderdata.get(i).offerPriceChangeByStore.equals("0.00")) {
                                txt_verification_payment.text = offersmadeOrderdata.get(i).paymentName + "\n₹ " + offersmadeOrderdata.get(i).offerFinalAmount
                            } else {
                                txt_verification_payment.text = offersmadeOrderdata.get(i).paymentName + "\n₹ " + offersmadeOrderdata.get(i).offerPriceChangeByStore
                            }
                            txt_verification_time.text = (offersmadeOrderdata.get(i).receiverVerifyDate.toString())
                            if (offersmadeOrderdata.get(i).isSkipReceiverOptDate.equals("1")) {
                                txt_verification_number.text = "Skip"
                            } else {
                                txt_verification_number.text = AppConstant.getPreferenceText(AppConstant.BUYER_PHONE)
                            }
                            deliverLayout()
                        }


                        if (intent.getStringExtra("orderId") != null) {

                            orderDetailApi(intent.getStringExtra("orderId") ?: "")
                        } else {
                            setData()//Skip,2020-03-19 17:17:49
                        }


                    }


                } else {
                    simpleSwipeRefreshLayout!!.isRefreshing = false
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@PaymentActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@PaymentActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                }
            }


            override fun onFailure(call: Call<GetStoreListResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar!!.visibility = View.GONE
                simpleSwipeRefreshLayout!!.isRefreshing = false

            }
        })
    }

    private fun setPaymentMethod() {
        for (i in customOrderdata?.paymentMethod!!.indices) {
            Log.e("PaymentMetohods","IsCOD   "+customOrderdata?.paymentMethod!!.get(i).name.toString())

            payList.add(customOrderdata?.paymentMethod!!.get(i).name.toString())

            val distinct = payList.toSet().toList();

            payIdList.add(customOrderdata?.paymentMethod?.get(i)?.id.toString())

            val arrayAdapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1, distinct
            )

            spin_paymnt.adapter = arrayAdapter
        }


        spin_paymnt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pay = payList[position]
                payId = payIdList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

    }

    override fun onRefresh() {
        orderListApi()
    }
}