package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.presentation.adapters.Adaptador;
import com.example.sedora.presentation.managers.NotificacionManagerSedora;
import com.example.sedora.R;

public class RecyclerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Adaptador adaptador;
    private NotificacionManagerSedora notificacionManagerSedora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificaciones);

        ImageButton buttonIcon = findViewById(R.id.button_icon);

        notificacionManagerSedora = new NotificacionManagerSedora();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pasa el NotificacionManagerSedora al adaptador
        adaptador = new Adaptador(notificacionManagerSedora);
        recyclerView.setAdapter(adaptador);

        // Configura el clic en el button_icon para regresar a la ProfileActivity
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecyclerActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar la actividad actual
            }
        });

    }

}

