package io.github.g_c_3.ardour

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File

// Sideload flavor: "Check for updates" runs only on this tap, never on open or in the background (DECISIONS.md 36).
object UpdateFeature {
    fun addRows(activity: Activity, column: LinearLayout, currentVersion: String) {
        fun dp(n: Int) = (n * activity.resources.displayMetrics.density).toInt()
        column.addView(TextView(activity).apply {
            text = "Updates"
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(24), 0, dp(8))
        })
        val action = Button(activity).apply { text = "Check for updates" }
        val status = TextView(activity).apply {
            textSize = 14f
            alpha = 0.7f
            setPadding(0, dp(8), 0, dp(8))
        }
        column.addView(action)
        column.addView(status)
        val rows = UpdateRows(activity, currentVersion, action, status)
        action.setOnClickListener { rows.onTap() }
    }
}

private class UpdateRows(
    val activity: Activity,
    val currentVersion: String,
    val action: Button,
    val status: TextView,
) {
    private var newer: CheckResult.Newer? = null
    private var busy = false

    fun onTap() {
        if (busy) return
        val pending = newer
        if (pending == null) check() else install(pending)
    }

    private fun ui(block: () -> Unit) = activity.runOnUiThread { if (!activity.isDestroyed) block() }

    private fun check() {
        busy = true
        action.isEnabled = false
        status.text = "Checking..."
        Thread {
            val result = UpdateCheck.check(currentVersion)
            ui {
                busy = false
                action.isEnabled = true
                show(result)
            }
        }.start()
    }

    private fun show(result: CheckResult) {
        when (result) {
            is CheckResult.Newer -> {
                newer = result
                status.text = "Version ${result.version} is available.\n\n${result.notes}"
                action.text = "Download and install"
            }
            CheckResult.UpToDate -> status.text = "Ardour is up to date ($currentVersion)."
            CheckResult.RateLimited -> status.text = "Try again later."
            is CheckResult.Failed -> status.text = "Could not check for updates: ${result.reason}."
        }
    }

    private fun install(update: CheckResult.Newer) {
        if (!activity.packageManager.canRequestPackageInstalls()) {
            status.text = "Allow Ardour to install apps in the screen that opens, go back, then tap again."
            activity.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${activity.packageName}")),
            )
            return
        }
        busy = true
        action.isEnabled = false
        status.text = "Downloading... 0%"
        Thread {
            try {
                val file = UpdateCheck.download(update, File(activity.cacheDir, "update")) { pct ->
                    ui { status.text = "Downloading... $pct%" }
                }
                ui { status.text = "Checksum verified. Asking Android to install..." }
                ApkInstaller.install(activity, file)
                ui {
                    busy = false
                    action.isEnabled = true
                    status.text = "Confirm the install in the Android screen."
                }
            } catch (e: SecurityException) {
                ui {
                    busy = false
                    action.isEnabled = true
                    status.text = "The download did not match its checksum. Nothing was installed."
                }
            } catch (e: Exception) {
                ui {
                    busy = false
                    action.isEnabled = true
                    status.text = "Update failed. Nothing was installed."
                }
            }
        }.start()
    }
}
