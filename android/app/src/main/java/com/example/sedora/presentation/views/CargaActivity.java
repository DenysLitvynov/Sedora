package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.R;
import com.example.sedora.firebase.MetasSeeder;

public class CargaActivity extends AppCompatActivity {

   @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_de_carga);

       //MetasSeeder.seedMetas();


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