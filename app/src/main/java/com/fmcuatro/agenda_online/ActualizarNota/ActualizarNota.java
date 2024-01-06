package com.fmcuatro.agenda_online.ActualizarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fmcuatro.agenda_online.AgregarNota.AgregarNota;
import com.fmcuatro.agenda_online.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

// se implementa el evento para poder seleccionar un evento en el spinner
// implements AdapterView.OnItemSelectedListener

public class ActualizarNota extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // declaramos las vistas del diseño
    TextView tvidNota, tvUidUsuario, tvCorreoUsuario, tvFechaHoraRegistro, tvFecha, tvEstado, tvEstadoNuevo;
    EditText edtTitulo, edtDescripcion;
    Button btn_calendario;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    // para el manejo de los estados
    ImageView ivTareaFinalizada, ivTareaNoFinalizada;
    Spinner spEstadoNuevo;

    // para el manejo de la fecha
    int dia, mes, anio;

    // declaramos los string necesarios para almacenar los datos recuperados desde la actividad anterior
    String idNotaR, uidUsuarioR, correoUsuarioR, fechaHoraRegistroR, tituloR, descripcionR, fechaNotaR, estadoR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cambiarTema(firebaseUser.getEmail());

        setContentView(R.layout.activity_actualizar_nota);

        // el actionBar de toda la vida para volver atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Actualizar nota");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // metodo para inicializar las vistas
        inicializar();

        // recuperamos los datos desde la actividad anterior
        recuperarDatos();

        // setemos los datos en cada una de las vistas actuales
        setDatos();

        // comprobamos el estado de la nota
        comprobarEstadoNota();

        // metodo para manipular el spinner
        spinnerEstado();

        // establecemos un evento al boton  calendario
        btn_calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // metodo para seleccionar fecha
                seleccionarFecha();
            }
        });
    }

    // metodo para inicializar las vistas del diseño
    private void inicializar() {
        tvidNota = findViewById(R.id.tvIdNotaEn);
        tvUidUsuario = findViewById(R.id.tvUidUsuarioEn);
        tvCorreoUsuario = findViewById(R.id.tvCorreoUsuarioEn);
        tvFechaHoraRegistro = findViewById(R.id.tvFechaHoraActualEn);
        tvFecha = findViewById(R.id.tvFechaEn);
        tvEstado = findViewById(R.id.tvEstadoEn);
        edtTitulo = findViewById(R.id.edtTituloEn);
        edtDescripcion = findViewById(R.id.edtDescripcionEn);
        btn_calendario = findViewById(R.id.btn_calendarioEn);
        ivTareaFinalizada = findViewById(R.id.ivTareaFinalizada);
        ivTareaNoFinalizada = findViewById(R.id.ivTareaNoFinalizada);
        spEstadoNuevo = findViewById(R.id.spEstadoNuevo);
        tvEstadoNuevo = findViewById(R.id.tvEstadoNuevo);
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

    // metodo para setear los datos en la vista correspondiente
    private void setDatos() {
        tvidNota.setText(idNotaR);
        tvUidUsuario.setText(uidUsuarioR);
        tvCorreoUsuario.setText(correoUsuarioR);
        tvFechaHoraRegistro.setText(fechaHoraRegistroR);
        edtTitulo.setText(tituloR);
        edtDescripcion.setText(descripcionR);
        tvFecha.setText(fechaNotaR);
        tvEstado.setText(estadoR);
    }

    // comprobamos el estado de la nota
    private void comprobarEstadoNota() {
        String estadoNota = tvEstado.getText().toString();

        if (estadoNota.equals("No Finalizado"))  {
            ivTareaNoFinalizada.setVisibility(View.VISIBLE);
        } else {
            ivTareaFinalizada.setVisibility(View.VISIBLE);
        }
    }

    // metodo para menejar el estado del Spinner
    private void spinnerEstado() {
        // hacemos un adapter de la matriz de cadena para luego darle diseño por defecto
        // el orden, es el siguiente:
        // 1. el contexto, es decir la actividad
        // 2. donde hemos establecido los valores que va acontener ese spiner
        // 3. establecemos un diseño para el spiner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estadosNota, android.R.layout.simple_spinner_item);

        // adaptador del spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstadoNuevo.setAdapter(adapter);
        spEstadoNuevo.setOnItemSelectedListener(this);
    }

    // metodos que se usan para seleccionar dentro del Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String ESTADO_ACTUAL = tvEstado.getText().toString();
        String posicion_1 = adapterView.getItemAtPosition(1).toString(); // obtenemos el estado finalizado

        String estadoSeleccionado = adapterView.getItemAtPosition(i).toString();
        tvEstadoNuevo.setText(estadoSeleccionado);

        if(ESTADO_ACTUAL.equals("Finalizado")) {
            tvEstadoNuevo.setText(posicion_1);
            spEstadoNuevo.setVisibility(View.GONE);
        }
    }

    // metodo para actualizar
    // se actualizara: titulo, descripcion, fecha y el estado (este ultimo no es reversible, una vez finalizada, siempre finalizada
    private void actualizarNota() {
        // obtenemos los datos que vamos a actualizar
        String tituloActualizar = edtTitulo.getText().toString();
        String descripcionActualizar = edtDescripcion.getText().toString();
        String fechaActualizar = tvFecha.getText().toString();
        String estadoActualizar = tvEstadoNuevo.getText().toString();

        // hacemos la llamada a FireBase para lo que vamos a actualizar
        // instancia
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // establecemos la referencia
        DatabaseReference databaseReference = firebaseDatabase.getReference("notas_publicadas");
        // consulta para realizar la actualizacion comparando los id
        Query query = databaseReference.orderByChild("idNota").equalTo(idNotaR);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // aqui vamos a hacer la actualizacion en tiempo real
                // con el for hace el recorrido de las notas hasta que coicidan con el id
                // que es el que hemos seleccionado
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().child("titulo").setValue(tituloActualizar);
                    ds.getRef().child("descripcion").setValue(descripcionActualizar);
                    ds.getRef().child("fechaNota").setValue(fechaActualizar);
                    ds.getRef().child("estado").setValue(estadoActualizar);
                }

                Toast.makeText(ActualizarNota.this, "Nota actualizada con exito", Toast.LENGTH_SHORT).show();
                onBackPressed(); // que nos dirija a la actividad anterior
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // cualquier error nos saltaria aqui, y aqui se tendreia que manejar
            }
        });
    }

    private void cambiarTema(String email) {

        if (email.endsWith(".utec.edu.sv")) {
            setTheme(R.style.Theme_Agenda_Online_Edu);
        } else {
            setTheme(R.style.Theme_Agenda_Online);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // cuando no se selecciona no hacemos nada
    }

    // para usar un menu en la actividad actual
    // inflamos el menu que creamos en la actividad
    // esto quiere decir que aparece implementado
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // gestionamos que hara el item de guardar al ser presionado
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itActualizarNota:
                // Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                actualizarNota();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // para usar el metodo de volver a la actividad anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // metodo para seleccionar la fecha
    private void seleccionarFecha() {
        final Calendar calendario = Calendar.getInstance();
        dia = calendario.get(Calendar.DAY_OF_MONTH);
        mes = calendario.get(Calendar.MONTH);
        anio = calendario.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ActualizarNota.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anioSeleccionado, int mesSeleccioado, int diaSeleccionado) {
                // formateamos el dia y el mes
                String diaFormateado, mesFormateado;

                // obtenemos el dia
                if (diaSeleccionado < 10) {
                    diaFormateado = "0"+String.valueOf(diaSeleccionado); // 9/01/2023 <> 09/01/2023
                } else {
                    diaFormateado = String.valueOf(diaSeleccionado); // 17/09/2053
                }

                // obtenemos el mes
                int mesInicio = mesSeleccioado + 1;

                if (mesInicio < 10) {
                    mesFormateado = "0"+String.valueOf(mesSeleccioado); // 09/1/2023 <> 09/01/2023
                } else {
                    mesFormateado = String.valueOf(mesSeleccioado); // 17/09/2053
                }

                // set fecha en TextView
                tvFecha.setText(diaFormateado+"/"+mesFormateado+"/"+anioSeleccionado);
            }
        }, anio, mes, dia);
        // para poder ver el calendario en la pantalla del dispositivo
        datePickerDialog.show();
    }

}