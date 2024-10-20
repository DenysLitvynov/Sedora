package com.example.sedora;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PopupActivity extends AppCompatActivity {

    Dialog dialog1;
    Dialog dialog2;

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

    private void popUpEstiramientosController(){

    }
}