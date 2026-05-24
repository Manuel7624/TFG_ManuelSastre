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

public class IntroducirSaldo extends AppCompatActivity {

    TextView tvError;

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

        botonContinuarIntroducirSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String saldoInicial = campoSaldoInicialIntroducirSaldo.getText().toString().trim();

                // Campo vacio
                if(saldoInicial.isEmpty()){
                    mostrarError(getString(R.string.ObligatorioSaldo));
                    return;
                }

                // Formato incorrecto
                try {
                    Double.parseDouble(saldoInicial);
                } catch (NumberFormatException e) {
                    mostrarError(getString(R.string.SaldoErroneo));
                    return;
                }

                // Continuar
                Double saldo = Double.parseDouble(saldoInicial);
                Consultas con = new Consultas(IntroducirSaldo.this);
                con.insertarSaldoInicial(saldo, MainActivity.CorreoActual);

                Intent intent = new Intent(IntroducirSaldo.this, Principal.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }
}