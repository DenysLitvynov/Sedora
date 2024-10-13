package com.example.sedora;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GraficaActivity extends AppCompatActivity {

   private  TextView titulo_Grafica;
   private String  grafica_elegida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_grafica);

        ImageView button=findViewById(R.id.boton_Regreso);

        //Recuperamos el string establecido en ProgresoActividado al elegir una grafica.
        grafica_elegida= getIntent().getStringExtra("Titulo_de_Grafica");

        titulo_Grafica=findViewById(R.id.Titulo);

        titulo_Grafica.setText(grafica_elegida);

        cargarDatos();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GraficaActivity.this,ProgresoActivity.class);
                startActivity(intent);

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Esta dependiendo que se elegio, carga los datos(POR AHORA SON FALSOS)

    @SuppressLint("SetTextI18n")
    private void cargarDatos(){

        TextView vmin= findViewById(R.id.Valor_min);
        TextView vprom= findViewById(R.id.Valor_promedio);
        TextView vmax= findViewById(R.id.Valor_max);

        switch (grafica_elegida){
            case "Horas Sentado":
                vmin.setText("5hrs");
                vprom.setText("12hrs");
                vmax.setText("19hrs");
                break;

            case "Horas Sensibles":
                vmin.setText("5:00");
                vprom.setText("15:00");
                vmax.setText("21:00");
                break;

            case "Progreso Avisos":
                vmin.setText("5");
                vprom.setText("10");
                vmax.setText("20");
                break;

            case "Avisos Ignorados":
                vmin.setText("1");
                vprom.setText("7");
                vmax.setText("22");
                break;
        }
    }

}
