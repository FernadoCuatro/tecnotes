package com.fmcuatro.agenda_online.ImportantesNotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fmcuatro.agenda_online.ActualizarNota.ActualizarNota;
import com.fmcuatro.agenda_online.Detalle.DetalleNota;
import com.fmcuatro.agenda_online.ListarNotas.ListarNotas;
import com.fmcuatro.agenda_online.Objetos.Nota;
import com.fmcuatro.agenda_online.R;
import com.fmcuatro.agenda_online.ViewHolder.ViewHolderNota;
import com.fmcuatro.agenda_online.ViewHolder.ViewHolderNotaImportante;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NotasImportantes extends AppCompatActivity {
    // declaramos
    RecyclerView rvNotasImportantes;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference miUsuario;
    DatabaseReference notasImportantes;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    // 1. objeto Nota
    // 2. View Holder creado que se llama notaImportante
    FirebaseRecyclerAdapter<Nota, ViewHolderNotaImportante> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> firebaseRecyclerOptions;

    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); // obtenemos el usuario actual

        cambiarTema(firebaseUser.getEmail());

        setContentView(R.layout.activity_notas_archivadas);

        // inicializamos las vistas
        rvNotasImportantes = findViewById(R.id.rvNotasImportantes);
        rvNotasImportantes.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();

        miUsuario = firebaseDatabase.getReference("Usuarios");
        notasImportantes = firebaseDatabase.getReference("Notas importantes");

        // el actionBar de toda la vida para volver atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detalle nota importante");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // metodo para comprobar el usuario
        comprobarUsuario();
    }
    
    // metodo para comprobar que el usuario ha iniciado sesion
    private void comprobarUsuario() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Ocurrio un error, estamos solucionandolo", Toast.LENGTH_SHORT).show();
        } else {
            listarNotasImportantes();
        }
    }

    // metodo para listar las notas importantes
    private void listarNotasImportantes() {
        // hacemos referencia a la base de datos
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(miUsuario.child(firebaseUser.getUid()).child("Notas importantes"), Nota.class).build();

        // creamos un adaptador
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolderNotaImportante>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderNotaImportante ViewHolderNotaImportante, int position, @NonNull Nota nota) {
                // establecemos el metodo que hemos creado en Nota
                ViewHolderNotaImportante.setearDatos(
                        getApplicationContext(),
                        nota.getIdNota(),
                        nota.getUidUsuario(),
                        nota.getCorreoUsuario(),
                        nota.getFechaHoraActual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFechaNota(),
                        nota.getEstado()
                );
            }

            @NonNull
            @Override
            public ViewHolderNotaImportante onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflamos el item que contiene el diseño de como se listan las notas
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_importante, parent, false);

                // creamos el objeto
                ViewHolderNotaImportante viewHolderNotaImportante = new ViewHolderNotaImportante(view);
                // establecemos los eventos
                viewHolderNotaImportante.setOnclickListener(new ViewHolderNotaImportante.ClickListener() {
                    @Override
                    public void onItemClick(View view, int posicion) {
                        // Toast.makeText(ListarNotas.this, "onItemClick", Toast.LENGTH_SHORT).show();
                        // startActivity(new Intent(ListarNotas.this, DetalleNota.class));

                        // obtenemos los datos de una nota seleccionada
                        String id_nota = getItem(posicion).getIdNota();
                        String uidUsuario = getItem(posicion).getUidUsuario();
                        String correoUsuario = getItem(posicion).getCorreoUsuario();
                        String fechaHoraRegistro = getItem(posicion).getFechaHoraActual();
                        String titulo = getItem(posicion).getTitulo();
                        String descripcion = getItem(posicion).getDescripcion();
                        String fechaNota = getItem(posicion).getFechaNota();
                        String estado = getItem(posicion).getEstado();

                        // el intent para el envio de los datos a la siguiente actividad
                        Intent intent = new Intent(NotasImportantes.this, DetalleNota.class);
                        intent.putExtra("idNota", id_nota);
                        intent.putExtra("uidUsuario", uidUsuario);
                        intent.putExtra("correoUsuario", correoUsuario);
                        intent.putExtra("fechaHoraRegistro", fechaHoraRegistro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fechaNota", fechaNota);
                        intent.putExtra("estado", estado);

                        // abrimos la actividad
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int posicion) {
                       // no tiene accion cuando se tiene un click precionado
                    }
                });

                return viewHolderNotaImportante;
            }
        };
        // establecemos como se listar las notas
        linearLayoutManager = new LinearLayoutManager(NotasImportantes.this, LinearLayoutManager.VERTICAL, false);
        // con esto establecemos que el diseño sea inverso
        // que se liste desde el ultimo hasta el primer registro
        linearLayoutManager.setReverseLayout(true);
        // le estamos diciendo que empiece a llenar la pantalla desde la parte superior
        linearLayoutManager.setStackFromEnd(true);

        rvNotasImportantes.setLayoutManager(linearLayoutManager);
        rvNotasImportantes.setAdapter(firebaseRecyclerAdapter);
    }

    // Metodo para cambiar el tema dependiendo del email
    private void cambiarTema(String email) {

        if (email.endsWith(".utec.edu.sv")) {
            setTheme(R.style.Theme_Agenda_Online_Edu);
        } else {
            setTheme(R.style.Theme_Agenda_Online);
        }
    }

    // que liste los datos con el onStart D1
    @Override
    protected void onStart() {
        super.onStart();

        // creamos una condicion
        if (firebaseRecyclerAdapter != null) {
            // si no es nulo estara escuchando que las notas sean leidas
            firebaseRecyclerAdapter.startListening();
        }
    }

    // para volver atras de toda la vida
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}