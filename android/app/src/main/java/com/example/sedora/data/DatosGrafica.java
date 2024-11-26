package com.example.sedora.data;


import android.util.Log;

import androidx.annotation.Nullable;

import com.example.sedora.presentation.managers.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//EN ESTA CLASE SE CONSTRUYEN TODOS LOS DATOS NECESARIOS PARA CREAR UNA GRAFICA
public class DatosGrafica {

    private String nombre_grafica;
    private List<Double> valoresX= new ArrayList<>();
    private List<Double> valoresY= new ArrayList<>();

    private List<Date> valoresXFechas=new ArrayList<>();

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

    public  void añadir_una_lista_de_datos_Nuevos(List<Double> valores_nuevosX,List<Double> valores_nuevosY) {

        for (int i = 0; i < valores_nuevosX.size(); i++) {
                this.valoresX.add(valores_nuevosX.get(i));
        }

        for (int i = 0; i < valores_nuevosY.size(); i++) {
            this.valoresY.add(valores_nuevosY.get(i));
        }
    }

    //Recibe el campo que quiers evaaluar y lo añade a lista que quieres
    //Ejemplo: quiero los datos de presion el dia de ayer,
    // graficaPresion.(presion)
    //Mete la presion en su lista Y y el dia en su lista X (esto dentro de la funcion)
    public void getDatos_de_Firebase(String campo,String fecha){

        //FirebaseHelper firebaseHelper=new FirebaseHelper();
        FirebaseFirestore database= FirebaseFirestore.getInstance();

        database.collection("usuarios").document("L9ZGAhbYotZIqlK4aKMKkgAFWbL2")//Cambiar esto por un get usuario
                .collection("Datos").document(fecha)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore", "Error al leer", error);
                        } else if (value!=null) {
                            Double valor= value.getDouble(campo);
                            Log.d("PRUEBA", "VALOR: "+valor );
                        }
                    }
                });

    }
}
