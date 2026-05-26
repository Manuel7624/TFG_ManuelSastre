package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Principal extends AppCompatActivity {

    TextView campoSaludo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        campoSaludo = findViewById(R.id.campoSaludoUsuarioPrincipal);
        TextView campoSaldoActualPrincipal = findViewById(R.id.campoSaldoActualPrincipal);
        ImageView perfilUsuario = findViewById(R.id.imagenPerfilUsuarioPrincipal);
        TextView campoIngresosCardPrincipal = findViewById(R.id.campoIngresosCardPrincipal);
        TextView campoGastosCardPrincipal = findViewById(R.id.campoGastosCardPrincipal);




        // Cargar saludo al usuario

        String nombreUsuario = Consultas.obtenerNombreUsuario(this, CorreoActual);
        campoSaludo.setText("Hola, " + nombreUsuario + " 👋");


        // Ir a perfil de usuario

        perfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, PerfilUsuario.class);
                startActivity(intent);
            }
        });


        // Cargar saldo actual

        double saldo = Consultas.obtenerSaldoActual(this, MainActivity.CorreoActual);
        campoSaldoActualPrincipal.setText(saldo + " €");

        if(saldo < 0){

            campoSaldoActualPrincipal.setTextColor(getColor(R.color.gasto));

        } else {

            campoSaldoActualPrincipal.setTextColor(getColor(R.color.tarjetas_cards));

        }


        // Cargar ingresos y gastos

        double ingresos = Consultas.obtenerIngresos(this, MainActivity.CorreoActual);
        double gastos = Consultas.obtenerGastos(this, MainActivity.CorreoActual);

        campoIngresosCardPrincipal.setText(String.format(ingresos + " €"));
        campoGastosCardPrincipal.setText(String.format(gastos + " €"));

    }
}