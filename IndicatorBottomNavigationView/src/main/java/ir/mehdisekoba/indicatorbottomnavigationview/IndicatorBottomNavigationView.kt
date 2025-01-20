package ir.mehdisekoba.indicatorbottomnavigationview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationBarView
import kotlin.math.abs

private const val DEFAULT_SCALE = 1f
private const val MAX_SCALE = 1f
private const val BASE_DURATION = 300L
private const val VARIABLE_DURATION = 300L


class IndicatorBottomNavigationView : BottomNavigationView,
    NavigationBarView.OnItemSelectedListener {

    private var externalSelectedListener: OnItemSelectedListener? = null
    private var animator: ValueAnimator? = null

    private val indicator = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorSurface,
            Color.WHITE
        )
    }

    private val shadow = RectF()
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var path = Path()

    private var indicatorShadowColor = MaterialColors.getColor(
        context,
        com.google.android.material.R.attr.colorPrimary,
        Color.WHITE
    )
    private var indicatorHeaderColor = MaterialColors.getColor(
        context,
        com.google.android.material.R.attr.colorPrimary,
        Color.WHITE
    )
    private var indicatorHeaderHeight = 20f
    private var indicatorShadowVisible = true

    private val defaultSize = 70f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        attrs?.apply {
            val ta = context.obtainStyledAttributes(this, R.styleable.IndicatorBottomNavigationView)
            indicatorHeaderHeight = ta.getDimension(
                R.styleable.IndicatorBottomNavigationView_indicatorHeaderHeight,
                indicatorHeaderHeight
            )
            indicatorHeaderColor = ta.getColor(
                R.styleable.IndicatorBottomNavigationView_indicatorHeaderColor,
                indicatorHeaderColor
            )
            indicatorShadowColor = ta.getColor(
                R.styleable.IndicatorBottomNavigationView_indicatorShadowColor,
                indicatorShadowColor
            )
            indicatorShadowVisible = ta.getBoolean(
                R.styleable.IndicatorBottomNavigationView_indicatorShadowVisible,
                indicatorShadowVisible
            )

            ta.recycle()
        }
    }

    init {
        itemRippleColor = ColorStateList.valueOf(Color.TRANSPARENT)
        itemBackground = ColorDrawable(Color.TRANSPARENT)
        super.setOnItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (externalSelectedListener?.onNavigationItemSelected(item) != false) {
            onItemSelected(item.itemId)
            return true
        }
        return false
    }

    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        externalSelectedListener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        doOnPreDraw {
            onItemSelected(selectedItemId, false)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelAnimator(setEndValues = true)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (isLaidOut) {
            val cornerRadius = indicator.height() / 2f
            paint.color = indicatorHeaderColor
            canvas.drawRoundRect(indicator, cornerRadius, cornerRadius, paint)
            if (indicatorShadowVisible) {
                canvas.drawPath(path, shadowPaint)
            }
        }
    }

    private fun onItemSelected(itemId: Int, animate: Boolean = true) {
        if (!isLaidOut) return

        cancelAnimator(setEndValues = false)

        val itemView = findViewById<View>(itemId) ?: return
        val fromCenterX = indicator.centerX()
        val fromScale = indicator.width() / defaultSize

        animator = ValueAnimator.ofFloat(MAX_SCALE, fromScale, DEFAULT_SCALE).apply {
            addUpdateListener {
                val progress = it.animatedFraction
                val distanceTravelled = linearInterpolation(progress, fromCenterX, itemView.centerX)

                val scale = it.animatedValue as Float
                val indicatorWidth = defaultSize * 1.2f * scale

                val left = distanceTravelled - indicatorWidth + defaultSize / 3
                val top = indicator.top
                val right = distanceTravelled + indicatorWidth - defaultSize / 3
                val bottom = indicator.top + indicatorHeaderHeight

                indicator.set(left, top, right, bottom)

                val leftShadow =
                    (indicator.centerX() - indicator.width() / 2) + indicatorHeaderHeight / 2
                val topShadow = itemView.top + indicatorHeaderHeight
                val rightShadow =
                    (indicator.centerX() + indicator.width() / 2) - indicatorHeaderHeight / 2
                val bottomShadow = itemView.bottom.toFloat()

                shadowPaint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    itemView.height.toFloat(),
                    intArrayOf(getColorWithAlpha(indicatorShadowColor, 40), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )

                shadow.set(leftShadow, topShadow, rightShadow, bottomShadow)
                path = Path()
                path.apply {
                    moveTo(
                        shadow.centerX() - shadow.width() / 2,
                        shadow.centerY() - shadow.height() / 2
                    )
                    lineTo(
                        shadow.centerX() + shadow.width() / 2,
                        shadow.centerY() - shadow.height() / 2
                    )
                    lineTo(
                        shadow.centerX() + shadow.width() / 2 + defaultSize / 2,
                        shadow.centerY() + shadow.height() / 2
                    )
                    lineTo(
                        shadow.centerX() - shadow.width() / 2 - defaultSize / 2,
                        shadow.centerY() + shadow.height() / 2
                    )
                    lineTo(
                        shadow.centerX() - shadow.width() / 2,
                        shadow.centerY() - shadow.height() / 2
                    )
                }

                invalidate()
            }

            interpolator = LinearOutSlowInInterpolator()

            val distanceToMove = abs(fromCenterX - itemView.centerX)
            duration = if (animate) calculateDuration(distanceToMove) else 0L

            start()
        }
    }

    private fun getColorWithAlpha(color: Int, ratio: Int): Int {
        return ColorUtils.setAlphaComponent(color, ((ratio * 255) / 100))
    }

    private fun linearInterpolation(t: Float, a: Float, b: Float) = (1 - t) * a + t * b

    private fun calculateDuration(distance: Float) =
        (BASE_DURATION + VARIABLE_DURATION * (distance / width).coerceIn(0f, 1f)).toLong()

    private val View.centerX get() = left + width / 2f

    private fun cancelAnimator(setEndValues: Boolean) = animator?.let {
        if (setEndValues) {
            it.end()
        } else {
            it.cancel()
        }
        it.removeAllUpdateListeners()
        animator = null
    }
}