package com.example.sedora.presentation.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.R;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.example.sedora.presentation.managers.SensorDataService;

public class AjustesActivity extends AppCompatActivity {

    private static final String TAG = "AjustesActivity";

    private SharedPreferences sharedPreferences;

    private Switch switchNotificaciones;
    private Switch switchLuz;
    private Switch switchSonido;
    private Switch switchTemperatura;
    private Switch switchPostura;
    private Switch switchDistancia;
    private Switch switchEstiramientos;
    private Switch switchDescansos;
    private Switch switchHidratacion;

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

        cargarEstadoSwitches();

        notificacionManager = new NotificacionManager();

        // Listener para el switch de notificaciones globales
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) { // No pulsado = habilitado
                Log.d(TAG, "Switch Notificaciones: Habilitado");
                Log.d(TAG, "Habilitando todos los switches individuales.");
                activarTodosLosSwitches();
                notificacionManager.permitirNotificaciones();
                SensorDataService.areNotificationsBlocked = false;
                Log.d(TAG, "Notificaciones permitidas globalmente.");
            } else { // Pulsado = no habilitado
                Log.d(TAG, "Switch Notificaciones: No habilitado");
                Log.d(TAG, "Deshabilitando todos los switches individuales.");
                desactivarTodosLosSwitches();
                notificacionManager.bloquearNotificaciones();
                SensorDataService.areNotificationsBlocked = true;
                Log.d(TAG, "Notificaciones bloqueadas globalmente.");
            }
            guardarEstadoSwitches();
            notificacionManager.logEstadosNotificaciones();
            Log.d(TAG, "Estado de areNotificationsBlocked: " + SensorDataService.areNotificationsBlocked);
        });

        // Configuración de listeners para switches individuales
        configurarSwitchIndividual(switchLuz, "Luz",
                () -> {
                    notificacionManager.permitirLuz();
                    SensorDataService.isNotiLuzBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearLuz();
                    SensorDataService.isNotiLuzBlocked = true;
                });

        configurarSwitchIndividual(switchSonido, "Sonido",
                () -> {
                    notificacionManager.permitirSonido();
                    SensorDataService.isNotiSonidoBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearSonido();
                    SensorDataService.isNotiSonidoBlocked = true;
                });

        configurarSwitchIndividual(switchTemperatura, "Temperatura",
                () -> {
                    notificacionManager.permitirTemperatura();
                    SensorDataService.isNotiTemperaturaBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearTemperatura();
                    SensorDataService.isNotiTemperaturaBlocked = true;
                });

        configurarSwitchIndividual(switchPostura, "Postura",
                () -> {
                    notificacionManager.permitirPostura();
                    SensorDataService.isNotiPosturaBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearPostura();
                    SensorDataService.isNotiPosturaBlocked = true;
                });

        configurarSwitchIndividual(switchDistancia, "Distancia",
                () -> {
                    notificacionManager.permitirDistancia();
                    SensorDataService.isNotiDistanciaBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearDistancia();
                    SensorDataService.isNotiDistanciaBlocked = true;
                });

        configurarSwitchIndividual(switchEstiramientos, "Estiramientos",
                () -> {
                    notificacionManager.permitirEstiramientos();
                    SensorDataService.isNotiEstiramientosBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearEstiramientos();
                    SensorDataService.isNotiEstiramientosBlocked = true;
                });

        configurarSwitchIndividual(switchDescansos, "Descansos",
                () -> {
                    notificacionManager.permitirDescansos();
                    SensorDataService.isNotiDescansosBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearDescansos();
                    SensorDataService.isNotiDescansosBlocked = true;
                });

        configurarSwitchIndividual(switchHidratacion, "Hidratación",
                () -> {
                    notificacionManager.permitirHidratacion();
                    SensorDataService.isNotiHidratacionBlocked = false;
                },
                () -> {
                    notificacionManager.bloquearHidratacion();
                    SensorDataService.isNotiHidratacionBlocked = true;
                });


        // Obtén el Header
        Header header = findViewById(R.id.header);

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Ajustes");

//        // Comprueba las notificaciones
//        NotificacionManager notificacionManager = new NotificacionManager();
//        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
//        header.updateNotificationIcon(hasNotifications);

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
    private void configurarSwitchIndividual(Switch switchComponent, String nombre, Runnable
            habilitar, Runnable deshabilitar){
        switchComponent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) { // No pulsado = habilitado
                habilitar.run();
                Log.d(TAG, "Switch " + nombre + ": Habilitado");
            } else { // Pulsado = no habilitado
                deshabilitar.run();
                Log.d(TAG, "Switch " + nombre + ": No habilitado");
            }
            guardarEstadoSwitches();
            notificacionManager.logEstadosNotificaciones();
        });
    }

    private void activarTodosLosSwitches() {
        switchLuz.setChecked(false);
        switchSonido.setChecked(false);
        switchTemperatura.setChecked(false);
        switchPostura.setChecked(false);
        switchDistancia.setChecked(false);
        switchEstiramientos.setChecked(false);
        switchDescansos.setChecked(false);
        switchHidratacion.setChecked(false);
    }

    private void desactivarTodosLosSwitches() {
        switchLuz.setChecked(true);
        switchSonido.setChecked(true);
        switchTemperatura.setChecked(true);
        switchPostura.setChecked(true);
        switchDistancia.setChecked(true);
        switchEstiramientos.setChecked(true);
        switchDescansos.setChecked(true);
        switchHidratacion.setChecked(true);
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
        editor.apply();
    }

    private void cargarEstadoSwitches() {
        SharedPreferences sharedPreferences = getSharedPreferences("ajustes", MODE_PRIVATE);
        switchNotificaciones.setChecked(sharedPreferences.getBoolean("switchNotificaciones", false));
        switchLuz.setChecked(sharedPreferences.getBoolean("switchLuz", false));
        switchSonido.setChecked(sharedPreferences.getBoolean("switchSonido", false));
        switchTemperatura.setChecked(sharedPreferences.getBoolean("switchTemperatura", false));
        switchPostura.setChecked(sharedPreferences.getBoolean("switchPostura", false));
        switchDistancia.setChecked(sharedPreferences.getBoolean("switchDistancia", false));
        switchEstiramientos.setChecked(sharedPreferences.getBoolean("switchEstiramientos", false));
        switchDescansos.setChecked(sharedPreferences.getBoolean("switchDescansos", false));
        switchHidratacion.setChecked(sharedPreferences.getBoolean("switchHidratacion", false));
    }

}

