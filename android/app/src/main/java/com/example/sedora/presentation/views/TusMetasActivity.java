package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.R;

public class TusMetasActivity extends AppCompatActivity {

    private ImageView botonProximasMetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tus_metas);

        // Configura el texto del header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Tus Metas");

        // Inicializa y configura el botón para abrir la actividad TusMetas2Activity
        botonProximasMetas = findViewById(R.id.boton_proximas_metas);
        botonProximasMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TusMetasActivity.this, TusMetas2Activity.class);
                startActivity(intent);
            }
        });
    }
}
