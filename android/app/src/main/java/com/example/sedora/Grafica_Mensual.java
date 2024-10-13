package com.example.sedora;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Grafica_Mensual extends Fragment {

    private GraficaActivity grafica_elegida;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.grafica_mensual, container, false);
        GraficaActivity graficaMain = (GraficaActivity) getActivity();
        assert graficaMain != null;
        cargarDatos(vista,graficaMain.getGrafica_elegida());


        // Inflate the layout for this fragment
        return vista;
    }


    @SuppressLint("SetTextI18n")
    public void cargarDatos(View vista_graficas,String grafica_elegida) {

        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);

        switch (grafica_elegida) {
            case "Horas Sentado":
                vmin.setText("12hrs");
                vprom.setText("20hrs");
                vmax.setText("30hrs");
                break;

            case "Horas Sensibles":
                vmin.setText("9:00");
                vprom.setText("18:00");
                vmax.setText("24:00");
                break;

            case "Progreso Avisos":
                vmin.setText("12");
                vprom.setText("25");
                vmax.setText("30");
                break;

            case "Avisos Ignorados":
                vmin.setText("5");
                vprom.setText("15");
                vmax.setText("35");
                break;
        }
    }
}