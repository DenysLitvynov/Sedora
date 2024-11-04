package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PantallaInicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);  // Carga inicialmente pantalla_inicio

        //TEXTO DEL HEADER
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Inicio");

        // Configurar el popup
        Popup_pantalla_inicio popupPantallaInicio = new Popup_pantalla_inicio(this, this);
        popupPantallaInicio.setupPopup();  // Configurar el popup para la imagen con el ID "popup_ambiente"

        configurarPantallaInicio();

        //FUNCIONALIDAD BOTONES MENU

        MenuManager funcionMenu = new MenuManager();


        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaInicio(PantallaInicioActivity.this);
            }
        });

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaProgreso(PantallaInicioActivity.this);
            }
        });

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaAjustes(PantallaInicioActivity.this);
            }
        });

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPerfil(PantallaInicioActivity.this);
            }
        });

        /*FIN FUNCIONALIDAD BOTONES MENU*/
    }

    private void configurarPantallaInicio() {
        // Encuentra la ImageView en el layout pantalla_inicio
        ImageView imageView21 = findViewById(R.id.imageView21);

        // Configura el OnClickListener para ir a TusMetasActivity
        imageView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la actividad TusMetasActivity
                Intent intent = new Intent(PantallaInicioActivity.this, TusMetasActivity.class);
                startActivity(intent);
            }
        });
    }

    public void irARecyclerActivity(View view) {
        Intent intent = new Intent(this, RecyclerActivity.class);
        startActivity(intent);
    }

}
