package pharmlane.com.PharmLaneStore.activities

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import pharmlane.com.PharmLaneStore.R
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.activity_terms_conditions.*
import kotlinx.android.synthetic.main.activity_terms_conditions.img_back
import kotlinx.android.synthetic.main.activity_terms_conditions.webview


class DemoActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)//  set status text dark

        setContentView(R.layout.activity_demo)

        img_back.setOnClickListener(this)
        webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (progressBarDemo.isVisible) {
                    progressBarDemo.visibility = View.GONE
                }
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Toast.makeText(this@DemoActivity, "Error:$description", Toast.LENGTH_SHORT)
                    .show()
            }
        })
       // webview.loadUrl("https://www.pharmlane.com/admin/demo-management-storeview")
        webview.loadUrl("https://app.pharmlane.com/pharmlen/admin/demo-management-storeview")

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }
        }
    }
}
