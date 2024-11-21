package com.example.sedora;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

//Esta clase es para recibir anuncios de android. POr ejemplo, un aviso de que se reseteo el teleofno por ejemplo
public class miBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Si se ha ha completado el reinicio/ boot, inicia el servicio
        //El servicio seguira corriendo automaticamente aunque se reinicie el telefono.
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent servicioIntent= new Intent(context,miServicio.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(servicioIntent);
            }
        }

    }
}
