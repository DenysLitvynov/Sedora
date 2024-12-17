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
import com.example.sedora.presentation.views.GraficaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafica extends Fragment {

    private static final String ARG_SEM_MENS = "semanal_mensual"; // Para diferenciar entre semanal y mensual
    private String tipoGrafica; // "semanal" o "mensual"
    private ScrollView scrollView;
    private DatosGrafica puntosHorasSentado=new DatosGrafica("Horas Sentado",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosHorasSensibles=new DatosGrafica("Horas Sensibles",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosAvisosIgnorados=new DatosGrafica("Avisos Ignorados",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosProgresoAvisos=new DatosGrafica("Progreso Avisos",new ArrayList<>(),new ArrayList<>());
    private DatosGrafica puntosProgresoAvisos2=new DatosGrafica("Progreso Avisos",new ArrayList<>(),new ArrayList<>());

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
        if (mensual_o_semanal.equals("semanal")){
            switch (grafica_elegida) {
                case "Horas Sentado":

                    crearGraficaDatos("presion1Promedio",puntosHorasSentado,vistaGrafica,"Cantidad de Horas","Dias",
                            true,false,vista_graficas);
                    break;

                case "Horas Sensibles":
                        crearGraficaNotis(vista_graficas,vistaGrafica,puntosHorasSensibles,
                                "Horas","Dias",null,true,false);
                    break;

                case "Progreso Avisos":
                    crearGraficaNotis(vista_graficas,vistaGrafica,puntosProgresoAvisos,
                            "Horas","Dias","Distancia al monitor",true,false);
                    hacerVisible_Grafica2(vista_graficas,true);
                    break;

                case "Avisos Ignorados":
                    //LOS VALORES DE X TIENEN QUE ESTAR EN ORDEN ASCENTDE O DA ERROR
                    crearGraficaNotis(vista_graficas,vistaGrafica,puntosProgresoAvisos,
                            "Avisos","Dias",null,true,false);
                    break;
            }
        }
         else if (mensual_o_semanal.equals("mensual")) {
            switch (grafica_elegida) {
                case "Horas Sentado":
                    crearGraficaDatos("presion1Promedio",puntosHorasSentado,vistaGrafica,"Horas","Dias",
                            false,false,vista_graficas);
                    break;

                case "Horas Sensibles":
                    crearGraficaNotis(vista_graficas,vistaGrafica,puntosHorasSensibles,
                            "Horas","Dias",null,false,false);

                    break;

                case "Progreso Avisos":
                    crearGraficaNotis(vista_graficas,vistaGrafica,puntosProgresoAvisos,
                            "Horas","Dias","Distancia al monitor",false,false);
                    hacerVisible_Grafica2(vista_graficas,false);
                    break;

                case "Avisos Ignorados":
                    crearGraficaNotis(vista_graficas,vistaGrafica,puntosAvisosIgnorados,
                            "Avisos","Dias",null,false,false);
                    break;
            }
        }
    }


    //===========================================================================================================================
    //En base que el campo que quieres hacerle una grafica, rellena la lista y llama a crearGrafica dentro de la funcion
    //Ejemplo: quiero los datos de presion el dia de ayer,
    // graficaPresion.(presion)
    //Mete la presion en su lista Y y el dia en su lista X  y crea la grafica(esto dentro de la funcion)
    private void crearGraficaDatos(String campo, DatosGrafica grafica_a_cual_se_añade, GraphView vistaGrafica,String yTitulo, String xTitulo,boolean es_semanal,boolean esGrafica2,View vista) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String usuarioUID = auth.getCurrentUser().getUid();

        database.collection("usuarios")
                .document(usuarioUID)//AQUI EL USER
                .collection("Datos").get()//SI LAS NOTIS ESTAN EN OTRA COLECION HABRA QUE CAMBIAR ESTO A UNA VARIABLE
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String fecha = document.getId();
                            Double valor = document.contains(campo) ? document.getDouble(campo) : null;
                            if (valor != null) {
                                grafica_a_cual_se_añade.añadir_nuevo_Dato(fecha, valor);
                            }
                        }
                        if (es_semanal==true){
                            // Llama a crearGrafica aquí, cuando los datos ya estén listos
                            grafica_a_cual_se_añade.filtrarDatosPorSemanaActual();
                            crearGrafica(grafica_a_cual_se_añade, vistaGrafica, yTitulo, xTitulo);
                            setMaximo_Minimos_Promedios(vista,grafica_a_cual_se_añade, false);
                            if (esGrafica2){
                                setMaximo_Minimos_Promedios(vista,grafica_a_cual_se_añade, true);
                            }
                            //ESTO SE PODRIA HACER EN UN CALLBACK pa no meter to.do en la funcion,
                            // pero no se como van los callbacks en JAVA. Esta nota es pa mi.
                        }
                        grafica_a_cual_se_añade.filtrarDatosPorMesActual();
                        crearGrafica(grafica_a_cual_se_añade, vistaGrafica, yTitulo, xTitulo);
                        setMaximo_Minimos_Promedios(vista,grafica_a_cual_se_añade, false);
                        if (esGrafica2){
                            setMaximo_Minimos_Promedios(vista,grafica_a_cual_se_añade, true);
                        }
                    }
                }).addOnFailureListener(e -> Log.e("Firestore", "Error al recuperar documentos", e));
    }

    private void crearGraficaNotis(View vista, GraphView vistaGrafica, DatosGrafica grafica_a_cual_se_añade, String yTitulo,
                                   String xTitulo, String filtroTitulo, boolean es_semanal, boolean esGrafica2) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String usuarioUID = auth.getCurrentUser().getUid();

        // Cambiar la colección según sea necesario
        String coleccion = "notificaciones"; // Puedes cambiarlo a una variable dinámica

        database.collection("usuarios")
                .document(usuarioUID)
                .collection(coleccion)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Map para almacenar el conteo de notificaciones por día
                        Map<String, Integer> conteoPorDia = new HashMap<>();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Obtener el valor del campo "titulo"
                            String titulo = document.getString("titulo");
                            String horaCompleta = document.getString("hora");

                            // Aplicar el filtro: solo procesar si "titulo" coincide con el filtro deseado
                            if (titulo != null && titulo.equals(filtroTitulo)) {
                                if (horaCompleta != null) {
                                    try {
                                        // Extraer solo el día (dd/MM/yyyy) de la cadena "hora"
                                        String dia = horaCompleta.split(" ")[1]; // "12/12/2024" de "11:44 12/12/2024"

                                        // Formatear el día al formato yyyy-MM-dd
                                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date fecha = inputFormat.parse(dia); // Parsear la fecha de entrada
                                        String diaFormateado = outputFormat.format(fecha); // Formatear la fecha

                                        // Contar notificaciones por día formateado
                                        conteoPorDia.put(diaFormateado, conteoPorDia.getOrDefault(diaFormateado, 0) + 1);

                                    } catch (ParseException e) {
                                        e.printStackTrace(); // Manejo de errores si el formato de fecha es inválido
                                    }
                                }
                            }else if (horaCompleta!=null){
                                try {

                                    // Extraer solo el día (dd/MM/yyyy) de la cadena "hora"
                                    String dia = horaCompleta.split(" ")[1]; // "12/12/2024" de "11:44 12/12/2024"

                                    // Formatear el día al formato yyyy-MM-dd
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date fecha = inputFormat.parse(dia); // Parsear la fecha de entrada
                                    String diaFormateado = outputFormat.format(fecha); // Formatear la fecha

                                    // Contar notificaciones por día formateado
                                    conteoPorDia.put(diaFormateado, conteoPorDia.getOrDefault(diaFormateado, 0) + 1);

                                } catch (ParseException e) {
                                    e.printStackTrace(); // Manejo de errores si el formato de fecha es inválido
                                }

                            }
                        }
                        // Añadir datos al objeto de gráfica
                        for (Map.Entry<String, Integer> entry : conteoPorDia.entrySet()) {
                            String dia = entry.getKey();
                            int valor = entry.getValue();
                            double valorDouble=valor;
                            // Añadir a la gráfica
                            grafica_a_cual_se_añade.añadir_nuevo_Dato(dia, valorDouble);
                        }

                        if (es_semanal==true) {
                            grafica_a_cual_se_añade.filtrarDatosPorSemanaActual();
                            // Crear la gráfica con los datos recopilados
                            crearGrafica(grafica_a_cual_se_añade, vistaGrafica, yTitulo, xTitulo);
                            setMaximo_Minimos_Promedios(vista, grafica_a_cual_se_añade, false);
                        }
                        // Crear la gráfica con los datos recopilados
                        crearGrafica(grafica_a_cual_se_añade, vistaGrafica, yTitulo, xTitulo);
                        setMaximo_Minimos_Promedios(vista, grafica_a_cual_se_añade, false);
                        if (esGrafica2){
                            setMaximo_Minimos_Promedios(vista, grafica_a_cual_se_añade, true);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al recuperar notificaciones", e));
    }

