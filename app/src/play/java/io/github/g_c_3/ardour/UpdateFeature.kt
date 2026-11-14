package io.github.g_c_3.ardour

import android.app.Activity
import android.widget.LinearLayout

// The play flavor has no updater: Play delivers updates (DECISIONS.md 8).
object UpdateFeature {
    @Suppress("UNUSED_PARAMETER")
    fun addRows(activity: Activity, column: LinearLayout, currentVersion: String) = Unit
}
