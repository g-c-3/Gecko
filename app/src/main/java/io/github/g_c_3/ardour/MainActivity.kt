package io.github.g_c_3.ardour

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

class MainActivity : Activity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this)
        val match = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val gateMessage = EngineAdapter.launchGateMessage(this)
        if (gateMessage != null) {
            // Gate unmet: message and Play Store button only, nothing is loaded (DECISIONS.md 28, 34).
            root.addView(gateView(gateMessage), match)
        } else {
            root.addView(EngineAdapter.createView(this) { root.post { recreate() } }, match)
            root.addView(settingsButton(), settingsButtonParams())
        }
        // Target SDK 36 draws edge to edge; keep the page clear of the system bars.
        root.setOnApplyWindowInsetsListener { v, insets ->
            v.setPadding(
                insets.systemWindowInsetLeft,
                insets.systemWindowInsetTop,
                insets.systemWindowInsetRight,
                insets.systemWindowInsetBottom,
            )
            insets
        }
        setContentView(root)
    }

    // Temporary entry to Settings until the browser menu (Phase 4).
    private fun settingsButton(): View = Ui.floatingPill(this, "Settings").apply {
        setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
    }

    private fun settingsButtonParams() = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        Gravity.BOTTOM or Gravity.END,
    ).apply { setMargins(Ui.dp(this@MainActivity, 16), Ui.dp(this@MainActivity, 16), Ui.dp(this@MainActivity, 16), Ui.dp(this@MainActivity, 16)) }

    private fun gateView(message: String): View {
        val pad = Ui.dp(this, 32)
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Ui.PAPER)
            setPadding(pad, pad, pad, pad)
        }
        column.addView(Ui.title(this, "Update WebView").apply { textSize = 28f })
        column.addView(Ui.body(this, message).apply {
            textSize = 16f
            setPadding(0, Ui.dp(this@MainActivity, 16), 0, Ui.dp(this@MainActivity, 28))
        })
        column.addView(Ui.pillButton(this, "Open Play Store").apply { setOnClickListener { openWebViewPage() } })
        return column
    }

    // Hands off to the Play Store or browser on the user's tap; Ardour sends nothing itself (DECISIONS.md 34).
    private fun openWebViewPage() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEBVIEW_PAGE)))
        } catch (_: ActivityNotFoundException) {
            // No app can open the link; the message above still tells the user what to do.
        }
    }

    override fun onDestroy() {
        EngineAdapter.destroy()
        super.onDestroy()
    }

    private companion object {
        const val WEBVIEW_PAGE = "https://play.google.com/store/apps/details?id=com.google.android.webview"
    }
}
