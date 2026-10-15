package pharmlane.com.PharmLaneStore.activities

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_terms_conditions.*
import org.json.JSONObject
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.interfaces.RetrofitInterface
import pharmlane.com.PharmLaneStore.response.TandCResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import retrofit2.Call
import retrofit2.Callback


class TermsConditionsActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_terms_conditions)

        img_back.setOnClickListener(this)

       // getTandC()

        webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (progressBarTandC.isVisible) {
                    progressBarTandC.visibility = View.GONE
                }
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.proceed() // When an error occurs, ignore and go on
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Toast.makeText(this@TermsConditionsActivity, "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        })
       // webview.loadUrl("https://www.pharmlane.com/terms-us")
        webview.loadUrl("https://www.pharmlane.com/terms.php")
        //webview.loadUrl("http://96.126.124.89/medical/web/terms-us")

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
        }
    }





    fun getTandC() {

        progressBarTandC.visibility = View.VISIBLE
        val apiService =
            AppConstant.getClient()!!.create<RetrofitInterface>(RetrofitInterface::class.java)
        val call = apiService.getTandC()

        call.enqueue(object : Callback<TandCResponse> {
            override fun onResponse(
                call: Call<TandCResponse>,
                response: retrofit2.Response<TandCResponse>
            ) {
                progressBarTandC.visibility = View.GONE
                if (response.isSuccessful) {

                    val storeTandC = response.body()!!.getData()?.get(0)?.storeTermsConditions
                    txt_TandC.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(storeTandC, Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(storeTandC)
                    }

                } else {

                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(
                            this@TermsConditionsActivity,
                            AppConstant.serverErrorMessage(jObjError),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(this@TermsConditionsActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            }


            override fun onFailure(call: Call<TandCResponse>, t: Throwable) {
                progressBarTandC.visibility = View.GONE


            }
        })
    }
}
