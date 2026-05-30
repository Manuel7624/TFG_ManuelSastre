package com.example.balancea.Ventanas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.balancea.Modelos.Usuario;

/**
 * Actividad encargada del registro de nuevos usuarios en la aplicación.
 * Realiza validaciones de campos, formato de correo, longitud de contraseña
 * y verifica si el usuario ya existe antes de proceder al registro.
 */
public class RegistroUsuario extends AppCompatActivity {

    /** Iconos de estado para los campos de entrada (nombre, correo, contraseñas). */
    ImageView imagenNombre, imagenCorreo, imagenPass, imagenConfirmarPass;
    
    /** TextView para mostrar mensajes de error durante el registro. */
    TextView tvError;
    
    /** Contador para controlar la ignorancia de cambios en el TextWatcher tras un error. */
    private int ignorarCambios = 0;

    /**
     * Inicializa la actividad y configura los eventos de validación y botones.
     * @param savedInstanceState Estado guardado de la instancia.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de componentes
        EditText nom = findViewById(R.id.campoNombreUsuarioRegistroUsuario);
        EditText correo = findViewById(R.id.campoCorreoRegistroUsuario);
        EditText pass = findViewById(R.id.campoPassRegistroUsuario);
        EditText confpass = findViewById(R.id.campoConfirmarPassRegistroUsuario);
        Button reg = findViewById(R.id.botonRegistrarseRegistroUsuario);
        Button inise = findViewById(R.id.botonIniciarSesionRegistroUsuario);
        ImageView verPass = findViewById(R.id.imagenVerPassConRegistroUsuario);
        ImageView verConfPass = findViewById(R.id.imagenVerPassConfConRegistroUsuario);

        tvError = findViewById(R.id.campoErrorRegistroUsuario);
        imagenNombre = findViewById(R.id.imagenNombreRegistro);
        imagenCorreo = findViewById(R.id.imagenCorreoRegistro);
        imagenPass = findViewById(R.id.imagenPassRegistro);
        imagenConfirmarPass = findViewById(R.id.imagenConfirmarPassRegistro);

        // Control de visibilidad de contraseña (campo pass)
        verPass.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                pass.setTransformationMethod(null);
                verPass.setImageResource(R.drawable.ojo_normal);
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                    event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                pass.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                verPass.setImageResource(R.drawable.ojo_off);
            }
            pass.setSelection(pass.getText().length());
            return true;
        });

        // Control de visibilidad de contraseña (campo confirmar pass)
        verConfPass.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                confpass.setTransformationMethod(null);
                verConfPass.setImageResource(R.drawable.ojo_normal);
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                    event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                confpass.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                verConfPass.setImageResource(R.drawable.ojo_off);
            }
            confpass.setSelection(confpass.getText().length());
            return true;
        });

        // Watcher para resetear estados de error al escribir de nuevo
        android.text.TextWatcher resetWatcher = new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ignorarCambios > 0) {
                    ignorarCambios--;
                    return;
                }
                tvError.setVisibility(View.GONE);
                imagenNombre.setImageResource(R.drawable.usuario_normal);
                imagenCorreo.setImageResource(R.drawable.email_normal);
                imagenPass.setImageResource(R.drawable.pass_normal);
                imagenConfirmarPass.setImageResource(R.drawable.pass_normal);
            }
            public void afterTextChanged(android.text.Editable s) {}
        };

        nom.addTextChangedListener(resetWatcher);
        correo.addTextChangedListener(resetWatcher);
        pass.addTextChangedListener(resetWatcher);
        confpass.addTextChangedListener(resetWatcher);

        // Lógica del botón registrar
        reg.setOnClickListener(view -> {
            String nombreUsuario = nom.getText().toString().trim();
            String correoUsuario = correo.getText().toString().trim().toLowerCase();
            String contrasena = pass.getText().toString();
            String confirmar = confpass.getText().toString();

            Consultas con = new Consultas(RegistroUsuario.this);

            // Validaciones de campos
            if (nombreUsuario.isEmpty() || correoUsuario.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
                mostrarError(getString(R.string.CamposVacios));
                if (nombreUsuario.isEmpty()) imagenNombre.setImageResource(R.drawable.usuario_error);
                if (correoUsuario.isEmpty()) imagenCorreo.setImageResource(R.drawable.email_error);
                if (contrasena.isEmpty()) imagenPass.setImageResource(R.drawable.pass_error);
                if (confirmar.isEmpty()) imagenConfirmarPass.setImageResource(R.drawable.pass_error);
                return;
            }

            if (nombreUsuario.length() < 3) {
                mostrarError(getString(R.string.NombreMinimoCaracteres));
                imagenNombre.setImageResource(R.drawable.usuario_error);
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoUsuario).matches()) {
                mostrarError(getString(R.string.FormatoCorreoErroneo));
                imagenCorreo.setImageResource(R.drawable.email_error);
                return;
            }

            if (con.correoExistente(correoUsuario)) {
                mostrarError(getString(R.string.CorreoYaRegistrado));
                imagenCorreo.setImageResource(R.drawable.email_error);
                return;
            }

            if (contrasena.length() < 8) {
                mostrarError(getString(R.string.ContrasenaMinimoCaracteres));
                imagenPass.setImageResource(R.drawable.pass_error);
                return;
            }

            if (!contrasena.equals(confirmar)) {
                mostrarError(getString(R.string.ContrasenasNoCoinciden));
                ignorarCambios = 2;
                imagenPass.setImageResource(R.drawable.pass_error);
                imagenConfirmarPass.setImageResource(R.drawable.pass_error);
                pass.setText("");
                confpass.setText("");
                return;
            }

            // Registro exitoso
            Usuario u = new Usuario(nombreUsuario, correoUsuario, contrasena);
            con.registrarUsuario(u);
            MainActivity.CorreoActual = correoUsuario;
            startActivity(new Intent(RegistroUsuario.this, IntroducirSaldo.class));
            finish();
        });

        // Volver a la pantalla de login
        inise.setOnClickListener(view -> {
            startActivity(new Intent(RegistroUsuario.this, MainActivity.class));
            finish();
        });
    }

    /**
     * Muestra un mensaje de error en la pantalla.
     * @param mensaje Texto del error a mostrar.
     */
    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }
}
