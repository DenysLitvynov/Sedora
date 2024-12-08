package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.Meta;
import com.example.sedora.presentation.adapters.MetaAdapter;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.google.firebase.firestore.FirebaseFirestore;
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
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            metasList.clear(); // Limpia la lista antes de agregar nuevos elementos
                            for (DocumentSnapshot document : querySnapshot) {
                                Meta meta = document.toObject(Meta.class);
                                if (meta != null) {
                                    metasList.add(meta);
                                }
                            }

                            // Ordenar metas por numeroMeta
                            metasList.sort((meta1, meta2) -> Integer.compare(meta1.getNumeroMeta(), meta2.getNumeroMeta()));

                            // Encontrar la meta actual (estado == "actual")
                            for (Meta meta : metasList) {
                                if ("actual".equals(meta.getEstado())) {
                                    metaActualList.add(meta);
                                    break; // Solo hay una meta actual
                                }
                            }

                            // Encontrar las próximas metas (estado == "pendiente")
                            boolean isAfterCurrent = false;
                            for (Meta meta : metasList) {
                                if (isAfterCurrent && "pendiente".equals(meta.getEstado())) {
                                    proximasMetasList.add(meta);
                                }
                                if (!metaActualList.isEmpty() && meta.equals(metaActualList.get(0))) {
                                    isAfterCurrent = true;
                                }
                                if (proximasMetasList.size() == 2) break; // Limitar a las dos siguientes
                            }

                            // Configurar adaptadores
                            metaActualAdapter = new MetaAdapter(TusMetasActivity.this, metaActualList, 0); // 0 para meta actual
                            proximasMetasAdapter = new MetaAdapter(TusMetasActivity.this, proximasMetasList, 1); // 1 para próximas metas

                            recyclerMetaActual.setAdapter(metaActualAdapter);
                            recyclerProximasMetas.setAdapter(proximasMetasAdapter);
                        }
                    } else {
                        System.err.println("Error al cargar las metas: " + task.getException());
                    }
                });
    }
}