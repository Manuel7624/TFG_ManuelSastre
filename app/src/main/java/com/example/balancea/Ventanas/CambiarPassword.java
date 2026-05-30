package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;

/**
 * Actividad que permite al usuario cambiar su contraseña actual por una nueva.
 * Realiza validaciones de seguridad como la longitud mínima y la coincidencia de campos.
 */
public class CambiarPassword extends AppCompatActivity {

    /** Botón para regresar a la pantalla anterior. */
    ImageView botonVolver;

    /** Campos de entrada para la contraseña antigua, la nueva y su confirmación. */
    EditText editPasswordActual, editNuevaPassword, editConfirmarPassword;

    /** Botón para procesar y guardar el cambio de contraseña. */
    TextView botonGuardarPassword;

    /** TextView para mostrar mensajes de error durante el proceso. */
    TextView textErrorCambiarPassword;

    /** Iconos de "ojo" para alternar la visibilidad de las contraseñas en los campos. */
    ImageView imagenVePassAcCambiarPassword, imagenVePassNueCambiarPassword, imagenVePassConCambiarPassword;

    /**
     * Inicializa la actividad y configura los eventos de validación y cambio de contraseña.
     * @param savedInstanceState Estado guardado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambiar_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de componentes
        botonVolver = findViewById(R.id.botonVolver);
        editPasswordActual = findViewById(R.id.editPasswordActual);
        editNuevaPassword = findViewById(R.id.editNuevaPassword);
        editConfirmarPassword = findViewById(R.id.editConfirmarPassword);
        botonGuardarPassword = findViewById(R.id.botonGuardarPassword);
        textErrorCambiarPassword = findViewById(R.id.textErrorCambiarPassword);
        imagenVePassAcCambiarPassword = findViewById(R.id.imagenVePassAcCambiarPassword);
        imagenVePassNueCambiarPassword = findViewById(R.id.imagenVePassNueCambiarPassword);
        imagenVePassConCambiarPassword = findViewById(R.id.imagenVePassConCambiarPassword);

        botonVolver.setOnClickListener(v -> finish());

        // Lógica para guardar la nueva contraseña
        botonGuardarPassword.setOnClickListener(v -> {
            textErrorCambiarPassword.setVisibility(View.GONE);

            String passwordActual = editPasswordActual.getText().toString().trim();
            String nuevaPassword = editNuevaPassword.getText().toString().trim();
            String confirmarPassword = editConfirmarPassword.getText().toString().trim();

            // Validaciones de negocio
            if (passwordActual.isEmpty() || nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
                mostrarError("Completa todos los campos");
                return;
            }

            Consultas consultas = new Consultas(this);
            // Verifica que la contraseña actual introducida sea correcta
            if (!consultas.acceder(CorreoActual, passwordActual)) {
                mostrarError("La contraseña actual es incorrecta");
                editPasswordActual.setText("");
                return;
            }

            if (nuevaPassword.length() < 8) {
                mostrarError("La contraseña debe tener mínimo 8 caracteres");
                return;
            }

            if (!nuevaPassword.equals(confirmarPassword)) {
                mostrarError("Las contraseñas no coinciden");
                editConfirmarPassword.setText("");
                return;
            }

            if (passwordActual.equals(nuevaPassword)) {
                mostrarError("La nueva contraseña no puede ser igual");
                return;
            }

            // Intento de actualización en la base de datos
            if (Consultas.cambiarPassword(this, CorreoActual, nuevaPassword)) {
                finish();
            } else {
                mostrarError("Error al cambiar la contraseña");
            }
        });

        // Configuración de los botones de visibilidad de contraseña (Ojos)
        configurarOjoVisibilidad(imagenVePassAcCambiarPassword, editPasswordActual);
        configurarOjoVisibilidad(imagenVePassNueCambiarPassword, editNuevaPassword);
        configurarOjoVisibilidad(imagenVePassConCambiarPassword, editConfirmarPassword);
    }

    /**
     * Configura el comportamiento de un ImageView para mostrar/ocultar el texto de un EditText.
     * @param ojo ImageView que actúa como botón.
     * @param campo EditText cuyo tipo de transformación se verá alterado.
     */
    private void configurarOjoVisibilidad(ImageView ojo, EditText campo) {
        ojo.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                campo.setTransformationMethod(null);
                ojo.setImageResource(R.drawable.ojo_normal);
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                    event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                campo.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                ojo.setImageResource(R.drawable.ojo_off);
            }
            campo.setSelection(campo.getText().length());
            return true;
        });
    }

    /**
     * Muestra un mensaje de error en la interfaz.
     * @param mensaje Texto a mostrar.
     */
    private void mostrarError(String mensaje) {
        textErrorCambiarPassword.setText(mensaje);
        textErrorCambiarPassword.setVisibility(View.VISIBLE);
    }
}
