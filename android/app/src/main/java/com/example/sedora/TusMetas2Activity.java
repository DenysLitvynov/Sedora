package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class TusMetas2Activity extends AppCompatActivity {

    private ImageView volverMetas3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tus_metas_2);

        // Inicializa el botón de volver
        volverMetas3 = findViewById(R.id.volver_metas3);

        // Configura el botón para volver a la pantalla de "Tus Metas"
        volverMetas3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vuelve a la actividad TusMetasActivity
                Intent intent = new Intent(TusMetas2Activity.this, TusMetasActivity.class);
                startActivity(intent);
                finish(); // Opcional, si quieres que se elimine esta actividad de la pila
            }
        });
    }
}
