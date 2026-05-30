package com.example.balancea.Ventanas;

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
import com.example.balancea.DbHelper;
import com.example.balancea.R;

/**
 * Actividad de inicio de sesión (Login).
 * Permite al usuario acceder a su cuenta mediante correo y contraseña,
 * o navegar hacia la pantalla de registro.
 */
public class MainActivity extends AppCompatActivity {

    /** Almacena el correo del usuario que ha iniciado sesión para ser usado en toda la app. */
    public static String CorreoActual;

    /** Iconos de los campos de texto que cambian de color según el estado (normal/error). */
    ImageView imagenCorreo;
    ImageView imagenPass;

    /** TextView para mostrar mensajes de error específicos durante el login. */
    TextView campoCorreoConInco;

    /** Contador para ignorar eventos del TextWatcher al limpiar campos tras un error. */
    private int ignorarCambios = 0;

    /**
     * Inicializa la actividad y configura los eventos de los botones y validaciones.
     * @param savedInstanceState Estado guardado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los componentes de la interfaz
        EditText correo = findViewById(R.id.campoCorreoLogin);
        EditText pass = findViewById(R.id.campoPassLogin);
        ImageView imagenVerPass = findViewById(R.id.imagenVerPassLogin);
        Button inicarSesion = findViewById(R.id.botonIniciarSesionLogin);
        Button registrarse = findViewById(R.id.botonRegistrarseLogin);
        campoCorreoConInco = findViewById(R.id.campoCorreoConInco);
        imagenCorreo = findViewById(R.id.imagenCorreoLogin);
        imagenPass = findViewById(R.id.imagenPassLogin);

        // Inicialización innecesaria pero mantenida por compatibilidad de código actual
        DbHelper dbHelper = new DbHelper(this);

        // Configuración para mostrar/ocultar contraseña al mantener pulsado el ojo
        imagenVerPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    pass.setTransformationMethod(null);
                    imagenVerPass.setImageResource(R.drawable.ojo_normal);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP || event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                    pass.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                    imagenVerPass.setImageResource(R.drawable.ojo_off);
                }
                pass.setSelection(pass.getText().length());
                return true;
            }
        });

        // Listeners para limpiar avisos de error cuando el usuario vuelve a escribir
        correo.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ignorarCambios > 0) {
                    ignorarCambios--;
                    return;
                }
                campoCorreoConInco.setVisibility(View.GONE);
                imagenCorreo.setImageResource(R.drawable.email_normal);
                imagenPass.setImageResource(R.drawable.pass_normal);
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        pass.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ignorarCambios > 0) {
                    ignorarCambios--;
                    return;
                }
                campoCorreoConInco.setVisibility(View.GONE);
                imagenCorreo.setImageResource(R.drawable.email_normal);
                imagenPass.setImageResource(R.drawable.pass_normal);
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Lógica del botón de inicio de sesión
        inicarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoUsuario = correo.getText().toString().trim().toLowerCase();
                String contrasenya = pass.getText().toString();
                Consultas con = new Consultas(MainActivity.this);

                // Validaciones de campos vacíos y formato
                if(correoUsuario.isEmpty() || contrasenya.isEmpty()){
                    campoCorreoConInco.setText(R.string.CamposVacios);
                    campoCorreoConInco.setVisibility(View.VISIBLE);
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoUsuario).matches()) {
                    campoCorreoConInco.setText(R.string.CorrInvalido);
                    campoCorreoConInco.setVisibility(View.VISIBLE);
                    imagenCorreo.setImageResource(R.drawable.email_error);
                    return;
                }

                // Intento de acceso a la base de datos
                if(con.acceder(correoUsuario, contrasenya)){
                    CorreoActual = correoUsuario;
                    Intent intent = new Intent(MainActivity.this, Principal.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Gestión de error en credenciales
                    ignorarCambios = 2;
                    campoCorreoConInco.setText(R.string.CorrConIncorrectos);
                    campoCorreoConInco.setVisibility(View.VISIBLE);
                    imagenCorreo.setImageResource(R.drawable.email_error);
                    imagenPass.setImageResource(R.drawable.pass_error);
                    correo.setText("");
                    pass.setText("");
                }
            }
        });

        // Navegación a la pantalla de registro
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo.setText("");
                pass.setText("");
                Intent intent = new Intent(MainActivity.this, RegistroUsuario.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
