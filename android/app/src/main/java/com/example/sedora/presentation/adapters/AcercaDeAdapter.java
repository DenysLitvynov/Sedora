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

public class AcercaDeAdapter extends RecyclerView.Adapter<AcercaDeAdapter.FAQViewHolder> {

    private final List<RecyclerViewItem> AcercaDeItems;

    public AcercaDeAdapter(List<RecyclerViewItem> AcercaDeItems) {
        this.AcercaDeItems = AcercaDeItems;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        RecyclerViewItem faqItem = AcercaDeItems.get(position);
        holder.bind(faqItem);
    }

    @Override
    public int getItemCount() {
        return AcercaDeItems.size();
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewHeader;
        private final TextView textViewContent;
        private final ImageButton buttonToggle;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewFAQHeader);
            textViewContent = itemView.findViewById(R.id.textViewFAQContent);
            buttonToggle = itemView.findViewById(R.id.buttonToggle);
        }

        public void bind(RecyclerViewItem AcercaDeItems) {
            textViewHeader.setText(AcercaDeItems.getTitle());
            textViewContent.setText(AcercaDeItems.getContent());

            boolean isExpanded = AcercaDeItems.isExpanded();
            textViewContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            buttonToggle.setImageResource(isExpanded ? R.drawable.flecha_arriba : R.drawable.flecha_abajo);

            buttonToggle.setOnClickListener(v -> {
                AcercaDeItems.setExpanded(!AcercaDeItems.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
