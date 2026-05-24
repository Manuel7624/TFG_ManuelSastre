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

public class MainActivity extends AppCompatActivity {

    public static String CorreoActual;

    ImageView imagenCorreo;
    ImageView imagenPass;
    TextView campoCorreoConInco;

    private int ignorarCambios = 0;

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
        EditText correo = findViewById(R.id.campoCorreoLogin);
        EditText pass = findViewById(R.id.campoPassLogin);

        Button inicarSesion = findViewById(R.id.botonIniciarSesionLogin);
        Button registrarse = findViewById(R.id.botonRegistrarseLogin);

        campoCorreoConInco = findViewById(R.id.campoCorreoConInco);
        imagenCorreo = findViewById(R.id.imagenCorreoLogin);
        imagenPass = findViewById(R.id.imagenPassLogin);

        DbHelper dbHelper = new DbHelper(this);

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

        inicarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correoUsuario = correo.getText().toString().trim().toLowerCase();
                String contrasenya = pass.getText().toString();
                Consultas con = new Consultas(MainActivity.this);

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

                if(con.acceder(correoUsuario, contrasenya)){
                    CorreoActual = correoUsuario;
                    Intent intent = new Intent(MainActivity.this, Principal.class);
                    startActivity(intent);
                    finish();
                } else {
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