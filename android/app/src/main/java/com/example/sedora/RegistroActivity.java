package com.example.sedora;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.GoogleAuthProvider;

public class RegistroActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 9001; // Usa el mismo número que en LoginActivity
    private String correo = "";
    private String contraseña = "";
    private String confirmContraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña, etConfirmContraseña;
    private TextView tvCorreo, tvContraseña, tvConfirmContraseña;
    private CheckBox checkboxPrivacyPolicy;
    private ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        etCorreo = findViewById(R.id.edtEmail);
        etContraseña = findViewById(R.id.edtContraseña);
        etConfirmContraseña = findViewById(R.id.edtConfirmContraseña);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvContraseña = findViewById(R.id.tvContraseña);
        tvConfirmContraseña = findViewById(R.id.tvConfirmContraseña);
        checkboxPrivacyPolicy = findViewById(R.id.checkbox_privacy_policy);
        contenedor = findViewById(R.id.registroLayout);
        ImageButton buttonIcon = findViewById(R.id.button_icon);

        // Inicializar FirebaseAuth y GoogleSignInClient
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de que este ID esté configurado en tu proyecto de Firebase
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");

        // Configura el clic en el button_icon para regresar a la LoginActivity
        buttonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar la actividad actual
            }
        });
    }

    public void registroCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialogo.dismiss();
                            if (task.isSuccessful()) {
                                if (auth.getCurrentUser() != null) {
                                    auth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> emailTask) {
                                                    if (emailTask.isSuccessful()) {
                                                        Toast.makeText(RegistroActivity.this,
                                                                "Se ha enviado un correo de verificación. Por favor, verifica tu correo antes de iniciar sesión.",
                                                                Toast.LENGTH_LONG).show();
                                                    } else {
                                                        mensaje("Error al enviar el correo de verificación.");
                                                    }
                                                }
                                            });
                                }
                                auth.signOut();
                                new Handler().postDelayed(() -> {
                                    Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }, 2000);
                            } else {
                                mensaje(task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }
    }


    public void autentificarGoogle(View v) {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

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

    private void googleAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(RegistroActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            mensaje(task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();
        confirmContraseña = etConfirmContraseña.getText().toString(); // Confirmación de contraseña

        // Limpiar mensajes de error
        tvCorreo.setText("");  // Limpiamos el TextView en vez de setError
        tvContraseña.setText("");
        tvConfirmContraseña.setText("");

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
        } else if (!contraseña.equals(confirmContraseña)) { // Validar confirmación
            tvConfirmContraseña.setText("Las contraseñas no coinciden");
        } else if (!checkboxPrivacyPolicy.isChecked()) { // Verificar si se acepta la política de privacidad
            Snackbar.make(contenedor, "Debes aceptar la política de privacidad", Snackbar.LENGTH_LONG).show();
        } else {
            return true;
        }
        return false;
    }

}
