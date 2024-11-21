package com.example.sedora.presentation.views;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.R;
import com.example.sedora.presentation.managers.NotificacionManager;

public class PoliticaDePrivacidad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.politica_de_privacidad);

        // Configurar el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Política de Privacidad");

        // Configuración de notificaciones
        NotificacionManager notificacionManager = new NotificacionManager();
        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
        Header header = findViewById(R.id.header);
        header.updateNotificationIcon(hasNotifications);

        // Cargar el contenido estático en el TextView
        TextView textViewContent = findViewById(R.id.textViewPoliticaDePrivacidadContent);
        String politicaDePrivacidadContent = "¿Qué servicios ofrecemos?\n" + "Fecha de entrada en vigor: 19/10/2024\n\n"
                + "En Sedora, respetamos su privacidad y estamos comprometidos a proteger los datos personales "
                + "que usted comparte con nosotros. Esta Política de Privacidad describe cómo recopilamos, utilizamos, "
                + "protegemos y compartimos su información, así como los derechos que usted tiene sobre ella.\n\n"
                + "1. Información que recopilamos\n"
                + "- Información personal: como su nombre, dirección de correo electrónico, y otros datos que usted proporciona.\n"
                + "- Datos de uso: como hábitos de postura, tiempos de uso, y patrones de comportamiento.\n"
                + "- Información de dispositivo: como la dirección IP, tipo de dispositivo, sistema operativo y datos de diagnóstico.\n\n"
                + "2. Cómo utilizamos su información\n"
                + "Utilizamos los datos que recopilamos para proporcionar y mejorar la funcionalidad de la aplicación.\n\n"
                + "3. Cómo compartimos su información\n" +
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
                "Teléfono: 666666666";

        // Crear SpannableString
        SpannableString spannableString = new SpannableString(politicaDePrivacidadContent);

// Resaltar las frases específicas
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "¿Qué servicios ofrecemos?"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 348, 380, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "1. Información que recopilamos"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 692, 727, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "2. Cómo utilizamos su información"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 828, 862, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "3. Cómo compartimos su información"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 1341, 1372, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "4. Seguridad de su información"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 1624, 1640, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "5. Sus derechos"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 2010, 2038, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "6. Cambios en esta política"
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 2246, 2259, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // "7. Contacto"

// Asigna el texto al TextView
        textViewContent.setText(spannableString);

    }
}
