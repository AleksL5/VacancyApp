package com.example.vacancyapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class BadgeDrawable(
    private val context: Context,
    private val baseDrawableRes: Int,
    private val badgeCount: Int
) : Drawable() {

    private val paintText = Paint().apply {
        color = Color.WHITE
        textSize = 36f // Размер текста
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val paintCircle = Paint().apply {
        color = Color.RED // Цвет круга
        isAntiAlias = true
    }

    private val baseDrawable = ContextCompat.getDrawable(context, baseDrawableRes)

    override fun draw(canvas: Canvas) {
        baseDrawable?.setBounds(bounds)
        baseDrawable?.draw(canvas)

        if (badgeCount > 0) {
            // Положение кружка
            val radius = 24f
            val cx = bounds.right - radius
            val cy = bounds.top + radius

            // Рисуем кружок
            canvas.drawCircle(cx, cy, radius, paintCircle)

            // Рисуем текст внутри кружка
            val textY = cy - ((paintText.descent() + paintText.ascent()) / 2)
            canvas.drawText(badgeCount.toString(), cx, textY, paintText)
        }
    }

    override fun setAlpha(alpha: Int) {
        paintText.alpha = alpha
        paintCircle.alpha = alpha
        baseDrawable?.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paintText.colorFilter = colorFilter
        paintCircle.colorFilter = colorFilter
        baseDrawable?.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return baseDrawable?.intrinsicWidth ?: 0
    }

    override fun getIntrinsicHeight(): Int {
        return baseDrawable?.intrinsicHeight ?: 0
    }
}