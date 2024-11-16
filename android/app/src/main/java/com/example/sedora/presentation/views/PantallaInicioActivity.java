package com.example.sedora.presentation.views;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.miServicio;
import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.example.sedora.presentation.managers.Popup_pantalla_inicio;
import com.example.sedora.R;

public class PantallaInicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicio);  // Carga inicialmente pantalla_inicio

        //INICIO DE SERVICIO
        if (!foregroundServiceRunning()) {
            Intent intent = new Intent(this, miServicio.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }
        }
        //INICIO DE SERVICIO


        // Obtén el Header
        Header header = findViewById(R.id.header);

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Inicio");

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


    //TODO
    //Originalmente esto era metodo de miServicio pero no lo puedo llamar desde ahi porque luego tengo que cambiar la clase entera
    //el TODO es hacer que el serviico se inicie despues que el usuario hace login, lo puse aqui porque aqui me cuadra, como ustedes vean
    //pero donde se inice el servicio, tiene que ir esta funcion que ve si se esta ejecutando ya un servicio
    public  boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (miServicio.class.getName().equals(service.service.getClassName())){
                return true;
            }

        }
        return false;
    }

}
