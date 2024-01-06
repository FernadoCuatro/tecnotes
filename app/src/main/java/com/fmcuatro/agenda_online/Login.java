package com.fmcuatro.agenda_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Login extends AppCompatActivity {
    // inicializamos
    EditText edtCorreoLogin, edtPassLogin;
    Button btnIngresar;
    TextView tvUsuarioNuevo;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    // string para validar los datos
    String correo = "", password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // declaramos el actionbar pero que le pertenezca a androidx para poner titulo y la fecha para atrás
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        edtCorreoLogin = findViewById(R.id.edtCorreoLogin);
        edtPassLogin = findViewById(R.id.edtPassLogin);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvUsuarioNuevo = findViewById(R.id.tvUsuarioNuevo);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        // creamos los dos eventos
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // para iniciar sesión
                try {
                    validarDatos();
                } catch (Exception e) {
                    Toast.makeText(Login.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvUsuarioNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // para mandar a registro
                startActivity(new Intent(Login.this, Registro.class));
            }
        });
    }

    private void validarDatos() throws Exception {
        correo = edtCorreoLogin.getText().toString();
        password = edtPassLogin.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo no valido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();
        } else {
            loginUsuario();
        }
    }

    private void loginUsuario() throws Exception {
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();
        // utilizamos Firebase
        firebaseAuth.signInWithEmailAndPassword(correo, Utility.encryptPassword(password))
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // si se completa, es decir el login es valido, entramos aca
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser.getUid().toString().equals("")) {
                                Toast.makeText(Login.this, "El UID del usuario es nulo.", Toast.LENGTH_SHORT).show();
                            } else {
                                DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
                                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // if (firebaseUser.isEmailVerified()) {
                                        if (dataSnapshot.exists()) {
                                            try {
                                                String fechaRegistro = dataSnapshot.child("fechaAuthentication").getValue(String.class);

                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                Date fechaRegistroDate = dateFormat.parse(fechaRegistro);
                                                Date fechaActual = Calendar.getInstance().getTime();

                                                long diferenciaTiempo = fechaActual.getTime() - fechaRegistroDate.getTime();
                                                long tiempoMilisegundos = 6L * 30L * 24L * 60L * 60L * 1000L;
                                                //long tiempoMilisegundos = 1 * 60 * 1000;

                                                if (diferenciaTiempo > tiempoMilisegundos) {
                                                    Toast.makeText(Login.this, "Debes verificar tu correo nuevamente.", Toast.LENGTH_SHORT).show();
                                                    enviarEmailAuthentiacion(firebaseUser);
                                                } else {
                                                    progressDialog.dismiss();
                                                    // obtenemos el correo electronico del usuario actual
                                                    startActivity(new Intent(Login.this, MenuPrincipal.class));
                                                    // un mesajito en HD
                                                    Toast.makeText(Login.this, "Hola " + firebaseUser.getEmail() + ", gracias por estar aquí.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        // }
                                        //    else {
                                        //enviar correo de authenticacion
                                        //    Toast.makeText(Login.this, "Verifica tu usuario", Toast.LENGTH_SHORT).show();
                                        //    try {
                                        //       enviarEmailAuthentiacion(firebaseUser);
                                        //    } catch (Exception e) {
                                        //     throw new RuntimeException(e);
                                        // }
                                        //}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle potential errors here
                                        progressDialog.dismiss();
                                        Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Verifique si Correo y Contraseña son correctos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // si ocurre un error, venimos para aca
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void enviarEmailAuthentiacion(FirebaseUser CurrentUser) throws Exception {
        try {
            CurrentUser.sendEmailVerification().addOnCompleteListener(task -> {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                if (task.isSuccessful()) {
                    Log.d("LogRoger", "se envio el correo");
                    Toast.makeText(Login.this, "Correo de verificación enviado a " + CurrentUser.getEmail(), Toast.LENGTH_SHORT).show();
                    actualizarFechaAutenticacion(databaseReference, CurrentUser.getUid());
                } else {
                    Log.d("LogRoger", "No se envio el correo");
                    Toast.makeText(Login.this, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private void actualizarFechaAutenticacion(DatabaseReference databaseReference, String uid) {
        DatabaseReference usuarioRef = databaseReference.child("Usuarios").child(uid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaActual = sdf.format(new Date());
        usuarioRef.child("fechaAuthentication").setValue(fechaActual);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}