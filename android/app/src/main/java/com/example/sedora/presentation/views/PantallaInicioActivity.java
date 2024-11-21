package com.example.sedora.presentation.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.Header;
import android.Manifest;
import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.example.sedora.presentation.managers.Popup_pantalla_inicio;
import com.example.sedora.R;

public class PantallaInicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);  // Carga inicialmente pantalla_inicio

        // Solicitar permiso de notificaciones
        requestNotificationPermission();

        //---INICIO HEADER---
        Header header = findViewById(R.id.header);

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Inicio");
        //---FIN HEADER---

        // Comprueba las notificaciones
        NotificacionManager notificacionManager = new NotificacionManager();
        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
        header.updateNotificationIcon(hasNotifications);


        // Configurar el popup
        Popup_pantalla_inicio popupPantallaInicio = new Popup_pantalla_inicio(this, this);
        popupPantallaInicio.setupPopup();  // Configurar el popup para la imagen con el ID "popup_ambiente"

        configurarPantallaInicio();

        //FUNCIONALIDAD BOTONES MENU
        MenuManager funcionMenu = new MenuManager();


        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaInicio(PantallaInicioActivity.this);
            }
        });

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaProgreso(PantallaInicioActivity.this);
            }
        });

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaAjustes(PantallaInicioActivity.this);
            }
        });

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPerfil(PantallaInicioActivity.this);
            }
        });

        /*FIN FUNCIONALIDAD BOTONES MENU*/
    }

    private void configurarPantallaInicio() {
        // Encuentra la ImageView en el layout pantalla_inicio
        ImageView imageView21 = findViewById(R.id.imageView21);

        // Configura el OnClickListener para ir a TusMetasActivity
        imageView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la actividad TusMetasActivity
                Intent intent = new Intent(PantallaInicioActivity.this, TusMetasActivity.class);
                startActivity(intent);
            }
        });
    }

    public void irARecyclerActivity(View view) {
        Intent intent = new Intent(this, RecyclerActivity.class);
        startActivity(intent);
    }

    // Solicitar permiso de notificaciones
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verifica si el permiso ya está concedido
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Solicita el permiso
                Log.d("Permiso", "Solicitando permiso de notificaciones...");
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                Log.d("Permiso", "Permiso de notificaciones ya concedido.");
            }
        } else {
            Log.d("Permiso", "No se requiere POST_NOTIFICATIONS en esta versión de Android.");
        }
    }

    // Manejar la respuesta del usuario al solicitar el permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permiso", "Permiso de notificaciones concedido.");
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Permiso", "Permiso de notificaciones denegado.");
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
