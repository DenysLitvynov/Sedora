package com.example.sedora;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Popup_pantalla_inicio {

    private Context context;
    private Activity activity;

    // Constructor para inicializar el contexto y la actividad
    public Popup_pantalla_inicio(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    // Método para configurar el Popup
    public void setupPopup() {
        // Obtener referencia al layout principal y al ImageView
        ConstraintLayout mainLayout = activity.findViewById(R.id.pantalla_inicio); // Cambia a tu ID correcto
        ImageView imageView = activity.findViewById(R.id.popup_ambiente);  // Tu ImageView donde se hará clic para abrir el popup

        // Configurar el evento de clic en el ImageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflar el layout del popup (popup_layout.xml)
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.pantalla_inicio_2, null);  // Asegúrate de que 'pantalla_inicio_2' es correcto

                // Crear el PopupWindow
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                // Mostrar el popup centrado en la pantalla
                popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

                // Encuentra la ImageView del botón de cerrar en el popup
                ImageView closePopupButton = popupView.findViewById(R.id.cerrar_popup_inicio); // Asegúrate de que este es el ID del botón de cerrar

                // Configurar el evento de clic en el botón de cerrar
                closePopupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cerrar el PopupWindow
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }
}