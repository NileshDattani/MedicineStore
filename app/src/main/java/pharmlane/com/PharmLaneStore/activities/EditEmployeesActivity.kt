package pharmlane.com.PharmLaneStore.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.loginactivities.*
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.StoreDetailResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_edit_employees.*
import kotlinx.android.synthetic.main.activity_edit_employees.img_back
import kotlinx.android.synthetic.main.activity_store_detail.*
import kotlinx.android.synthetic.main.activity_store_profile.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class EditEmployeesActivity : AppCompatActivity(), View.OnClickListener {

    private var emp1_name = ""
    private var emp2_name = ""
    private var emp1_mobile = ""
    private var emp2_mobile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark

        setContentView(R.layout.activity_edit_employees)

        img_back.setOnClickListener(this)
        btn_update.setOnClickListener(this)


        edt_employee1.setText(AppConstant.getPreferenceText(AppConstant.EMP1_NAME))
        edt_employee1_mo_no.setText(AppConstant.getPreferenceText(AppConstant.EMP1_MOBILE))
        edt_employee2.setText(AppConstant.getPreferenceText(AppConstant.EMP2_NAME))
        edt_employee2_mo_no.setText(AppConstant.getPreferenceText(AppConstant.EMP2_MOBILE))


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update -> {

                emp1_name = edt_employee1.text.toString().trim()
                emp1_mobile = edt_employee1_mo_no.text.toString().trim()
                emp2_name = edt_employee2.text.toString().trim()
                emp2_mobile = edt_employee2_mo_no.text.toString().trim()


//                if(emp1_name.length < 1 && emp1_mobile.length == 10){
//
//                }
//

                if (emp1_name.length > 1 || emp1_mobile.length == 10) {
                    if (emp1_name.length < 1 && emp1_mobile.length < 10) {
                        Toast.makeText(this@EditEmployeesActivity, "Please enter employee name and phone number", Toast.LENGTH_LONG).show()
                    } else {
                        if (emp1_name.length < 1) {
                            Toast.makeText(this@EditEmployeesActivity, "Please enter employee name", Toast.LENGTH_LONG).show()
                        } else if (emp1_mobile.length != 10) {
                            Toast.makeText(this@EditEmployeesActivity, "Please enter valid Mobile number", Toast.LENGTH_LONG).show()
                        } else {
                            if (emp2_name.length > 1 && emp2_mobile.length == 10) {

                                if (emp2_name.length < 1 && emp2_mobile.length < 10) {
                                    Toast.makeText(this@EditEmployeesActivity, "Please enter employee name and phone number", Toast.LENGTH_LONG).show()
                                } else {
                                    if (emp2_name.length < 1) {
                                        Toast.makeText(this@EditEmployeesActivity, "Please enter employee name", Toast.LENGTH_LONG).show()
                                    } else if (emp2_mobile.length != 10) {
                                        Toast.makeText(this@EditEmployeesActivity, "Please enter valid Mobile number", Toast.LENGTH_LONG).show()
                                    } else {
                                        update_storeDetail()
                                    }
                                }
                            } else {
                                emp2_name = ""
                                emp2_name = ""
                                update_storeDetail()
                            }
//
                        }
                    }
                } else if (emp2_name.length > 1 || emp2_name.length == 10) {

                    if (emp2_name.length < 1 && emp2_mobile.length < 10) {
                        Toast.makeText(this@EditEmployeesActivity, "Please enter employee name and phone number", Toast.LENGTH_LONG).show()
                    } else {
                        if (emp2_name.length < 1) {
                            Toast.makeText(this@EditEmployeesActivity, "Please enter employee name", Toast.LENGTH_LONG).show()
                        } else if (emp2_mobile.length != 10) {
                            Toast.makeText(this@EditEmployeesActivity, "Please enter valid Mobile number", Toast.LENGTH_LONG).show()
                        } else {
                            if (emp1_name.length > 1 && emp1_mobile.length == 10) {

                                if (emp1_name.length < 1 && emp1_mobile.length < 10) {
                                    Toast.makeText(this@EditEmployeesActivity, "Please enter employee name and phone number", Toast.LENGTH_LONG).show()
                                } else {
                                    if (emp1_name.length < 1) {
                                        Toast.makeText(this@EditEmployeesActivity, "Please enter employee name", Toast.LENGTH_LONG).show()
                                    } else if (emp1_mobile.length != 10) {
                                        Toast.makeText(this@EditEmployeesActivity, "Please enter valid Mobile number", Toast.LENGTH_LONG).show()
                                    } else {
                                        update_storeDetail()
                                    }
                                }
                            } else {
                                emp1_name = ""
                                emp1_name = ""
                                update_storeDetail()
                            }
                        }
                    }
                }else{
                    emp1_name = ""
                    emp1_name = ""
                    emp2_name = ""
                    emp2_name = ""
                    update_storeDetail()
                }


//                if (emp1_name.length < 1) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter employee name", Toast.LENGTH_LONG).show()
//                } else if (emp1_mobile.length < 10 && emp1_mobile.length > 10) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter mobile number", Toast.LENGTH_LONG).show()
//                } else if (emp2_name.length < 1) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter employee name", Toast.LENGTH_LONG).show()
//                } else if (emp2_mobile.length < 10 && emp2_mobile.length > 10) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter mobile number", Toast.LENGTH_LONG).show()
//                } else {
//
//                    update_storeDetail()
//
//                }


//
//                if (emp1_name.length < 1) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter employee name", Toast.LENGTH_LONG).show()
//                } else if (emp1_mobile.length < 10 && emp1_mobile.length > 10) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter mobile number", Toast.LENGTH_LONG).show()
//                } else if (emp2_name.length < 1) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter employee name", Toast.LENGTH_LONG).show()
//                } else if (emp2_mobile.length < 10 && emp2_mobile.length > 10) {
//                    Toast.makeText(this@EditEmployeesActivity, "please enter mobile number", Toast.LENGTH_LONG).show()
//                } else {
//
//                    update_storeDetail()
//
//                }


            }
            R.id.img_back -> {
                finish()
            }
        }

    }

    private fun update_storeDetail() {
//        Progressbar.visibility = View.VISIBLE

        val register = Register()
        register.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        register.employee1_mobile = emp1_mobile
        register.employee2_mobile = emp2_mobile
        register.employee1_name = emp1_name
        register.employee2_name = emp2_name

        val apiService =
                AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.update_store_detail(register)

        call.enqueue(object : Callback<StoreDetailResponse> {
            override fun onResponse(
                    call: Call<StoreDetailResponse>,
                    response: retrofit2.Response<StoreDetailResponse>
            ) {
                //Progressbar.visibility = View.GONE
                if (response.isSuccessful) {

                    // startActivity(Intent(this@EditEmployeesActivity, StoreProfileActivity::class.java))
                    finish()


                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                                this@EditEmployeesActivity,
                                AppConstant.serverErrorMessage(jObjError),
                                Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@EditEmployeesActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                    }

                }
            }


            override fun onFailure(call: Call<StoreDetailResponse>, t: Throwable) {
                // progressBarHandler.hide();
                // Progressbar.visibility = View.GONE
            }
        })
    }


}
