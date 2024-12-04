package com.example.sedora.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//EN ESTA CLASE SE CONSTRUYEN TODOS LOS DATOS NECESARIOS PARA CREAR UNA GRAFICA
public class DatosGrafica {

    private String nombre_grafica;
    private List<String> valoresX = new ArrayList<>();
    private List<Double> valoresY = new ArrayList<>();

    //private List<Date> valoresXFechas=new ArrayList<>();

    public DatosGrafica(String nombre_grafica, List<String> valoresX, List<Double> valoresY) {
        this.nombre_grafica = nombre_grafica;
        this.valoresX = valoresX;
        this.valoresY = valoresY;
    }


    public String getNombre_grafica() {
        return nombre_grafica;
    }

    public void setNombre_grafica(String nombre_grafica) {
        this.nombre_grafica = nombre_grafica;
    }

    public List<String> getValoresX() {
        return valoresX;
    }

    public void setValoresX(List<String> valoresX) {
        this.valoresX = valoresX;
    }

    public List<Double> getValoresY() {
        return valoresY;
    }

    public void setValoresY(List<Double> valoresY) {
        this.valoresY = valoresY;
    }

    //=============================================================================================
    //=============================================================================================

    //METODOS

    //=============================================================================================
    //=============================================================================================

    public String get_promedio_AsString() {
        if (valoresY.isEmpty()) {
            return "0"; // Mensaje si la lista está vacía
        }
        double suma = 0;
        for (double valor : valoresY) {
            suma += valor;
        }
        double promedio = suma / valoresY.size();
        return String.valueOf(promedio);
    }

    public String getValorMaximo_asString() {

        if (valoresY.isEmpty()) {
            return "0"; // Mensaje si la lista está vacía
        }
        return String.valueOf(Collections.max(valoresY));
    }

    public String getValorMinimo_asString() {
        if (valoresY.isEmpty()) {
            return "0"; // Mensaje si la lista está vacía
        }
        return String.valueOf(Collections.min(valoresY));

    }

    public void añadir_nuevo_Dato(String valornuevoX, double valornuevoY) {
        this.valoresX.add(valornuevoX);
        this.valoresY.add(valornuevoY);
    }

    public void añadir_una_lista_de_datos_Nuevos(List<String> valores_nuevosX, List<Double> valores_nuevosY) {

        for (int i = 0; i < valores_nuevosX.size(); i++) {
            this.valoresX.add(valores_nuevosX.get(i));
        }

        for (int i = 0; i < valores_nuevosY.size(); i++) {
            this.valoresY.add(valores_nuevosY.get(i));
        }
    }



    //ESTOS METODOS NO SE USAN HASTA QUE TENGAMOS MAS DIAS
    //Estara vacio siempre la lista si estamos en diciembre y tenemos los datos en noviembre
    private List<String> filtrarPorSemanaActual(List<String> fechas) {
        List<String> fechasFiltradas = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato según tus cadenas
        Calendar calendarioActual = Calendar.getInstance();
        int semanaActual = calendarioActual.get(Calendar.WEEK_OF_YEAR);
        int añoActual = calendarioActual.get(Calendar.YEAR);

        for (String fechaString : fechas) {
            try {
                Date fecha = sdf.parse(fechaString);
                Calendar calendarioFecha = Calendar.getInstance();
                calendarioFecha.setTime(fecha);

                // Verifica si la semana y el año coinciden
                if (calendarioFecha.get(Calendar.WEEK_OF_YEAR) == semanaActual &&
                        calendarioFecha.get(Calendar.YEAR) == añoActual) {
                    fechasFiltradas.add(fechaString);
                }
            } catch (ParseException e) {
                e.printStackTrace(); // Manejo de errores si el formato de fecha es inválido
            }
        }
        return fechasFiltradas;
    }

    public void filtrarDatosPorSemanaActual() {
        List<String> fechasFiltradas = filtrarPorSemanaActual(valoresX);
        List<Double> valoresFiltradosY = new ArrayList<>();

        for (String fechaFiltrada : fechasFiltradas) {
            int index = valoresX.indexOf(fechaFiltrada);
            if (index != -1) {
                valoresFiltradosY.add(valoresY.get(index));
            }
        }

        // Actualiza las listas con los datos filtrados
        this.valoresX = fechasFiltradas;
        this.valoresY = valoresFiltradosY;
    }

}
