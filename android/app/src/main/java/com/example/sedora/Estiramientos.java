package com.example.sedora;

import android.content.Context;
import android.graphics.drawable.Drawable;

public enum Estiramientos {
    ESTIRAMIENTO_1(R.drawable.estiramiento, 10),
    ESTIRAMIENTO_2(R.drawable.estiramiento2, 10),
    ESTIRAMIENTO_3(R.drawable.estiramiento3, 15),
    ESTIRAMIENTO_4(R.drawable.estiramiento4, 10),
    ESTIRAMIENTO_5(R.drawable.estiramiento5, 20),
    ESTIRAMIENTO_6(R.drawable.estiramiento6, 7),
    ESTIRAMIENTO_7(R.drawable.estiramiento7, 10),
    ESTIRAMIENTO_8(R.drawable.estiramiento8, 12),
    ESTIRAMIENTO_9(R.drawable.estiramiento9, 20),
    ESTIRAMIENTO_10(R.drawable.estiramiento10, 25);

    private final int drawableResId;
    private final int segundos;

    Estiramientos(int drawableResId, int segundos) {
        this.drawableResId = drawableResId;
        this.segundos = segundos;
    }

    public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(drawableResId);
    }

    public int getSegundos() {
        return segundos;
    }
}
