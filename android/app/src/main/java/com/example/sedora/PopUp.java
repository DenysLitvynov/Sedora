package com.example.sedora;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

//Clase que crea y maneja los popups de la app.
public class PopUp {

    private Dialog dialog;
    private Activity contexto;
    private CountDownTimer countDownTimer;

    public PopUp(Activity contexto, int layoutResource) {
        this.contexto = contexto;
        this.dialog = new Dialog(contexto);
        this.dialog.setContentView(layoutResource);
        this.dialog.getWindow().setBackgroundDrawable(contexto.getDrawable(R.drawable.fondo_pop_up));
        this.dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.dialog.setCancelable(false);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void enseñarPopUp() {
        dialog.show();
    }

    public void cerrarPopUp() {
        dialog.dismiss();
    }


    //Especifica el cual es el boton de cerrar del pop up
    public void setupBotonCerrar(int buttonId) {
        ImageButton closeButton = dialog.findViewById(buttonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarPopUp();
            }
        });
    }

    //================================================================================================================



    //ESPECIFICOS_DE_POPUPS

    //Estos metodos son para pop_ups_especificicos.

    //===============================================================================================================


    //===============================================================================================================
    //Estiramientos
    //=================================================================================================================
    public void setupEstiramientosController(Button botonSiguiente, Button botonCancelar, ImageView estiramientosImagen, TextView numeroEstiramientos, int[] currentIndex) {
        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex[0] < Estiramientos.values().length) {
                    Estiramientos estiramiento = Estiramientos.values()[currentIndex[0]];
                    estiramientosTimer(estiramiento.getSegundos(), dialog.findViewById(R.id.contador), currentIndex, botonSiguiente, estiramientosImagen, numeroEstiramientos);
                    Drawable drawable = estiramiento.getDrawable(contexto.getApplicationContext());
                    estiramientosImagen.setImageDrawable(drawable);
                    numeroEstiramientos.setText((currentIndex[0] + 1) + "/10");
                    currentIndex[0]++;

                    // Si es el último estiramiento, cambia el texto del botón
                    if (currentIndex[0] == Estiramientos.values().length) {
                        botonSiguiente.setText("Finalizar");
                    }
                } else {
                    resetEstiramientos(botonSiguiente, currentIndex);  // Reinicia cuando se completan todos
                }
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarPopUp();
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        });
    }

    private void estiramientosTimer(int segundos, TextView textViewCounter, int[] currentIndex, Button botonSiguiente, ImageView estiramientosImagen, TextView numeroEstiramientos) {
        int tiempoTotal = segundos * 1000;
        countDownTimer = new CountDownTimer(tiempoTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewCounter.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                textViewCounter.setText("Fin");

                // Si el temporizador finaliza en el último estiramiento, reinicia
                if (currentIndex[0] == Estiramientos.values().length) {
                    resetEstiramientos(botonSiguiente, currentIndex);
                }
            }
        }.start();
    }

    private void resetEstiramientos(Button botonSiguiente, int[] currentIndex) {
        // Restablece el índice y el texto del botón
        currentIndex[0] = 0;
        botonSiguiente.setText("Siguiente");

        // Opcional: Aquí puedes resetear cualquier otro componente visual si es necesario
        cerrarPopUp();
    }

    //============================================================================================
    //Estiramientos
    //============================================================================================
}
