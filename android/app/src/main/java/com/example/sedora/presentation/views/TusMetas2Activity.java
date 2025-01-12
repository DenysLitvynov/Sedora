package com.example.sedora.presentation.views;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sedora.R;
import com.example.sedora.model.EstadoMeta;
import com.example.sedora.model.MetaUsuario;
import com.example.sedora.presentation.adapters.MetaUsuarioAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TusMetas2Activity extends AppCompatActivity {

    private RecyclerView recyclerViewMetas;
    private MetaUsuarioAdapter metaUsuarioAdapter;
    private List<MetaUsuario> metasList;
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

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("usuarios")
        .document(usuario.getUid())
                .collection("metasUsuario")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (DocumentSnapshot document : querySnapshot) {
                            String estado = document.getString("estadoMeta");
                            if (EstadoMeta.PENDIENTE.name().equals(estado)) {
                                MetaUsuario metaUsuario = document.toObject(MetaUsuario.class);
                                metasList.add(metaUsuario);
                            }
                        }


                        if (metaUsuarioAdapter == null) {
                            metaUsuarioAdapter = new MetaUsuarioAdapter(TusMetas2Activity.this, metasList, 1);
                            recyclerViewMetas.setAdapter(metaUsuarioAdapter);
                        } else {
                            metaUsuarioAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(this, "Error al cargar las metas pendientes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}