package com.example.sedora;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Grafica_Semanal extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista=inflater.inflate(R.layout.grafica_semanal, container, false);
        GraficaActivity graficaMain = (GraficaActivity) getActivity();
        assert graficaMain != null;
        cargarDatos(vista,graficaMain.getGrafica_elegida());

        // Inflate the layout for this fragment
        return vista;
    }

    //Esta dependiendo que se elegio, carga los datos(POR AHORA SON FALSOS)

    @SuppressLint("SetTextI18n")
    public void cargarDatos(View vista_graficas, String grafica_elegida) {

        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);

        switch (grafica_elegida) {
            case "Horas Sentado":
                vmin.setText("5hrs");
                vprom.setText("12hrs");
                vmax.setText("19hrs");
                break;

            case "Horas Sensibles":
                vmin.setText("5:00");
                vprom.setText("15:00");
                vmax.setText("21:00");
                break;

            case "Progreso Avisos":
                vmin.setText("5");
                vprom.setText("10");
                vmax.setText("20");
                break;

            case "Avisos Ignorados":
                vmin.setText("1");
                vprom.setText("7");
                vmax.setText("22");
                break;
        }

    }
}