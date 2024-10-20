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

    private  Dialog dialog1;
    private  Dialog dialog2;
    private Estiramientos estiramientos;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.prueba_pop_up);

        dialog1=crearPopUp(R.layout.activity_popup);

        dialog2=crearPopUp(R.layout.pop_up_estiramientos);

        Button boton= findViewById(R.id.button5);

        Button botonSeguir=dialog1.findViewById(R.id.button4);


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.show();
            }
        });

        botonSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {dialog2.show();}
        });

        popUpEstiramientosController();
        cerrarPopUps(dialog1,R.id.cerrar);
        cerrarPopUps(dialog2,R.id.cerrar);

    }


    //Se usa asi: crearPopUp(R.layout.mi_layout_popup);
    private  Dialog crearPopUp(int layout_a_popUp ){
        Dialog dialog=new Dialog(PopupActivity.this);
        dialog.setContentView(layout_a_popUp);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.fondo_pop_up));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        return  dialog;
    }

    private  void cerrarPopUps(Dialog popUp_a_cerrar,int id_boton){
       ImageButton boton= popUp_a_cerrar.findViewById(id_boton);
       boton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                cerrarAmbosPopups();
           }
       });
    }

    private void cerrarAmbosPopups(){
        if (dialog1.isShowing()) {
            dialog1.dismiss(); // Cierra dialog1 si está abierto
        }
        if (dialog2.isShowing()) {
            dialog2.dismiss(); // Cierra dialog2 si está abierto
        }
    }


    private void popUpEstiramientosController() {
        Button botonSiguiente = dialog2.findViewById(R.id.siguiente);
        Button botonCancelar = dialog2.findViewById(R.id.Cancelar);
        TextView numeroEstiramientos= dialog2.findViewById(R.id.numeroEstiramientos);
        final ImageView[] estiramientosImagen = {dialog2.findViewById(R.id.Estiramientos)}; // Inicializa tu ImageView
        final int[] currentIndex = {0}; // Reinicia el índice al abrir el popup

        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Verifica si hay más estiramientos
                if (currentIndex[0] < Estiramientos.values().length) {

                    botonSiguiente.setText("Siguiente");
                    // Obtén el estiramiento actual
                    Estiramientos estiramiento = Estiramientos.values()[currentIndex[0]];

                    // Llama a estiramientosTimer usando el valor del estiramiento actual
                    estiramientosTimer(estiramiento.getSegundos());

                    // Muestra la imagen en el ImageView
                    Drawable drawable = estiramiento.getDrawable(getApplicationContext());
                    estiramientosImagen[0].setImageDrawable(drawable);

                    // Actualiza el TextView con el número de estiramientos
                    numeroEstiramientos.setText((currentIndex[0] + 1) + "/10");
                    // Incrementa el índice para el siguiente estiramiento
                    currentIndex[0]++;
                    // Cambia el texto del botón si es el último estiramiento
                    if (currentIndex[0] == Estiramientos.values().length) {
                        botonSiguiente.setText("Finalizar");
                    }

                } else {
                    dialog1.dismiss();
                    dialog2.dismiss();
                }
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss(); // Cierra el otro diálogo si es necesario
                dialog2.dismiss(); // Cierra el popup
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }// Cancela el temporizador si se cierra el popup
            }
        });
    }


    private void estiramientosTimer(int cuantos_segs) {
        // Encuentra el TextView del contador
        TextView textViewCounter = dialog2.findViewById(R.id.contador);

        // Establece el tiempo total en milisegundos
        int tiempoTotal = cuantos_segs * 1000;

        // Crea el CountDownTimer para contar hacia atrás cada segundo
        countDownTimer = new CountDownTimer(tiempoTotal, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Actualiza el TextView con el número de segundos restantes
                textViewCounter.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                // Al finalizar el contador, muestra "Fin" o realiza otra acción
                textViewCounter.setText("Fin");
            }
        }.start(); // Inicia el contador
    }

}

