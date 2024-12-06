package com.example.sedora.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.Notificacion;
import com.example.sedora.presentation.adapters.Adaptador.ViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorNotificacionesFirestoreUI extends FirestoreRecyclerAdapter<Notificacion, ViewHolder> {
    private View.OnClickListener onClickListener;
    private Context context;

    public AdaptadorNotificacionesFirestoreUI(@NonNull FirestoreRecyclerOptions<Notificacion> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Notificacion notificacion) {
        holder.icono.setImageResource(notificacion.getIcono());
        holder.titulo.setText(notificacion.getTitulo());
        holder.mensaje.setText(notificacion.getMensaje());
        holder.hora.setText(notificacion.getHora());
        holder.tipo.setText(notificacion.getTipo());
        holder.numeroAvisos.setText(String.valueOf(notificacion.getNumeroAvisos()));

        if (notificacion.getTipo().equals("Aviso")) {
            holder.tipo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.rojo));
        } else if (notificacion.getTipo().equals("Recordatorio")) {
            holder.tipo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.amarillo));
        } else {
            holder.tipo.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.verde_primario));
        }
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }

    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()) {
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
    }
}
