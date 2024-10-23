package com.example.sedora;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

public class Header_NotManager {

    private ImageView buttonIcon;

    public void setupHeader(final Activity activity, View rootView) {
        buttonIcon = rootView.findViewById(R.id.flechavuelta); // Asegúrate de que el ID sea correcto

        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish(); // Esto cerrará la actividad actual y volverá a la anterior
            }
        });
    }
}