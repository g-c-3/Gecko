package io.github.g_c_3.ardour

import android.content.Context
import android.view.View
import org.mozilla.geckoview.GeckoPreferenceController
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

// The only file that calls GeckoView (DECISIONS.md 3). An engine API change is fixed here.
object EngineAdapter {
    // Placeholder until the address bar (Phase 4).
    private const val START_PAGE = "https://example.com"

    // Telemetry and data upload off (DECISIONS.md 2).
    private val OFF_PREFS = listOf(
        "datareporting.healthreport.uploadEnabled",
        "toolkit.telemetry.enabled",
    )

    private var runtime: GeckoRuntime? = null
    private var session: GeckoSession? = null

    fun createView(context: Context): View {
        val rt = runtime ?: GeckoRuntime.create(context.applicationContext).also {
            runtime = it
            for (pref in OFF_PREFS) {
                GeckoPreferenceController.setGeckoPref(
                    pref,
                    false,
                    GeckoPreferenceController.PREF_BRANCH_USER,
                )
            }
        }
        val s = session ?: GeckoSession().also {
            // Workaround for Mozilla bug 1758212 (quick start).
            it.setContentDelegate(object : GeckoSession.ContentDelegate {})
            it.open(rt)
            it.loadUri(START_PAGE)
            session = it
        }
        return GeckoView(context).also { it.setSession(s) }
    }
}
