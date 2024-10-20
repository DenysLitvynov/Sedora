package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProgresoActivity extends AppCompatActivity {



    @Override
    //================================INICIO DE ON CREATE==============================================
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_progreso);

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
    }
    //================================FIN DE ON CREATE==============================================

    //METODO PARA ESTABLECER EL TITULO DE LA GRAFICA / DATOS A CARGAR

    private void establecer_Titulo_Grafica(String paginaSelecionada){
        Intent intent = new Intent(ProgresoActivity.this, GraficaActivity.class);
        intent.putExtra("Titulo_de_Grafica",paginaSelecionada);
        startActivity(intent);

    }
}