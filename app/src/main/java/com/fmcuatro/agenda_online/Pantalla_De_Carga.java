package com.fmcuatro.agenda_online;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Pantalla_De_Carga extends AppCompatActivity {
    // validamos si el usuario ya inicio sesion en la app
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);

        // Configurar el modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().hide(); // para ocultar la barra

        firebaseAuth = FirebaseAuth.getInstance();

        // para la pantalla de carga
        int tiempo = 3000; // esto es igual a 3 segundos
        // utilizamos el objeto que nos permite ejecutar dentro del run lineas de codigo
        // luego de un tiempo determinado
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // lo que haremos es que despues de 3 segundos nos diriga de esta actividad a la
                // actividad principal
                // startActivity(new Intent(Pantalla_De_Carga.this, MainActivity.class));
                // finish();
                verificarUsuario();
            }
        }, tiempo);
    }

    // metodo para validar que el usuario no ha iniciado sesion
    private void verificarUsuario() {
        // en esta linea estamos obteniendo al usuario actual
        // el usuario que se registro previamente y que ha iniciado sesion
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // condicion para identificar si el usuario ha iniciado o no
        if (firebaseUser == null){
            startActivity(new Intent(Pantalla_De_Carga.this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(Pantalla_De_Carga.this, MenuPrincipal.class));
            finish();
        }
    }
}