package com.example.sedora.data;

import com.example.sedora.model.SensorData;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class SensorDataList {

    private List<SensorData> listaDatos;
    private Random random;

    public SensorDataList() {
        listaDatos = new ArrayList<>();
        random = new Random();
        añadirDatosEjemplo();
    }

    private void añadirDatosEjemplo() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            Timestamp timestamp = new Timestamp(calendar.getTime());

            listaDatos.add(new SensorData(
                    generarLuminosidad(),
                    generarPresion(),
                    generarPresion(),
                    generarRuido(),
                    generarTemperatura(),
                    generarHumedad(),
                    generarProximidad(),
                    generarPostura(),
                    generarDistanciaMonitor(),
                    generarIluminacion(),
                    generarTiempoSentado(),
                    generarHidratacion(),
                    timestamp
            ));

            calendar.add(Calendar.MINUTE, -1);
        }
    }

    private double generarLuminosidad() {
        return 200.0 + (random.nextDouble() * 200.0); // Rango: 200.0 - 400.0
    }

    private double generarPresion() {
        return 1.0 + (random.nextDouble() * 1.0); // Rango: 1.0 - 2.0
    }

    private double generarRuido() {
        return 30.0 + (random.nextDouble() * 40.0); // Rango: 30.0 - 70.0
    }

    private double generarTemperatura() {
        return 18.0 + (random.nextDouble() * 10.0); // Rango: 18.0 - 28.0
    }

    private double generarHumedad() {
        return 30.0 + (random.nextDouble() * 40.0); // Rango: 30.0 - 70.0
    }

    private double generarProximidad() {
        return 0.0 + (random.nextDouble() * 1.0); // Rango: 0.0 - 1.0
    }

    private double generarPostura() {
        return random.nextDouble() * 10.0; // Rango: 0.0 - 10.0 (ejemplo)
    }

    private double generarDistanciaMonitor() {
        return 40.0 + (random.nextDouble() * 20.0); // Rango: 40.0 - 60.0 (ejemplo)
    }

    private double generarIluminacion() {
        return 100.0 + (random.nextDouble() * 400.0); // Rango: 100.0 - 500.0 (ejemplo)
    }

    private double generarTiempoSentado() {
        return random.nextDouble() * 300.0; // Rango: 0.0 - 300.0 minutos (ejemplo)
    }

    private double generarHidratacion() {
        return random.nextDouble() * 2.0; // Rango: 0.0 - 2.0 litros (ejemplo)
    }

    public List<SensorData> getListaDatos() {
        return listaDatos;
    }
}
