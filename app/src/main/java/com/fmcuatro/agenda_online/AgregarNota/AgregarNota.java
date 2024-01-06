package com.fmcuatro.agenda_online.AgregarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fmcuatro.agenda_online.AlarmNotification;
import com.fmcuatro.agenda_online.Objetos.Nota;
import com.fmcuatro.agenda_online.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AgregarNota extends AppCompatActivity {
    // declaramos
    TextView tvUidUsuario, tvCorreoUsuario, tvFechaHoraActual, tvFecha, tvEstado;
    EditText edtTitulo, edtDescripcion;
    Button btn_calendario;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    // para almacenar el dia mes y anio
    int dia, mes, anio;

    // la base de datos de Firebase
    DatabaseReference databaseReference;

    //canal
    public static String MY_CHANNEL_ID = "myChannel";
    private static int notificationIdCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cambiarTema(firebaseUser.getEmail());
        setContentView(R.layout.activity_agregar_nota);

        //creacion del canal
        createChannel();

        // inicializamos
        inicializarVariables();
        // recuerpamos los datos enviados desde la otra actividad
        obtenerDatos();
        // obtenemos la fecha actual
        obtenerFechaHoraActual();

        // el actionBar de toda la vida para volver atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // evento al boton calendario
        btn_calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendario = Calendar.getInstance();
                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarNota.this, new DatePickerDialog.OnDateSetListener() {
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
                            mesFormateado = "0" + String.valueOf(mesInicio); // 09/1/2023 <> 09/01/2023
                        } else {
                            mesFormateado = String.valueOf(mesInicio); // 17/09/2053
                        }

                        // set fecha en TextView
                        tvFecha.setText(diaFormateado+"/"+mesFormateado+"/"+anioSeleccionado);
                    }
                }, anio, mes, dia);
                // para poder ver el calendario en la pantalla del dispositivo
                datePickerDialog.show();
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

    // metodo para inicializar las variables
    private void inicializarVariables() {
        tvUidUsuario = findViewById(R.id.tvUidUsuario);
        tvCorreoUsuario = findViewById(R.id.tvCorreoUsuario);
        tvFechaHoraActual = findViewById(R.id.tvFechaHoraActual);
        tvFecha = findViewById(R.id.tvFecha);
        tvEstado = findViewById(R.id.tvEstado);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        btn_calendario = findViewById(R.id.btn_calendario);

        // creamos la instancia
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // recuperamos los datos enviados desde la activiy anterior
    private void obtenerDatos() {
        String uidRecuperado = getIntent().getStringExtra("uidUsuario");
        String correoecuperado = getIntent().getStringExtra("correoUsuario");

        tvUidUsuario.setText(uidRecuperado);
        tvCorreoUsuario.setText(correoecuperado);
    }

    // obtenemos la fecha y hora
    private void obtenerFechaHoraActual() {
        // se obtendra de esta forma: 13-11-2023/06:30:20 pm
        // se obtiene la fecha y hora del dispositivo
        String fechaHoraRegistro = new SimpleDateFormat("dd-MM-YYYY/HH:mm:ss a", Locale.getDefault()).format(System.currentTimeMillis());
        tvFechaHoraActual.setText(fechaHoraRegistro);
    }

    // metodo que permite al usuario agregar una nota a base de datos
    private void agregarNota() {
        // obtenemos los datos
        String uidUsuario = tvUidUsuario.getText().toString();
        String correoUsuario = tvCorreoUsuario.getText().toString();
        String fechaHoraActual = tvFechaHoraActual.getText().toString();
        String titulo = edtTitulo.getText().toString();
        String descripcion = edtDescripcion.getText().toString();
        String fecha = tvFecha.getText().toString();
        String estado = tvEstado.getText().toString();
        // obtenemos la key del usuario
        String idNota = databaseReference.push().getKey();

        // validamos los datos
        if (!uidUsuario.equals("") && !correoUsuario.equals("") && !fechaHoraActual.equals("") && !titulo.equals("")
            && !descripcion.equals("") && !fecha.equals("") && !estado.equals("")) {
            // si los datos son validos, se crea el objeto
            String uidSinPrefijo = uidUsuario.substring(5);
            Nota nota = new Nota(idNota, uidSinPrefijo, correoUsuario, fechaHoraActual, titulo, descripcion, fecha, estado);

            // establecer el nombre de la base de datos
            String nombreDB = "notas_publicadas";
            databaseReference.child(nombreDB).child(idNota).setValue(nota);

            Toast.makeText(this, "Se agrego la nota exitosamente", Toast.LENGTH_SHORT).show();

            //Agregar la alerta en la hora estipulada
            scheduleNotification(titulo, descripcion, fecha);

            onBackPressed();
        } else {
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    // para usar un menu en la actividad actual
    // inflamos el menu que creamos en la actividad
    // esto quiere decir que aparece implementado
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // gestionamos que hara el item de guardar al ser presionado
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itAgregarNota:
                agregarNota();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //Metodo que crea el canal para notificacione
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    MY_CHANNEL_ID,
                    "TECNOTES",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("TECNOTES");

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);
        }
    }

    //Metodo que configura la notificacion
    private void scheduleNotification(String titulo, String descripcion, String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(fecha);

            // Configurar la notificación para la fecha seleccionada a las 12:00 AM
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

            int notificationId = generarNotificationId();

            // Crear un objeto Data con los datos que quieres pasar al Worker
            Data inputData = new Data.Builder()
                    .putString("texto", titulo)
                    .putString("descripcion", descripcion)
                    .putInt("notificationId", notificationId)
                    .build();

            Constraints.Builder constraintsBuilder = new Constraints.Builder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                constraintsBuilder.setRequiresDeviceIdle(false);
            }
            Constraints constraints = constraintsBuilder.build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(AlarmNotification.class)
                    .setInputData(inputData)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private synchronized int generarNotificationId() {
        notificationIdCounter++;
        return notificationIdCounter;
    }
}