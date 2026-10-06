package pharmlane.com.PharmLaneStore.loginactivities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Login
import pharmlane.com.PharmLaneStore.response.LoginResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.AppConstant.CONSTANT_NUMBER
import pharmlane.com.PharmLaneStore.utills.AppConstant.isEmpty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_set_delivery.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    var number = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }//  set status text dark


        setContentView(R.layout.activity_login)

        btn_otp.setOnClickListener(this)
        text_register.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_otp -> {

                number = text_num.text.toString().trim()

                when {
                    isEmpty(number) -> {
                        Toast.makeText(this, getString(R.string.enter_mobilenumber), Toast.LENGTH_SHORT).show()
                    }
                    number.length < 10 -> {
                        Toast.makeText(this, getString(R.string.enter_valid_number), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        loginApi()
                    }
                }
            }
            R.id.text_register -> {
                startActivity(Intent(this@LoginActivity, PersonalInfoActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    fun loginApi() {

        progressBar.visibility = View.VISIBLE
        val login = Login()
        login.phone = number

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.login(login)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                    call: Call<LoginResponse>,
                    response: retrofit2.Response<LoginResponse>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {

                    startActivity(Intent(this@LoginActivity, VerifyActivity::class.java).putExtra(CONSTANT_NUMBER, number))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    AppConstant.setPreferenceText(
                            AppConstant.STORE_ID,
                            response.body()!!.data!!.storeId!!
                    )
                    AppConstant.setPreferenceText(
                            AppConstant.PHONE,
                            response.body()!!.data!!.phone!!
                    )
                    AppConstant.setPreferenceText(
                            AppConstant.NAME,
                            response.body()!!.data!!.ownerName!!
                    )
                    AppConstant.setPreferenceText(
                            AppConstant.LOGINFROM,
                            response.body()!!.data!!.loginName!!
                    )
                    AppConstant.setPreferenceText(
                            AppConstant.STORE_PHOTO,
                            response.body()!!.data!!.storePhoto!!
                    )


                    try {
                        AppConstant.setPreferenceText(
                                AppConstant.EMP1_NAME,
                                response.body()!!.data!!.employee1Name!!
                        )
                        AppConstant.setPreferenceText(
                                AppConstant.EMP2_NAME,
                                response.body()!!.data!!.employee2Name!!
                        )
                        AppConstant.setPreferenceText(
                                AppConstant.EMP1_MOBILE,
                                response.body()!!.data!!.employee1Mobile!!
                        )
                        AppConstant.setPreferenceText(
                                AppConstant.EMP2_MOBILE,
                                response.body()!!.data!!.employee2Mobile!!
                        )
                    }catch (e:Exception){

                    }



/*

                    AppConstant.setPreferenceText(
                        AppConstant.PREF_USER_PROFILE_IMAGE,
                        response.body()!!.data!!.profilePic!!
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.PREF_USER_ID,
                        response.body()!!.data!!.userId!!
                    )

                    AppConstant.setPreferenceText(
                        AppConstant.PREF_USER_NAME,
                        response.body()!!.data!!.fullname!!
                    )
                    AppConstant.setPreferenceText(
                        AppConstant.PREF_USER_PHONE,
                        response.body()!!.data!!.phone!!
                    )


                    startActivity(
                        Intent(this@LoginActivity, VerifyActivity::class.java).putExtra(
                            CONSTANT_NUMBER,
                            number
                        )
                    )
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
*/

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@LoginActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                }
            }


            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // progressBarHandler.hide();
                progressBar.visibility = View.GONE
            }
        })
    }

}