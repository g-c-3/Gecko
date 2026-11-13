package io.github.g_c_3.ardour

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class SettingsActivity : Activity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(24))
        }
        column.addView(label("Settings", 28f, bold = true))
        column.addView(section("About"))
        column.addView(row("Ardour version", appVersion()))
        column.addView(row("WebView version", EngineAdapter.webViewVersion(this) ?: "not found"))

        val scroll = ScrollView(this).apply {
            addView(column, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            // Edge to edge at target SDK 36: keep content clear of the system bars.
            setOnApplyWindowInsetsListener { v, insets ->
                v.setPadding(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom,
                )
                insets
            }
        }
        setContentView(scroll)
    }

    @Suppress("DEPRECATION")
    private fun appVersion(): String = packageManager.getPackageInfo(packageName, 0).versionName ?: "unknown"

    private fun dp(n: Int): Int = (n * resources.displayMetrics.density).toInt()

    private fun label(text: String, size: Float, bold: Boolean = false) = TextView(this).apply {
        this.text = text
        textSize = size
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setPadding(0, 0, 0, dp(8))
    }

    private fun section(title: String) = label(title, 14f, bold = true).apply {
        setPadding(0, dp(24), 0, dp(8))
    }

    private fun row(name: String, value: String) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, dp(8), 0, dp(8))
        addView(TextView(context).apply { text = name; textSize = 16f })
        addView(TextView(context).apply { text = value; textSize = 14f; alpha = 0.7f })
    }
}
