package com.example.alea

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AvatarView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var gender: String = "Hombre"
    private var height: Float = 1.7f  // Altura en metros
    private var weight: Float = 70f   // Peso en kilogramos

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    fun updateAvatar(gender: String, height: Float, weight: Float) {
        this.gender = gender
        this.height = height
        this.weight = weight
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        // Ajustar dimensiones según peso y altura
        val bodyWidth = when {
            weight > 75f -> 120f // Persona "pesada"
            weight < 60f -> 80f  // Persona "ligera"
            else -> 100f         // Persona "media"
        }
        val bodyHeight = height * 200f // Altura en metros multiplicada para ajustar visualmente

        // Colores según género
        val bodyColor = if (gender == "Hombre") Color.BLUE else Color.MAGENTA

        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Dibujar torso
        paint.color = bodyColor
        val bodyTop = centerY - bodyHeight / 2
        canvas.drawRect(
            centerX - bodyWidth / 2,
            bodyTop,
            centerX + bodyWidth / 2,
            bodyTop + bodyHeight,
            paint
        )

        // Dibujar cabeza
        paint.color = Color.YELLOW
        val headRadius = bodyWidth / 2.5f
        canvas.drawCircle(centerX, bodyTop - headRadius, headRadius, paint)

        // Dibujar brazos
        paint.color = bodyColor
        val armLength = bodyHeight / 2
        val armY = bodyTop + bodyHeight / 4
        canvas.drawLine(centerX - bodyWidth / 2, armY, centerX - bodyWidth / 2 - armLength, armY, paint) // Brazo izq
        canvas.drawLine(centerX + bodyWidth / 2, armY, centerX + bodyWidth / 2 + armLength, armY, paint) // Brazo der

        // Dibujar piernas
        val legStartY = bodyTop + bodyHeight
        val legLength = bodyHeight / 2
        canvas.drawLine(centerX - bodyWidth / 4, legStartY, centerX - bodyWidth / 4, legStartY + legLength, paint) // Pierna izq
        canvas.drawLine(centerX + bodyWidth / 4, legStartY, centerX + bodyWidth / 4, legStartY + legLength, paint) // Pierna der
    }

}
