package com.example.sedora.presentation.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sedora.data.DatosGrafica;
import com.example.sedora.R;
import com.example.sedora.presentation.managers.FirebaseHelper;
import com.example.sedora.presentation.views.GraficaActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grafica extends Fragment {

    private static final String ARG_SEM_MENS = "semanal_mensual"; // Para diferenciar entre semanal y mensual
    private String tipoGrafica; // "semanal" o "mensual"
    private ScrollView scrollView;
    private DatosGrafica puntosHorasSentado=new DatosGrafica("Horas Sentado",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosHorasSensibles=new DatosGrafica("Horas Sensibles",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosAvisosIgnorados=new DatosGrafica("Avisos Ignorados",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosProgresoAvisos=new DatosGrafica("Progreso Avisos",new ArrayList<>(),new ArrayList<>());


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


    //Nota: Una vez veamos como sirve el firebase con las graqficas, cambair esta funcion para cargue datos basado en el usuario
    public void cargarDatos(View vista_graficas, String grafica_elegida,String mensual_o_semanal,GraphView vistaGrafica){
        TextView vmin = vista_graficas.findViewById(R.id.Valor_min);
        TextView vprom = vista_graficas.findViewById(R.id.Valor_promedio);
        TextView vmax = vista_graficas.findViewById(R.id.Valor_max);


        ImageView grafica = vista_graficas.findViewById(R.id.grafica1);

        grafica.setImageResource(R.drawable.graficadiatemp);

        if (mensual_o_semanal.equals("semanal")){
            switch (grafica_elegida) {
                case "Horas Sentado":
//                    List<Double>x= Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
//                    List<Double>y= Arrays.asList(5.0, 3.0, 8.0, 20.0, 4.0);
//
//                    DatosGrafica horasSentado= new DatosGrafica("Horas Sentado",x,y);
//                    crearGrafica(horasSentado,vistaGrafica,"Avisos","Horas");


//                    puntosHorasSentado.añadir_nuevo_Dato("1", 5.0);
//                    puntosHorasSentado.añadir_nuevo_Dato("2", 10.0);
                    getDatos_de_Firebase("presion1Promedio",puntosHorasSentado,vistaGrafica);


                    vmin.setText("5hrs");
                    vprom.setText("12hrs");
                    vmax.setText("19hrs");
                    break;

                case "Horas Sensibles":
//                    List<Double> xSensibles = Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
//                    List<Double> ySensibles = Arrays.asList(6.0, 11.0, 16.0, 21.0, 26.0);
//                    DatosGrafica horasSensibles = new DatosGrafica("Horas Sensibles", xSensibles, ySensibles);
//                    crearGrafica(horasSensibles,vistaGrafica,"Avisos","Horas");

                    vmin.setText("5:00");
                    vprom.setText("15:00");
                    vmax.setText("21:00");
                    break;

                case "Progreso Avisos":
//                    List<Double> xAvisos = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
//                    List<Double> yAvisos = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
//                    DatosGrafica progresoAvisos = new DatosGrafica("Progreso Avisos", xAvisos, yAvisos);
//                    crearGrafica(progresoAvisos,vistaGrafica,"Avisos","Dias");

                    vmin.setText("5");
                    vprom.setText("10");
                    vmax.setText("20");
                    hacerVisible_Grafica2(vista_graficas);
                    grafica.setImageResource(R.drawable.avisossemanales);

                    break;

                case "Avisos Ignorados":

                    //LOS VALORES DE X TIENEN QUE ESTAR EN ORDEN ASCENTDE O DA ERROR

//                    List<Double> xIgnorados = Arrays.asList(3.0, 4.5, 5.0, 9.0, 10.0);
//                    List<Double> yIgnorados = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
//                    DatosGrafica avisosIgnorados = new DatosGrafica("AvisosIgnorados", xIgnorados, yIgnorados);

//                    crearGrafica(avisosIgnorados,vistaGrafica,"Avisos Ignoras","Dias");

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
//                    List<Double>x= Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
//                    List<Double>y= Arrays.asList(5.0, 10.0, 15.0, 20.0, 25.0);
//
//                    DatosGrafica horasSentado= new DatosGrafica("Horas Sentado",x,y);
//                    crearGrafica(horasSentado,vistaGrafica,"Avisos","Horas");

                    vmin.setText("12hrs");
                    vprom.setText("20hrs");
                    vmax.setText("30hrs");
                    break;

                case "Horas Sensibles":

//                    List<Double> xSensibles = Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0);
//                    List<Double> ySensibles = Arrays.asList(6.0, 11.0, 16.0, 21.0, 26.0);
//                    DatosGrafica horasSensibles = new DatosGrafica("Horas Sensibles", xSensibles, ySensibles);
//                    crearGrafica(horasSensibles,vistaGrafica,"Avisos","Dias");

                    vmin.setText("9:00");
                    vprom.setText("18:00");
                    vmax.setText("24:00");
                    break;

                case "Progreso Avisos":
//                    List<Double> xAvisos = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
//                    List<Double> yAvisos = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
//                    DatosGrafica progresoAvisos = new DatosGrafica("Progreso Avisos", xAvisos, yAvisos);
//                    crearGrafica(progresoAvisos,vistaGrafica,"Avisos","Horas");

                    vmin.setText("12");
                    vprom.setText("25");
                    vmax.setText("30");
                    hacerVisible_Grafica2(vista_graficas);
                    grafica.setImageResource(R.drawable.avisosmensuales);

                    break;

                case "Avisos Ignorados":

//                    List<Double> xIgnorados = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
//                    List<Double> yIgnorados = Arrays.asList(6.0, 11.0, 20.0, 21.0, 30.0);
//                    DatosGrafica avisosIgnorados = new DatosGrafica("AvisosIgnorados", xIgnorados, yIgnorados);
//
//                    crearGrafica(avisosIgnorados,vistaGrafica,"Avisos","Horas");


                    vmin.setText("5");
                    vprom.setText("15");
                    vmax.setText("35");
                    grafica.setImageResource(R.drawable.avisosmensuales);
                    break;
            }
        }
    }



    //Recibe el campo que quiers evaaluar y lo añade a lista que quieres
    //Ejemplo: quiero los datos de presion el dia de ayer,
    // graficaPresion.(presion)
    //Mete la presion en su lista Y y el dia en su lista X (esto dentro de la funcion)
    private void getDatos_de_Firebase(String campo, DatosGrafica grafica_a_cual_se_añade,GraphView vistaGrafica) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("usuarios")
                .document("L9ZGAhbYotZIqlK4aKMKkgAFWbL2")//AQUI EL USER
                .collection("Datos").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String fecha = document.getId();
                            Double valor = document.contains(campo) ? document.getDouble(campo) : null;
                            if (valor != null) {
                                grafica_a_cual_se_añade.añadir_nuevo_Dato(fecha, valor);
                            }
                        }
                        // Llama a crearGrafica aquí, cuando los datos ya estén listos
                        crearGrafica(grafica_a_cual_se_añade, vistaGrafica, "Horas", "Dias");
                        //ESTO SE PODRIA HACER EN UN CALLBACK pa no meter to.do en la funcion,
                        // pero no se como van los callbacks en JAVA. Esta nota es pa mi.
                    }
                }).addOnFailureListener(e -> Log.e("Firestore", "Error al recuperar documentos", e));
    }


    public void crearGrafica(DatosGrafica grafica_a_Construir, GraphView vista_Grafica, String yTitulo, String xTitulo) {

        List<Double> yValues = grafica_a_Construir.getValoresY();
        List<String> xValues = grafica_a_Construir.getValoresX();
        int color = ContextCompat.getColor(getContext(), R.color.verde_secundario);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        Log.d("DEBUG", "Y Values: " + yValues.toString());
        Log.d("DEBUG", "X Values: " + xValues.toString());

        // Usar índices como valores X para graficar
        for (int i = 0; i < yValues.size(); i++) {
            series.appendData(new DataPoint(i, yValues.get(i)), true, yValues.size());
        }
        vista_Grafica.addSeries(series);

       // vista_Grafica.getGridLabelRenderer().setVerticalAxisTitle(yTitulo);
        vista_Grafica.getGridLabelRenderer().setHorizontalAxisTitle(xTitulo);

        vista_Grafica.getGridLabelRenderer().setNumHorizontalLabels(xValues.size());// Limita el número de etiquetas visibles a 4

        vista_Grafica.getViewport().setScalable(false);
        vista_Grafica.getViewport().setScrollable(false);

        //vista_Grafica.getGridLabelRenderer().setHumanRounding(true); // Evita redondeos para mostrar los valores originales
//        // Rotar etiquetas para evitar superposición
//        vista_Grafica.getGridLabelRenderer().setHorizontalLabelsAngle(45); // Rota las etiquetas 45 grados
        series.setDrawDataPoints(true);

        series.setColor(color); // Cambia el color de la línea
        series.setThickness(8); // Cambia el grosor de la línea


        // Formatear etiquetas del eje X para mostrar las fechas, pa que sirva con strings
        vista_Grafica.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int index = (int) value;
                    if (index >= 0 && index < xValues.size()) {
                        String fullDate = xValues.get(index);
                        return fullDate.substring(5); // Muestra solo "11-21"
                    }
                    return "";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

    }



    public void hacerVisible_Grafica2(View vista_graficas) {

        TextView subtitulo1=vista_graficas.findViewById(R.id.textView5);
        TextView subtitulo2=vista_graficas.findViewById(R.id.subtitulo);

        GraphView grafica2= vista_graficas.findViewById(R.id.Grafica2F);
        subtitulo1.setVisibility(View.VISIBLE);
        subtitulo2.setVisibility(View.VISIBLE);


//        grafica2.setVisibility(View.VISIBLE);
//        List<Double> xAvisos = Arrays.asList(1.0, 4.0, 5.0, 9.0, 12.0);
//        List<Double> yAvisos = Arrays.asList(6.0, 1.0, 20.0, 10.0, 9.0);
//        DatosGrafica progresoAvisos = new DatosGrafica("Progreso Avisos", xAvisos, yAvisos);
//        crearGrafica(progresoAvisos,grafica2,"Avisos","Horas");


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
//        scrollView.setOnTouchListener(null);
    }

}