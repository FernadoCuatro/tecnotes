package com.fmcuatro.agenda_online.Detalle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fmcuatro.agenda_online.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DetalleNota extends AppCompatActivity {
    // iniciamos los tv desde la vista del detalle de la nota
    TextView tvIdNotaDn, tvIdUsuarioDn, tvCorreoDn, tvTituloDn, tvDescripcionDn, tvFechaRegistroDn, tvFechaNotaDn, tvEstadoDn;
    Button btnImportante;

    // declaramos los string necesarios para almacenar los datos recuperados desde la actividad anterior
    String idNotaR, uidUsuarioR, correoUsuarioR, fechaHoraRegistroR, tituloR, descripcionR, fechaNotaR, estadoR;

    // autenticacion con firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    // para comprobar la nota importante
    Boolean comprobarNotaImportante = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cambiarTema(firebaseUser.getEmail());

        setContentView(R.layout.activity_detalle_nota);

        // el actionBar de toda la vida para volver atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detalle nota");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // inicializamos las vistas
        inicializarVistas();

        // recuperamos los datos
        recuperarDatos();

        // seteamos los datos
        setearDatosRecuperados();

        // verificamos que la nota sea importante o no
        verificarNotaImportante();

        // evento para botones
        btnImportante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // evento para el boton
                if (comprobarNotaImportante) {
                    eliminarNotasImportantes();
                } else {
                    agregarNotasImportantes();
                }
            }
        });
    }

    // Metodo para cambiar el tema dependiendo del email
    private void cambiarTema(String email) {

        if (email.endsWith(".utec.edu.sv")) {
            setTheme(R.style.Theme_Agenda_Online_Edu);
        } else {
            setTheme(R.style.Theme_Agenda_Online);
        }
    }

    // inicializamos las vistas
    private void inicializarVistas() {
        tvIdNotaDn = findViewById(R.id.tvIdNotaDn);
        tvIdUsuarioDn = findViewById(R.id.tvIdUsuarioDn);
        tvCorreoDn = findViewById(R.id.tvCorreoDn);
        tvTituloDn = findViewById(R.id.tvTituloDn);
        tvDescripcionDn = findViewById(R.id.tvDescripcionDn);
        tvFechaRegistroDn = findViewById(R.id.tvFechaRegistroDn);
        tvFechaNotaDn = findViewById(R.id.tvFechaNotaDn);
        tvEstadoDn = findViewById(R.id.tvEstadoDn);
        btnImportante = findViewById(R.id.btnImportante);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); // obtenemos al usuario actual
    }

    // metodo para recuperar los datos desde la actividad anterior
    private void recuperarDatos() {
        Bundle bundle = getIntent().getExtras();

        idNotaR = bundle.getString("idNota");
        uidUsuarioR = bundle.getString("uidUsuario");
        correoUsuarioR = bundle.getString("correoUsuario");
        fechaHoraRegistroR = bundle.getString("fechaHoraRegistro");
        tituloR = bundle.getString("titulo");
        descripcionR = bundle.getString("descripcion");
        fechaNotaR = bundle.getString("fechaNota");
        estadoR = bundle.getString("estado");
    }

    // establecemos los datos en los tv
    private void setearDatosRecuperados() {
        tvIdNotaDn.setText(idNotaR);
        tvIdUsuarioDn.setText(uidUsuarioR);
        tvCorreoDn.setText(correoUsuarioR);
        tvTituloDn.setText(tituloR);
        tvDescripcionDn.setText(descripcionR);
        tvFechaRegistroDn.setText(fechaHoraRegistroR);
        tvFechaNotaDn.setText(fechaNotaR);
        tvEstadoDn.setText(estadoR);
    }

    // metodo para agregar notas importantes
    private void agregarNotasImportantes() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ocurrio un error, no es tu culpa", Toast.LENGTH_SHORT).show();
        } else {
            // aqui vamos a ingresar la nota importante
            Bundle bundle = getIntent().getExtras();

            // obtenemos la nota desde la actividad anterior
            idNotaR = bundle.getString("idNota");
            uidUsuarioR = bundle.getString("uidUsuario");
            correoUsuarioR = bundle.getString("correoUsuario");
            fechaHoraRegistroR = bundle.getString("fechaHoraRegistro");
            tituloR = bundle.getString("titulo");
            descripcionR = bundle.getString("descripcion");
            fechaNotaR = bundle.getString("fechaNota");
            estadoR = bundle.getString("estado");

            // creamos un indentificador para la nota importante
            // creamos un shash para enviar los datos recuperados desde la actividad anterior
            HashMap<String, String> notaImportante = new HashMap<>();

            // mandamos los datos a la base de datos de firebase
            notaImportante.put("idNota", idNotaR);
            notaImportante.put("uidUsuario", uidUsuarioR);
            notaImportante.put("correoUsuario", correoUsuarioR);
            notaImportante.put("fechaHoraActual", fechaHoraRegistroR);
            notaImportante.put("titulo", tituloR);
            notaImportante.put("descripcion", descripcionR);
            notaImportante.put("fechaNota", fechaNotaR);
            notaImportante.put("estado", estadoR);

            // nombre de la base de datos donde tenemos almacenados a todos los usuarios
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
            // nombre en la base de datos donde vamos a alojar las notas importantes
            databaseReference.child(firebaseAuth.getUid()).child("Notas importantes").child(idNotaR)
                    .setValue(notaImportante)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetalleNota.this, "Se añadio a notas importantes", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetalleNota.this, "Ocurrio un error, no fue tu culpa +" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // metodo para eliminar notas importantes
    private void eliminarNotasImportantes() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ocurrio un error, no es tu culpa", Toast.LENGTH_SHORT).show();
        } else {
            // aqui vamos a eliminar la nota importante
            Bundle bundle = getIntent().getExtras();

            // obtenemos la nota desde la actividad anterior
            idNotaR = bundle.getString("idNota");

            // identificador de la nota
            // ruta donde se encontrara esa nota importante
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
            databaseReference.child(firebaseAuth.getUid()).child("Notas importantes").child(idNotaR)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetalleNota.this, "Se quito de notas importantes", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetalleNota.this, "Ocurrio un error, no es tu culpa. +"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // verificamos la nota importante
    private void verificarNotaImportante() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ocurrio un error, no es tu culpa", Toast.LENGTH_SHORT).show();
        } else {
            // aqui vamos a eliminar la nota importante
            Bundle bundle = getIntent().getExtras();

            // obtenemos la nota desde la actividad anterior
            // identificador de la nota
            idNotaR = bundle.getString("idNota");

            // ruta donde se encontrara esa nota importante
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
            databaseReference.child(firebaseAuth.getUid()).child("Notas importantes").child(idNotaR)
                    // addValueEventListener nos servira para hacer cambios en la base de datos en tiempo real
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            comprobarNotaImportante = snapshot.exists();

                            // cremamos condicion para identificar el icono que se va a mostrar
                            if (comprobarNotaImportante) {
                                String importante = "Importante";
                                btnImportante.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.icono_nota_importante, 0, 0);
                                btnImportante.setText(importante);
                            } else {
                                String no_importante = "No importante";
                                btnImportante.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.icono_nota_no_importante, 0, 0);
                                btnImportante.setText(no_importante);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // si se cancela el proceso, que no pasara
                        }
                    });



        }
    }

    // para usar el metodo de volver a la actividad anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}