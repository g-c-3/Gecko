package io.github.g_c_3.ardour

import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import org.json.JSONObject

internal sealed class CheckResult {
    class Newer(val version: String, val notes: String, val apkUrl: String, val sha256: String) : CheckResult()
    object UpToDate : CheckResult()
    object RateLimited : CheckResult()
    class Failed(val reason: String) : CheckResult()
}

// The approved update connection (DECISIONS.md 33, 36): one GET to the latest release, then the APK from that release.
// Nothing is sent but the requests; the fixed User-Agent keeps the platform default (device details) out of them.
internal object UpdateCheck {
    private const val LATEST = "https://api.github.com/repos/g-c-3/weber/releases/latest"
    private const val DOWNLOAD_PREFIX = "https://github.com/g-c-3/weber/releases/download/"

    // Compared number by number, never as text.
    fun parseVersion(text: String): List<Int>? {
        val parts = text.trim().removePrefix("v").split(".")
        if (parts.size != 3) return null
        return parts.map { it.toIntOrNull() ?: return null }
    }

    fun isNewer(latest: List<Int>, current: List<Int>): Boolean {
        for (i in 0..2) {
            if (latest[i] != current[i]) return latest[i] > current[i]
        }
        return false
    }

    fun check(currentVersion: String): CheckResult {
        val conn = open(LATEST)
        try {
            val code = conn.responseCode
            val body = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""
            if (code == 429 || (code == 403 && body.contains("rate limit", ignoreCase = true))) {
                return CheckResult.RateLimited
            }
            if (code !in 200..299) return CheckResult.Failed("server answered $code")
            val json = JSONObject(body)
            val latest = parseVersion(json.getString("tag_name")) ?: return CheckResult.Failed("unreadable version")
            val current = parseVersion(currentVersion) ?: return CheckResult.Failed("unreadable installed version")
            if (!isNewer(latest, current)) return CheckResult.UpToDate
            val version = latest.joinToString(".")
            val name = "Ardour-$version.apk"
            val notes = json.optString("body")
            val assets = json.getJSONArray("assets")
            var url: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.getString("name") == name) url = asset.getString("browser_download_url")
            }
            if (url == null || !url.startsWith(DOWNLOAD_PREFIX)) return CheckResult.Failed("APK not found in the release")
            val sha = Regex("([0-9a-fA-F]{64})\\s+\\*?" + Regex.escape(name))
                .find(notes)?.groupValues?.get(1)?.lowercase()
                ?: return CheckResult.Failed("checksum not found in the release notes")
            return CheckResult.Newer(version, notes.trim(), url, sha)
        } catch (e: IOException) {
            return CheckResult.Failed("no connection")
        } catch (e: Exception) {
            return CheckResult.Failed("unreadable answer")
        } finally {
            conn.disconnect()
        }
    }

    // Verifies SHA-256 against the release notes; a mismatch deletes the file and throws SecurityException.
    fun download(update: CheckResult.Newer, dir: File, onProgress: (Int) -> Unit): File {
        dir.mkdirs()
        val file = File(dir, "Ardour-${update.version}.apk")
        val conn = open(update.apkUrl)
        try {
            if (conn.responseCode !in 200..299) throw IOException("server answered ${conn.responseCode}")
            val total = conn.contentLengthLong
            val digest = MessageDigest.getInstance("SHA-256")
            conn.inputStream.use { input ->
                file.outputStream().use { out ->
                    val buffer = ByteArray(64 * 1024)
                    var done = 0L
                    var last = -1
                    while (true) {
                        val n = input.read(buffer)
                        if (n < 0) break
                        out.write(buffer, 0, n)
                        digest.update(buffer, 0, n)
                        done += n
                        if (total > 0) {
                            val pct = (done * 100 / total).toInt()
                            if (pct != last) {
                                last = pct
                                onProgress(pct)
                            }
                        }
                    }
                }
            }
            val actual = digest.digest().joinToString("") { "%02x".format(it) }
            if (actual != update.sha256) {
                file.delete()
                throw SecurityException("checksum mismatch")
            }
            return file
        } finally {
            conn.disconnect()
        }
    }

    private fun open(url: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 30000
            setRequestProperty("User-Agent", "Ardour")
            setRequestProperty("Accept", "application/vnd.github+json")
        }
}
