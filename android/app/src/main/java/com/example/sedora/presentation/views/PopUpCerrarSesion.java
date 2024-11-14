package com.example.sedora.presentation.views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sedora.R;

public class PopUpCerrarSesion {

    private Context context;

    public PopUpCerrarSesion(Context context) {
        this.context = context;
    }

    public void mostrarDialogo(final LogoutListener listener) {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.box_background_blanco);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cerrar_sesion, null);
        dialog.setContentView(view);

        TextView mensaje = view.findViewById(R.id.Mensaje);
        Button btnNo = view.findViewById(R.id.btnNo);
        Button btnYes = view.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLogoutConfirmed();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface LogoutListener {
        void onLogoutConfirmed();
    }
}
