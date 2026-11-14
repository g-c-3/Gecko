package io.github.g_c_3.ardour

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

// Hand-built look shared by every screen (DECISIONS.md 30, 38): plain views, drawn here, spring motion.
object Ui {
    val INK = 0xFF242630.toInt()
    val EMBER = 0xFFE8451D.toInt()
    val PAPER = 0xFFF6F5F3.toInt()
    val CARD = 0xFFFFFFFF.toInt()
    val LINE = 0xFFE8E6E2.toInt()
    val MUTED = 0xFF646875.toInt()

    fun dp(context: Context, n: Int): Int = (n * context.resources.displayMetrics.density + 0.5f).toInt()

    private fun medium(): Typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

    fun title(context: Context, text: String) = TextView(context).apply {
        this.text = text
        textSize = 32f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        setTextColor(INK)
        letterSpacing = -0.02f
    }

    fun sectionLabel(context: Context, text: String) = TextView(context).apply {
        this.text = text.uppercase()
        textSize = 12f
        typeface = medium()
        setTextColor(MUTED)
        letterSpacing = 0.08f
        setPadding(dp(context, 4), dp(context, 28), 0, dp(context, 10))
    }

    fun body(context: Context, text: String) = TextView(context).apply {
        this.text = text
        textSize = 14f
        setTextColor(MUTED)
        setLineSpacing(0f, 1.25f)
    }

    fun card(context: Context) = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        background = GradientDrawable().apply {
            setColor(CARD)
            cornerRadius = dp(context, 20).toFloat()
            setStroke(dp(context, 1), LINE)
        }
        setPadding(dp(context, 20), dp(context, 8), dp(context, 20), dp(context, 8))
    }

    fun row(context: Context, label: String, value: String) = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, dp(context, 14), 0, dp(context, 14))
        addView(TextView(context).apply {
            text = label
            textSize = 16f
            typeface = medium()
            setTextColor(INK)
        })
        addView(TextView(context).apply {
            text = value
            textSize = 14f
            setTextColor(MUTED)
            setPadding(0, dp(context, 2), 0, 0)
        })
    }

    fun divider(context: Context) = View(context).apply {
        setBackgroundColor(LINE)
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
    }

    fun pillButton(context: Context, text: String) = TextView(context).apply {
        this.text = text
        textSize = 16f
        typeface = medium()
        setTextColor(0xFFFFFFFF.toInt())
        gravity = Gravity.CENTER
        setPadding(dp(context, 24), dp(context, 15), dp(context, 24), dp(context, 15))
        background = GradientDrawable().apply {
            setColor(INK)
            cornerRadius = dp(context, 28).toFloat()
        }
        elevation = dp(context, 3).toFloat()
        isClickable = true
        isFocusable = true
        springPress(this)
    }

    // Small pill floating over a page.
    fun floatingPill(context: Context, text: String) = TextView(context).apply {
        this.text = text
        textSize = 14f
        typeface = medium()
        setTextColor(0xFFFFFFFF.toInt())
        gravity = Gravity.CENTER
        setPadding(dp(context, 18), dp(context, 11), dp(context, 18), dp(context, 11))
        background = GradientDrawable().apply {
            setColor(0xEB242630.toInt())
            cornerRadius = dp(context, 24).toFloat()
        }
        elevation = dp(context, 6).toFloat()
        isClickable = true
        isFocusable = true
        springPress(this)
    }

    fun setBusy(view: View, busy: Boolean) {
        view.isEnabled = !busy
        view.alpha = if (busy) 0.55f else 1f
    }

    // Settles with a small overshoot. Animator duration scale 0 (system reduce-animations) ends it at once.
    fun enter(views: List<View>) {
        views.forEachIndexed { i, v ->
            v.alpha = 0f
            v.translationY = dp(v.context, 18).toFloat()
            v.animate().alpha(1f).translationY(0f)
                .setStartDelay(i * 60L).setDuration(560).setInterpolator(SpringInterpolator()).start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun springPress(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN ->
                    v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(90)
                        .setInterpolator(DecelerateInterpolator()).start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(420)
                        .setInterpolator(SpringInterpolator()).start()
            }
            false
        }
    }
}

// Underdamped spring (damping ratio 0.62): about 8% overshoot, settled by t = 1.
class SpringInterpolator : Interpolator {
    override fun getInterpolation(t: Float): Float {
        if (t >= 1f) return 1f
        val zeta = 0.62f
        val wn = 14f
        val wd = wn * sqrt(1f - zeta * zeta)
        val k = zeta / sqrt(1f - zeta * zeta)
        return 1f - exp(-zeta * wn * t) * (cos(wd * t) + k * sin(wd * t))
    }
}

class ThinProgress(context: Context) : View(context) {
    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.LINE }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.EMBER }
    private val rect = RectF()
    var value = 0f
        set(v) {
            field = v.coerceIn(0f, 1f)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        val r = height / 2f
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, r, r, track)
        rect.set(0f, 0f, width * value, height.toFloat())
        if (value > 0f) canvas.drawRoundRect(rect, r, r, fill)
    }
}

class BackButton(context: Context) : View(context) {
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Ui.CARD
        setShadowLayer(Ui.dp(context, 3).toFloat(), 0f, Ui.dp(context, 1).toFloat(), 0x22000000)
    }
    private val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Ui.LINE
        style = Paint.Style.STROKE
        strokeWidth = Ui.dp(context, 1).toFloat()
    }
    private val chevron = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Ui.INK
        style = Paint.Style.STROKE
        strokeWidth = Ui.dp(context, 2).toFloat()
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val path = Path()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        isClickable = true
        isFocusable = true
        Ui.springPress(this)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(width, height) / 2f - Ui.dp(context, 4)
        canvas.drawCircle(cx, cy, radius, ring)
        canvas.drawCircle(cx, cy, radius, edge)
        val s = radius * 0.32f
        path.reset()
        path.moveTo(cx + s * 0.5f, cy - s)
        path.lineTo(cx - s * 0.5f, cy)
        path.lineTo(cx + s * 0.5f, cy + s)
        canvas.drawPath(path, chevron)
    }
}
