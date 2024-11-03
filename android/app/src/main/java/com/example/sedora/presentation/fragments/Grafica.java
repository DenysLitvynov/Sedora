package com.example.sedora.presentation.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sedora.DatosGrafica;
import com.example.sedora.R;
import com.example.sedora.presentation.views.GraficaActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.List;

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

        final View stopPoint = vista.findViewById(R.id.stopPoint);

        GraphView grafica=vista.findViewById(R.id.Grafica);

        scrollView=vista.findViewById(R.id.scrollView);

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                // Obtener la posición Y inferior del stopPoint relativo al ScrollView
                int stopPointBottom = stopPoint.getBottom();  // Esto te da la posición inferior del stopPoint dentro del padre (ScrollView)

                // Obtener la altura visible del ScrollView
                int scrollViewHeight = scrollView.getHeight();

                // Obtener la posición Y actual más la altura visible del ScrollView
                int scrollViewBottomY = scrollY + scrollViewHeight;

                // Verificar si la gráfica actual es "Progreso Avisos"
                if (!"Progreso Avisos".equals(graficaMain.getGrafica_elegida())) {
                    // Si no es "Progreso Avisos", limitar el desplazamiento al stopPoint
                    if (scrollViewBottomY >= stopPointBottom) {
                        scrollView.scrollTo(scrollX, stopPointBottom - scrollViewHeight); // Fijar el scroll en el punto límite
                    }
                } else {
                    // En "Progreso Avisos", permitir desplazamiento completo
                    scrollView.setOnTouchListener(null);
                }
            }
        });

        // Dependiendo del tipo de gráfica, cargamos los datos correspondientes
        if ("semanal".equals(tipoGrafica)) {
            cargarDatos(vista, graficaMain.getGrafica_elegida(),"semanal",grafica);
        } else if ("mensual".equals(tipoGrafica)) {
            cargarDatos(vista, graficaMain.getGrafica_elegida(),"mensual",grafica);
        }
        // Inflate the layout for this fragment
        return vista;
    }


//    //Esta dependiendo que se elegio, carga los datos(POR AHORA SON FALSOS)
    public void crearGrafica(DatosGrafica grafica_a_Construir,GraphView vista_Grafica){

        List<Double> yValues=grafica_a_Construir.getValoresY();
        List<Double> xValues=grafica_a_Construir.getValoresX();
        int color= ContextCompat.getColor(getContext(), R.color.verde_secundario);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        for (int i = 0; i < yValues.size(); i++) {
            series.appendData(new DataPoint(xValues.get(i), yValues.get(i)), true, yValues.size());
        }

        vista_Grafica.addSeries(series);
        vista_Grafica.getGridLabelRenderer().setVerticalAxisTitle("Avisos");
        vista_Grafica.getGridLabelRenderer().setHorizontalAxisTitle("Horas");
        series.setDrawDataPoints(true);

        series.setColor(color); // Cambia el color de la línea
        series.setThickness(8); // Cambia el grosor de la línea
    }


    public void cargarDatos(View vista_graficas, String grafica_elegida,String mensual_o_semanal,GraphView vistaGrafica){
        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);

        ImageView grafica = vista_graficas.findViewById(R.id.grafica1);

        grafica.setImageResource(R.drawable.graficadiatemp);

        if (mensual_o_semanal.equals("semanal")){
            switch (grafica_elegida) {
                case "Horas Sentado":
                    List<Double>x= Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
                    List<Double>y= Arrays.asList(5.0, 3.0, 8.0, 20.0, 4.0);

                    DatosGrafica horasSentado= new DatosGrafica("Horas Sentado",x,y);
                    crearGrafica(horasSentado,vistaGrafica);

                    vmin.setText("5hrs");
                    vprom.setText("12hrs");
                    vmax.setText("19hrs");
                    break;

                case "Horas Sensibles":
                    List<Double> xSensibles = Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
                    List<Double> ySensibles = Arrays.asList(6.0, 11.0, 16.0, 21.0, 26.0);
                    DatosGrafica horasSensibles = new DatosGrafica("Horas Sensibles", xSensibles, ySensibles);
                    crearGrafica(horasSensibles,vistaGrafica);

                    vmin.setText("5:00");
                    vprom.setText("15:00");
                    vmax.setText("21:00");
                    break;

                case "Progreso Avisos":
                    List<Double> xAvisos = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
                    List<Double> yAvisos = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
                    DatosGrafica progresoAvisos = new DatosGrafica("Progreso Avisos", xAvisos, yAvisos);
                    crearGrafica(progresoAvisos,vistaGrafica);

                    vmin.setText("5");
                    vprom.setText("10");
                    vmax.setText("20");
                    hacerVisible_Grafica2(vista_graficas);
                    grafica.setImageResource(R.drawable.avisossemanales);

                    break;

                case "Avisos Ignorados":

                    //LOS VALORES DE X TIENEN QUE ESTAR EN ORDEN ASCENTDE O DA ERROR

                    List<Double> xIgnorados = Arrays.asList(3.0, 4.5, 5.0, 9.0, 10.0);
                    List<Double> yIgnorados = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
                    DatosGrafica avisosIgnorados = new DatosGrafica("AvisosIgnorados", xIgnorados, yIgnorados);

                    crearGrafica(avisosIgnorados,vistaGrafica);

                    vmin.setText("1");
                    vprom.setText("7");
                    vmax.setText("22");
                    grafica.setImageResource(R.drawable.avisossemanales);
                    break;
            }
        }
         else if (mensual_o_semanal.equals("mensual")) {

            switch (grafica_elegida) {
                case "Horas Sentado":
                    List<Double>x= Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
                    List<Double>y= Arrays.asList(5.0, 10.0, 15.0, 20.0, 25.0);

                    DatosGrafica horasSentado= new DatosGrafica("Horas Sentado",x,y);
                    crearGrafica(horasSentado,vistaGrafica);

                    vmin.setText("12hrs");
                    vprom.setText("20hrs");
                    vmax.setText("30hrs");
                    break;

                case "Horas Sensibles":

                    List<Double> xSensibles = Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
                    List<Double> ySensibles = Arrays.asList(6.0, 11.0, 16.0, 21.0, 26.0);
                    DatosGrafica horasSensibles = new DatosGrafica("Horas Sensibles", xSensibles, ySensibles);
                    crearGrafica(horasSensibles,vistaGrafica);

                    vmin.setText("9:00");
                    vprom.setText("18:00");
                    vmax.setText("24:00");
                    break;

                case "Progreso Avisos":
                    List<Double> xAvisos = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
                    List<Double> yAvisos = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
                    DatosGrafica progresoAvisos = new DatosGrafica("Progreso Avisos", xAvisos, yAvisos);
                    crearGrafica(progresoAvisos,vistaGrafica);

                    vmin.setText("12");
                    vprom.setText("25");
                    vmax.setText("30");
                    hacerVisible_Grafica2(vista_graficas);
                    grafica.setImageResource(R.drawable.avisosmensuales);

                    break;

                case "Avisos Ignorados":

                    List<Double> xIgnorados = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
                    List<Double> yIgnorados = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
                    DatosGrafica avisosIgnorados = new DatosGrafica("AvisosIgnorados", xIgnorados, yIgnorados);

                    crearGrafica(avisosIgnorados,vistaGrafica);


                    vmin.setText("5");
                    vprom.setText("15");
                    vmax.setText("35");
                    grafica.setImageResource(R.drawable.avisosmensuales);
                    break;
            }
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
        TextView promText2 = vista_graficas.findViewById(R.id.textView16);
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