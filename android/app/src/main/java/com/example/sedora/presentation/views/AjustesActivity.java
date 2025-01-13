package com.example.sedora.presentation.views;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.model.Notificacion;
import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.R;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.example.sedora.presentation.managers.SensorDataService;

public class AjustesActivity extends AppCompatActivity {

    private Switch switchNotificaciones = null;
    private Switch switchLuz = null;
    private Switch switchSonido = null;
    private Switch switchTemperatura = null;
    private Switch switchPostura = null;
    private Switch switchDistancia = null;
    private Switch switchEstiramientos = null;
    private Switch switchDescansos = null;
    private Switch switchHidratacion = null;

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

        // Configurar switches como apagados por defecto
        switchNotificaciones.setChecked(false);
        switchLuz.setChecked(false);
        switchSonido.setChecked(false);
        switchTemperatura.setChecked(false);
        switchPostura.setChecked(false);
        switchDistancia.setChecked(false);
        switchEstiramientos.setChecked(false);
        switchDescansos.setChecked(false);
        switchHidratacion.setChecked(false);

        cargarEstadoSwitches();

        notificacionManager = new NotificacionManager();

        // Listener para el switch de notificaciones globales
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "Switch Notificaciones: Activado");
                Log.d(TAG, "Activando todos los switches individuales.");
                activarTodosLosSwitches();
                notificacionManager.permitirNotificaciones();
                SensorDataService.areNotificationsBlocked = true;
                Log.d(TAG, "Notificaciones bloqueadas globalmente.");
                guardarEstadoSwitches();
            } else {
                Log.d(TAG, "Switch Notificaciones: Desactivado");
                Log.d(TAG, "Desactivando todos los switches individuales.");
                desactivarTodosLosSwitches();
                notificacionManager.bloquearNotificaciones();
                SensorDataService.areNotificationsBlocked = false;
                Log.d(TAG, "Notificaciones permitidas globalmente.");
                guardarEstadoSwitches();
            }
            notificacionManager.logEstadosNotificaciones();
            // Log para ver el valor de areNotificationsBlocked
            Log.d(TAG, "Estado de areNotificationsBlocked: " + SensorDataService.areNotificationsBlocked);
        });

// Cambiar estado de la notificación de luz
        switchLuz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirLuz();
                SensorDataService.isNotiLuzBlocked = true;
            } else {
                notificacionManager.bloquearLuz();
                SensorDataService.isNotiLuzBlocked = false;
            }
            Log.d(TAG, "Switch Luz: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();
        });

// Cambiar estado de la notificación de sonido
        switchSonido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirSonido();
                SensorDataService.isNotiSonidoBlocked = true;
            } else {
                notificacionManager.bloquearSonido();
                SensorDataService.isNotiSonidoBlocked = false;
            }
            Log.d(TAG, "Switch Sonido: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();
        });

// Cambiar estado de la notificación de temperatura
        switchTemperatura.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirTemperatura();
                SensorDataService.isNotiTemperaturaBlocked = true;
            } else {
                notificacionManager.bloquearTemperatura();
                SensorDataService.isNotiTemperaturaBlocked = false;
            }
            Log.d(TAG, "Switch Temperatura: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();

        });

// Cambiar estado de la notificación de postura
        switchPostura.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirPostura();
                SensorDataService.isNotiPosturaBlocked = true;
            } else {
                notificacionManager.bloquearPostura();
                SensorDataService.isNotiPosturaBlocked = false;
            }
            Log.d(TAG, "Switch Postura: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();

        });

// Cambiar estado de la notificación de distancia
        switchDistancia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirDistancia();
                SensorDataService.isNotiDistanciaBlocked = true;
            } else {
                notificacionManager.bloquearDistancia();
                SensorDataService.isNotiDistanciaBlocked = false;
            }
            Log.d(TAG, "Switch Distancia: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();

        });

// Cambiar estado de la notificación de estiramientos
        switchEstiramientos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirEstiramientos();
                SensorDataService.isNotiEstiramientosBlocked = true;
            } else {
                notificacionManager.bloquearEstiramientos();
                SensorDataService.isNotiEstiramientosBlocked = false;
            }
            Log.d(TAG, "Switch Estiramientos: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();

        });

// Cambiar estado de la notificación de descansos
        switchDescansos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirDescansos();
                SensorDataService.isNotiDescansosBlocked = true;
            } else {
                notificacionManager.bloquearDescansos();
                SensorDataService.isNotiDescansosBlocked = false;
            }
            Log.d(TAG, "Switch Descansos: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();

        });

// Cambiar estado de la notificación de hidratación
        switchHidratacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                notificacionManager.permitirHidratacion();
                SensorDataService.isNotiHidratacionBlocked = true;
            } else {
                notificacionManager.bloquearHidratacion();
                SensorDataService.isNotiHidratacionBlocked = false;
            }
            Log.d(TAG, "Switch Hidratacion: " + (isChecked ? "Activado, noti bloqueada" : "Desactivado, noti permitida"));

            notificacionManager.logEstadosNotificaciones();
            guardarEstadoSwitches();

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

    private void guardarEstadoSwitches() {
        SharedPreferences sharedPreferences = getSharedPreferences("ajustes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("switchNotificaciones", switchNotificaciones.isChecked());
        editor.putBoolean("switchLuz", switchLuz.isChecked());
        editor.putBoolean("switchSonido", switchSonido.isChecked());
        editor.putBoolean("switchTemperatura", switchTemperatura.isChecked());
        editor.putBoolean("switchPostura", switchPostura.isChecked());
        editor.putBoolean("switchDistancia", switchDistancia.isChecked());
        editor.putBoolean("switchEstiramientos", switchEstiramientos.isChecked());
        editor.putBoolean("switchDescansos", switchDescansos.isChecked());
        editor.putBoolean("switchHidratacion", switchHidratacion.isChecked());

        editor.apply(); // Aplicar los cambios
    }


    private void cargarEstadoSwitches() {
        SharedPreferences sharedPreferences = getSharedPreferences("ajustes", MODE_PRIVATE);

        // Cargar el estado de cada switch
        boolean notificacionesActivadas = sharedPreferences.getBoolean("switchNotificaciones", false);
        switchNotificaciones.setChecked(notificacionesActivadas);

        switchLuz.setChecked(sharedPreferences.getBoolean("switchLuz", false));
        switchSonido.setChecked(sharedPreferences.getBoolean("switchSonido", false));
        switchTemperatura.setChecked(sharedPreferences.getBoolean("switchTemperatura", false));
        switchPostura.setChecked(sharedPreferences.getBoolean("switchPostura", false));
        switchDistancia.setChecked(sharedPreferences.getBoolean("switchDistancia", false));
        switchEstiramientos.setChecked(sharedPreferences.getBoolean("switchEstiramientos", false));
        switchDescansos.setChecked(sharedPreferences.getBoolean("switchDescansos", false));
        switchHidratacion.setChecked(sharedPreferences.getBoolean("switchHidratacion", false));

        // Si las notificaciones están activadas, activar todos los switches
        if (notificacionesActivadas) {
            activarTodosLosSwitches();
        } else {
            desactivarTodosLosSwitches();
        }
    }



}
