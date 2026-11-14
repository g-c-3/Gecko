package io.github.g_c_3.ardour

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File

// Sideload flavor: "Check for updates" runs only on this tap, never on open or in the background (DECISIONS.md 36).
object UpdateFeature {
    fun addRows(activity: Activity, column: LinearLayout, currentVersion: String) {
        column.addView(Ui.sectionLabel(activity, "Updates"))
        val card = Ui.card(activity)
        val status = Ui.body(activity, "Check whether a newer Ardour is available.").apply {
            setPadding(0, Ui.dp(activity, 12), 0, 0)
        }
        val progress = ThinProgress(activity).apply { visibility = View.GONE }
        val action = Ui.pillButton(activity, "Check for updates")
        card.addView(status)
        card.addView(progress, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(activity, 20)).apply {
            topMargin = Ui.dp(activity, 6)
        })
        card.addView(action, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = Ui.dp(activity, 16)
            bottomMargin = Ui.dp(activity, 12)
        })
        column.addView(card, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val rows = UpdateRows(activity, currentVersion, action, status, progress, Pulse(action))
        action.setOnClickListener { rows.onTap() }
    }
}

private class UpdateRows(
    val activity: Activity,
    val currentVersion: String,
    val action: TextView,
    val status: TextView,
    val progress: ThinProgress,
    val pulse: Pulse,
) {
    private var newer: CheckResult.Newer? = null
    private var busy = false

    fun onTap() {
        if (busy) return
        val pending = newer
        if (pending == null) check() else install(pending)
    }

    private fun ui(block: () -> Unit) = activity.runOnUiThread { if (!activity.isDestroyed) block() }

    private fun setBusy(value: Boolean) {
        busy = value
        Ui.setBusy(action, value)
        // The light pulses only while an update waits to be installed and nothing is running.
        if (value) pulse.stop() else if (newer != null) pulse.start()
    }

    private fun check() {
        setBusy(true)
        progress.value = 0f
        progress.indeterminate = true
        progress.visibility = View.VISIBLE
        status.text = "Checking…"
        Thread {
            val result = UpdateCheck.check(currentVersion)
            ui {
                progress.indeterminate = false
                progress.visibility = View.GONE
                setBusy(false)
                show(result)
            }
        }.start()
    }

    private fun show(result: CheckResult) {
        when (result) {
            is CheckResult.Newer -> {
                newer = result
                // Release notes start with the engine requirement; the checksums that follow stay out of sight.
                val summary = result.notes.substringBefore("\n\n").take(280)
                status.text = "Version ${result.version} is available.\n\n$summary"
                action.text = "Download and install"
                pulse.start()
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
        setBusy(true)
        progress.indeterminate = false
        progress.value = 0f
        progress.visibility = View.VISIBLE
        status.text = "Downloading… 0%"
        Thread {
            try {
                val file = UpdateCheck.download(update, File(activity.cacheDir, "update")) { pct ->
                    ui {
                        progress.value = pct / 100f
                        status.text = "Downloading… $pct%"
                    }
                }
                ui { status.text = "Checksum verified. Asking Android to install…" }
                ApkInstaller.install(activity, file)
                ui {
                    setBusy(false)
                    status.text = "Confirm the install in the Android screen."
                }
            } catch (e: SecurityException) {
                ui {
                    setBusy(false)
                    progress.visibility = View.GONE
                    status.text = "The download did not match its checksum. Nothing was installed."
                }
            } catch (e: Exception) {
                ui {
                    setBusy(false)
                    progress.visibility = View.GONE
                    status.text = "Update failed. Nothing was installed."
                }
            }
        }.start()
    }
}
