package com.liuyuchuan.easymusic.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.annotation.Size
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.liuyuchuan.easymusic.R

/**
 * Created by liuyuchuan on 16/05/2018.
 */
class LoadingDrawable(context: Context) : Drawable(), Animatable, Animator.AnimatorListener {

    companion object {
        private const val DEFAULT_DURATION = 1500L

        private const val NUM_POINTS = 5

        private const val MAX_SWIPE_DEGREES = 0.8f * 360
        private const val FULL_GROUP_ROTATION = 3.0f * 360

        private val LEVEL_SWEEP_ANGLE_OFFSETS = floatArrayOf(1.0f, 7.0f / 8.0f, 5.0f / 8.0f)

        private const val START_TRIM_DURATION_OFFSET = 0.5f
        private const val END_TRIM_DURATION_OFFSET = 1.0f

        private const val DEFAULT_SIZE = 48 // dp

        private val LINEAR_INTERPOLATOR = LinearInterpolator()
        private val MATERIAL_INTERPOLATOR = FastOutSlowInInterpolator()
        private val ACCELERATE_INTERPOLATOR = AccelerateInterpolator()
        private val DECELERATE_INTERPOLATOR = DecelerateInterpolator()
    }

    private val paint: Paint

    private val circleBounds = RectF() // draw arc in bounds

    private var rotationCount = 0f
    private var groupRotation = 0f

    private var endDegrees = 0f
    private var startDegrees = 0f
    private var originEndDegrees = 0f
    private var originStartDegrees = 0f

    @Size(3)
    private val levelColors: IntArray
    @Size(3)
    private val levelSwipeDegrees = floatArrayOf(0f, 0f, 0f)

