package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class Principal extends AppCompatActivity {

    TextView campoSaludo;
    TextView botonEsteMes;
    TextView botonGeneral;
    TextView campoIngresosResumen;
    TextView campoGastosResumen;

    PieChart graficaResumen;

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


        // COMPONENTES

        campoSaludo = findViewById(R.id.campoSaludoUsuarioPrincipal);

        TextView campoSaldoActualPrincipal = findViewById(R.id.campoSaldoActualPrincipal);

        ImageView perfilUsuario = findViewById(R.id.imagenPerfilUsuarioPrincipal);

        TextView campoIngresosCardPrincipal = findViewById(R.id.campoIngresosCardPrincipal);

        TextView campoGastosCardPrincipal = findViewById(R.id.campoGastosCardPrincipal);

        botonEsteMes = findViewById(R.id.campoEsteMesPrincipal);

        botonGeneral = findViewById(R.id.campoGeneralPrincipal);

        campoIngresosResumen = findViewById(R.id.campoIngresosResumenPrincipal);

        campoGastosResumen = findViewById(R.id.campoGastosResumenPrincipal);

        graficaResumen = findViewById(R.id.graficaResumen);


        // DISEÑO GRAFICA

        graficaResumen.getDescription().setEnabled(false);

        graficaResumen.setDrawHoleEnabled(true);

        graficaResumen.setHoleRadius(82f);

        graficaResumen.setTransparentCircleRadius(0f);

        graficaResumen.getLegend().setEnabled(false);

        graficaResumen.setRotationEnabled(false);

        graficaResumen.setHighlightPerTapEnabled(false);


        // CARGAR SALUDO

        String nombreUsuario = Consultas.obtenerNombreUsuario(this, CorreoActual);

        campoSaludo.setText("Hola, " + nombreUsuario + " 👋");


        // PERFIL USUARIO

        perfilUsuario.setOnClickListener(v -> {

            Intent intent = new Intent(Principal.this, PerfilUsuario.class);

            startActivity(intent);
        });


        // SALDO ACTUAL

        double saldo = Consultas.obtenerSaldoActual(this, CorreoActual);

        campoSaldoActualPrincipal.setText(saldo + " €");

        if (saldo < 0) {

            campoSaldoActualPrincipal.setTextColor(getColor(R.color.gasto));

        } else {

            campoSaldoActualPrincipal.setTextColor(getColor(R.color.tarjetas_cards));
        }


        // INGRESOS Y GASTOS GENERALES

        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);

        double gastos = Consultas.obtenerGastos(this, CorreoActual);

        campoIngresosCardPrincipal.setText(ingresos + " €");

        campoGastosCardPrincipal.setText(gastos + " €");


        // CARGAR GRAFICA INICIAL

        cargarGrafica(true);


        // BOTON ESTE MES

        botonEsteMes.setOnClickListener(v -> {

            botonEsteMes.setBackgroundResource(R.drawable.tab_activo);

            botonGeneral.setBackgroundResource(R.drawable.tab_inactivo);

            botonEsteMes.setTextColor(getColor(R.color.tarjetas_cards));

            botonGeneral.setTextColor(getColor(R.color.texto_principal));

            cargarGrafica(true);

        });


        // BOTON GENERAL

        botonGeneral.setOnClickListener(v -> {

            botonGeneral.setBackgroundResource(R.drawable.tab_activo);

            botonEsteMes.setBackgroundResource(R.drawable.tab_inactivo);

            botonGeneral.setTextColor(getColor(R.color.tarjetas_cards));

            botonEsteMes.setTextColor(getColor(R.color.texto_principal));

            cargarGrafica(false);

        });
    }


    private void cargarGrafica(boolean soloEsteMes) {

        double ingresos;

        double gastos;


        if (soloEsteMes) {

            ingresos = Consultas.obtenerIngresosMesActual(this, CorreoActual);

            gastos = Consultas.obtenerGastosMesActual(this, CorreoActual);

        } else {

            ingresos = Consultas.obtenerIngresos(this, CorreoActual);

            gastos = Consultas.obtenerGastos(this, CorreoActual);
        }


        campoIngresosResumen.setText(ingresos + " €");

        campoGastosResumen.setText(gastos + " €");


        if (ingresos == 0 && gastos == 0) {

            graficaResumen.clear();

            return;
        }


        ArrayList<PieEntry> entradas = new ArrayList<>();

        entradas.add(new PieEntry((float) ingresos, "Ingresos"));

        entradas.add(new PieEntry((float) gastos, "Gastos"));


        PieDataSet dataSet = new PieDataSet(entradas, "");


        ArrayList<Integer> colores = new ArrayList<>();

        colores.add(Color.parseColor("#4A90E2"));

        colores.add(Color.parseColor("#FF5A52"));


        dataSet.setColors(colores);

        dataSet.setSliceSpace(6f);

        dataSet.setAutomaticallyDisableSliceSpacing(true);

        dataSet.setSelectionShift(0f);

        dataSet.setDrawValues(false);

        graficaResumen.setDrawEntryLabels(false);


        PieData pieData = new PieData(dataSet);

        graficaResumen.setData(pieData);

        graficaResumen.animateY(500);

        graficaResumen.invalidate();
    }
}