package github.informramiz.progressbarbutton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private var radius = 0f
    private var circleInsetVertical = 0f
    private var circleCenterY = 0f
    private var circleCenterX = 0f

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
    }

    init {
        setBackgroundColor(Color.GREEN)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = height / 3.5f
        circleInsetVertical = (height - radius*2) / 2
        circleCenterY = circleInsetVertical + radius
        circleCenterX = width / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(circleCenterX, circleCenterY, radius, paint)
    }
}