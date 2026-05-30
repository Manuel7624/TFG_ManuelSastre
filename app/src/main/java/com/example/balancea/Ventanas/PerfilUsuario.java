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

/**
 * Actividad que gestiona el perfil del usuario.
 * Muestra la información personal, permite cambiar la contraseña y cerrar la sesión.
 */
public class PerfilUsuario extends AppCompatActivity {

    /** Botón para regresar a la pantalla anterior. */
    ImageView btnBack;

    /** Botón para navegar a la pantalla de cambio de contraseña. */
    ImageView botonCambiarPassword;

    /** TextViews para mostrar el nombre y el correo del usuario actual. */
    TextView txtNombre;
    TextView txtCorreo;

    /** CardView que actúa como botón para cerrar la sesión del usuario. */
    CardView cardLogout;

    /**
     * Inicializa la actividad y carga los datos del perfil del usuario.
     * @param savedInstanceState Estado guardado de la instancia.
     */
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

        // Inicialización de componentes
        btnBack = findViewById(R.id.btnBack);
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        cardLogout = findViewById(R.id.cardLogout);
        botonCambiarPassword = findViewById(R.id.botonCambiarPassword);

        // Carga de datos del usuario
        txtCorreo.setText(CorreoActual);

        String nombre = Consultas.obtenerNombreUsuario(this, CorreoActual);

        // Formatea el nombre para que la primera letra sea mayúscula
        if (!nombre.isEmpty()) {
            nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        }

        txtNombre.setText(nombre);

        // Evento para cambiar contraseña
        botonCambiarPassword.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilUsuario.this, CambiarPassword.class);
            startActivity(intent);
        });

        // Evento para volver
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Lógica de cierre de sesión
        cardLogout.setOnClickListener(v -> {
            CorreoActual = ""; // Limpia la sesión
            Intent intent = new Intent(PerfilUsuario.this, MainActivity.class);
            // Limpia el stack de actividades para evitar volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
