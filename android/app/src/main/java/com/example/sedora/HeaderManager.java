package com.example.sedora;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HeaderManager extends AppCompatActivity {

    private ImageView buttonIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header);

        buttonIcon = findViewById(R.id.notificaciones);

        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeaderManager.this, RecyclerActivity.class);
                startActivity(intent);
            }
        });
    }

}