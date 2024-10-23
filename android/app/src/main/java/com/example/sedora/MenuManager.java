package com.example.sedora;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;

public class MenuManager {

    public static void cambiarColorIcono(ImageView icono, Context context) {

        int verdePrimario = ContextCompat.getColor(context, R.color.verde_primario);
        icono.setColorFilter(verdePrimario, PorterDuff.Mode.SRC_IN);
    }

    public static void restaurarColorIcono(ImageView icono, Context context) {

        int verdeSecundario = ContextCompat.getColor(context, R.color.verde_secundario);
        icono.setColorFilter(verdeSecundario, PorterDuff.Mode.SRC_IN);
    }

    public static void manejarCambioIconos(ImageView iconoSeleccionado, ImageView[] iconosNoSeleccionados, Context context) {

        cambiarColorIcono(iconoSeleccionado, context);
        for (ImageView icono : iconosNoSeleccionados) {
            restaurarColorIcono(icono, context);

        }

    }

    public static void abrirPantallaInicio(Context context) {
        Intent intent = new Intent(context, PantallaInicioActivity.class);
        context.startActivity(intent);
    }

    public static void abrirPantallaProgreso(Context context) {
        Intent intent = new Intent(context, ProgresoActivity.class);
        context.startActivity(intent);

    }

    public static void abrirPantallaAjustes(Context context) {
        Intent intent = new Intent(context, AjustesActivity.class);
        context.startActivity(intent);
    }

    public static void abrirPantallaPerfil(Context context){

        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);

    }

}