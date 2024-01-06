package com.fmcuatro.agenda_online.ListarNotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.fmcuatro.agenda_online.Objetos.Nota;
import com.fmcuatro.agenda_online.R;
import com.fmcuatro.agenda_online.ViewHolder.ViewHolderNota;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListarNotas extends AppCompatActivity {
    // declaramos
    RecyclerView recyclerviewNotas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // creamos la forma de listar las notas
    LinearLayoutManager linearLayoutManager;

    // se agrega la dependencia
    // implementation 'com.firebaseui:firebase-ui-database:8.0.0'
    // la funcion principal es que se usa un detector de eventos para detectar cambios
    // en la base de datos, esto escucha los cambios en tiempo real
    // parametro 1, el objeto de Nota
    FirebaseRecyclerAdapter<Nota, ViewHolderNota> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> options;

    // creamos el Dialog para mostrar las opciones al presionar un item
    Dialog dialog;

    // para validar que se muestren las notas solo del usuario actual
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instancia para mostrar las notas solo del usuario actual
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser(); // con esta linea de codigo obtenemos al usuario que ha iniciado sesion
        cambiarTema(user.getEmail());

        setContentView(R.layout.activity_listar_notas);

        // declaramos el actionbar pero que le pertenezca a androidx para poner titulo y la fecha para atrás
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // inicializamos
        recyclerviewNotas = findViewById(R.id.recyclerviewNotas);
        recyclerviewNotas.setHasFixedSize(true); // le declaramos que adapte su tamaño a los elementos que se liste

        // instancia en la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("notas_publicadas");

        // inicializamos el dialog
        dialog = new Dialog(ListarNotas.this);

        // metodo que nos permite listar desde la base de datos
        listarNotasUsuarios();
    }

    // Metodo para cambiar el tema dependiendo del email
    private void cambiarTema(String email) {

        if (email.endsWith(".utec.edu.sv")) {
            setTheme(R.style.Theme_Agenda_Online_Edu);
        } else {
            setTheme(R.style.Theme_Agenda_Online);
        }
    }

    private void listarNotasUsuarios() {
        // hacemos la consulta para listar las notas solo del usuario actual
        Query query = databaseReference.orderByChild("uidUsuario").equalTo(user.getUid());
        // le vamos a decir que ordenamos las notas de acuerdo al uid del usuario actual

        // hacemos referencia a la base de datos
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        
        // creamos un adaptador
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolderNota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderNota ViewHolderNota, int position, @NonNull Nota nota) {
                // establecemos el metodo que hemos creado en Nota
                ViewHolderNota.setearDatos(
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
            public ViewHolderNota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflamos el item que contiene el diseño de como se listan las notas
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
                
                // creamos el objeto
                ViewHolderNota viewHolderNota = new ViewHolderNota(view);
                // establecemos los eventos
                viewHolderNota.setOnclickListener(new ViewHolderNota.ClickListener() {
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
                        Intent intent = new Intent(ListarNotas.this, DetalleNota.class);
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
                        // Toast.makeText(ListarNotas.this, "onItemLongClick", Toast.LENGTH_SHORT).show();
                        // declarar las vistas para eliminar y actualizar
                        Button btnEliminar, btnActualizar;
                        // obtenemos el id de la nota que hemos seleccionado
                        String id_nota = getItem(posicion).getIdNota();

                        // obtener los datos de la nota seleccionada
                        // tomando en cuenta que la id_nota ya fue obtenida
                        String uidUsuario = getItem(posicion).getUidUsuario();
                        String correoUsuario = getItem(posicion).getCorreoUsuario();
                        String fechaHoraRegistro = getItem(posicion).getFechaHoraActual();
                        String titulo = getItem(posicion).getTitulo();
                        String descripcion = getItem(posicion).getDescripcion();
                        String fechaNota = getItem(posicion).getFechaNota();
                        String estado = getItem(posicion).getEstado();

                        // realizamos la conexion con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);
                        // incializamos las vistas
                        btnEliminar = dialog.findViewById(R.id.btnEliminar);
                        btnActualizar = dialog.findViewById(R.id.btnActualizar);

                        // asignamos eventos al presionar los botones
                        btnEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(ListarNotas.this, "Eliminar nota", Toast.LENGTH_SHORT).show();
                                // metodo para eliminar una nota
                                eliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        btnActualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Toast.makeText(ListarNotas.this, "Actualizar nota", Toast.LENGTH_SHORT).show();
                                // startActivity(new Intent(ListarNotas.this, ActualizarNota.class));
                                // enviamos los datos en la Activity
                                Intent intent = new Intent(ListarNotas.this, ActualizarNota.class);
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

                                dialog.dismiss();
                            }
                        });

                        // mostramos el cuadro de dialogo
                        dialog.show();
                    }
                });

                return viewHolderNota;
            }
        };
        // establecemos como se listar las notas
        linearLayoutManager = new LinearLayoutManager(ListarNotas.this, LinearLayoutManager.VERTICAL, false);
        // con esto establecemos que el diseño sea inverso
        // que se liste desde el ultimo hasta el primer registro
        linearLayoutManager.setReverseLayout(true);
        // le estamos diciendo que empiece a llenar la pantalla desde la parte superior
        linearLayoutManager.setStackFromEnd(true);

        recyclerviewNotas.setLayoutManager(linearLayoutManager);
        recyclerviewNotas.setAdapter(firebaseRecyclerAdapter);
    }

    // metodo para eliminar una nota
    private void eliminarNota(String id_nota) {
        // Toast.makeText(this, id_nota, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(ListarNotas.this);
        builder.setTitle("Eliminar nota");
        builder.setMessage("¿Desea eliminar la nota?");
        builder.setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // si el usuario esta seguro, creamos la consulta necesaria
                // para eliminar la nota de la base de datos
                Query query = databaseReference.orderByChild("idNota").equalTo(id_nota);
                
                // si los dos id son equivalentes tanto en la db como la que vamos a eliminar, entonces continua
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // hacemos el proceso para eliminar, usamos el for para recorrer en la db
                        // las notas registradas y cuando los id sean equivalentes se va a mochar
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(ListarNotas.this, "Se elimino la nota", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // que nos muestre un mensaje del posible error
                        Toast.makeText(ListarNotas.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toast.makeText(ListarNotas.this, "Se cancelo el proceso", Toast.LENGTH_SHORT).show();
            }
        });
        
        // para que el Dialog sea visible
        builder.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // creamos una condicion
        if (firebaseRecyclerAdapter != null) {
            // si no es nulo estara escuchando que las notas sean leidas
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}