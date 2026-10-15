package pharmlane.com.PharmLaneStore.utills

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Android 15+ draws every screen of an app targeting API 35+ behind the status and navigation
 * bars, and API 36 removed the opt-out. These layouts were designed for the old behaviour, so
 * this pads each screen's root by the system bar (and, for resizing screens, keyboard) insets,
 * which puts the content back where it used to be.
 */
object SystemBarInsets : Application.ActivityLifecycleCallbacks {

    fun install(application: Application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            application.registerActivityLifecycleCallbacks(this)
        }
    }

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        val window = activity.window
        val attributes = window.attributes
        // Screens such as the splash already draw behind the system bars on purpose.
        val drawsBehindBars = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (attributes.flags and drawsBehindBars != 0) return

        val decor = window.decorView as ViewGroup
        var root: View = decor.findViewById(android.R.id.content) ?: return
        while (root.parent !== decor) {
            root = root.parent as? View ?: return
        }

        val adjust = attributes.softInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST
        val padForKeyboard = adjust == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE ||
                adjust == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            var types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            if (padForKeyboard) types = types or WindowInsetsCompat.Type.ime()
            val bars = insets.getInsets(types)
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(root)

        // The app uses light themes; keep the status and navigation bar icons dark so they stay visible.
        WindowCompat.getInsetsController(window, decor).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
