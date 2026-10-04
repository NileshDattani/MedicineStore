package pharmlane.com.PharmLaneStore

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import pharmlane.com.PharmLaneStore.utills.AppConstant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Starts every activity of the app on Android 16 (API 36) and fails on any crash other than the
 * known screens that need launch extras the real app always supplies.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ActivityLaunchTest {

    @Before
    fun noRealNetwork() {
        AppConstant.retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:9/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Test
    fun everyActivityStarts() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val activities = context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            .activities.orEmpty()
            .map { it.name }
            .filter { it.startsWith("pharmlane.") }
        assertTrue(activities.isNotEmpty())

        val failures = mutableListOf<String>()
        for (name in activities) {
            @Suppress("UNCHECKED_CAST")
            val cls = Class.forName(name) as Class<out Activity>
            try {
                Robolectric.buildActivity(cls).setup()
                shadowOf(Looper.getMainLooper()).idle()
            } catch (t: Throwable) {
                val root = generateSequence(t) { it.cause }.last()
                val trace = root.stackTrace.take(4).joinToString(" <- ")
                println("LAUNCH ${cls.simpleName}: $root at $trace")
                if (!isMissingLaunchExtra(cls, root)) failures += "${cls.simpleName}: $root at $trace"
                continue
            }
            println("LAUNCH ${cls.simpleName}: ok")
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    /** A screen that `!!`-dereferences the order (or category) it is always started with. */
    private fun isMissingLaunchExtra(cls: Class<*>, t: Throwable): Boolean =
        cls.simpleName in NEEDS_LAUNCH_EXTRAS && t is NullPointerException &&
                t.stackTrace.firstOrNull()?.className == cls.name

    private companion object {
        val NEEDS_LAUNCH_EXTRAS = setOf(
            "CustomMakeOfferActivity", "CustomOrderDetailActivityFromReview", "CustomViewOfferActivity",
            "MakeOfferActivity", "OrderDetailActivityFromReview", "RateCustomerActivity",
            "SelectMerchActivity", "SetDeliveryActivity", "ViewAllProductsActivity",
            "ViewAllProductsOrderActivity",
        )
    }
}
