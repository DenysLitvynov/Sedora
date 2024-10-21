package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HeaderManager extends AppCompatActivity {

    private TextView nombreSeccion;
    private ImageView notificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nombreSeccion = findViewById(R.id.nombreseccion);
        notificaciones = findViewById(R.id.notificaciones);

        ImageView btnHome = findViewById(R.id.btnHome);
        ImageView btnProgreso = findViewById(R.id.btnProgreso);
        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        ImageView perfil = findViewById(R.id.Perfil);


        btnHome.setOnClickListener(view -> {
            cambiarTextoHeader("Inicio");

        });

        btnProgreso.setOnClickListener(view -> {
            cambiarTextoHeader("Progreso");

        });

        btnAjustes.setOnClickListener(view -> {
            cambiarTextoHeader("Ajustes");

        });

        perfil.setOnClickListener(view -> {
            cambiarTextoHeader("Perfil");
            MenuManager.abrirPantallaProfile(this);
        });


        notificaciones.setOnClickListener(view -> {
            Intent intent = new Intent(HeaderManager.this, PruebaActivity2.class);
            startActivity(intent);
        });
    }

    private void cambiarTextoHeader(String texto) {
        nombreSeccion.setText(texto);
    }
}