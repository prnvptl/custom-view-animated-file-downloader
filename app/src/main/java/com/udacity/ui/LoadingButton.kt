package com.udacity.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var textRect = Rect()

    // progress 0-1
    private var progress = 0.5f

    // colors
    private var whiteColor = context.getColor(R.color.white)
    private var primaryColor = context.getColor(R.color.colorPrimary)
    private var darkPrimary = context.getColor(R.color.colorPrimaryDark)
    private var loadingCricleColor = context.getColor(R.color.colorAccent)

    // text
    private var downloadText = context.getString(R.string.download)
    private var downloadInProgressText = context.getString(R.string.button_loading)

    private var text = ""
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = whiteColor
        textSize = 45.0f
    }

    private val progressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = darkPrimary
    }

    private val progressCriclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = loadingCricleColor
    }

    private var valueAnimator = ValueAnimator()


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> {
                text = downloadInProgressText
                startAnimation()
            }
            ButtonState.Clicked -> {
            }
            ButtonState.Completed -> {
                text = downloadText
                stopAnimation()
                progress = 0f
            }
        }
        invalidate()
    }

    init {
        buttonState = ButtonState.Completed
        text = downloadText
    }

    fun updateState(newState: ButtonState) {
        buttonState = newState
    }

    private fun startAnimation() {
        stopAnimation()
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).also { animator ->
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.duration = 2000
            animator.addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            animator.start()
        }
    }

    private fun stopAnimation() {
        if(valueAnimator.isRunning) valueAnimator.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            // draw background
            drawColor(primaryColor)
            textPaint.getTextBounds(text, 0, text.length, textRect)
            if(buttonState == ButtonState.Loading) {
                canvas.drawRect(0f, 0f, progress * widthSize, heightSize.toFloat(), progressBarPaint)

                val left = widthSize / 2f + textRect.width() / 2f + 20.0f
                val top = heightSize / 2f - 20.0f
                val degree = progress * 360f
                canvas.drawArc(
                    left,
                    top,
                    left + 24.0f * 2f,
                    top + 24.0f * 2f,
                    0f,
                    degree,
                    true,
                    progressCriclePaint
                )
            }
            drawText(text, widthSize.toFloat() / 2, heightSize.toFloat() / 2 - textRect.centerY() , textPaint)

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}