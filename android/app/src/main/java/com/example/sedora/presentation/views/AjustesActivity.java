package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

        // FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(v -> funcionMenu.abrirPantallaInicio(AjustesActivity.this));

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(v -> funcionMenu.abrirPantallaProgreso(AjustesActivity.this));

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(v -> funcionMenu.abrirPantallaAjustes(AjustesActivity.this));

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(v -> funcionMenu.abrirPantallaPerfil(AjustesActivity.this));

        // FUNCIONALIDAD TEXTVIEW Y RECUADROS PARA ABRIR OTRAS PÁGINAS

        // Ver Sobre Nosotros
        View viewSobreNosotros = findViewById(R.id.view4); // Recuadro
        TextView tvVerSobreNosotros = findViewById(R.id.textView31); // Texto
        View.OnClickListener sobreNosotrosListener = v -> {
            Intent intent = new Intent(AjustesActivity.this, AcercaDe.class);
            startActivity(intent);
        };
        viewSobreNosotros.setOnClickListener(sobreNosotrosListener);
        tvVerSobreNosotros.setOnClickListener(sobreNosotrosListener);

        // Ver Preguntas Frecuentes (FAQ)
        View viewFAQ = findViewById(R.id.view5); // Recuadro
        TextView tvVerFAQ = findViewById(R.id.textView33); // Texto
        View.OnClickListener faqListener = v -> {
            Intent intent = new Intent(AjustesActivity.this, FAQ.class);
            startActivity(intent);
        };
        viewFAQ.setOnClickListener(faqListener);
        tvVerFAQ.setOnClickListener(faqListener);

        // Ver Política de Privacidad
        View viewPoliticaPrivacidad = findViewById(R.id.view10); // Recuadro
        TextView tvVerPoliticaPrivacidad = findViewById(R.id.textView35); // Texto
        View.OnClickListener politicaPrivacidadListener = v -> {
            Intent intent = new Intent(AjustesActivity.this, PoliticaDePrivacidad.class);
            startActivity(intent);
        };
        viewPoliticaPrivacidad.setOnClickListener(politicaPrivacidadListener);
        tvVerPoliticaPrivacidad.setOnClickListener(politicaPrivacidadListener);
    }


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


