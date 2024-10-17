package com.example.sedora;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Grafica extends Fragment {


    private static final String ARG_SEM_MENS = "semanal_mensual"; // Para diferenciar entre semanal y mensual

    private String tipoGrafica; // "semanal" o "mensual"

    public static Grafica newInstance(String tipo) {
        Grafica fragment = new Grafica();
        Bundle args = new Bundle();
        args.putString(ARG_SEM_MENS, tipo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipoGrafica = getArguments().getString(ARG_SEM_MENS);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.grafica_a_cargar, container, false);
        GraficaActivity graficaMain = (GraficaActivity) getActivity();
        assert graficaMain != null;

        // Dependiendo del tipo de gráfica, cargamos los datos correspondientes
        if ("semanal".equals(tipoGrafica)) {
            cargarDatosSemanales(vista, graficaMain.getGrafica_elegida());
        } else if ("mensual".equals(tipoGrafica)) {
            cargarDatosMensuales(vista, graficaMain.getGrafica_elegida());
        }

        // Inflate the layout for this fragment
        return vista;
    }

    //Esta dependiendo que se elegio, carga los datos(POR AHORA SON FALSOS)

    @SuppressLint("SetTextI18n")
    public void cargarDatosSemanales(View vista_graficas, String grafica_elegida) {
        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);

        ImageView grafica = vista_graficas.findViewById(R.id.imageView2);

        grafica.setImageResource(R.drawable.graficadiatemp);

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

    @SuppressLint("SetTextI18n")
    public void cargarDatosMensuales(View vista_graficas, String grafica_elegida) {

        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);
        ImageView grafica = vista_graficas.findViewById(R.id.imageView2);

        grafica.setImageResource(R.drawable.graficamestemp);

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