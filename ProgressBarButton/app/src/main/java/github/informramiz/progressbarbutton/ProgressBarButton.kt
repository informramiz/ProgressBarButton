package github.informramiz.progressbarbutton

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

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

    private val sweepAngle: Float
        get() = progress * 360f
    private var horizontalAnimationRect = RectF()
    private val arcOvalRect = RectF()

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
    }

    init {
        setBackgroundColor(Color.GREEN)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCircleDimensions()
        calculateHorizontalAnimationRectangleDimensions()
        //TODO: Remove this as it is here just for testing
        progress = 1f
    }

    private fun calculateCircleDimensions() {
        val radius = height / 3.5f
        val circleInsetVertical = (height - radius * 2) / 2
        arcOvalRect.left = width / 2f
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

    private fun onProgressUpdated() {
        horizontalAnimationRect.right = width * progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.CYAN
        canvas.drawRect(horizontalAnimationRect, paint)
        paint.color = Color.GRAY
        canvas.drawArc(arcOvalRect, 0f, sweepAngle, true, paint)
    }
}