package com.udacity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

class DownloadImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val bgColor = context.getColor(R.color.colorPrimaryDark)
    private val cloudColor = context.getColor(R.color.cloud)

    private val arrow = Path().apply {
        val tailSide = 100.0F
        val headToTail = 2 * tailSide

        moveTo( -tailSide / 2, -tailSide)
        lineTo(tailSide / 2, -tailSide)
        lineTo(tailSide / 2, 0.0F)
        lineTo( headToTail - tailSide, 0.0F)
        lineTo(0.0F, headToTail - tailSide)
        lineTo( -headToTail + tailSide, 0.0F)
        lineTo(-tailSide / 2, 0.0F)
        lineTo(-tailSide / 2, -tailSide)
    }

    private val cloud = Path().apply{
        val w = 200.0F
        val h = 150.0F
        addRect(-w, -0.5F*h, 0.9F*w, h, Path.Direction.CCW)

        var radius = 120.0F
        addCircle( -w, h - radius, radius, Path.Direction.CCW )

        radius = 170.0F
        addCircle( -0.1F*w, h-1.2F*radius, radius, Path.Direction.CCW )

        radius = 130.0F
        addCircle( 0.9F*w, h - radius, radius, Path.Direction.CCW )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if ( canvas == null ) return

        canvas.drawColor(bgColor)

        canvas.save()
        canvas.translate(0.5F*width, 0.5F*height)

        canvas.clipPath(cloud)
        canvas.clipOutPath(arrow)
        canvas.drawColor(cloudColor)

        canvas.restore()

    }
}