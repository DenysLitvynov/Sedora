package com.example.sedora.presentation.views;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.model.Notificacion;
import com.example.sedora.presentation.adapters.AdaptadorNotificacionesFirestoreUI;
import com.example.sedora.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RecyclerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdaptadorNotificacionesFirestoreUI adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificaciones);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Consulta Firestore
        Query query = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("notificaciones")
                .limit(50);

        FirestoreRecyclerOptions<Notificacion> opciones = new FirestoreRecyclerOptions.Builder<Notificacion>()
                .setQuery(query, Notificacion.class)
                .build();

        adaptador = new AdaptadorNotificacionesFirestoreUI(opciones, this);
        recyclerView.setAdapter(adaptador);

        adaptador.startListening();

        // Configura el texto del header
        View headerView = findViewById(R.id.header);
        TextView headerTitle = headerView.findViewById(R.id.headerTitleTextView);
        if (headerTitle != null) {
            headerTitle.setText("Notificaciones");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }
}
