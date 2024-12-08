package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.MetasSeeder;
import com.example.sedora.R;
import com.google.firebase.FirebaseApp;

public class CargaActivity extends AppCompatActivity {
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_de_carga);

       // Inicializar Firebase
       /*FirebaseApp.initializeApp(this);  // Asegúrate de que Firebase está inicializado*/

       // Llamar al Seeder para agregar los datos a Firestore
       /*MetasSeeder.seedMetas();  // Aquí se ejecuta el Seeder y agrega los datos a Firestore*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CargaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 900);

    }
}