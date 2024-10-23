package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecyclerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Adaptador adaptador;
    private NotificacionManager notificacionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificaciones);

        ImageButton buttonIcon = findViewById(R.id.button_icon);


        notificacionManager = new NotificacionManager();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pasa el NotificacionManager al adaptador
        adaptador = new Adaptador(notificacionManager);
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

