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
            Timestamp timestamp = new Timestamp(calendar.getTime());

            listaDatos.add(new SensorData(
                    300.0 + (i * 0.5),
                    1.0 + (i * 0.02),
                    1.1 + (i * 0.01),
                    55.0 + (i * 0.3),
                    22.5 + (i * 0.1),
                    45.0 + (i * 0.2),
                    0.5 + (i * 0.01),
                    timestamp
            ));

            calendar.add(Calendar.MINUTE, -1);
        }
    }

    public List<SensorData> getListaDatos() {
        return listaDatos;
    }
}


