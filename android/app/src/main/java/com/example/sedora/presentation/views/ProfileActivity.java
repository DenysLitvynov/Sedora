package com.example.sedora.presentation.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.sedora.Header;
import com.example.sedora.R;
import com.example.sedora.presentation.managers.NotificacionManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.example.sedora.presentation.managers.MenuManager;


public class ProfileActivity extends AppCompatActivity {

    private EditText nuevoNombre;
    private EditText nuevoCorreo;
    private EditText nuevaContraseña;
    private Button botonCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        // Obtén el Header
        Header header = findViewById(R.id.header);

        // Configura el título del Header
        TextView headerTitle = findViewById(R.id.headerTitleTextView);
        headerTitle.setText("Perfil");

        // Comprueba las notificaciones
        NotificacionManager notificacionManager = new NotificacionManager();
        boolean hasNotifications = !notificacionManager.getNotificaciones().isEmpty();
        header.updateNotificationIcon(hasNotifications);


        // para entrar en la página de notificaciones
        //ImageButton buttonIcon = findViewById(R.id.button_icon);


        nuevoNombre = findViewById(R.id.nuevoNombre2);
        nuevoCorreo = findViewById(R.id.nuevoCorreo);
        nuevaContraseña = findViewById(R.id.nuevaContraseña);
        Button botonActualizar = findViewById(R.id.botonActualizar);
        Button botonMostrarCampos = findViewById(R.id.botonEditar);
        botonCancelar = findViewById(R.id.botonCancelar);


        nuevoNombre.setEnabled(false);
        nuevoCorreo.setEnabled(false);
        nuevaContraseña.setEnabled(false);
        botonActualizar.setVisibility(View.GONE);
        botonCancelar.setVisibility(View.GONE);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario != null) {
            nuevoNombre.setText(usuario.getDisplayName());
            nuevoCorreo.setText(usuario.getEmail());

            cargarImagenPerfil(usuario.getPhotoUrl());

            botonMostrarCampos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Habilitar los EditText y mostrar botones de actualizar y cancelar
                    nuevoNombre.setEnabled(true);
                    nuevoCorreo.setEnabled(true);
                    nuevaContraseña.setEnabled(true);
                    botonActualizar.setVisibility(View.VISIBLE);
                    botonCancelar.setVisibility(View.VISIBLE);
                    botonMostrarCampos.setVisibility(View.GONE);
                }
            });

            botonCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restaurar valores originales y deshabilitar los EditText
                    nuevoNombre.setText(usuario.getDisplayName());
                    nuevoCorreo.setText(usuario.getEmail());
                    nuevaContraseña.setText("");
                    nuevoNombre.setEnabled(false);
                    nuevoCorreo.setEnabled(false);
                    nuevaContraseña.setEnabled(false);
                    botonActualizar.setVisibility(View.GONE);
                    botonCancelar.setVisibility(View.GONE);
                    botonMostrarCampos.setVisibility(View.VISIBLE);
                }
            });

            botonActualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actualizarPerfil(usuario);
                }
            });
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //FUNCIONALIDAD BOTONES MENUS

        MenuManager funcionMenu = new MenuManager();


        ImageView btnPantallaPrincipal = findViewById(R.id.btnHome);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaInicio(ProfileActivity.this);
            }
        });

        ImageView btnPantallaProgreso = findViewById(R.id.btnProgreso);
        btnPantallaProgreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaProgreso(ProfileActivity.this);
            }
        });

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaAjustes(ProfileActivity.this);
            }
        });

        ImageView btnPantallaPerfil = findViewById(R.id.Perfil);
        btnPantallaPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPerfil(ProfileActivity.this);
            }
        });

        /*FIN FUNCIONALIDAD BOTONES*/

        /*
        // Configura el clic en el button_icon para entrar en Notificaciones
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, RecyclerActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar la actividad actual
            }
        });*/

    }

    private void cargarImagenPerfil(Uri urlImagen) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(10);

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });

        if (urlImagen != null) {
            NetworkImageView foto = findViewById(R.id.imagen);
            foto.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
    }

    private void actualizarPerfil(FirebaseUser usuario) {
        String nuevoNombre = this.nuevoNombre.getText().toString();
        String nuevoEmail = nuevoCorreo.getText().toString();
        String nuevaPass = nuevaContraseña.getText().toString();

        UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombre)
                .build();

        usuario.updateProfile(perfil).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Perfil actualizado exitosamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al actualizar el perfil.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Actualizar el correo
        if (!nuevoEmail.isEmpty()) {
            usuario.updateEmail(nuevoEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Correo actualizado exitosamente.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error al actualizar el correo. Puede necesitar reautenticarse.", Toast.LENGTH_SHORT).show();
                        manejarReautenticacion(usuario);
                    }
                }
            });
        }

        // Actualizar la contraseña
        if (!nuevaPass.isEmpty()) {
            usuario.updatePassword(nuevaPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error al actualizar la contraseña. Puede necesitar reautenticarse.", Toast.LENGTH_SHORT).show();
                        manejarReautenticacion(usuario);
                    }
                }
            });
        }
    }

    private void manejarReautenticacion(FirebaseUser usuario) {
        String email = "viejo.correo@ejemplo.com";
        String password = "viejaContraseña";

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        usuario.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Reautenticación exitosa. Ahora puedes intentar actualizar el perfil nuevamente.", Toast.LENGTH_SHORT).show();
                    actualizarPerfil(usuario);
                } else {
                    Toast.makeText(ProfileActivity.this, "Reautenticación fallida.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cerrarSesion(View view) {
        AuthUI.getInstance().signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });
    }
}
