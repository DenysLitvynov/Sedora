package com.example.sedora.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sedora.R;
import com.example.sedora.model.RecyclerViewItem;

import java.util.List;

public class PoliticaDePrivacidadAdapter extends RecyclerView.Adapter<PoliticaDePrivacidadAdapter.PoliticaDePrivacidadViewHolder> {

    private final List<RecyclerViewItem> PoliticaDePrivacidadItems;

    public PoliticaDePrivacidadAdapter(List<RecyclerViewItem> PoliticaDePrivacidadItems) {
        this.PoliticaDePrivacidadItems = PoliticaDePrivacidadItems;
    }

    @NonNull
    @Override
    public PoliticaDePrivacidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        return new PoliticaDePrivacidadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoliticaDePrivacidadViewHolder holder, int position) {
        RecyclerViewItem faqItem = PoliticaDePrivacidadItems.get(position);
        holder.bind(faqItem);
    }

    @Override
    public int getItemCount() {
        return PoliticaDePrivacidadItems.size();
    }

    public class PoliticaDePrivacidadViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewHeader;
        private final TextView textViewContent;
        private final ImageButton buttonToggle;

        public PoliticaDePrivacidadViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewFAQHeader);
            textViewContent = itemView.findViewById(R.id.textViewFAQContent);
            buttonToggle = itemView.findViewById(R.id.buttonToggle);
        }

        public void bind(RecyclerViewItem PoliticaDePrivacidadItems) {
            textViewHeader.setText(PoliticaDePrivacidadItems.getTitle());
            textViewContent.setText(PoliticaDePrivacidadItems.getContent());

            boolean isExpanded = PoliticaDePrivacidadItems.isExpanded();
            textViewContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            buttonToggle.setImageResource(isExpanded ? R.drawable.flecha_arriba : R.drawable.flecha_abajo);

            buttonToggle.setOnClickListener(v -> {
                PoliticaDePrivacidadItems.setExpanded(!PoliticaDePrivacidadItems.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
