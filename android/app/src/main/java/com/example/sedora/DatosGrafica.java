package com.example.sedora;


import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.List;

//EN ESTA CLASE SE CONSTRUYEN TODOS LOS DATOS NECESARIOS PARA CREAR UNA GRAFICA
public class DatosGrafica {

    private String nombre_grafica;
    private List<Double> valoresX= new ArrayList<>();
    private List<Double> valoresY= new ArrayList<>();

    public DatosGrafica(String nombre_grafica, List<Double> valoresX, List<Double> valoresY) {
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

    public List<Double> getValoresX() {
        return valoresX;
    }

    public void setValoresX(List<Double> valoresX) {
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

    public double calcular_valor_Promedio(){
        return 0;
    }

    public double getValorMaximo(){
        return 0;
    }

    public double getValorMinimo(){
        return 0;
    }

    public  void añadir_nuevo_Dato(double valornuevoX,double valornuevoY){
        this.valoresX.add(valornuevoX);
        this.valoresY.add(valornuevoY);
    }

    public  void añadir_varios_datos_Nuevos(List<Double> valores_nuevosX,List<Double> valores_nuevosY) {

        for (int i = 0; i < valores_nuevosX.size(); i++) {
                this.valoresX.add(valores_nuevosX.get(i));
        }

        for (int i = 0; i < valores_nuevosY.size(); i++) {
            this.valoresY.add(valores_nuevosY.get(i));
        }
    }
}
