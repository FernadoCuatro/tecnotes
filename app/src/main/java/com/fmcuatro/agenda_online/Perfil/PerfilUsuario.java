package com.fmcuatro.agenda_online.Perfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fmcuatro.agenda_online.MenuPrincipal;
import com.fmcuatro.agenda_online.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilUsuario extends AppCompatActivity {
    // declaramos datos
    TextView tvCorreo, tvUid, tvFechaCreacion, tvFechaAuth;
    EditText edtNombres, edtEdad, edtTelefono, edtDomicilio, edtUniversidad, edtProfesion;
    Button btn_guardar_perfil;

    // declaramos fb db
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inicializamos y cambio de tema
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        cambiarTema(firebaseUser.getEmail());

        setContentView(R.layout.activity_perfil_usuario);

        // metodo para inicializar
        inicializarVariables();

        // declaramos el actionbar pero que le pertenezca a androidx para poner titulo y la fecha para atrás
        // ActionBar actionBar = getSupportActionBar();
        // actionBar.setTitle("Perfil");
        getSupportActionBar().hide(); // para ocultar la barra

        // evento para botones
        btn_guardar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // metodo para inicializar
    private void inicializarVariables() {
        tvCorreo = findViewById(R.id.tvCorreo);
        tvUid = findViewById(R.id.tvUid);
        edtNombres = findViewById(R.id.edtNombres);
        edtEdad = findViewById(R.id.edtEdad);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtDomicilio = findViewById(R.id.edtDomicilio);
        edtUniversidad = findViewById(R.id.edtUniversidad);
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
        tvFechaAuth = findViewById(R.id.tvFechaAuth);
        btn_guardar_perfil = findViewById(R.id.btn_guardar_perfil);

        // instancia a base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    // Metodo para cambiar el tema dependiendo del email
    private void cambiarTema(String email) {
        if (email.endsWith(".utec.edu.sv")) {
            setTheme(R.style.Theme_Agenda_Online_Edu);
        } else {
            setTheme(R.style.Theme_Agenda_Online);
        }
    }

    // metodo para lectura de datos
    private void lecturaDatos() {
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // validamos que el usuario exista
                if (snapshot.exists()) {
                    // obtenemos sus datos
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String fechaCreacion = ""+snapshot.child("fechaCreacion").getValue();
                    String fechaAuthentication = ""+snapshot.child("fechaAuthentication").getValue();

                    // seteo de los datos
                    tvUid.setText(uid);
                    tvCorreo.setText(correo);
                    edtNombres.setText(correo);
                     tvFechaCreacion.setText("Fecha de creación del usuario: " + fechaCreacion);
                     tvFechaAuth.setText("Fecha de autenticación del usuario: " + fechaAuthentication);
                } else {
                    Toast.makeText(PerfilUsuario.this, "Esperando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // este metodo es por si cualquier se cancela
                Toast.makeText(PerfilUsuario.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // verificamos el inicio de sesion
    private void comprobarInicioSesion() {
        if (firebaseUser != null) {
            lecturaDatos();
        } else {
            startActivity(new Intent(PerfilUsuario.this, MenuPrincipal.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        comprobarInicioSesion();
    }
}