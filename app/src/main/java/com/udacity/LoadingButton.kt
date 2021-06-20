package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize : Int by Delegates.observable(0) {property, oldValue, newValue ->
        rectBox.bottom = newValue.toFloat()
    }
    private var animationProgress = 0.0F // fraction from 0 to 1

    private val valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F).apply {
        duration = 2000
        addUpdateListener { updatedAnimation ->
            animationProgress = updatedAnimation.animatedValue as Float
            buttonText = "We are loading"
            invalidate()
        }

        addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                animationProgress = 0.0F
                buttonText = "Download"
            }
        })
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var textBounds = Rect()
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

    private var rectBox = RectF(0.0F, 0.0F, 1.0F, 1.0F)
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
    }

    private var arcBox = RectF(0.0F, 0.0F, 1.0F, 1.0F)
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorAccent)
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
            val delim = animationProgress * widthSize.toFloat()
            rectBox.left = 0.0F
            rectBox.right = delim
            drawRect(rectBox, rectPaint)
            rectPaint.color = bgColor
            rectBox.left = delim
            rectBox.right = widthSize.toFloat()
            drawRect(rectBox, rectPaint)

            // note the subtraction to make it vertically assigned
            val pos_x = (widthSize/2).toFloat()
            val pos_y = (heightSize/2).toFloat() - ( buttonTextPaint.descent() + buttonTextPaint.ascent() ) / 2

            buttonTextPaint.getTextBounds(buttonText, 0, buttonText.length, textBounds)
            arcBox.left = pos_x + textBounds.width() * 1.05F / 2
            arcBox.top = (heightSize.toFloat() - textBounds.height()) / 2
            arcBox.right = arcBox.left + textBounds.height()
            arcBox.bottom = arcBox.top +textBounds.height()
            drawArc(arcBox, 0.0F, 360.0F * animationProgress, true, arcPaint )

            drawText(buttonText, pos_x, pos_y, buttonTextPaint)
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
        val res = super.performClick()
        runLoadingAnimation()
        return res
    }

    private fun runLoadingAnimation() {
        valueAnimator.start()
    }

}