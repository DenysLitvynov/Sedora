package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaInicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);  // Carga inicialmente pantalla_inicio

        // Configurar el popup
        Popup_pantalla_inicio popupPantallaInicio = new Popup_pantalla_inicio(this, this);
        popupPantallaInicio.setupPopup();  // Configurar el popup para la imagen con el ID "popup_ambiente"

        configurarPantallaInicio();
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
}
