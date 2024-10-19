package com.example.sedora;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space; // Espacio en píxeles

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = space; // Espacio superior
        outRect.bottom = space; // Espacio inferior

        // Para evitar el espacio superior en el primer elemento
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.top = 0; // O ajusta a un valor que desees
        }
    }
}
