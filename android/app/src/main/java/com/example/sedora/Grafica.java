package com.example.sedora;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Grafica extends Fragment {


    private static final String ARG_SEM_MENS = "semanal_mensual"; // Para diferenciar entre semanal y mensual

    private String tipoGrafica; // "semanal" o "mensual"
    private ScrollView scrollView;

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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.grafica_a_cargar, container, false);
        GraficaActivity graficaMain = (GraficaActivity) getActivity();
        assert graficaMain != null;

        scrollView = vista.findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // Esto deshabilita el desplazamiento
            }
        });


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

    //NOTA RECORDATORIA:
    //Ya cuando tengamos mas claro como van lo de los datos se debe modificiar estas funciones.
    @SuppressLint("SetTextI18n")
    public void cargarDatosSemanales(View vista_graficas, String grafica_elegida) {
        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);

        ImageView grafica = vista_graficas.findViewById(R.id.grafica1);

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
                hacerVisible_Grafica2(vista_graficas);
                grafica.setImageResource(R.drawable.avisossemanales);

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
        ImageView grafica = vista_graficas.findViewById(R.id.grafica1);

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
                hacerVisible_Grafica2(vista_graficas);
                grafica.setImageResource(R.drawable.avisosmensuales);

                break;

            case "Avisos Ignorados":
                vmin.setText("5");
                vprom.setText("15");
                vmax.setText("35");
                break;
        }
    }

    public void hacerVisible_Grafica2(View vista_graficas) {


        // Hacer visible la gráfica
        ImageView grafica = vista_graficas.findViewById(R.id.Grafica2);
        TextView subtitulo1=vista_graficas.findViewById(R.id.textView5);
        TextView subtitulo2=vista_graficas.findViewById(R.id.subtitulo);

        grafica.setVisibility(View.VISIBLE);
        subtitulo1.setVisibility(View.VISIBLE);
        subtitulo2.setVisibility(View.VISIBLE);


        // Hacer visibles los valores asociados
        TextView vmin = vista_graficas.findViewById(R.id.Valor_min2);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_prom2);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max2);
        vmin.setVisibility(View.VISIBLE);
        vprom.setVisibility(View.VISIBLE);
        vmax.setVisibility(View.VISIBLE);

        // Hacer visibles los textos asociados (Min, Promedio, Max)
        TextView minText = vista_graficas.findViewById(R.id.textView15);
        TextView promText = vista_graficas.findViewById(R.id.textView16);
        TextView maxText = vista_graficas.findViewById(R.id.textView13);
        minText.setVisibility(View.VISIBLE);
        promText.setVisibility(View.VISIBLE);
        maxText.setVisibility(View.VISIBLE);

        // Hacer visibles los círculos (Circ1, Circ2, Circ3)
        ImageView circ1 = vista_graficas.findViewById(R.id.Circ1);
        ImageView circ2 = vista_graficas.findViewById(R.id.Circ2);
        ImageView circ3 = vista_graficas.findViewById(R.id.Circ3);
        circ1.setVisibility(View.VISIBLE);
        circ2.setVisibility(View.VISIBLE);
        circ3.setVisibility(View.VISIBLE);

        // Hacer visibles otros textos y valores relacionados
        TextView valorMin = vista_graficas.findViewById(R.id.Valor_min);
        TextView valorPromedio = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView valorMax = vista_graficas.findViewById(R.id.Valor_max);
        valorMin.setVisibility(View.VISIBLE);
        valorPromedio.setVisibility(View.VISIBLE);
        valorMax.setVisibility(View.VISIBLE);

        TextView minText2 = vista_graficas.findViewById(R.id.Min_text);
        TextView promText2 = vista_graficas.findViewById(R.id.Promedio_text);
        TextView maxText2 = vista_graficas.findViewById(R.id.max_text);
        minText2.setVisibility(View.VISIBLE);
        promText2.setVisibility(View.VISIBLE);
        maxText2.setVisibility(View.VISIBLE);

        vmin.setText("4");
        vprom.setText("9");
        vmax.setText("18");
        scrollView.setOnTouchListener(null);


    }

}