package com.example.balancea.Ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;

/**
 * Actividad que permite al usuario ingresar su saldo inicial tras el registro.
 * Valida que el formato numérico sea correcto antes de guardarlo como el primer movimiento.
 */
public class IntroducirSaldo extends AppCompatActivity {

    /** TextView para mostrar mensajes de error durante la introducción del saldo. */
    TextView tvError;

    /**
     * Inicializa la actividad y configura la lógica del botón de continuar.
     * @param savedInstanceState Estado guardado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_introducir_saldo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvError = findViewById(R.id.campoErrorIntroducirSaldo);
        EditText campoSaldoInicialIntroducirSaldo = findViewById(R.id.campoSaldoInicialIntroducirSaldo);
        Button botonContinuarIntroducirSaldo = findViewById(R.id.botonContinuarIntroducirSaldo);

        // Evento para procesar el saldo inicial
        botonContinuarIntroducirSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String saldoInicial = campoSaldoInicialIntroducirSaldo.getText().toString().trim();

                // Validación de campo vacío
                if(saldoInicial.isEmpty()){
                    mostrarError(getString(R.string.ObligatorioSaldo));
                    return;
                }

                // Validación de formato numérico (double)
                try {
                    Double.parseDouble(saldoInicial);
                } catch (NumberFormatException e) {
                    mostrarError(getString(R.string.SaldoErroneo));
                    return;
                }

                // Registro del saldo en la base de datos
                Double saldo = Double.parseDouble(saldoInicial);
                Consultas con = new Consultas(IntroducirSaldo.this);
                con.insertarSaldoInicial(saldo, MainActivity.CorreoActual);

                // Navegación a la pantalla principal
                Intent intent = new Intent(IntroducirSaldo.this, Principal.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Muestra un mensaje de error en la interfaz.
     * @param mensaje Texto del error a mostrar.
     */
    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }
}
