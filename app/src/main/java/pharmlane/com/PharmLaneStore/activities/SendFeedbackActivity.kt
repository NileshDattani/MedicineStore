package pharmlane.com.PharmLaneStore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.model.Feedback
import pharmlane.com.PharmLaneStore.response.CommonResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.activity_send_feedback.*
import kotlinx.android.synthetic.main.activity_send_feedback.img_back
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class SendFeedbackActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_send_feedback)

        img_back.setOnClickListener(this)
        btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
            R.id.btn -> {

                val edtSubmit = edt_submit.text.toString().trim()
                if (edtSubmit.isEmpty()){
                    Toast.makeText(
                        this@SendFeedbackActivity,
                        R.string.message_is_empty, Toast.LENGTH_SHORT
                    ).show()
                }else{
                    sendFeedBack()
//                    finish()
                }
            }
        }
    }


    fun sendFeedBack() {

        progressBarfeedback.visibility = View.VISIBLE
        val feedback = Feedback()
        feedback.user_type = "1"
        feedback.user_id = AppConstant.getPreferenceText(AppConstant.STORE_ID)
        feedback.feedback = edt_submit.text.toString()
        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.sendFeedBack(feedback)

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: retrofit2.Response<CommonResponse>
            ) {
                progressBarfeedback.visibility = View.GONE
                if (response.isSuccessful) {
                    edt_submit.setText("")
                    Toast.makeText(this@SendFeedbackActivity,"Your feedback submitted successfully!",Toast.LENGTH_LONG).show()
                        finish()
                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@SendFeedbackActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@SendFeedbackActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                progressBarfeedback.visibility = View.GONE


            }
        })
    }
}
