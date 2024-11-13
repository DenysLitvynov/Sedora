package com.example.sedora.data;

import com.example.sedora.model.SensorData;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SensorDataList {
    private List<SensorData> listaDatos;

    public SensorDataList() {
        listaDatos = new ArrayList<>();
        añadirDatosEjemplo();
    }

    private void añadirDatosEjemplo() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            // Retrocede un día para cada nuevo dato
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Timestamp timestamp = new Timestamp(calendar.getTime());  // Firebase Timestamp

            listaDatos.add(new SensorData(
                    300.0 + (i * 0.5),  // luminosidad
                    1.0 + (i * 0.02),   // presion1
                    1.1 + (i * 0.01),   // presion2
                    55.0 + (i * 0.3),   // ruido
                    22.5 + (i * 0.1),   // temperatura
                    45.0 + (i * 0.2),   // humedad
                    0.5 + (i * 0.01),   // proximidad
                    timestamp           // timestamp de tipo Firebase Timestamp
            ));
        }
    }

    public List<SensorData> getListaDatos() {
        return listaDatos;
    }
}

