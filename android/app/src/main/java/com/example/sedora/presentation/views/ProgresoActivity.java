package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sedora.presentation.managers.MenuManager;
import com.example.sedora.R;

public class ProgresoActivity extends AppCompatActivity {



    @Override
    //================================INICIO DE ON CREATE==============================================
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_progreso);


        // para entrar en la página de notificaciones
        ImageButton buttonIcon = findViewById(R.id.button_icon);



        View horasSentado =findViewById(R.id.HorasSentado);

        View horasSensibles =findViewById(R.id.HorasSensibles);

        View progresoAvisos =findViewById(R.id.ProgresoAvisos);

        View avisosIgnorados =findViewById(R.id.AvisosIgnorados);

        //________________________________________________________________________________________

        //ON CLICK LISTENERS PARA LOS BOTONES(INICIO)

        //________________________________________________________________________________________
        horasSentado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                establecer_Titulo_Grafica("Horas Sentado");
            }
        });

        horasSensibles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               establecer_Titulo_Grafica("Horas Sensibles");

            }
        });

        progresoAvisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                establecer_Titulo_Grafica("Progreso Avisos");

            }
        });

        avisosIgnorados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                establecer_Titulo_Grafica("Avisos Ignorados");
            }
        });
        //________________________________________________________________________________________

        //ON CLICK LISTENERS PARA LOS BOTONES(FIN)

        //________________________________________________________________________________________
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //FUNCIONALIDAD BOTONES MENUS

        MenuManager funcionMenu = new MenuManager();


        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaInicio(ProgresoActivity.this);
            }
        });

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaProgreso(ProgresoActivity.this);
            }
        });

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaAjustes(ProgresoActivity.this);
            }
        });

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPerfil(ProgresoActivity.this);
            }
        });

        /*FIN FUNCIONALIDAD BOTONES*/


        // Configura el clic en el button_icon para entrar en Notificaciones
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgresoActivity.this, RecyclerActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar la actividad actual
            }
        });

    }


    //================================FIN DE ON CREATE==============================================

    //METODO PARA ESTABLECER EL TITULO DE LA GRAFICA / DATOS A CARGAR

    private void establecer_Titulo_Grafica(String paginaSelecionada){
        Intent intent = new Intent(ProgresoActivity.this, GraficaActivity.class);
        intent.putExtra("Titulo_de_Grafica",paginaSelecionada);
        startActivity(intent);

    }

}