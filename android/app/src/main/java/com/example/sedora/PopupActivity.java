package com.example.sedora;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class PopupActivity extends AppCompatActivity {

    private PopUp popUpEstiramientos;
    private PopUp popUpInfo;
    private int[] currentIndex = {0}; // Para llevar el control de los estiramientos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_pop_up);

        popUpEstiramientos = new PopUp(this, R.layout.pop_up_estiramientos);
        popUpInfo = new PopUp(this, R.layout.activity_popup);

        // Configura botones de cerrar
        popUpInfo.setupBotonCerrar(R.id.cerrar);
        popUpEstiramientos.setupBotonCerrar(R.id.cerrar);

        // Muestra el primer popup
        Button botonAbrirInfo = findViewById(R.id.button5);
        botonAbrirInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpInfo.enseñarPopUp();
            }
        });

        // Controlador de estiramientos
        Button botonSeguir = popUpInfo.getDialog().findViewById(R.id.button4);
        botonSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpEstiramientos.enseñarPopUp();
                if (popUpEstiramientos.getDialog().isShowing()==true){
                    popUpInfo.cerrarPopUp();
                }
            }
        });

        // Configura el controlador de los estiramientos
        Button botonSiguiente = popUpEstiramientos.getDialog().findViewById(R.id.siguiente);
        Button botonCancelar = popUpEstiramientos.getDialog().findViewById(R.id.Cancelar);
        ImageView estiramientosImagen = popUpEstiramientos.getDialog().findViewById(R.id.Estiramientos);
        TextView numeroEstiramientos = popUpEstiramientos.getDialog().findViewById(R.id.numeroEstiramientos);

        popUpEstiramientos.setupEstiramientosController(botonSiguiente, botonCancelar, estiramientosImagen, numeroEstiramientos, currentIndex);
    }

}

