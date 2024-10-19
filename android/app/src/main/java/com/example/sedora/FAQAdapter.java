package com.example.sedora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private final List<FAQItem> faqItems;

    public FAQAdapter(List<FAQItem> faqItems) {
        this.faqItems = faqItems;
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
        FAQItem faqItem = faqItems.get(position);
        holder.bind(faqItem);
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
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

        public void bind(FAQItem faqItem) {
            textViewHeader.setText(faqItem.getTitle());
            textViewContent.setText(faqItem.getContent());

            boolean isExpanded = faqItem.isExpanded();
            textViewContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            buttonToggle.setImageResource(isExpanded ? R.drawable.flecha_arriba : R.drawable.flecha_abajo);

            buttonToggle.setOnClickListener(v -> {
                faqItem.setExpanded(!faqItem.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
