package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.R;
import com.example.sedora.presentation.managers.NotificacionManager;

public class AjustesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);

        // Obtén el Header
        Header header = findViewById(R.id.header);

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Ajustes");

        // Comprueba las notificaciones
        NotificacionManager notificacionManager = new NotificacionManager();
        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
        header.updateNotificationIcon(hasNotifications);


        // para entrar en la página de notificaciones
        //ImageButton buttonIcon = findViewById(R.id.button_icon);



        //FUNCIONALIDAD BOTONES MENUS

        MenuManager funcionMenu = new MenuManager();

        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaInicio(AjustesActivity.this);
            }
        });

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaProgreso(AjustesActivity.this);
            }
        });

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaAjustes(AjustesActivity.this);
            }
        });

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPerfil(AjustesActivity.this);
            }
        });

        // FUNCIONALIDAD TEXTVIEW PARA ABRIR OTRAS PÁGINAS

        // Ver Sobre Nosotros
        TextView tvVerSobreNosotros = findViewById(R.id.textView31);
        tvVerSobreNosotros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesActivity.this, AcercaDe.class);
                startActivity(intent);
            }
        });

        // Ver Política de Privacidad
        TextView tvVerPoliticaPrivacidad = findViewById(R.id.textView35);
        tvVerPoliticaPrivacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesActivity.this, PoliticaDePrivacidad.class);
                startActivity(intent);
            }
        });

        // Ver Preguntas Frecuentes (FAQ)
        TextView tvVerFAQ = findViewById(R.id.textView33);
        tvVerFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesActivity.this, FAQ.class);
                startActivity(intent);
            }
        });

        /*
        // Configura el clic en el button_icon para entrar en Notificaciones
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesActivity.this, RecyclerActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar la actividad actual
            }
        });*/
    }

}
