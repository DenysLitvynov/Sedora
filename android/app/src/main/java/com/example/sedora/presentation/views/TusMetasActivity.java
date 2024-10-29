package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.R;

public class TusMetasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tus_metas);  // Carga el layout de "tus metas"

        // Encuentra el botón volver_metas en el layout tus_metas
        ImageView volverMetas = findViewById(R.id.volver_metas);

        // Configura el OnClickListener para volver a PantallaInicioActivity
        volverMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresa a PantallaInicioActivity usando un Intent
                Intent intent = new Intent(TusMetasActivity.this, PantallaInicioActivity.class);
                startActivity(intent);
                finish(); // Finaliza esta actividad para que no esté en la pila de backstack
            }
        });
    }
}
