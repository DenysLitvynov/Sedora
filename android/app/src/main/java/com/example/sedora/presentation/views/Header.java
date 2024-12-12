package com.example.sedora.presentation.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.R;
import com.example.sedora.presentation.managers.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Header extends LinearLayout {

    private ImageView campananotificaciones;

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int layoutResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "headerLayout", 0);

        if (layoutResource != 0) {
            LayoutInflater.from(context).inflate(layoutResource, this, true);
        }

        ImageView flechavuelta = findViewById(R.id.flechavuelta);
        if (flechavuelta != null) {
            flechavuelta.setOnClickListener(v -> ((AppCompatActivity) context).finish());
        }

        campananotificaciones = findViewById(R.id.campananotificaciones);
        if (campananotificaciones != null) {
            campananotificaciones.setOnClickListener(v -> {
                Intent intent = new Intent(context, com.example.sedora.presentation.views.RecyclerActivity.class);
                context.startActivity(intent);
            });


            // Verificar notificaciones dinámicamente
            verificarNotificaciones(context);
        }
    }


    private void verificarNotificaciones(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            updateNotificationIcon(false);
            return;
        }

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.escucharNotificacionesActivas(user.getUid(), this::updateNotificationIcon);
    }


    // Método para actualizar el icono de notificaciones
    public void updateNotificationIcon(boolean hasNotifications) {
        if (campananotificaciones != null) {
            if (hasNotifications) {
                campananotificaciones.setImageResource(R.drawable.campananotificado); // Icono si hay notificaciones
            } else {
                campananotificaciones.setImageResource(R.drawable.campana); // Icono si no hay notificaciones
            }
        }
    }

}
