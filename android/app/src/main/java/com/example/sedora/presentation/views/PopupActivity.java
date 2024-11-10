package com.example.sedora.presentation.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.presentation.managers.PopUp;
import com.example.sedora.R;

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

