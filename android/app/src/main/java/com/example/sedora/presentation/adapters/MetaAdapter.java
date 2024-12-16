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
import com.example.sedora.model.Meta;

import java.util.List;

public class MetaAdapter extends RecyclerView.Adapter<MetaAdapter.MetaViewHolder> {

    private final List<Meta> metas;
    private final Context context;
    private final int tipoVista; // 0: Meta Actual, 1: Próximas Metas

    public MetaAdapter(Context context, List<Meta> metas, int tipoVista) {
        this.context = context;
        this.metas = metas;
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
        Meta meta = metas.get(position);

        // Configurar datos
        holder.metaTitulo.setText(meta.getNombre());
        holder.metaDescripcion.setText(meta.getDescripcion());
        holder.metaNumero.setText(String.valueOf(meta.getNumeroMeta()));

        holder.metaProgresoBar.setMax(meta.getProgresoTotal());
        holder.metaProgresoBar.setProgress(meta.getProgresoActual());

        String progresoTexto = meta.getProgresoActual() + "/" + meta.getProgresoTotal();
        holder.metaProgresoTexto.setText(progresoTexto);

        int imageResId = context.getResources().getIdentifier(meta.getImagenDrawableName(), "drawable", context.getPackageName());
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
        return metas.size();
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