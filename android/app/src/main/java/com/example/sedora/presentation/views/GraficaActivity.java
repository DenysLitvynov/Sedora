package com.example.sedora.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sedora.presentation.fragments.Grafica;
import com.example.sedora.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GraficaActivity extends AppCompatActivity {

   private  TextView titulo_Grafica;

    public String getGrafica_elegida() {
        return grafica_elegida;
    }

    private String  grafica_elegida;
    private String nombres[]= new String[]{"Esta Semana","Este Mes"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_grafica);


        ImageView button=findViewById(R.id.botonRegreso);

        //Recuperamos el string establecido en ProgresoActividado al elegir una grafica.
        grafica_elegida= getIntent().getStringExtra("Titulo_de_Grafica");

        titulo_Grafica=findViewById(R.id.Titulo);

        titulo_Grafica.setText(grafica_elegida);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GraficaActivity.this, ProgresoActivity.class);
                startActivity(intent);

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Pestañas
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
        TabLayout tabs = findViewById(R.id.cambioGrafica);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setText(nombres[position]);
                    }
                }
        ).attach();
    }

    public class MiPagerAdapter extends FragmentStateAdapter {
        public MiPagerAdapter(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            //return nombres.length;
            return  2;
        }

        @Override
        @NonNull
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return  Grafica.newInstance("semanal");//Grafica para parametros semanales
                case  1:
                    return Grafica.newInstance("mensual");//Grafica para parametros mensuales
            }
            return null;
    }

    }

}
