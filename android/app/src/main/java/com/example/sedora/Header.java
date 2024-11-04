package com.example.sedora;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Header extends LinearLayout {

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Obtén el layout especificado en el atributo app:headerLayout
        int layoutResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "headerLayout", 0);

        // Infla el layout especificado en el atributo
        if (layoutResource != 0) {
            LayoutInflater.from(context).inflate(layoutResource, this, true);

            // Configura la flecha de retroceso
            ImageView flechavuelta = findViewById(R.id.flechavuelta);
            if (flechavuelta != null) {
                flechavuelta.setOnClickListener(v -> ((AppCompatActivity) context).finish());

            }
        }
    }
}
