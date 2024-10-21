package com.example.sedora;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;

public class MenuManager {

    public static void cambiarColorIcono(ImageView icono, Context context) {
        // Usa ContextCompat para obtener el color
        int verdePrimario = ContextCompat.getColor(context, R.color.verde_primario);
        icono.setColorFilter(verdePrimario, PorterDuff.Mode.SRC_IN);
    }

    public static void restaurarColorIcono(ImageView icono, Context context) {
        // Usa ContextCompat para obtener el color
        int verdeSecundario = ContextCompat.getColor(context, R.color.verde_secundario);
        icono.setColorFilter(verdeSecundario, PorterDuff.Mode.SRC_IN);
    }

    public static void manejarCambioIconos(ImageView iconoSeleccionado, ImageView[] iconosNoSeleccionados, Context context) {
        cambiarColorIcono(iconoSeleccionado, context);
        for (ImageView icono : iconosNoSeleccionados) {
            restaurarColorIcono(icono, context);
        }
    }

    public static void abrirPantallaProfile(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }

    /*
    public void abrirNuevaPagina(Context context) {
        Intent intent = new Intent(context,PruebaActivity.class);
        context.startActivity(intent);
        intent.putExtra("TITULO_SECCION", "Settings");
        context.startActivity(intent);
    }
    public void abrirNotificaciones(Context context) {
        Intent intent = new Intent(context, PruebaActivity2.class);
        intent.putExtra("TITULO_SECCION", "Notificaciones");
        context.startActivity(intent);
    }
    */

}