package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingDelimiter = 0.0F // the right end of the loading bar as a fraction of the whole length

    private val valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F).apply {
        duration = 2000
        addUpdateListener { updatedAnimation ->
            loadingDelimiter = updatedAnimation.animatedValue as Float
            buttonText = "We are loading"
            invalidate()
        }

        addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                loadingDelimiter = 0.0F
                buttonText = "Download"
            }
        })
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        color = context.getColor(R.color.white)
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    // cache of the custom attr values
    private var bgColor = 0
    private var ldColor = 0
    private var buttonText = ""

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            bgColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, context.getColor(R.color.colorPrimary))
            ldColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, context.getColor(R.color.colorPrimaryDark))
            buttonText =getString(R.styleable.LoadingButton_buttonText)?:"Download"
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply{
            rectPaint.color = ldColor
            val delim = loadingDelimiter * widthSize.toFloat()
            drawRect(0.0F, 0.0F, delim, heightSize.toFloat(), rectPaint)
            rectPaint.color = bgColor
            drawRect(delim, 0.0F, widthSize.toFloat(), heightSize.toFloat(), rectPaint)

            // note the subtraction to make it vertically assigned
            val pos_y = (heightSize/2).toFloat() - ( buttonTextPaint.descent() + buttonTextPaint.ascent() ) / 2
            drawText(buttonText, (widthSize/2).toFloat(), pos_y, buttonTextPaint)
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

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        runLoadingAnimation()
        return true
    }

    private fun runLoadingAnimation() {
        valueAnimator.start()
    }

}