package com.fmcuatro.agenda_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    EditText edtNombres, edtCorreo, edtContrasenia, edtConfirmarContrasenia;
    Button btnRegistrarUsuario;
    TextView tvTengoCuenta;

    // para todo esto necesitamos la librería de FirebaseAuthentication.
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    // declaramos 4 cadenas para capturar los datos ingresados en el EditText
    String nombre = "", correo = "", password = "",  confirmarPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // declaramos el actionbar pero que le pertenezca a androidx para poner titulo y la fecha para atrás
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // definimos los datos con las variables
        edtNombres = findViewById(R.id.edtNombres);
        edtCorreo = findViewById(R.id.edtCorreo);
        edtContrasenia = findViewById(R.id.edtContrasenia);
        edtConfirmarContrasenia = findViewById(R.id.edtConfirmarContrasenia);
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);
        tvTengoCuenta = findViewById(R.id.tvTengoCuenta);

        // inicializamos Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        // inicializamos el ProgressDialog
        progressDialog = new ProgressDialog(Registro.this);
        progressDialog.setTitle("Espere, por favor.");
        // se crea el progressDialog pero hasta que se cierre el
        // usuario puede volver a accionar en pantalla de lo contrario esta bloqueado hasta que finalice
        progressDialog.setCanceledOnTouchOutside(false);

        // asignamos los eventos, del botón y del TextView
        btnRegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // método del proceso para el registro
                try {
                    validarDatos();
                } catch (Exception e) {
                    Toast.makeText(Registro.this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvTengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // método para enviar al login si tiene una cuenta
                startActivity(new Intent(Registro.this, Login.class));
            }
        });
    }

    // creamos método para validar datos
    private void validarDatos () throws Exception {
        nombre = edtNombres.getText().toString();
        correo = edtCorreo.getText().toString();
        password = edtContrasenia.getText().toString();
        confirmarPassword = edtConfirmarContrasenia.getText().toString();

        // validaciones para los campos
        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese correo electrónico valido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmarPassword)) {
            Toast.makeText(this, "Confirme contraseña", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coiciden", Toast.LENGTH_SHORT).show();
        } else {
            crearCuenta();
        }
    }

    private void crearCuenta() throws Exception {
        progressDialog.setMessage("Creando su cuenta");
        progressDialog.show();

        // creando el usuario en Firebase
        firebaseAuth.createUserWithEmailAndPassword(correo,  Utility.encryptPassword(password))
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // metodo que nos permite realizar el registro
                        // y si entramos aqui es porque el registro es exitoso
                        try {
                            guardarInformacion();
                        } catch (Exception e) {
                            Toast.makeText(Registro.this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // el OnFailureListener es cuando el registro no fue exitoso
                        // que no hay conexion o que a habido una mala implementacion
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarInformacion() throws Exception {
        progressDialog.setMessage("Guardando su información");
        // progressDialog.dismiss();

        // obtener la identificacion de usuario actual
        String uid = firebaseAuth.getUid();

        // configurar datos para agrear en la base de datos
        HashMap<String, String> Datos = new HashMap<>();
        Datos.put("uid", uid); // el primer dato envuiado es el uid
        Datos.put("correo", correo);
        Datos.put("nombres", nombre);
        Datos.put("password", Utility.encryptPassword(password));
        Datos.put("fechaCreacion", getCurrentDateTime());
        Datos.put("fechaAuthentication", getCurrentDateTime());

        // registro en la base de datos
        // no se antes implementar la dependecia implementation 'com.google.firebase:firebase-database:19.6.0'
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid)
                .setValue(Datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        // si el registro fue exitoso, estamos por aca
                        Toast.makeText(Registro.this, "Cuenta creada con exito", Toast.LENGTH_SHORT).show();
                        // y que luego nos mande a la otra actividad
                        startActivity(new Intent(Registro.this, MenuPrincipal.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // nos servira cuando presionemos la flecha hacia atras
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}