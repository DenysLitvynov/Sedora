package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.Meta;
import com.example.sedora.presentation.adapters.MetaAdapter;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TusMetasActivity extends AppCompatActivity {

    private RecyclerView recyclerMetaActual;
    private RecyclerView recyclerProximasMetas;
    private MetaAdapter metaActualAdapter;
    private MetaAdapter proximasMetasAdapter;
    private List<Meta> metasList;
    private List<Meta> metaActualList;
    private List<Meta> proximasMetasList;
    private FirebaseFirestore db;
    private ImageView botonProximasMetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tus_metas);

        // Configurar el Header
        Header header = findViewById(R.id.header);
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Tus Metas");

//        // Comprueba las notificaciones
//        NotificacionManager notificacionManager = new NotificacionManager();
//        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
//        header.updateNotificationIcon(hasNotifications);

        // Inicializar RecyclerViews
        recyclerMetaActual = findViewById(R.id.recycler_meta_actual);
        recyclerProximasMetas = findViewById(R.id.recycler_proximas_metas);
        recyclerMetaActual.setLayoutManager(new LinearLayoutManager(this));
        recyclerProximasMetas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Firebase y listas
        db = FirebaseFirestore.getInstance();
        metasList = new ArrayList<>();
        metaActualList = new ArrayList<>();
        proximasMetasList = new ArrayList<>();

        // Cargar datos desde Firestore
        cargarMetasDesdeFirestore();

        // Configurar el botón para abrir "TusMetas2Activity"
        botonProximasMetas = findViewById(R.id.boton_proximas_metas);
        botonProximasMetas.setOnClickListener(v -> {
            Intent intent = new Intent(TusMetasActivity.this, TusMetas2Activity.class);
            startActivity(intent);
        });
    }

    private void cargarMetasDesdeFirestore() {
        db.collection("metas")
                .orderBy("numeroMeta", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar las metas.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        metasList.clear();
                        metaActualList.clear();
                        proximasMetasList.clear();

                        for (DocumentSnapshot document : querySnapshot) {
                            Meta meta = document.toObject(Meta.class);
                            if (meta != null) {
                                metasList.add(meta);
                                if ("actual".equals(meta.getEstado())) {
                                    metaActualList.add(meta);
                                } else if ("pendiente".equals(meta.getEstado())) {
                                    proximasMetasList.add(meta);
                                }
                            }
                        }

                        // Limitar las próximas metas a las 2 siguientes
                        if (proximasMetasList.size() > 2) {
                            proximasMetasList = proximasMetasList.subList(0, 2);
                        }

                        actualizarAdaptadores();
                    }
                });
    }

    private void actualizarAdaptadores() {
        if (metaActualAdapter == null) {
            metaActualAdapter = new MetaAdapter(this, metaActualList, 0);
            recyclerMetaActual.setAdapter(metaActualAdapter);
        } else {
            metaActualAdapter.notifyDataSetChanged();
        }

        if (proximasMetasAdapter == null) {
            proximasMetasAdapter = new MetaAdapter(this, proximasMetasList, 1);
            recyclerProximasMetas.setAdapter(proximasMetasAdapter);
        } else {
            proximasMetasAdapter.notifyDataSetChanged();
        }
    }

}