package io.github.g_c_3.ardour

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

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
    private fun settingsButton(): View = TextView(this).apply {
        text = "Settings"
        textSize = 14f
        setTextColor(Color.WHITE)
        setPadding(dp(16), dp(10), dp(16), dp(10))
        background = GradientDrawable().apply {
            setColor(0xCC242630.toInt())
            cornerRadius = dp(24).toFloat()
        }
        setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
    }

    private fun settingsButtonParams() = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        Gravity.BOTTOM or Gravity.END,
    ).apply { setMargins(dp(16), dp(16), dp(16), dp(16)) }

    private fun dp(n: Int): Int = (n * resources.displayMetrics.density).toInt()

    private fun gateView(message: String): View {
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
        }
        column.addView(TextView(this).apply {
            text = message
            textSize = 18f
            gravity = Gravity.CENTER
        })
        column.addView(Button(this).apply {
            text = "Open Play Store"
            setOnClickListener { openWebViewPage() }
        })
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