    private val progressAnimator: ValueAnimator
    private val animatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        computeProgress(animation.animatedValue as Float)
        invalidateSelf()
    }

    init {
        val density = context.resources.displayMetrics.density
        val size = (DEFAULT_SIZE * density).toInt() // 48dp

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = size / 12f // 4dp
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        setBounds(0, 0, size, size)

        val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
        levelColors = intArrayOf(
                ColorUtils.setAlphaComponent(colorAccent, 0xff / 3),
                ColorUtils.setAlphaComponent(colorAccent, 0xff / 3 * 2),
                ColorUtils.setAlphaComponent(colorAccent, 0xff)
        )

        progressAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        progressAnimator.repeatCount = ValueAnimator.INFINITE
        progressAnimator.repeatMode = ValueAnimator.RESTART
        progressAnimator.duration = DEFAULT_DURATION
        progressAnimator.interpolator = LinearInterpolator() // replace the default interpolator
        progressAnimator.addUpdateListener(animatorUpdateListener)
        progressAnimator.addListener(this)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        val halfSize = Math.min(bounds.width(), bounds.height()) / 2
        val centerX = bounds.centerX()
        val centerY = bounds.centerY()
        circleBounds.set(
                (centerX - halfSize).toFloat(),
                (centerY - halfSize).toFloat(),
                (centerX + halfSize).toFloat(),
                (centerY + halfSize).toFloat()
        )
        val strokeWidth = halfSize / 6f
        paint.strokeWidth = strokeWidth // size / 12
        circleBounds.inset(strokeWidth, strokeWidth) // shrink for paint stroke width

        /*
            __   _____________________   _____ border
           /    |     ___________     |  __
          /     |    /  _______  \    |    \__ stroke width = size / 12
         |      |   /##/# ___ #\##\   |  __/
         |      |  |##/##/   \##\##|  |
        size    |  |##|#|     |#|##|  |  <---- arc center
         |      |  |##\##\___/##/##|  |
         |      |   \##\_______/##/   |  _____ radius = circleBounds.width() / 2 = size * 5 / 6
          \     |    \___________/    |    \__ inset = strokeWidth = size / 12
           \__  |_____________________|  __/
        */
    }

    override fun getIntrinsicWidth(): Int = bounds.width()
    override fun getIntrinsicHeight(): Int = bounds.height()

    override fun draw(canvas: Canvas) {
        canvas.save()

        bounds.run {
            canvas.rotate(groupRotation, centerX().toFloat(), centerY().toFloat())
        }

        repeat(3) { i ->
            if (levelSwipeDegrees[i] != 0f) {
                paint.color = levelColors[i]
                canvas.drawArc(circleBounds, endDegrees, levelSwipeDegrees[i], false, paint)
            }
        }

        canvas.restore()
    }

    private fun computeProgress(progress: Float) {
        // Moving the prepare trim only occurs in the first 50% of a single ring animation
        if (progress <= START_TRIM_DURATION_OFFSET) {
            val startTrimProgress = progress / START_TRIM_DURATION_OFFSET
            startDegrees = originStartDegrees + MAX_SWIPE_DEGREES * MATERIAL_INTERPOLATOR.getInterpolation(startTrimProgress)

            val swipeDegrees = endDegrees - startDegrees
            val levelSwipeDegreesProgress = Math.abs(swipeDegrees) / MAX_SWIPE_DEGREES

            val level1Increment = DECELERATE_INTERPOLATOR.getInterpolation(levelSwipeDegreesProgress) - LINEAR_INTERPOLATOR.getInterpolation(levelSwipeDegreesProgress)
            val level3Increment = ACCELERATE_INTERPOLATOR.getInterpolation(levelSwipeDegreesProgress) - LINEAR_INTERPOLATOR.getInterpolation(levelSwipeDegreesProgress)

            levelSwipeDegrees[0] = -swipeDegrees * LEVEL_SWEEP_ANGLE_OFFSETS[0] * (1.0f + level1Increment)
            levelSwipeDegrees[1] = -swipeDegrees * LEVEL_SWEEP_ANGLE_OFFSETS[1] * 1.0f
            levelSwipeDegrees[2] = -swipeDegrees * LEVEL_SWEEP_ANGLE_OFFSETS[2] * (1.0f + level3Increment)
        }

        // Moving the end trim starts after 50% of a single ring animation
        if (progress > START_TRIM_DURATION_OFFSET) {
            val endTrimProgress = (progress - START_TRIM_DURATION_OFFSET) / (END_TRIM_DURATION_OFFSET - START_TRIM_DURATION_OFFSET)
            endDegrees = originEndDegrees + MAX_SWIPE_DEGREES * MATERIAL_INTERPOLATOR.getInterpolation(endTrimProgress)

            val swipeDegrees = endDegrees - startDegrees
            val levelSwipeDegreesProgress = Math.abs(swipeDegrees) / MAX_SWIPE_DEGREES

            when {
                levelSwipeDegreesProgress > LEVEL_SWEEP_ANGLE_OFFSETS[1] -> {
                    levelSwipeDegrees[0] = -swipeDegrees
                    levelSwipeDegrees[1] = MAX_SWIPE_DEGREES * LEVEL_SWEEP_ANGLE_OFFSETS[1]
                    levelSwipeDegrees[2] = MAX_SWIPE_DEGREES * LEVEL_SWEEP_ANGLE_OFFSETS[2]
                }
                levelSwipeDegreesProgress > LEVEL_SWEEP_ANGLE_OFFSETS[2] -> {
                    levelSwipeDegrees[0] = 0f
                    levelSwipeDegrees[1] = -swipeDegrees
                    levelSwipeDegrees[2] = MAX_SWIPE_DEGREES * LEVEL_SWEEP_ANGLE_OFFSETS[2]
                }
                else -> {
                    levelSwipeDegrees[0] = 0f
                    levelSwipeDegrees[1] = 0f
                    levelSwipeDegrees[2] = -swipeDegrees
                }
            }
        }

        groupRotation = FULL_GROUP_ROTATION / NUM_POINTS * progress + FULL_GROUP_ROTATION * (rotationCount / NUM_POINTS)
    }

    override fun onAnimationRepeat(animation: Animator?) {
        originEndDegrees = endDegrees
        originStartDegrees = endDegrees

        startDegrees = endDegrees
        rotationCount = (rotationCount + 1) % NUM_POINTS
    }

    override fun onAnimationStart(animation: Animator?) {
        rotationCount = 0f
    }

    override fun onAnimationEnd(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun scheduleSelf(what: Runnable?, `when`: Long) {
        super.scheduleSelf(what, `when`)
        start()
        // Log.e("LoadingDrawable", "scheduleSelf")
    }

    override fun unscheduleSelf(what: Runnable?) {
        super.unscheduleSelf(what)
        stop()
        // Log.e("LoadingDrawable", "unscheduleSelf")
    }

    override fun isRunning(): Boolean = progressAnimator.isRunning

    override fun start() {
        if (progressAnimator.isRunning) {
            return
        }

        // reset value
        originEndDegrees = 0f
        originStartDegrees = 0f

        endDegrees = 0f
        startDegrees = 0f

        levelSwipeDegrees[0] = 0f
        levelSwipeDegrees[1] = 0f
        levelSwipeDegrees[2] = 0f

        // Log.e("LoadingDrawable", "prepare")
        progressAnimator.start()
    }

    override fun stop() {
        if (!progressAnimator.isRunning) {
            return
        }

        // Log.e("LoadingDrawable", "stop")
        progressAnimator.end()
    }
}
