package com.example.sedora.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//EN ESTA CLASE SE CONSTRUYEN TODOS LOS DATOS NECESARIOS PARA CREAR UNA GRAFICA
public class DatosGrafica {

    private String nombre_grafica;
    private List<String> valoresX= new ArrayList<>();
    private List<Double> valoresY= new ArrayList<>();

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

public String get_promedio_AsString(){
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

    public String getValorMaximo_asString(){

        if (valoresY.isEmpty()) {
            return "0"; // Mensaje si la lista está vacía
        }
        return String.valueOf(Collections.max(valoresY));
    }

    public String getValorMinimo_asString(){
        if (valoresY.isEmpty()) {
            return "0"; // Mensaje si la lista está vacía
        }
        return String.valueOf(Collections.min(valoresY));

    }

    public  void añadir_nuevo_Dato(String valornuevoX,double valornuevoY){
        this.valoresX.add(valornuevoX);
        this.valoresY.add(valornuevoY);
    }

    public  void añadir_una_lista_de_datos_Nuevos(List<String> valores_nuevosX,List<Double> valores_nuevosY) {

        for (int i = 0; i < valores_nuevosX.size(); i++) {
                this.valoresX.add(valores_nuevosX.get(i));
        }

        for (int i = 0; i < valores_nuevosY.size(); i++) {
            this.valoresY.add(valores_nuevosY.get(i));
        }
    }
}
