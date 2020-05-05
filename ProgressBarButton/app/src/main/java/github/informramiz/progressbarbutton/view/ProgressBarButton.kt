package github.informramiz.progressbarbutton.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.postDelayed
import github.informramiz.progressbarbutton.R

/**
 * Created by Ramiz Raja on 01/05/2020
 */

class ProgressBarButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    @FloatRange(from = 0.0, to = 1.0)
    var progress = 0f
        set(value) {
            field = value.coerceAtLeast(0f).coerceAtMost(1f)
            onProgressUpdated()
        }

    private var state: State =
        State.NORMAL
        set(value) {
            field = value
            onStateUpdated()
        }

    private val sweepAngle: Float
        get() = progress * 360f
    private var horizontalAnimationRect = RectF()
    private val arcOvalRect = RectF()

    @ColorInt
    private var textColor: Int = ContextCompat.getColor(context,
        R.color.progress_button_text_color
    )
    @ColorInt
    private var normalBackgroundColor = ContextCompat.getColor(context,
        R.color.progress_button_color
    )
    @ColorInt
    private var horizontalProgressColor = ContextCompat.getColor(context,
        R.color.progress_button_horizontal_progress_color
    )
    @ColorInt
    private var circularProgressColor = ContextCompat.getColor(context,
        R.color.progress_button_circular_progress_color
    )
    @Dimension
    private val suggestedMinWidth = resources.getDimensionPixelSize(R.dimen.progress_button_min_width)
    @Dimension
    private val suggestedMinHeight = resources.getDimensionPixelSize(R.dimen.progress_button_min_height)

    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = 40f
        color = textColor
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs,
            R.styleable.ProgressBarButton
        ) {
            progress = getFloat(R.styleable.ProgressBarButton_progress, 0f).coerceAtMost(1f).coerceAtLeast(0f)
            textColor = getColor(R.styleable.ProgressBarButton_textColor, textColor)
            normalBackgroundColor = getColor(R.styleable.ProgressBarButton_normalBackgroundColor, normalBackgroundColor)
            horizontalProgressColor = getColor(R.styleable.ProgressBarButton_horizontalProgressColor, horizontalProgressColor)
            circularProgressColor = getColor(R.styleable.ProgressBarButton_circularProgressColor, circularProgressColor)
        }
    }

    private fun onProgressUpdated() {
        if (progress > 0f) {
            state =
                State.LOADING
        } else if (progress == 0f && state == State.LOADING){
            state =
                State.NORMAL
        }
        horizontalAnimationRect.right = width * progress
        invalidate()

        if (progress == 1f) {
            postDelayed(100) {
                resetState()
            }
        }
    }

    private fun onStateUpdated() {
        isClickable = state != State.LOADING
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCircleDimensions()
        calculateHorizontalAnimationRectangleDimensions()
    }

    private fun calculateCircleDimensions(start: Float = width/2f) {
        val radius = height / 4.5f
        val circleInsetVertical = (height - radius * 2) / 2
        arcOvalRect.left = start
        arcOvalRect.top = circleInsetVertical
        arcOvalRect.right = arcOvalRect.left + radius * 2
        arcOvalRect.bottom = arcOvalRect.top + radius * 2
    }

    private fun calculateHorizontalAnimationRectangleDimensions() {
        horizontalAnimationRect.apply {
            top = 0f
            left = 0f
            bottom = height.toFloat()
            right = width * progress
        }
    }

    private fun resetState() {
        progress = 0f
        state =
            State.NORMAL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(normalBackgroundColor)
        paint.color = horizontalProgressColor
        canvas.drawRect(horizontalAnimationRect, paint)

        //draw text
        paint.color = textColor
        val text = resources.getString(state.labelResId)
        val textWidth = paint.measureText(text)
        val textStartX = width/2 - textWidth/2f
        canvas.drawText(text, textStartX, height/1.75f, paint)

        //draw circular progress
        paint.color = circularProgressColor
        calculateCircleDimensions(textStartX + textWidth + 10)
        canvas.drawArc(arcOvalRect, 0f, sweepAngle, true, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //define views desired/suggested min width (padding is also part of the view)
        val minWidth = suggestedMinWidth + paddingStart + paddingEnd
        //resolve and select either our suggested width (minWidth)
        // or specified by the parent of this view (widthMeasureSpec)
        val finalWidth = resolveSizeAndState(minWidth, widthMeasureSpec, 0)

        //define views desired/suggested min height (padding is also part of the view)
        val minHeight = suggestedMinHeight + paddingTop + paddingBottom
        //resolve and select either our suggested height (minHeight)
        // or specified by the parent of this view (heightMeasureSpec)
        val finalHeight = resolveSizeAndState(minHeight, heightMeasureSpec, 0)

        //let the parent know the possible final width and height and let it decide what to
        //do next
        setMeasuredDimension(finalWidth, finalHeight)
    }

    enum class State(@field:StringRes val labelResId: Int) {
        NORMAL(R.string.button_state_normal),
        LOADING(R.string.button_loading),
    }
}