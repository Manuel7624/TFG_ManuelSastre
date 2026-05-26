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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import android.database.Cursor;

public class Principal extends AppCompatActivity {

    TextView campoSaludo;
    TextView botonEsteMes;
    TextView botonGeneral;
    TextView campoIngresosResumen;
    TextView campoGastosResumen;

    PieChart graficaResumen;
    TextView campoPorcentajeBalancePrincipal;
    ImageView iconoMovimiento;
    TextView campoTituloMovimiento;
    TextView campoFechaMovimiento;
    TextView campoImporteMovimiento;

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

        campoPorcentajeBalancePrincipal = findViewById(R.id.campoPorcentajeBalancePrincipal);

        graficaResumen = findViewById(R.id.graficaResumen);

        iconoMovimiento = findViewById(R.id.iconoMovimiento);

        campoTituloMovimiento = findViewById(R.id.campoTituloMovimiento);

        campoFechaMovimiento = findViewById(R.id.campoFechaMovimiento);

        campoImporteMovimiento = findViewById(R.id.campoImporteMovimiento);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);




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


        // CARGAR ULTIMO MOVIMIENTO

        cargarUltimoMovimiento();


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


        // NAVEGACION

        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            }

            if (id == R.id.nav_movimientos) {
                startActivity(new Intent(this, Movimientos.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (id == R.id.nav_stats) {
                startActivity(new Intent(this, Graficas.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        bottomNavigation.setOnItemReselectedListener(item -> {
            // no hacer nada si ya estás en la misma ventana
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


        // BALANCE

        double porcentajeBalance;


        if (gastos == 0) {

            porcentajeBalance = 100;

        } else {

            porcentajeBalance = ((ingresos - gastos) / gastos) * 100;
        }

        if (porcentajeBalance >= 0) {
            campoPorcentajeBalancePrincipal.setText("+" + (int) porcentajeBalance + "%");
        } else {
            campoPorcentajeBalancePrincipal.setText((int) porcentajeBalance + "%");
        }

        if (porcentajeBalance < 0) {

            campoPorcentajeBalancePrincipal.setTextColor(getColor(R.color.gasto));

        } else {

            campoPorcentajeBalancePrincipal.setTextColor(getColor(R.color.ingreso));
        }



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

    private void cargarUltimoMovimiento() {

        Cursor cursor = Consultas.obtenerUltimoMovimiento(this, CorreoActual);

        if (cursor.moveToFirst()) {

            String tipo = cursor.getString(0);
            String descripcion = cursor.getString(1);
            double cantidad = cursor.getDouble(2);
            String fecha = cursor.getString(3);
            int idCategoria = cursor.getInt(4);
            String categoria = cursor.getString(5);

            // ICONO CATEGORÍA

            switch (idCategoria) {

                case 1:
                    iconoMovimiento.setImageResource(R.drawable.ic_comida);
                    break;

                case 2:
                    iconoMovimiento.setImageResource(R.drawable.ic_transport);
                    break;

                case 3:
                    iconoMovimiento.setImageResource(R.drawable.ic_ocio);
                    break;

                case 4:
                    iconoMovimiento.setImageResource(R.drawable.ic_vivienda);
                    break;

                case 5:
                    iconoMovimiento.setImageResource(R.drawable.ic_salud);
                    break;

                case 6:
                    iconoMovimiento.setImageResource(R.drawable.ic_educacion);
                    break;

                case 7:
                    iconoMovimiento.setImageResource(R.drawable.ic_compras);
                    break;

                case 8:
                    iconoMovimiento.setImageResource(R.drawable.ic_otro_gasto);
                    break;

                case 9:
                    iconoMovimiento.setImageResource(R.drawable.ic_nomina);
                    break;

                case 10:
                    iconoMovimiento.setImageResource(R.drawable.ic_beca);
                    break;

                case 11:
                    iconoMovimiento.setImageResource(R.drawable.ic_regalo);
                    break;

                case 12:
                    iconoMovimiento.setImageResource(R.drawable.ic_venta);
                    break;

                case 13:
                    iconoMovimiento.setImageResource(R.drawable.ic_otros_ingresos);
                    break;

                case 14:
                    iconoMovimiento.setImageResource(R.drawable.ic_saldo_inicial_ingreso);
                    break;

                case 15:
                    iconoMovimiento.setImageResource(R.drawable.ic_saldo_incial_gasto);
                    break;

                default:
                    iconoMovimiento.setImageResource(R.drawable.ic_gasto);
                    break;
            }


            // TÍTULO

            campoTituloMovimiento.setText(categoria);

            // SUBTÍTULO

            String extra = "";

            if (descripcion != null && !descripcion.trim().isEmpty()) {
                extra = " · " + descripcion;
            }

            campoFechaMovimiento.setText(fecha + extra);


            // IMPORTE

            if (tipo.equals("gasto")) {

                campoImporteMovimiento.setText("-" + cantidad + " €");
                campoImporteMovimiento.setTextColor(getColor(R.color.gasto));
                iconoMovimiento.setBackgroundResource(R.drawable.bg_gasto);

            } else {

                campoImporteMovimiento.setText("+" + cantidad + " €");
                campoImporteMovimiento.setTextColor(getColor(R.color.ingreso));
                iconoMovimiento.setBackgroundResource(R.drawable.bg_ingreso);
            }
        }

        cursor.close();
    }
}