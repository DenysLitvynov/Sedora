package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.EstadoMeta;
import com.example.sedora.model.MetaUsuario;
import com.example.sedora.model.SensorMediaData;
import com.example.sedora.presentation.adapters.MetaUsuarioAdapter;
import com.example.sedora.presentation.managers.MetasManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class TusMetasActivity extends AppCompatActivity {

    private RecyclerView recyclerMetaActual;
    private RecyclerView recyclerProximasMetas;
    private MetaUsuarioAdapter metaActualAdapter;
    private MetaUsuarioAdapter proximasMetasAdapter;
    private List<MetaUsuario> metasList;
    private List<MetaUsuario> metaActualList;
    private List<MetaUsuario> proximasMetasList;
    private FirebaseFirestore db;
    private ImageView botonProximasMetas;
    private FirebaseUser currentUser;
    private TextView metasCompletadas;
    private MetasManager metasManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tus_metas);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

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

        metasCompletadas = findViewById(R.id.textViewMetasCompletadas);

        cargarMetasCompletadasDesdeFirestore();

        // Inicializar Firebase y listas
        db = FirebaseFirestore.getInstance();
        metasList = new ArrayList<>();
        metaActualList = new ArrayList<>();
        proximasMetasList = new ArrayList<>();

        botonProximasMetas = findViewById(R.id.boton_proximas_metas);
        botonProximasMetas.setOnClickListener(v -> {
            Intent intent = new Intent(TusMetasActivity.this, TusMetas2Activity.class);
            startActivity(intent);
        });

        metasManager = new MetasManager();
        cargarTusMetasActivityMetaActualDesdeFirestore();
        cargarProximasMetasDesdeFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void cargarProximasMetasDesdeFirestore() {
        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        for (DocumentSnapshot document : querySnapshot) {
                            String estado = document.getString("estadoMeta");
                            if (EstadoMeta.PENDIENTE.name().equals(estado)) {
                                if (proximasMetasList.size() < 2) {
                                    MetaUsuario metaUsuario = document.toObject(MetaUsuario.class);
                                    proximasMetasList.add(metaUsuario);
                                }

                            }
                        }


                        if (proximasMetasAdapter == null) {
                            proximasMetasAdapter = new MetaUsuarioAdapter(this, proximasMetasList, 0);
                            recyclerProximasMetas.setAdapter(proximasMetasAdapter);
                        } else {
                            proximasMetasAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(this, "Error al cargar las metas actuales.", Toast.LENGTH_SHORT).show();
                    }
                    actualizarAdaptadores();
                });
    }

    private void cargarTusMetasActivityMetaActualDesdeFirestore() {
        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        metaActualList.clear();

                        for (DocumentSnapshot document : querySnapshot) {
                            String estado = document.getString("estadoMeta");
                            if (EstadoMeta.ACTUAL.name().equals(estado)) {
                                MetaUsuario metaUsuario = document.toObject(MetaUsuario.class);
                                metaActualList.add(metaUsuario);
                            }
                        }

                        if (metaActualAdapter == null) {
                            metaActualAdapter = new MetaUsuarioAdapter(this, metaActualList, 0);
                            recyclerMetaActual.setAdapter(metaActualAdapter);
                        } else {
                            metaActualAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(this, "Error al cargar las metas actuales.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarMetasCompletadasDesdeFirestore() {
        db.collection("usuarios")
                .document(currentUser.getUid())
                .collection("metasUsuario")
                .whereEqualTo("estadoMeta", EstadoMeta.COMPLETADO.name()) // Filtrar por estado COMPLETADO
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        int metasCompletadasCount = querySnapshot.size(); // Contar metas completadas

                        // Actualizar el TextView con el número de metas completadas
                        metasCompletadas.setText(metasCompletadasCount + "/20");
                    } else {
                        Toast.makeText(this, "Error al cargar las metas completadas.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarAdaptadores() {
        if (metaActualAdapter == null) {
            metaActualAdapter = new MetaUsuarioAdapter(this, metaActualList, 0);
            recyclerMetaActual.setAdapter(metaActualAdapter);
        } else {
            metaActualAdapter.notifyDataSetChanged();
        }

        if (proximasMetasAdapter == null) {
            proximasMetasAdapter = new MetaUsuarioAdapter(this, proximasMetasList, 1);
            recyclerProximasMetas.setAdapter(proximasMetasAdapter);
        } else {
            proximasMetasAdapter.notifyDataSetChanged();
        }
    }

}