package io.github.g_c_3.ardour

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

class MainActivity : Activity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this)
        val match = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val gateMessage = EngineAdapter.launchGateMessage(this)
        if (gateMessage != null) {
            // Gate unmet: message only, nothing is loaded (DECISIONS.md 28).
            root.addView(
                TextView(this).apply {
                    text = gateMessage
                    textSize = 18f
                    gravity = Gravity.CENTER
                    setPadding(64, 64, 64, 64)
                },
                match,
            )
        } else {
            root.addView(EngineAdapter.createView(this) { root.post { recreate() } }, match)
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

    override fun onDestroy() {
        EngineAdapter.destroy()
        super.onDestroy()
    }
}
