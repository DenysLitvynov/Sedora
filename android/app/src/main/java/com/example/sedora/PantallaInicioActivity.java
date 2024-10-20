package com.example.sedora;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaInicioActivity extends AppCompatActivity {

    // Define una bandera para saber en qué layout estás
    private boolean enTusMetas = false;

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

        // Configura el OnClickListener para ir a tus_metas
        imageView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia al layout tus_metas
                setContentView(R.layout.tus_metas);
                enTusMetas = true;
                configurarTusMetas();
            }
        });
    }

    private void configurarTusMetas() {
        // Encuentra el botón volver_metas en el layout tus_metas
        ImageView volverMetas = findViewById(R.id.volver_metas);

        // Configura el OnClickListener para volver a pantalla_inicio
        volverMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia de nuevo al layout pantalla_inicio
                setContentView(R.layout.pantalla_inicio);
                enTusMetas = false;
                configurarPantallaInicio();
            }
        });
    }
}
