package pharmlane.com.PharmLaneStore.utills

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics


class AppController : MultiDexApplication() {
    internal var myContext: Context? = null

    val appContext: Context
        get() {

            if (myContext != null) {
                return myContext as Context
            } else {
                myContext = this
                return myContext as AppController
            }
        }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onCreate() {
        super.onCreate()

        /*if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }*/
     //   Fabric.with(this, Crashlytics())
        // Normally done by FirebaseInitProvider; explicit so Crashlytics never sees an uninitialised app.
        FirebaseApp.initializeApp(this)
        val crashlytics = FirebaseCrashlytics.getInstance()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
// To log a message to a crash report, use the following syntax:
        crashlytics.log("E/TAG: my message")
        instance = this
        myContext = this
        MultiDex.install(this)
        SystemBarInsets.install(this)
        /* FacebookSdk.sdkInitialize(this)
         FacebookSdk.setApplicationId(getResources().getString(R.string.fb_app_id))*/

    }

    companion object {

        @get:Synchronized
        var instance: AppController? = null
            private set
    }
}