package com.fmcuatro.agenda_online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fmcuatro.agenda_online.ImportantesNotas.NotasImportantes;
import com.fmcuatro.agenda_online.ListarNotas.ListarNotas;
import com.fmcuatro.agenda_online.Perfil.PerfilUsuario;
import com.fmcuatro.agenda_online.AgregarNota.AgregarNota;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {
    // declaramos
    TextView tvNombrePrincipal, tvCorreoPrincipal, tvUidPrincipal;

    CardView cardView;
    ProgressBar progressBarDatos;
    ProgressDialog progressDialog;
    LinearLayoutCompat lnNombres, lnEmail, lnVerificacion, lnFechaPago, lnFechaParciales;

    Button btnAgregarNotas, btnListarNotas, btnImportantes, btnPerfil, btnAcercaDe, btnCerrarSesion, btnEstadoCuenta;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    // mandamos a traer la cosa como que es tabla
    // pero dice que es la base de datos
    DatabaseReference Usuarios;

    // dialogo para la verificacion exitosa
    Dialog dialog_cuenta_verificada, dialog_informacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inicializamos y cambio de tema
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        cambiarTema(firebaseUser.getEmail());

        setContentView(R.layout.activity_menu_principal);

        // inicializamos
        btnAgregarNotas = findViewById(R.id.btnAgregarNotas);
        btnListarNotas = findViewById(R.id.btnListarNotas);
        btnImportantes = findViewById(R.id.btnImportantes);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnAcercaDe = findViewById(R.id.btnAcercaDe);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // inicializamos los dos LinearLayout de fechas destacadas
        lnFechaPago = findViewById(R.id.lnFechaPago);
        lnFechaParciales = findViewById(R.id.lnFechaParciales);

        //Cambiar color al cardview
        cardView = findViewById(R.id.cardView);
        if (firebaseUser.getEmail().endsWith(".utec.edu.sv")){
            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorEduPrimary));
            lnFechaPago.setVisibility(View.VISIBLE);
            lnFechaParciales.setVisibility(View.VISIBLE);
        }

        tvNombrePrincipal = findViewById(R.id.tvNombrePrincipal);
        tvCorreoPrincipal = findViewById(R.id.tvCorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);
        tvUidPrincipal = findViewById(R.id.tvUidPrincipal);

        lnNombres = findViewById(R.id.lnNombres);
        lnEmail = findViewById(R.id.lnEmail);

        lnVerificacion = findViewById(R.id.lnVerificacion);
        btnEstadoCuenta = findViewById(R.id.btnEstadoCuenta);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere, por favor.");
        progressDialog.setCanceledOnTouchOutside(false);

        // inicializamos el dialog para la cuenta verificada
        dialog_cuenta_verificada = new Dialog(this);

        // dialog para el acerca de
        dialog_informacion = new Dialog(this);

        // el nombre de la base de datos debe de coincidir con el firebase
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        // declaramos el actionbar pero que le pertenezca a androidx para poner titulo y la fecha para atrás
        // ActionBar actionBar = getSupportActionBar();
        // actionBar.setTitle("TecNotes");
        getSupportActionBar().hide(); // para ocultar la barra

        // LinearLayout de las fechas y eventos en los clicks
        lnFechaPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuPrincipal.this, "Fechas de pago, en desarrollo", Toast.LENGTH_SHORT).show();
            }
        });

        lnFechaParciales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuPrincipal.this, "Fecha de parciales, en desarrollo", Toast.LENGTH_SHORT).show();
            }
        });

        // evento para el boton de verificacion
        btnEstadoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // condicion para verificar el estado del usuario
                if (firebaseUser.isEmailVerified()) {
                    // Toast.makeText(MenuPrincipal.this, "Enhorabueno, tu cuenta esta verificada", Toast.LENGTH_SHORT).show();
                    animacionCuentaVerificada();
                } else {
                    verificarCuentaCorreo();
                }
                // creamos un metodo que nos muestre un dialog
            }
        });

        btnAgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // que nos mande a la actividad de agregar nota
                // mandamos el Uid y el correo
                String uidUsuario = tvUidPrincipal.getText().toString();
                String correoUsuario = tvCorreoPrincipal.getText().toString();

                Intent intent = new Intent(MenuPrincipal.this, AgregarNota.class);
                intent.putExtra("uidUsuario", uidUsuario);
                intent.putExtra("correoUsuario", correoUsuario);
                startActivity(intent);

                // Toast.makeText(MenuPrincipal.this, "Agrega Notas", Toast.LENGTH_SHORT).show();
            }
        });

        btnListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, ListarNotas.class));
                // Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });

        btnImportantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, NotasImportantes.class));
                // Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, PerfilUsuario.class));
                // Toast.makeText(MenuPrincipal.this, "Perfil del Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informacion();
                // Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show();
            }
        });

        // creamos evento para cerrar sesion
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creamos metodo
                salirAplicacion();
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

    // metodo para verificar la cuenta de correo
    private void verificarCuentaCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verifica tu cuenta")
                .setMessage("Se enviarán instrucciones a tu correo electrónico:  " + firebaseUser.getEmail())
                .setPositiveButton("Enviar verificación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enviarCorreoVerificar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // si el usuario cancela el proceso, no pasa nada
                    }
                }).show();
    }

    // enviamos el correo
    private void enviarCorreoVerificar() {
        progressDialog.setMessage("Enviando verificación a su correo electrónico: " + firebaseUser.getEmail());
        progressDialog.show();
        
        // enviamos el correo de verificacion
        firebaseUser.sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // si el envio fue exitoso
                        progressDialog.dismiss();
                        Toast.makeText(MenuPrincipal.this, "Instrucciones enviadas, revise su correo electrónico", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // si existe un error, se viene para aca
                        Toast.makeText(MenuPrincipal.this, "Ocurrio un error, no es tu culpa. +"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // se verifica si el usuario ya esta verificado por medio de su correo
    private void verificarEstadoCuenta() {
        String verificado = "Verificado";
        String noVerificado = "No verificado";

        if (firebaseUser.isEmailVerified()) {
            btnEstadoCuenta.setText(verificado);
            btnEstadoCuenta.setBackgroundColor(Color.rgb(41, 128, 185));
        } else {
            btnEstadoCuenta.setText(noVerificado);
            btnEstadoCuenta.setBackgroundColor(Color.rgb(231, 76, 60));
        }
    }

    // animacion para la cuenta verificada
    public void animacionCuentaVerificada() {
        // declaramos el boton
        Button btnEntendido;

        // conexion de la actividad con el diseño
        dialog_cuenta_verificada.setContentView(R.layout.dialogo_cuenta_verificada);
        btnEntendido = dialog_cuenta_verificada.findViewById(R.id.btnEntendido);

        // asignamos un evento al boton
        btnEntendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // consistira de que si precionamos ese boton, se oculte
                dialog_cuenta_verificada.dismiss();
            }
        });
        dialog_cuenta_verificada.show();
        dialog_cuenta_verificada.setCanceledOnTouchOutside(false); // nos servira para cuando se ejecute no se pueda cerrar cuando le de fuera del cuadro de dialogo
    }

    // animacion para el detalle de la aplicacion
    private void informacion() {
        Button btn_entendido;

        dialog_informacion.setContentView(R.layout.cuadro_dialogo_informacion);
        btn_entendido = dialog_informacion.findViewById(R.id.btn_entendido);

        // evento al boton
        btn_entendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // se oculte el cuadro de dialogo
                dialog_informacion.dismiss();
            }
        });

        dialog_informacion.show();
        dialog_informacion.setCanceledOnTouchOutside(false);
    }


    // para que esto se compruebe con el onStart mandamos a llamar el metodo
    // se ejecuta cuando la sesion es abierta
    @Override
    protected void onStart() {
        comprobarSesion();
        super.onStart();
    }

    // verificamos que el usuario tenga sesión iniciada
    private void comprobarSesion() {
        if (firebaseUser != null) {
            cargaDatos();
        } else {
            startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        }
    }

    // metodo para traer los datos de Firebase a la app
    private void cargaDatos() {
        // user contiene el nombre del usuario que a iniziado sesion
        Usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // el onDataChange nos permite leer en tiempo real
                // los datos que contenga el usuario en FireBase

                // verificamos el estado de la cuenta
                verificarEstadoCuenta();

                // verificamos que el usuario realmente exista
                if (snapshot.exists()) {
                    // obtenemos los datos
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();

                    // setear los datos en los TextView
                    tvUidPrincipal.setText("uid: " + uid);
                    tvNombrePrincipal.setText(nombres);
                    tvCorreoPrincipal.setText(correo);

                    // el progressBar se oculta
                    progressBarDatos.setVisibility(View.GONE);

                    // los TextView se muestran
                    lnNombres.setVisibility(View.VISIBLE);
                    lnEmail.setVisibility(View.VISIBLE);
                    lnVerificacion.setVisibility(View.VISIBLE);
                    // tvUidPrincipal.setVisibility(View.VISIBLE);

                    // activamos los botones luego que se carguen los datos
                    // habilitamos los botones del menú
                    btnAgregarNotas.setEnabled(true);
                    btnListarNotas.setEnabled(true);
                    btnImportantes.setEnabled(true);
                    btnPerfil.setEnabled(true);
                    btnAcercaDe.setEnabled(true);
                    btnCerrarSesion.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void salirAplicacion() {
        // cerramos la sesion
        firebaseAuth.signOut();
        // lanzamos una activity
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión, vuelve pronto.", Toast.LENGTH_LONG).show();
    }
}