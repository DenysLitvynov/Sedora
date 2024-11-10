package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sedora.R;
import com.example.sedora.model.RecyclerViewItem;
import com.example.sedora.presentation.managers.SpaceItemDecoration;
import com.example.sedora.presentation.adapters.FAQAdapter;

import java.util.ArrayList;
import java.util.List;

public class PoliticaDePrivacidad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.politica_de_privacidad);

        // Configura el texto del header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Política de privacidad");

        // para entrar en la página de notificaciones
        //ImageButton buttonIcon = findViewById(R.id.CamapanaNotificacionesPoliticaDePrivacidad);

        // Inicializar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPoliticaDePrivacidad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Agregar espaciado entre elementos
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_space); // Define el espaciado en `dimens.xml`
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        // Cargar datos de ejemplo
        List<RecyclerViewItem> PoliticaDePrivacidadItemList = new ArrayList<>();
        PoliticaDePrivacidadItemList.add(new RecyclerViewItem("¿Qué servicios ofrecemos?", "Fecha de entrada en vigor: 19/10/2024\n" +
                "\n" +
                "En Sedora, respetamos su privacidad y estamos comprometidos a proteger los datos personales que usted comparte con nosotros. Esta Política de Privacidad describe cómo recopilamos, utilizamos, protegemos y compartimos su información, así como los derechos que usted tiene sobre ella.\n" +
                "\n" +
                "1. Información que recopilamos\n" +
                "Al utilizar nuestra aplicación, podemos recopilar los siguientes tipos de información:\n" +
                "\n" +
                "Información personal: como su nombre, dirección de correo electrónico, y otros datos que usted proporciona al registrarse o interactuar con nuestra app.\n" +
                "Datos de uso: como hábitos de postura, tiempos de uso, y patrones de comportamiento, con el fin de proporcionarle alertas y recomendaciones personalizadas.\n" +
                "Información de dispositivo: como la dirección IP, tipo de dispositivo, sistema operativo y datos de diagnóstico.\n" +
                "2. Cómo utilizamos su información\n" +
                "Utilizamos los datos que recopilamos para:\n" +
                "\n" +
                "Proporcionar y mejorar la funcionalidad de la aplicación.\n" +
                "Personalizar la experiencia del usuario y ofrecer recomendaciones de ergonomía.\n" +
                "Monitorear y analizar el uso de la aplicación para mejorar su rendimiento.\n" +
                "Comunicar novedades, actualizaciones y otras notificaciones relacionadas con el servicio.\n" +
                "3. Cómo compartimos su información\n" +
                "No vendemos ni compartimos su información personal con terceros sin su consentimiento, excepto en los siguientes casos:\n" +
                "\n" +
                "Proveedores de servicios: Podemos compartir datos con terceros que proporcionan servicios en nuestro nombre, como análisis de datos o soporte técnico.\n" +
                "Requisitos legales: Compartiremos información si es necesario para cumplir con la ley, responder a procesos legales o proteger los derechos, propiedad o seguridad de Sedora, nuestros usuarios, o el público.\n" +
                "4. Seguridad de su información\n" +
                "Tomamos medidas razonables para proteger su información personal contra accesos no autorizados, pérdida, robo, alteración o destrucción. Sin embargo, ningún sistema es completamente seguro, y no podemos garantizar la seguridad absoluta de sus datos.\n" +
                "\n" +
                "5. Sus derechos\n" +
                "Como usuario, tiene derecho a:\n" +
                "\n" +
                "Acceder, rectificar o eliminar la información personal que tenemos sobre usted.\n" +
                "Retirar su consentimiento para el tratamiento de datos en cualquier momento.\n" +
                "Solicitar la portabilidad de los datos en un formato legible.\n" +
                "Presentar una queja ante la autoridad competente de protección de datos si considera que hemos infringido sus derechos.\n" +
                "6. Cambios en esta política\n" +
                "Nos reservamos el derecho de actualizar esta Política de Privacidad en cualquier momento. Si realizamos cambios, publicaremos la nueva política en la aplicación y actualizaremos la fecha de entrada en vigor.\n" +
                "\n" +
                "7. Contacto\n" +
                "Si tiene alguna pregunta o inquietud sobre esta Política de Privacidad, puede contactarnos en:\n" +
                "Correo electrónico: atencion-al-cliente@sedora.es\n" +
                "Teléfono: 666666666"));
        // Log.d("PoliticaDePrivacidad", "Número de ítems: " + PoliticaDePrivacidadItemList.size());

        // Configurar el adapter
        FAQAdapter faqAdapter = new FAQAdapter(PoliticaDePrivacidadItemList);
        recyclerView.setAdapter(faqAdapter);

        /* Funcionalidad al botón de flecha
        ImageButton flechaRetroceso = findViewById(R.id.FlechaRetrocesoPoliticaDePrivacidad);
        flechaRetroceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para redirigir a la actividad de ajustes
                Intent intent = new Intent(PoliticaDePrivacidad.this, AjustesActivity.class);
                startActivity(intent);
            }
        });

        // Configura el clic en el button_icon para entrar en Notificaciones
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PoliticaDePrivacidad.this, RecyclerActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar la actividad actual
            }
        });*/
    }
}
