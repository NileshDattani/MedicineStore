@file:JvmName("AppConstant")

package pharmlane.com.PharmLaneStore.utills


import org.json.JSONObject
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Patterns
import pharmlane.com.PharmLaneStore.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object AppConstant {

    // Must be https: the server 301-redirects http to https, and OkHttp re-sends a redirected
    // POST as a body-less GET, so every API call failed with "Unknown method" over http.
    const val BASE_URL = "https://app.pharmlane.com/pharmlen/api/"// "http://96.126.124.89/medical/api/"
   // const val BASE_URL= "https://www.pharmlane.com/"
    var MAINACTIVITY_LAT = ""
    var MAINACTIVITY_LONG = ""
    const val SHARED_PREF_NAME = "MEDICINESTOREPreference"
    const val IS_LOGIN = "MEDICINESTORE_IS_LOGIN"
    const val CONSTANT_NUMBER = "NUMBER"
    const val MERCHANDISE = "EDT_MERCH"
    const val ADD_BILL_API = "party/bill/add"
    const val PREF_FCM_TOKEN = "fcmToken"
    const val DEVICE_ID = "device_id"
    const val STORE_ID = "store_id"
    const val PHONE = "user_phone"
    const val NAME = "user_name"
    const val LOGINFROM = "loginFrom"
    const val STORE_PHOTO = "store_photo"
    const val BUYER_PHONE = "buyer_phone"
    const val FCMTOKEN = "fcmToken"
    const val FINAL_AMOUNT = "final_amount"
    const val OFFER_ID = "offer_id"
    const val QR_CODE = "qr_code"
    const val GPAY_QR_CODE = "gpay_qr_code"
    const val PAYTM_QR_CODE = "paytm_qr_code"
    const val PHONEPAY_QR_CODE = "phonepay_qr_code"



    const val STORE_OWNER = "Store_owner"


    const val OWNER_NAME = "owner_name"
    const val EMP1_NAME = "emp1_name"
    const val EMP2_NAME = "emp2_name"
    const val EMP1_MOBILE = "emp1_mobile"
    const val EMP2_MOBILE = "emp2_mobile"
    const val STORE_NAME = "store_name"
    const val OWNER_PHONE = "owner_phone"
    const val SINCE = "since"
    const val BUSINESS_TYPE = "business_type"
    const val GSET_NUMBER = "gst_number"
    const val GST_STATUS = "gst_status"
    const val GST_CERTIFICATE = "gst_certificate"
    const val DRUG_LICENCE = "drug_licence_number"
    const val DRUG_LICENCE_STATUS = "drug_licence_status"
    const val DRUG_LICENCE_IMG = "drug_licence_img"
    const val MERCHANDISE_CATEGORY = "merchandise_category"
    const val PAYMENT_METHOD = "paymenth_method"
    const val AREA = "area"
    const val SHOP_NO = "office_shop_no"
    const val STREET_ROAD = "street_road"
    const val LANDMARK = "landmark"
    const val BUILDING = "building"
    const val CITY = "city"
    const val ZIPCODE = "zipcode"
    const val GEOLOCATION = "geo_location"
    const val LOCATION = "location"
    const val STORELATITUDE = "store_lat"
    const val STORELONGITUDE = "store_long"

    const val REGISTERED_PHARMACIST_NAME = "pharmacist_name"


    const val REGISTER_API = "store_app/storeRegister"
    const val GET_ALL_DROPDOWN_API = "common/getAllDropdown"
    const val GET_QR_CODES_API = "store_app/paymentMethod"
    const val LOGIN_API = "store_app/storeLogin"
    const val GET_STORE_ORDER_LIST_API = "store_app/getStoreOrderList"
    const val GET_STORE_DETAIL_API = "store_app/getStoreDetailByMobile"
    const val GET_STORE_LIST_API = "store_app/getStoreList"
    const val VIEWALLSTORE_ORDER_API = "store_app/viewAllStoreOrder"
    const val ORDER_DETAIL_API = "order/orderDetail"
    const val GET_COLOR_CODE_API = "common/getColorCode"

    const val STORE_SUBMIT_OFFER_API = "store_app/storeSubmitOffer"
    const val ORDER_DETAIL_FOR_OFFER_API = "order/orderDetailForOffer"
    const val ORDER_TIMELINE_API = "store_app/getTimeLineUsingOrderId"
    const val ADD_INVOICE_OFFER_API = "store_app/addInvoiceOffer"
    const val VERIFY_PAYMENT_API = "store_app/verifyPayment"
    const val GET_INVOICE_API = "store_app/getInvoice"
    const val GENERATE_OTP_API = "store_app/generateOtp"
    const val VERIFY_OTP_API = "store_app/verifyOtp"
    const val SKIP_VERIFY_RECEIVER_API = "store_app/skipVerifyReceiver"
    const val MAKE_BUYER_REVIEW_API = "buyer_app/makeBuyerReview"
    const val GET_STORE_RATING_API = "store_app/getStoreRating"
    const val VERIFICATION_API = "common/sendOtpMessage"
    const val STORE_DETAIL_UPDATE_API = "store_app/storeDetailUpdate"
//    const val VIEW_ALL_ORDER_API = "store_app/viewAllStoreOrder"
    const val STORE_ALL_ORDER_API = "store_app/getFilterData" ///need to change
    const val COMMON_FILTER_API = "common/getStoreFilterList"
    const val STORE_SUB_PLANS = "store_app/mysubscription"
    const val STORE_SUB_PAY_METHODS = "store_app/paymentmetod"
    const val STORE_MAKE_SUB_PAY = "store_app/makeSubscriptionPayment"
    const val STORE_SUB_HISTORY = "store_app/subscriptionHistory"
    const val COMMON_NOTIFICATION_API = "common/getAppNotificationList"
    const val STORE_APP_CHECK_API = "store_app/checkStore"
    const val ADD_TOKEN = "common/addDeviceToken"
    const val TANDC = "common/getTermsConditions"
    const val CONTACT_US = "common/getContactUS"
    const val SEND_FEEDBACK = "common/addfeedback"
    const val SEEK_FINANCE = "store_app/SeekFinance"


    fun Context.setPreference(key: String, value: Any) {
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreference.edit()
        when (value) {
            is String -> {
                editor.putString(key, value)
            }
            is Boolean -> {
                editor.putBoolean(key, value)
            }
            is Int -> {
                editor.putInt(key, value)
            }
        }
        editor.apply()
    }

    fun updateStatusBarColor(color: String) {// Color must be in hexadecimal fromat
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         val window = window
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
         window.statusBarColor = Color.parseColor(color)
     }*/
    }

    fun Context.getPreferenceInt(key: String): Int =
        getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).getInt(key, 0)

    fun Context.getPreferenceString(key: String): String =
        getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).getString(key, "")!!

    fun Context.getPreferenceBoolean(key: String): Boolean =
        getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).getBoolean(key, false)


    var retrofit: Retrofit? = null

    fun getClient(): Retrofit? {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


        val client = OkHttpClient().newBuilder()
            .connectTimeout(80000, TimeUnit.SECONDS)
            .readTimeout(80000, TimeUnit.SECONDS)
            .writeTimeout(80000, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)

                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

    fun convertFromtoTime(startTime: String, endTime: String): String {
        val dateFormat1 = SimpleDateFormat("HH:mm:ss", Locale.US)
        val createdConvertedTime = dateFormat1.parse(startTime)
        val createdConvertedTime1 = dateFormat1.parse(endTime)
        return SimpleDateFormat("hh:mm a",Locale.US).format(createdConvertedTime).lowercase() + " to " + SimpleDateFormat("hh:mm a").format(createdConvertedTime1
        ).lowercase()
    }

    fun convertDelDate(from_date: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val createdConvertedDate = dateFormat.parse(from_date)

        return SimpleDateFormat("dd MMM, yyyy",Locale.US).format(createdConvertedDate)
    }
    fun convertDelDate1(from_date: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val createdConvertedDate = dateFormat.parse(from_date)

        return SimpleDateFormat("dd MMM,yyyy",Locale.US).format(createdConvertedDate)+" "+SimpleDateFormat("hh:mm a").format(createdConvertedDate).lowercase()
    }

    fun convertDelDate2(from_date: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val createdConvertedDate = dateFormat.parse(from_date)

        return SimpleDateFormat("dd MMM,yyyy",Locale.US).format(createdConvertedDate)+" "+SimpleDateFormat("hh:mm a").format(createdConvertedDate).lowercase()
    }

    fun submitofferDate(from_date: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val createdConvertedDate = dateFormat.parse(from_date)

        return SimpleDateFormat("yyyy-MM-dd",Locale.US).format(createdConvertedDate)
    }

    fun convertDelStartTimeApi(from_date: String): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val createdConvertedDate = dateFormat.parse(from_date)

        return SimpleDateFormat("HH:mm:ss",Locale.US).format(createdConvertedDate)

    }

    fun convertdatentimetoOrderTime(datenTime: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val createdConvertedDate = dateFormat.parse(datenTime)

        return SimpleDateFormat("dd MMM,yyyy hh:mm a",Locale.US).format(createdConvertedDate)

    }

    fun convertdatentimetoOrderTime2(datenTime: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val createdConvertedDate = dateFormat.parse(datenTime)

        return SimpleDateFormat("dd MMM,yy",Locale.US).format(createdConvertedDate)+" "+SimpleDateFormat("hh:mm a").format(createdConvertedDate).lowercase()

    }

    fun setsStatus(
        status1: String
    ): Int {
        if (status1.equals("Pending", ignoreCase = true)) {
            return R.color.st_pending
        } else if (status1.equals("Delivered", ignoreCase = true)) {
            return R.color.st_delivered
        } else if (status1.equals("Offer Made", ignoreCase = true)) {
            return R.color.st_offer_made
        } else  if (status1.equals(">24 hrs", ignoreCase = true)) {
            return R.color.st_hrs
        } else if (status1.equals("Active", ignoreCase = true)) {
            return R.color.st_active
        } else if (status1.equals("Invoiced", ignoreCase = true)) {
            return R.color.st_invoiced
        } else if (status1.equals("Accepted", ignoreCase = true)) {
            return R.color.st_accepted
        }  else if (status1.equals("Timed Out", ignoreCase = true)) {
            return R.color.st_timed_out
        }   else
            return 0
    }


    fun convertDateToTimer(datenTime: String): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)

        val cal = Calendar.getInstance()
        val currDate = dateFormat.format(cal.time)
        cal.time = dateFormat.parse(datenTime)
        cal.add(Calendar.DATE, 1)
        val convertedDate = dateFormat.format(cal.time)

        val startTime = dateFormat.parse(convertedDate)
        val stopTime = dateFormat.parse(currDate)

        val difference = startTime.getTime() - stopTime.getTime()

        val diffInSec = difference / 1000
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min =
            (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60)

        val hourss = if (hours < 0) -hours else hours

        val InSec = TimeUnit.MILLISECONDS.toSeconds(difference) % 60
        val day = TimeUnit.MILLISECONDS.toDays(difference)
        val hh = (TimeUnit.MILLISECONDS.toHours(difference) - TimeUnit.DAYS.toHours(day))
        val mm = (TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(difference)
        ))


        return "" + hh + "h:" + mm + "m:" + InSec + "s"
    }

    fun convert(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }


    fun setPreferenceText(key: String, value: String) {
        val sharedPreference =
            PreferenceManager.getDefaultSharedPreferences(AppController.instance?.appContext)
        //  SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPreferenceText(key: String): String {
        val sharedPreference =
            PreferenceManager.getDefaultSharedPreferences(AppController.instance?.appContext)
        // SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedPreference.getString(key, "").toString()
    }

    fun setPreferenceBoolean(key: String, value: Boolean) {
        val sharedPreference =
            PreferenceManager.getDefaultSharedPreferences(AppController.instance?.appContext)
        // SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        val editor = sharedPreference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPreferenceBoolean(key: String): Boolean {
        val sharedPreference =
            PreferenceManager.getDefaultSharedPreferences(AppController.instance?.appContext)
        // SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedPreference.getBoolean(key, false)
    }

    fun remove(key: String) {
        val sharedPreference =
            PreferenceManager.getDefaultSharedPreferences(AppController.instance?.appContext)
        val editor = sharedPreference.edit()
        editor.remove(key)
        editor.apply()

    }

    val String.isValidEmail: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(this).matches()


    fun Context.removePreference(key: String) {
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.remove(key).apply()
    }

    fun isEmpty(s: String?): Boolean {
        var b = true
        if (s != null && !s.isEmpty()) {
            b = false
        }
        return b
    }

    /** Message for a failed API response: the server sends either "message" or an "error" (string or object). */
    fun serverErrorMessage(body: JSONObject): String {
        body.optString("message").takeIf { it.isNotBlank() }?.let { return it }
        val error = body.opt("error")
        return when (error) {
            is JSONObject -> error.optString("message").takeIf { it.isNotBlank() } ?: "Server error"
            is String -> error
            else -> "Server error"
        }
    }
}
