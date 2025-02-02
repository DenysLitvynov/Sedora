package com.example.sedora.presentation.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sedora.R;
import com.example.sedora.data.SensorDataList;
import com.example.sedora.model.SensorData;
import com.example.sedora.presentation.managers.FirebaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 9001; // Puedes usar cualquier número
    private String correo = "";
    private String contraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextView tvCorreo, tvContraseña, tvRegistrate;
    private ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_de_sesion);

        etCorreo = findViewById(R.id.edtEmail);
        etContraseña = findViewById(R.id.edtContraseña);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvContraseña = findViewById(R.id.tvContraseña);
        contenedor = findViewById(R.id.inicioLayout);
        tvRegistrate = findViewById(R.id.tvRegistrate);

        // Inicializar FirebaseAuth y GoogleSignInClient
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");

        verificaSiUsuarioValidado();

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verificaSiUsuarioValidado() {
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this, PantallaInicioActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(i);
            finish();
        }
    }

    //---------------------------------------------------------------
    // Metodo para iniciar sesión con correo
    //---------------------------------------------------------------

    public void inicioSesionCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, task -> {
                        dialogo.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser usuario = auth.getCurrentUser();
                            if (usuario != null && usuario.isEmailVerified()) {
                                // Guardar la información del usuario en Firebase Firestore
                                FirebaseHelper.guardarUsuario(usuario);
                                FirebaseHelper.crearCarpetaUsuario(usuario);

                                //---------------------------------------------------------------
                                FirebaseHelper firebaseHelper = new FirebaseHelper();

                                // Crear una instancia de SensorDataList para obtener los datos simulados
                                SensorDataList sensorDataList = new SensorDataList();

                                // Iterar sobre los datos y guardarlos en Firestore
                                for (SensorData data : sensorDataList.getListaDatos()) {
                                    // Guardar cada toma de datos de sensores para el usuario
                                    firebaseHelper.guardarToma(usuario, data);
                                }

//                                // Subir notificaciones a Firestore
//                                NotificacionManager notificacionManager = new NotificacionManager();
//                                notificacionManager.subirNotificacionesAFirestore(usuario);

                                //---------------------------------------------------------------

                                Intent i = new Intent(LoginActivity.this, PantallaInicioActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                mensaje("Debes verificar tu correo antes de iniciar sesión.");
                                auth.signOut();
                            }
                        } else {
                            mensaje("Error al iniciar sesión: " + task.getException().getLocalizedMessage());
                        }
                    });
        }
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    public void autentificarGoogle(View v) {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleAuth(account.getIdToken());
            } catch (ApiException e) {
                mensaje("Error de autentificación con Google");
            }
        }
    }

    //---------------------------------------------------------------
    // Método para iniciar sesión con Google
    //---------------------------------------------------------------

    private void googleAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser usuario = auth.getCurrentUser();
                            if (usuario != null) {
                                // Guardar la información del usuario en Firebase Firestore

                                System.out.println("Guarda Información del usuario");
                                FirebaseHelper.guardarUsuario(usuario);
                                FirebaseHelper.crearCarpetaUsuario(usuario);


                                //---------------------------------------------------------------
                                FirebaseHelper firebaseHelper = new FirebaseHelper();

                                // Crear una instancia de SensorDataList para obtener los datos simulados
                                SensorDataList sensorDataList = new SensorDataList();

                                // Iterar sobre los datos y guardarlos en Firestore
                                for (SensorData data : sensorDataList.getListaDatos()) {
                                    // Guardar cada toma de datos de sensores para el usuario
                                    firebaseHelper.guardarToma(usuario, data);
                                }
                                //---------------------------------------------------------------

//                                // Subir notificaciones a Firestore
//                                NotificacionManager notificacionManager = new NotificacionManager();
//                                notificacionManager.subirNotificacionesAFirestore(usuario);


                                //CollectionReference metasReference = db.collection("metas");
                            }
                            verificaSiUsuarioValidado();
                        } else {
                            mensaje(task.getException().getLocalizedMessage());


                        }
                    }
                });
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();

        // Limpiar los errores antes de comenzar
        tvCorreo.setText("");
        tvContraseña.setText("");

        // Verificar los campos y mostrar errores en los TextViews correctos
        if (correo.isEmpty()) {
            tvCorreo.setText("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tvCorreo.setText("Correo no válido");
        } else if (contraseña.isEmpty()) {
            tvContraseña.setText("Introduce una contraseña");
        } else if (contraseña.length() < 6) {
            tvContraseña.setText("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            tvContraseña.setText("Ha de contener un número");
        } else if (!contraseña.matches(".*[A-Z].*")) {
            tvContraseña.setText("Ha de contener una letra mayúscula");
        } else {
            // Si no hay errores, devuelve true
            return true;
        }
        // Si hay errores, devuelve false
        return false;
    }

    //---------------------------------------------------------------
    // Autenticación Mediante Twitter
    //---------------------------------------------------------------

    public void autentificarTwitter(View v) {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        provider.addCustomParameter("lang", "es"); // Localizar a español

        auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser usuario = auth.getCurrentUser();
                    if (usuario != null) {
                        // Guardar la información del usuario en Firebase Firestore
                        FirebaseHelper.guardarUsuario(usuario);
                        FirebaseHelper.crearCarpetaUsuario(usuario);



                        //---------------------------------------------------------------
                        FirebaseHelper firebaseHelper = new FirebaseHelper();

                        // Crear una instancia de SensorDataList para obtener los datos simulados
                        SensorDataList sensorDataList = new SensorDataList();

                        // Iterar sobre los datos y guardarlos en Firestore
                        for (SensorData data : sensorDataList.getListaDatos()) {
                            // Guardar cada toma de datos de sensores para el usuario
                            firebaseHelper.guardarToma(usuario, data);
                        }
                        //---------------------------------------------------------------

//                        // Subir notificaciones a Firestore
//                        NotificacionManager notificacionManager = new NotificacionManager();
//                        notificacionManager.subirNotificacionesAFirestore(usuario);
                    }
                    verificaSiUsuarioValidado();
                })
                .addOnFailureListener(e -> mensaje("Error de autenticación con Twitter: " + e.getLocalizedMessage()));
    }

    //---------------------------------------------------------------
    // Autenticación Mediante Github
    //---------------------------------------------------------------

    public void autentificarGitHub(View v) {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        provider.addCustomParameter("allow_signup", "false");

        auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser usuario = auth.getCurrentUser();
                    if (usuario != null) {
                        // Guardar la información del usuario en Firebase Firestore
                        FirebaseHelper.guardarUsuario(usuario);
                        FirebaseHelper.crearCarpetaUsuario(usuario);


                        //---------------------------------------------------------------
                        FirebaseHelper firebaseHelper = new FirebaseHelper();

                        // Crear una instancia de SensorDataList para obtener los datos simulados
                        SensorDataList sensorDataList = new SensorDataList();

                        // Iterar sobre los datos y guardarlos en Firestore
                        for (SensorData data : sensorDataList.getListaDatos()) {
                            // Guardar cada toma de datos de sensores para el usuario
                            firebaseHelper.guardarToma(usuario, data);
                        }
                        //---------------------------------------------------------------

//                        // Subir notificaciones a Firestore
//                        NotificacionManager notificacionManager = new NotificacionManager();
//                        notificacionManager.subirNotificacionesAFirestore(usuario);

                    }
                    verificaSiUsuarioValidado();
                })
                .addOnFailureListener(e -> mensaje2("Error de autenticación con GitHub: " + e.getLocalizedMessage()));
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    private void mensaje2(String mensaje) {
        Snackbar.make(findViewById(R.id.inicioLayout), mensaje, Snackbar.LENGTH_LONG).show();
    }
}

