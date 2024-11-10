package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sedora.R;
import com.example.sedora.model.RecyclerViewItem;
import com.example.sedora.presentation.managers.SpaceItemDecoration;
import com.example.sedora.presentation.adapters.FAQAdapter;

import java.util.ArrayList;
import java.util.List;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq);

        // Configura el texto del header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("FAQ");

        // Inicializar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFAQ);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Agregar espaciado entre elementos
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_space); // Define el espaciado en `dimens.xml`
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        // Cargar datos de ejemplo
        List<RecyclerViewItem> faqItemList = new ArrayList<>();
        faqItemList.add(new RecyclerViewItem("¿Qué servicios ofrecemos?", "En Sedora, ofrecemos un conjunto de servicios diseñados para mejorar la ergonomía en el trabajo:\n" +
                "\n" +
                "- Monitoreo de Hábitos: Analizamos tus hábitos laborales y el tiempo que pasas sentado.\n" +
                "- Análisis de Posturas: Evaluamos tu postura y te damos recomendaciones para mejorarla.\n" +
                "- Consejos de Ergonomía: Proporcionamos sugerencias para optimizar tu entorno de trabajo.\n" +
                "- Notificaciones: Recibe alertas cuando es momento de hacer pausas o ajustar tu postura.\n" +
                "- Informes Personalizados: Accede a estadísticas sobre tu comportamiento y hábitos laborales.\n" +
                "- Nuestra aplicación está diseñada para ayudarte a crear un ambiente de trabajo más saludable y productivo."));
        faqItemList.add(new RecyclerViewItem("¿Métodos de pago?", "Ofrecemos la posibilidad de pago mediante transferencia bancaria, pago con tarjeta, bizum o PayPal"));
        faqItemList.add(new RecyclerViewItem("¿Hay soporte fuera de horarios?", "El soporte de nuestra aplicación se encuentra disponible de 8 a 20 horas de lunes a viernes"));
        faqItemList.add(new RecyclerViewItem("¿Es seguro compartirmis datos?", "Si, es seguro puesto que gestionamos los datos nosotros mismos y nos los dejamos en manos de terceros, encuentra más información sobre qué y para qué usamos tus datos en las politicas de privacidad y consentimiento"));
        faqItemList.add(new RecyclerViewItem("¿Puedo añadir más sensores?", "Inicialmente, Sedora está pensado para ser una solución completa a los problemas ergonómicos del día a día de nuestros usuarios. Por lo que la aplicación no está concevida para soportar nuevos sensores. Si crees que se debería añadir un nuevo sensor o funcionalidad, por favor haznoslo saber mediante el correo de contacto sedorainfo@sedora.es y estudiaremos la propuesta."));
        faqItemList.add(new RecyclerViewItem("¿Cómo puedo contactar con el equipo de soporte?", "Para contactar con el servicio técnico de Sedora, puede escribir a sedoraincidencias@sedora.es "));

        Log.d("FAQ", "Número de ítems: " + faqItemList.size());

        // Configurar el adapter
        FAQAdapter faqAdapter = new FAQAdapter(faqItemList);
        recyclerView.setAdapter(faqAdapter);

    }
}
