package com.example.sedora;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

public class HeaderManager {

    private ImageView buttonIcon;

    public void setupHeader(final Context context, View rootView) {

        buttonIcon = rootView.findViewById(R.id.campananoti);

        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RecyclerActivity.class);
                context.startActivity(intent);
            }
        });
    }
}