package com.liuyuchuan.easymusic.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.liuyuchuan.easymusic.R

/**
 * Created by liuyuchuan on 16/05/2018.
 */
class RefreshStateView : View, View.OnClickListener {

    companion object {
        const val STATE_EMPTY = 1
        const val STATE_ERROR = 2
        const val STATE_REFRESHING = 3
        const val STATE_HIDE = 4
    }

    private val loadingDrawable = LoadingDrawable(context) // implement of Drawable & Animatable
    private val dirtyRect = Rect()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val fontTop: Int
    private val fontHeight: Int

    private var centerX = 0
    private var centerY = 0

    var state = STATE_HIDE
        private set

    var refreshingText = "" // refreshing..

    var text = ""
        private set

    var retryCallback: RetryCallback? = null

    /**
     * call contentView.visibility = VISIBLE when RefreshStateView hide() called
     * call contentView.visibility = GONE when RefreshStateView show*() called
     *
     * the visibility of contentView and RefreshStateView is mutually exclusive.
     */
    private var contentView: View? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        visibility = View.GONE // default state is STATE_HIDE

        val dm = resources.displayMetrics

        val a = context.obtainStyledAttributes(attrs, R.styleable.RefreshStateView)

        val customTextSize = a.getDimension(R.styleable.RefreshStateView_textSize, 0f)
        val textSize = if (customTextSize != 0f) customTextSize else {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, dm) // 14sp
        }
        val customRingSize = a.getDimension(R.styleable.RefreshStateView_ringSize, 0f)
        val ringSize = if (customRingSize != 0f) customRingSize else {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, dm) // 48dp
        }.toInt()

        a.recycle()

        textPaint.color = Color.GRAY
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER

        val fontMetricsInt = textPaint.fontMetricsInt
        fontTop = fontMetricsInt.top
        fontHeight = fontMetricsInt.bottom - fontMetricsInt.top

        setOnClickListener(this)

        loadingDrawable.callback = this
        loadingDrawable.setBounds(0, 0, ringSize, ringSize)
    }

    // wrap_content is not support yet
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2
        centerY = h / 2
    }

    /*
        FontMetrics Example:

        ------------------------------ <-- bottom of last line
             space between lines       <-- leading  (e.g. 0)
        ------------------------------ <-- top      (e.g. -53)
                _       _  _______________ ascent   (e.g. -45)
           __ _| |_ __ | |__   __ _
          / _` | | '_ \| '_ \ / _` |
         | (_| | | |_) | | | | (_| |
          \__,_|_| .__/|_| |_|\__,_| _____ baseline (y = 0 in API canvas.drawText)
                 |_| _____________________ descent  (e.g. 12)
        ------------------------------ <-- bottom   (e.g. 14)
    */

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // single line text only!!
        val textWidth = textPaint.measureText(text)

        if (state == STATE_REFRESHING) {
            if (textWidth == 0f) {
                val drawableBounds = loadingDrawable.bounds
                val drawableWidth = drawableBounds.width()
                val drawableHeight = drawableBounds.height()

                val drawableLeft = centerX - drawableWidth / 2f
                val drawableTop = centerY - drawableHeight / 2f
                dirtyRect.set(drawableBounds)
                dirtyRect.offset(drawableLeft.toInt(), drawableTop.toInt())

                // canvas.save() // no need
                canvas.translate(drawableLeft, drawableTop)
                loadingDrawable.draw(canvas)
                // canvas.restore() // no need
            } else {
                val drawableBounds = loadingDrawable.bounds
                val drawableWidth = drawableBounds.width()
                val drawableHeight = drawableBounds.height()

                val contentHeight = drawableHeight + fontHeight

                val drawableLeft = centerX - drawableWidth / 2f
                val drawableTop = centerY - contentHeight / 2f
                dirtyRect.set(drawableBounds)
                dirtyRect.offset(drawableLeft.toInt(), drawableTop.toInt())

                canvas.save()
                canvas.translate(drawableLeft, drawableTop)
                loadingDrawable.draw(canvas)
                canvas.restore()

                canvas.drawText(
                        text,
                        centerX.toFloat(),
                        centerY - contentHeight / 2 + drawableHeight - fontTop.toFloat(),
                        textPaint
                )
            }
        } else {
            canvas.drawText(
                    text,
                    centerX.toFloat(),
                    centerY - fontHeight / 2f - fontTop.toFloat(),
                    textPaint
            )
        }
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)

        if (state == STATE_REFRESHING) {
            invalidate(dirtyRect)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (state == STATE_REFRESHING) {
            loadingDrawable.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loadingDrawable.stop()
    }

    override fun onClick(v: View?) {
        if (state != STATE_REFRESHING) {
            val cb = retryCallback
            if (cb != null && cb.onRetry(this)) {
                show(refreshingText, STATE_REFRESHING)
            }
        }
    }

    fun showEmpty(msg: String) {
        show(msg, STATE_EMPTY)
    }

    fun showError(msg: String) {
        show(msg, STATE_ERROR)
    }

    /**
     * change state and show refreshing view only, will not call RetryCallback
     */
    fun showRefreshing(msg: String) {
        refreshingText = msg
        show(refreshingText, STATE_REFRESHING)
    }

    fun hide() {
        if (state == STATE_HIDE) {
            return
        }

        state = STATE_HIDE
        if (visibility != GONE) {
            visibility = GONE
        }

        loadingDrawable.stop()

        // make contentView VISIBLE
        contentView?.takeIf { visibility != View.VISIBLE }?.visibility = View.VISIBLE

        invalidate()
    }

    private fun show(msg: String, nextState: Int) {
        if (text == msg && state == nextState) {
            return // nothing changed
        }

        state = nextState

        text = msg
        if (nextState == STATE_REFRESHING) {
            loadingDrawable.start()
        } else {
            loadingDrawable.stop()
        }
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }

        // make contentView GONE
        contentView?.takeIf { visibility != View.GONE }?.visibility = View.GONE

        invalidate()
    }

    fun bindContentView(view: View?) {
        this.contentView = view
    }

    interface RetryCallback {
        /**
         * return true if it is retrying, and state will switch to STATE_REFRESHING.
         */
        fun onRetry(view: RefreshStateView): Boolean
    }
}
