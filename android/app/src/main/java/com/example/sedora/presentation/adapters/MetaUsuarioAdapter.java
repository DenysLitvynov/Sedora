package com.example.sedora.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.MetaUsuario;

import java.util.List;

public class MetaUsuarioAdapter extends RecyclerView.Adapter<MetaUsuarioAdapter.MetaViewHolder> {

    private final List<MetaUsuario> metasUsuario;
    private final Context context;
    private final int tipoVista; // 0: Meta Actual, 1: Próximas Metas

    public MetaUsuarioAdapter(Context context, List<MetaUsuario> metasUsuario, int tipoVista) {
        this.context = context;
        this.metasUsuario = metasUsuario;
        this.tipoVista = tipoVista; // Aquí establecemos el tipo de vista
    }

    @NonNull
    @Override
    public MetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.modelo_meta; // Asume que el diseño base es el mismo
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MetaViewHolder holder, int position) {
        MetaUsuario metaUsuario = metasUsuario.get(position);

        // Configurar datos
        holder.metaTitulo.setText(metaUsuario.getMeta().getNombre());
        holder.metaDescripcion.setText(metaUsuario.getMeta().getDescripcion());
        holder.metaNumero.setText(String.valueOf(metaUsuario.getMeta().getNumeroMeta()));


        holder.metaProgresoBar.setMax(metaUsuario.getMeta().getPorcentaje());
        holder.metaProgresoBar.setProgress(metaUsuario.getPorcentajeActual());

        String progresoTexto = metaUsuario.getPorcentajeActual() + "/" + metaUsuario.getMeta().getPorcentaje();
        holder.metaProgresoTexto.setText(progresoTexto);

        int imageResId = context.getResources().getIdentifier(metaUsuario.getMeta().getImagenDrawableName(), "drawable", context.getPackageName());
        if (imageResId != 0) {
            holder.metaIcono.setImageResource(imageResId);
        } else {
            holder.metaIcono.setImageResource(R.drawable.sedora_logo); // Imagen por defecto
        }

        // Fondo dinámico
        if (tipoVista == 1) { // Próximas Metas
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.gris_claro));
        } else { // Meta Actual (si se usa en otra pantalla)
            holder.itemView.setBackgroundResource(R.drawable.box_background_blanco);
        }
    }

    @Override
    public int getItemCount() {
        return metasUsuario.size();
    }

    public static class MetaViewHolder extends RecyclerView.ViewHolder {
        TextView metaTitulo, metaDescripcion, metaNumero, metaProgresoTexto;
        ProgressBar metaProgresoBar;
        ImageView metaIcono;

        public MetaViewHolder(@NonNull View itemView) {
            super(itemView);
            metaTitulo = itemView.findViewById(R.id.metaTitulo);
            metaDescripcion = itemView.findViewById(R.id.metaDescripcion);
            metaNumero = itemView.findViewById(R.id.metaNumero);
            metaProgresoBar = itemView.findViewById(R.id.metaProgresoBar);
            metaProgresoTexto = itemView.findViewById(R.id.metaProgresoTexto);
            metaIcono = itemView.findViewById(R.id.metaIcono);
        }
    }
}