package com.example.sedora;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // O el layout que estés usando para MainActivity

        // Redirigir a ProfileActivity al abrir la aplicación
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish(); // Termina MainActivity para que no se pueda volver a ella
    }
}