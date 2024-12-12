package com.example.sedora.presentation.views;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sedora.R;
import com.example.sedora.model.Meta;
import com.example.sedora.presentation.adapters.MetaAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TusMetas2Activity extends AppCompatActivity {

    private RecyclerView recyclerViewMetas;
    private MetaAdapter metaAdapter;
    private List<Meta> metasList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tus_metas_2);

        recyclerViewMetas = findViewById(R.id.recycler_view_proximas_metas);
        recyclerViewMetas.setLayoutManager(new LinearLayoutManager(this));

        // Configurar Firebase
        db = FirebaseFirestore.getInstance();
        metasList = new ArrayList<>();

        // Cargar las metas desde Firestore que tengan estado "pendiente"
        cargarMetasPendientesDesdeFirestore();

        // Configurar el botón de "volver atrás"
        ImageView volverButton = findViewById(R.id.volver_metas3);
        volverButton.setOnClickListener(v -> onBackPressed()); // Regresa a la actividad anterior
    }

    private void cargarMetasPendientesDesdeFirestore() {
        db.collection("metas")
                .whereEqualTo("estado", "pendiente")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar las metas pendientes.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        metasList.clear();

                        for (DocumentSnapshot document : querySnapshot) {
                            Meta meta = document.toObject(Meta.class);
                            if (meta != null) {
                                metasList.add(meta);
                            }
                        }

                       metasList.sort((meta1, meta2) -> meta1.getNumeroMeta().compareTo(meta2.getNumeroMeta()));

                        if (metaAdapter == null) {
                            metaAdapter = new MetaAdapter(TusMetas2Activity.this, metasList, 1);
                            recyclerViewMetas.setAdapter(metaAdapter);
                        } else {
                            metaAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

}