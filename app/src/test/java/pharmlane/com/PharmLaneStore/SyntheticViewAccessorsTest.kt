package pharmlane.com.PharmLaneStore

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Inflates every layout and checks that each generated `kotlinx.android.synthetic` accessor
 * (see synthetics.gradle) finds its view and declares a type the inflated view really has.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SyntheticViewAccessorsTest {

    private val accessor = Regex(
        """public val android\.app\.Activity\.`?(\w+)`? get\(\) = findViewById<([\w.]+)>"""
    )

    @Test
    fun everyAccessorMatchesTheInflatedView() {
        val generated = File("build/generated/source/synthetics/main/kotlinx/android/synthetic/main")
        val layouts = generated.listFiles { f -> f.isDirectory }.orEmpty()
        assertTrue("no generated accessors in ${generated.absolutePath}", layouts.isNotEmpty())

        val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), R.style.AppTheme)
        val inflater = LayoutInflater.from(context)
        val problems = mutableListOf<String>()
        var checked = 0

        for (dir in layouts.sortedBy { it.name }) {
            val layout = dir.name
            val layoutId = context.resources.getIdentifier(layout, "layout", context.packageName)
            val parent = FrameLayout(context)
            try {
                inflater.inflate(layoutId, parent, true)
            } catch (e: Exception) {
                problems += "$layout: inflate failed: $e"
                continue
            }
            for (match in accessor.findAll(File(dir, "$layout.kt").readText())) {
                val (name, type) = match.destructured
                val id = context.resources.getIdentifier(name, "id", context.packageName)
                val view: View? = parent.findViewById(id)
                checked++
                when {
                    view == null -> problems += "$layout.$name: not found after inflating"
                    !Class.forName(type).isInstance(view) ->
                        problems += "$layout.$name: declared $type but inflated ${view.javaClass.name}"
                }
            }
        }
        println("Checked $checked view accessors in ${layouts.size} layouts")
        assertTrue(problems.joinToString("\n"), problems.isEmpty())
    }
}
