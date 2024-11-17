package com.example.sedora.presentation.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HalfDonutChart extends View {

    private Paint paint;
    private RectF rectF;
    private float scorePercentage; // Variable para almacenar el porcentaje de puntuación

    public HalfDonutChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE); // Usamos STROKE para el contorno
        rectF = new RectF();
    }

    // Método para establecer el porcentaje de puntuación
    public void setScorePercentage(float scorePercentage) {
        this.scorePercentage = scorePercentage;
        invalidate(); // Solicita redibujar cuando cambie la puntuación
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int strokeWidth = 30; // Grosor del "donut"
        int halfStrokeWidth = strokeWidth / 2;
        int radius = Math.min(width, height) / 2 - halfStrokeWidth;

        rectF.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        // Dibuja la mitad de fondo del donut en un color gris claro
        paint.setColor(Color.parseColor("#E0E0E0")); // Gris claro para el fondo
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rectF, 180, 180, false, paint);

        // Dibuja la parte rellena del donut en función del porcentaje
        paint.setColor(Color.parseColor("#009774"));
        float angle = 180 * scorePercentage / 100;
        canvas.drawArc(rectF, 180, angle, false, paint);
    }
}
