package pharmlane.com.PharmLaneStore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.SeekFinance
import pharmlane.com.PharmLaneStore.response.CommonResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_seek_finance.*
import kotlinx.android.synthetic.main.activity_seek_finance.img_back
import kotlinx.android.synthetic.main.activity_send_feedback.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class SeekFinanceActivity : AppCompatActivity(), View.OnClickListener {

    var edtCmpnyName = ""
    var edtLoanTerm = ""
    var edtLoanAmount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_seek_finance)

        img_back.setOnClickListener(this)
        send_btn.setOnClickListener(this)

        edt_cmpny_name
        edt_loan_term
        edt_loan_amount
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.send_btn -> {

                edtCmpnyName = edt_cmpny_name.text.toString().trim()
                edtLoanTerm  = edt_loan_term.text.toString().trim()
                edtLoanAmount = edt_loan_amount.text.toString().trim()
                if (!edtCmpnyName.isEmpty() && !edtLoanTerm.isEmpty() && !edtLoanAmount.isEmpty()){
                    seekAmount()

                }else{

                    if(edtCmpnyName.isEmpty()){
                        Toast.makeText(
                            this@SeekFinanceActivity,
                            "Please Enter Preferred Finance Company.", Toast.LENGTH_SHORT
                        ).show()
                    }else if(edtLoanTerm.isEmpty()){
                        Toast.makeText(
                            this@SeekFinanceActivity,
                            "Please Enter Loan Amount.", Toast.LENGTH_SHORT
                        ).show()
                    }else if(edtLoanAmount.isEmpty()){
                        Toast.makeText(
                            this@SeekFinanceActivity,
                            "Please Enter Loan Terms.", Toast.LENGTH_SHORT
                        ).show()
                    }


                }
            }
        }

    }


    fun seekAmount() {

        progressBarfinance.visibility = View.VISIBLE
        val seek_finance = SeekFinance()
        seek_finance.store_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        seek_finance.loan_terms = edtLoanTerm
        seek_finance.working_capital_loan_amount = edtLoanAmount
        seek_finance.preferred_finance_company = edtCmpnyName
        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.seekFinance(seek_finance)

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: retrofit2.Response<CommonResponse>
            ) {
                progressBarfinance.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(this@SeekFinanceActivity,"Submitted successfully!",
                        Toast.LENGTH_LONG).show()
                    edtCmpnyName = ""
                    edtLoanTerm = ""
                    edtLoanAmount = ""
                    finish()
                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@SeekFinanceActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@SeekFinanceActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                progressBarfinance.visibility = View.GONE


            }
        })
    }
}