//===========================================================================================================================

    private void  setMaximo_Minimos_Promedios(View vista,DatosGrafica datos,boolean esGrafica2){
        TextView vmin = vista.findViewById(R.id.Valor_min);
        TextView vprom = vista.findViewById(R.id.Valor_promedio);
        TextView vmax = vista.findViewById(R.id.Valor_max);

        vmin.setText(datos.getValorMinimo_asString());
        vprom.setText(datos.get_promedio_AsString());
        vmax.setText(datos.getValorMaximo_asString());

        if (esGrafica2){

            TextView vmin2 = vista.findViewById(R.id.Valor_min2);
            TextView vprom2 = vista.findViewById(R.id.Valor_prom2);
            TextView vmax2 = vista.findViewById(R.id.Valor_max2);

            vmin2.setVisibility(View.VISIBLE);
            vprom2.setVisibility(View.VISIBLE);
            vmax2.setVisibility(View.VISIBLE);

            vmin2.setText(datos.getValorMinimo_asString());
            vprom2.setText(datos.get_promedio_AsString());
            vmax2.setText(datos.getValorMaximo_asString());
        }

    }


    private void crearGrafica(DatosGrafica grafica_a_Construir, GraphView vista_Grafica, String yTitulo, String xTitulo) {

        List<Double> yValues = grafica_a_Construir.getValoresY();
        List<String> xValues = grafica_a_Construir.getValoresX();
        int color = ContextCompat.getColor(getContext(), R.color.verde_secundario);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        // Crear puntos de datos usando índices para X
        for (int i = 0; i < yValues.size(); i++) {
            series.appendData(new DataPoint(i, yValues.get(i)), true, yValues.size());
        }

        // *** FORMATEAR ETIQUETAS DEL EJE X ***
        vista_Grafica.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int index = (int) value; // Convertir valor X a índice
                    if (index >= 0 && index < xValues.size()) {
                        String fullDate = xValues.get(index);
                        return fullDate.substring(5); // Muestra solo "MM-DD"
                    }
                    return "";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        vista_Grafica.addSeries(series);

        // Configurar título de los ejes
        vista_Grafica.getGridLabelRenderer().setVerticalAxisTitle(yTitulo);

        vista_Grafica.getGridLabelRenderer().setHorizontalAxisTitle(xTitulo);

        vista_Grafica.getGridLabelRenderer().setPadding(25);
        vista_Grafica.getGridLabelRenderer().setTextSize(30);
        vista_Grafica.getGridLabelRenderer().setVerticalLabelsVisible(false);


        // Configurar el estilo de la línea
        series.setDrawDataPoints(true);  // Puntos visibles
        series.setColor(color);          // Cambia color
        series.setThickness(8);          // Grosor de la línea

        // *** CONFIGURAR ESCALA MANUAL ***
        // Ajusta los límites del eje X e Y manualmente
        vista_Grafica.getViewport().setXAxisBoundsManual(true);
//       vista_Grafica.getViewport().setYAxisBoundsManual(true);

            vista_Grafica.getViewport().setMinX(0);                        // Inicio del eje X
            vista_Grafica.getViewport().setMaxX(xValues.size());          // Fin del eje X
//        vista_Grafica.getViewport().setMinY(Collections.min(yValues));   // Mínimo del eje Y
//        vista_Grafica.getViewport().setMaxY(Collections.max(yValues));   // Máximo del eje Y

        // *** CONFIGURAR NÚMERO DE ETIQUETAS EN EL EJE X ***
        vista_Grafica.getGridLabelRenderer().setNumHorizontalLabels(xValues.size()+1);

        // *** OPCIONAL: Rotar etiquetas del eje X si es necesario ***
        vista_Grafica.getGridLabelRenderer().setHorizontalLabelsAngle(0);

        // 2. Deshabilitar espacio automático
        vista_Grafica.getViewport().setScrollable(false); // No permite mover
        vista_Grafica.getViewport().setScalable(false);   // No permite zoom automático

    }


    public void hacerVisible_Grafica2(View vista_graficas, boolean esSemanal) {

        TextView subtitulo1 = vista_graficas.findViewById(R.id.textView5);
        TextView subtitulo2 = vista_graficas.findViewById(R.id.subtitulo);

        GraphView grafica2 = vista_graficas.findViewById(R.id.Grafica2F);
        subtitulo1.setVisibility(View.VISIBLE);
        subtitulo2.setVisibility(View.VISIBLE);

        grafica2.setVisibility(View.VISIBLE);
        crearGraficaNotis(vista_graficas,grafica2,puntosProgresoAvisos2,
                "Horas","Dias","Descanso",esSemanal,true);

//
//        // Hacer visibles los valores asociados
//        TextView vmin = vista_graficas.findViewById(R.id.Valor_min2);
//        TextView vprom = vista_graficas.findViewById(R.id.Valor_prom2);
//        TextView vmax = vista_graficas.findViewById(R.id.Valor_max2);
//        vmin.setVisibility(View.VISIBLE);
//        vprom.setVisibility(View.VISIBLE);
//        vmax.setVisibility(View.VISIBLE);
//        vmin.setText(puntosProgresoAvisos2.getValorMinimo_asString());
//        vprom.setText(puntosProgresoAvisos2.get_promedio_AsString());
//        vmax.setText(puntosProgresoAvisos2.getValorMaximo_asString());

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
    }


}