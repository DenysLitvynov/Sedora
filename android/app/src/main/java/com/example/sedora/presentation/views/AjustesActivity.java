package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.R;
import com.example.sedora.presentation.managers.NotificacionManager;

public class AjustesActivity extends AppCompatActivity {

    private Switch switchNotificaciones, switchLuz, switchSonido, switchTemperatura,
            switchPostura, switchDistancia, switchEstiramientos, switchDescansos, switchHidratacion;

    private NotificacionManager notificacionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);

        // Inicializamos los switches
        switchNotificaciones = findViewById(R.id.switchNotificaciones);
        switchLuz = findViewById(R.id.switchLuz);
        switchSonido = findViewById(R.id.switchSonido);
        switchTemperatura = findViewById(R.id.switchTemperatura);
        switchPostura = findViewById(R.id.switchPostura);
        switchDistancia = findViewById(R.id.switchDistancia);
        switchEstiramientos = findViewById(R.id.switchEstiramientos);
        switchDescansos = findViewById(R.id.switchDescansos);
        switchHidratacion = findViewById(R.id.switchHidratacion);

        notificacionManager = new NotificacionManager();

        // Listener para el switch de notificaciones globales
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                activarTodosLosSwitches();
                notificacionManager.permitirNotificaciones();
            } else {
                desactivarTodosLosSwitches();
                notificacionManager.bloquearNotificaciones();
            }
        });

        // Listeners para cada switch individual de notificación
        switchLuz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleLuz(isChecked);
            }
        });

        switchSonido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleSonido(isChecked);
            }
        });

        switchTemperatura.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleTemperatura(isChecked);
            }
        });

        switchPostura.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.togglePostura(isChecked);
            }
        });

        switchDistancia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleDistancia(isChecked);
            }
        });

        switchEstiramientos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleEstiramientos(isChecked);
            }
        });

        switchDescansos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleDescansos(isChecked);
            }
        });

        switchHidratacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchNotificaciones.isChecked()) {
                notificacionManager.toggleHidratacion(isChecked);
            }
        });

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.textView8);
        headerTitle.setText("Ajustes");

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
        View viewSobreNosotros = findViewById(R.id.view4);
        TextView tvVerSobreNosotros = findViewById(R.id.textView31);
        View.OnClickListener sobreNosotrosListener = v -> {
            Intent intent = new Intent(AjustesActivity.this, AcercaDe.class);
            startActivity(intent);
        };
        viewSobreNosotros.setOnClickListener(sobreNosotrosListener);
        tvVerSobreNosotros.setOnClickListener(sobreNosotrosListener);

        // Ver Preguntas Frecuentes (FAQ)
        View viewFAQ = findViewById(R.id.view5);
        TextView tvVerFAQ = findViewById(R.id.textView33);
        View.OnClickListener faqListener = v -> {
            Intent intent = new Intent(AjustesActivity.this, FAQ.class);
            startActivity(intent);
        };
        viewFAQ.setOnClickListener(faqListener);
        tvVerFAQ.setOnClickListener(faqListener);

        // Ver Política de Privacidad
        View viewPoliticaPrivacidad = findViewById(R.id.view10);
        TextView tvVerPoliticaPrivacidad = findViewById(R.id.textView35);
        View.OnClickListener politicaPrivacidadListener = v -> {
            Intent intent = new Intent(AjustesActivity.this, PoliticaDePrivacidad.class);
            startActivity(intent);
        };
        viewPoliticaPrivacidad.setOnClickListener(politicaPrivacidadListener);
        tvVerPoliticaPrivacidad.setOnClickListener(politicaPrivacidadListener);
    }

    private void activarTodosLosSwitches() {
        switchLuz.setChecked(true);
        switchSonido.setChecked(true);
        switchTemperatura.setChecked(true);
        switchPostura.setChecked(true);
        switchDistancia.setChecked(true);
        switchEstiramientos.setChecked(true);
        switchDescansos.setChecked(true);
        switchHidratacion.setChecked(true);
    }

    private void desactivarTodosLosSwitches() {
        switchLuz.setChecked(false);
        switchSonido.setChecked(false);
        switchTemperatura.setChecked(false);
        switchPostura.setChecked(false);
        switchDistancia.setChecked(false);
        switchEstiramientos.setChecked(false);
        switchDescansos.setChecked(false);
        switchHidratacion.setChecked(false);
    }
}
