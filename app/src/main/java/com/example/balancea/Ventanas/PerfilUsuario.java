package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;

public class PerfilUsuario extends AppCompatActivity {

    ImageView btnBack;

    ImageView botonCambiarPassword;

    TextView txtNombre;
    TextView txtCorreo;

    CardView cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // COMPONENTES

        btnBack = findViewById(R.id.btnBack);
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        cardLogout = findViewById(R.id.cardLogout);
        botonCambiarPassword = findViewById(R.id.botonCambiarPassword);


        // CARGAR DATOS

        txtCorreo.setText(CorreoActual);

        String nombre = Consultas.obtenerNombreUsuario(this, CorreoActual);

        if (!nombre.isEmpty()) {
            nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        }

        txtNombre.setText(nombre);


        botonCambiarPassword.setOnClickListener(v -> {

            Intent intent = new Intent(PerfilUsuario.this, CambiarPassword.class);
            startActivity(intent);
        });

        // BOTON VOLVER

        btnBack.setOnClickListener(v -> {

            finish();
        });


        // CERRAR SESION

        cardLogout.setOnClickListener(v -> {

            CorreoActual = "";

            Intent intent = new Intent(PerfilUsuario.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            finish();
        });
    }
}