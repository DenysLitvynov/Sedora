package com.example.sedora.presentation.views;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.RecyclerViewItem;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.example.sedora.presentation.managers.SpaceItemDecoration;
import com.example.sedora.presentation.adapters.AcercaDeAdapter;

import java.util.ArrayList;
import java.util.List;

public class AcercaDe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);

        // Obtén el Header
        Header header = findViewById(R.id.header);

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Acerca De");

//        // Comprueba las notificaciones
//        NotificacionManager notificacionManager = new NotificacionManager();
//        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
//        header.updateNotificationIcon(hasNotifications);

        // Inicializar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewAcercaDe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Agregar espaciado entre elementos
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_space); // Define el espaciado en `dimens.xml`
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        // Cargar datos de ejemplo
        List<RecyclerViewItem> AcercaDeItemList = new ArrayList<>();
        AcercaDeItemList.add(new RecyclerViewItem("¿Quiénes somos?", "En Sedora, somos un equipo multidisciplinario comprometido con la mejora de la salud y el bienestar en el entorno laboral. Nuestra misión es integrar tecnología avanzada y ergonomía para optimizar la postura y los hábitos de trabajo. A través de innovadores sensores y análisis en tiempo real, proporcionamos soluciones prácticas que ayudan a prevenir problemas de salud asociados con el trabajo sedentario. Creemos en un enfoque proactivo para transformar el espacio de trabajo en un entorno más saludable, eficiente y cómodo para todos."));
        AcercaDeItemList.add(new RecyclerViewItem("¿Qué nos define?", "Nos define nuestro compromiso con la innovación, la salud y el bienestar en el entorno laboral. Combinamos tecnología de vanguardia con un enfoque ergonómico para crear soluciones que promueven mejores hábitos posturales. Nos destacamos por ofrecer un enfoque proactivo, personalizado y accesible para mejorar la calidad de vida en el trabajo, ayudando a nuestros usuarios a mantenerse cómodos, saludables y productivos cada día."));
        AcercaDeItemList.add(new RecyclerViewItem("¿Qué nos hace diferentes?", "Nos diferenciamos por integrar sensores inteligentes directamente en el entorno de trabajo para monitorizar y mejorar la ergonomía de manera continua. Ofrecemos soluciones personalizadas y adaptativas, que no solo detectan malos hábitos, sino que también proporcionan recomendaciones prácticas en tiempo real. Nuestra plataforma es fácil de usar y está diseñada pensando en la salud y el bienestar a largo plazo, permitiendo a los usuarios optimizar su postura y productividad de forma natural y efectiva."));
        AcercaDeItemList.add(new RecyclerViewItem("¿Cuales son nuestros valores?", "Nuestros valores se centran en la innovación, la salud y el bienestar. Creemos en el poder de la tecnología para mejorar la calidad de vida, promoviendo hábitos saludables en el entorno laboral y educativo. Nos guiamos por el compromiso con la excelencia, la ética profesional y la atención al detalle, siempre enfocados en ofrecer soluciones que realmente hagan una diferencia en la ergonomía y el bienestar de nuestros usuarios."));
        AcercaDeItemList.add(new RecyclerViewItem("¿Cuál es nuestro objetivo?", "Nuestro objetivo es mejorar la calidad de vida de las personas a través de soluciones tecnológicas que optimicen la ergonomía en espacios de trabajo y estudio. Buscamos promover hábitos saludables y prevenir problemas derivados de una mala postura, ayudando a nuestros usuarios a crear entornos más confortables, eficientes y saludables."));
        AcercaDeItemList.add(new RecyclerViewItem("¿Qué impacto buscamos generar?", "Buscamos generar un impacto positivo en la salud y el bienestar de las personas al transformar sus hábitos de trabajo y estudio. Queremos reducir los problemas asociados con la ergonomía deficiente, fomentando prácticas saludables y sostenibles. Aspiramos a crear entornos más seguros, productivos y adaptados a las necesidades individuales de cada usuario, contribuyendo a su bienestar a largo plazo. "));

        // Configurar el adapter
        AcercaDeAdapter acercaDeAdapter = new AcercaDeAdapter(AcercaDeItemList);
        recyclerView.setAdapter(acercaDeAdapter);

    }
}
