package com.example.sedora.presentation.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.model.Notificacion;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.example.sedora.R;

import java.util.List;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder> {
    private NotificacionManager notificacionManager; // Cambiamos a NotificacionManagerSedora

    public Adaptador(NotificacionManager notificacionManager) {
        this.notificacionManager = notificacionManager; // Guardamos la referencia
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<List<Notificacion>, List<Notificacion>> result = notificacionManager.getNotificaciones();
        Notificacion notificacion = result.first.get(position);  // Usamos la lista de habilitadas (first)

        holder.icono.setImageResource(notificacion.getIcono());
        holder.titulo.setText(notificacion.getTitulo());
        holder.mensaje.setText(notificacion.getMensaje());
        holder.hora.setText(notificacion.getHora());
        holder.tipo.setText(notificacion.getTipo());
        holder.numeroAvisos.setText(String.valueOf(notificacion.getNumeroAvisos()));

        if (notificacion.getTipo().equals("Aviso")) {
            holder.tipo.setText("Aviso");
            holder.tipo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.rojo));
        } else if (notificacion.getTipo().equals("Recordatorio")) {
            holder.tipo.setText("Recordatorio");
            holder.tipo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.amarillo));
        } else {
            holder.tipo.setText("Recomendación");
            holder.tipo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.verde_primario));
        }
    }

    @Override
    public int getItemCount() {
        // Obtener las notificaciones habilitadas a través del Pair
        Pair<List<Notificacion>, List<Notificacion>> result = notificacionManager.getNotificaciones();
        return result.first.size();  // Usamos la lista de habilitadas (first)
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icono;
        TextView titulo, mensaje, hora, tipo, numeroAvisos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icono = itemView.findViewById(R.id.icono);
            titulo = itemView.findViewById(R.id.titulo);
            mensaje = itemView.findViewById(R.id.mensaje);
            hora = itemView.findViewById(R.id.hora);
            tipo = itemView.findViewById(R.id.tipo_notificacion);
            numeroAvisos = itemView.findViewById(R.id.numero_avisos);
        }
    }
}
