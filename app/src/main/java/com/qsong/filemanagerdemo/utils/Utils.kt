package com.qsong.filemanagerdemo.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

object Utils {
    // Simulate creating a placeholder Bitmap for the demo
    fun createPlaceholderBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.LTGRAY
        canvas.drawRect(0F, 0F, 200F, 200F, paint)
        return bitmap
    }
}