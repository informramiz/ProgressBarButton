package github.informramiz.progressbarbutton

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes

/**
 * Created by Ramiz Raja on 01/05/2020
 */

class ProgressBarButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var progress = 0f
        set(value) {
            field = value
            onProgressUpdated()
        }

    var state: State = State.NORMAL
        set(value) {
            field = value
            onStateUpdated()
        }

    private val sweepAngle: Float
        get() = progress * 360f
    private var horizontalAnimationRect = RectF()
    private val arcOvalRect = RectF()
    @ColorInt
    private val textColor: Int = Color.BLACK
    private val suggestedMinWidth = resources.getDimensionPixelSize(R.dimen.progress_button_min_width)
    private val suggestedMinHeight = resources.getDimensionPixelSize(R.dimen.progress_button_min_height)

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
        textSize = 40f
    }

    init {
        setBackgroundColor(Color.GREEN)
    }

    private fun onProgressUpdated() {
        horizontalAnimationRect.right = width * progress
        invalidate()
    }

    private fun onStateUpdated() {
        isClickable = state != State.LOADING
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCircleDimensions()
        calculateHorizontalAnimationRectangleDimensions()
        //TODO: Remove this as it is here just for testing
        progress = 0.9f
        state = State.LOADING
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.CYAN
        canvas.drawRect(horizontalAnimationRect, paint)

        //draw text
        paint.color = textColor
        val text = resources.getString(state.labelResId)
        val textWidth = paint.measureText(text)
        val textStartX = width/2 - textWidth/2f
        canvas.drawText(text, textStartX, height/1.75f, paint)

        //draw circular progress
        paint.color = Color.GRAY
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
        FAILED(R.string.button_loading_failed),
        DONE(R.string.button_state_normal)
    }
}