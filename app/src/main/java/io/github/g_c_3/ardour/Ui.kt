package io.github.g_c_3.ardour

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
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
    val AMBER = 0xFFF2791A.toInt()

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

    // Ink gradient with a faint ember rim and an ember-tinted shadow: the glow marks the primary action.
    private fun glowPill(context: Context, radiusDp: Int) = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(0xFF33354A.toInt(), 0xFF1E202B.toInt()),
    ).apply {
        cornerRadius = dp(context, radiusDp).toFloat()
        setStroke(dp(context, 1), 0x40FF6A3D)
    }

    private fun styleGlow(view: View, elevationDp: Int, glowAlpha: Int) {
        view.elevation = dp(view.context, elevationDp).toFloat()
        view.outlineSpotShadowColor = (glowAlpha shl 24) or (EMBER and 0x00FFFFFF)
        view.outlineAmbientShadowColor = 0x33000000
    }

    fun pillButton(context: Context, text: String) = TextView(context).apply {
        this.text = text
        textSize = 16f
        typeface = medium()
        setTextColor(0xFFFFFFFF.toInt())
        gravity = Gravity.CENTER
        setPadding(dp(context, 24), dp(context, 15), dp(context, 24), dp(context, 15))
        background = glowPill(context, 28)
        styleGlow(this, 8, 0xB0)
        isClickable = true
        isFocusable = true
        springPress(this)
    }

    // Small pill floating over a page: quieter glow.
    fun floatingPill(context: Context, text: String) = TextView(context).apply {
        this.text = text
        textSize = 14f
        typeface = medium()
        setTextColor(0xFFFFFFFF.toInt())
        gravity = Gravity.CENTER
        setPadding(dp(context, 18), dp(context, 11), dp(context, 18), dp(context, 11))
        background = glowPill(context, 24)
        styleGlow(this, 6, 0x80)
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

// Slim bar with an ember-to-amber gradient. Determinate: a soft glow rides the leading edge.
// Indeterminate: a light sweeps along the track while something is being checked.
class ThinProgress(context: Context) : View(context) {
    private val barHeight = Ui.dp(context, 4).toFloat()
    private val glowRadius = Ui.dp(context, 10).toFloat()
    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Ui.LINE }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glow = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowShader = RadialGradient(
        0f, 0f, glowRadius, intArrayOf(0x80F2791A.toInt(), 0x00F2791A), null, Shader.TileMode.CLAMP,
    )
    private val glowMatrix = Matrix()
    private val rect = RectF()
    private val clip = Path()
    private var phase = 0.5f
    private var sweeper: ValueAnimator? = null

    var value = 0f
        set(v) {
            field = v.coerceIn(0f, 1f)
            invalidate()
        }

    var indeterminate = false
        set(v) {
            field = v
            if (v) startSweep() else stopSweep()
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        fill.shader = LinearGradient(0f, 0f, w.toFloat(), 0f, Ui.EMBER, Ui.AMBER, Shader.TileMode.CLAMP)
    }

    private fun startSweep() {
        if (sweeper != null) return
        if (!ValueAnimator.areAnimatorsEnabled()) {
            phase = 0.5f
            return
        }
        sweeper = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1200
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                phase = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun stopSweep() {
        sweeper?.cancel()
        sweeper = null
    }

    override fun onDetachedFromWindow() {
        stopSweep()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (indeterminate) startSweep()
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val cy = height / 2f
        val top = cy - barHeight / 2f
        val bottom = cy + barHeight / 2f
        val r = barHeight / 2f
        rect.set(0f, top, w, bottom)
        canvas.drawRoundRect(rect, r, r, track)
        if (indeterminate) {
            val seg = w * 0.35f
            val x = -seg + (w + seg) * phase
            clip.reset()
            clip.addRoundRect(rect, r, r, Path.Direction.CW)
            canvas.save()
            canvas.clipPath(clip)
            rect.set(x, top, x + seg, bottom)
            canvas.drawRoundRect(rect, r, r, fill)
            canvas.restore()
        } else if (value > 0f) {
            val end = w * value
            rect.set(0f, top, end, bottom)
            canvas.drawRoundRect(rect, r, r, fill)
            glowMatrix.setTranslate(end, cy)
            glowShader.setLocalMatrix(glowMatrix)
            glow.shader = glowShader
            canvas.drawCircle(end, cy, glowRadius, glow)
        }
    }
}

// Slow breathing light on one element that needs attention; never more than one per screen (DECISIONS.md 40).
class Pulse(private val view: View) {
    private val base = view.elevation
    private var anim: ObjectAnimator? = null

    fun start() {
        if (anim != null || !ValueAnimator.areAnimatorsEnabled()) return
        anim = ObjectAnimator.ofFloat(view, "elevation", base, base + Ui.dp(view.context, 10)).apply {
            duration = 1300
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    fun stop() {
        anim?.cancel()
        anim = null
        view.elevation = base
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
