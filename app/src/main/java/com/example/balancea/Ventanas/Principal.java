package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Principal extends AppCompatActivity {

    TextView campoSaludo;

    TextView botonEsteMes;
    TextView botonGeneral;

    TextView campoIngresosResumen;
    TextView campoGastosResumen;

    PieChart graficaResumen;

    TextView campoPorcentajeBalancePrincipal;

    // MOVIMIENTO 1

    ImageView iconoMovimiento;

    TextView campoTituloMovimiento;

    TextView campoFechaMovimiento;

    TextView campoImporteMovimiento;

    // MOVIMIENTO 2

    ImageView iconoMovimiento2;

    TextView campoTituloMovimiento2;

    TextView campoFechaMovimiento2;

    TextView campoImporteMovimiento2;

    ConstraintLayout layoutMovimiento2;

    private boolean salir = false;

    // BOTON AGREGAR

    FloatingActionButton botonAgregarMovimientoPrincipal;

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


        // MOVIMIENTO 1

        iconoMovimiento = findViewById(R.id.iconoMovimiento);

        campoTituloMovimiento = findViewById(R.id.campoTituloMovimiento);

        campoFechaMovimiento = findViewById(R.id.campoFechaMovimiento);

        campoImporteMovimiento = findViewById(R.id.campoImporteMovimiento);


        // MOVIMIENTO 2

        iconoMovimiento2 = findViewById(R.id.iconoMovimiento2);

        campoTituloMovimiento2 = findViewById(R.id.campoTituloMovimiento2);

        campoFechaMovimiento2 = findViewById(R.id.campoFechaMovimiento2);

        campoImporteMovimiento2 = findViewById(R.id.campoImporteMovimiento2);

        layoutMovimiento2 = findViewById(R.id.layoutMovimiento2);

        // BOTON AGREGAR

        botonAgregarMovimientoPrincipal = findViewById(R.id.botonAgregarMovimientoPrincipal);


        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);



        // CLICK BOTON AGREGAR

        botonAgregarMovimientoPrincipal.setOnClickListener(v -> {

            Intent intent = new Intent(this, AgregarMovimiento.class);

            startActivity(intent);
        });



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


        // INGRESOS Y GASTOS

        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);

        double gastos = Consultas.obtenerGastos(this, CorreoActual);

        campoIngresosCardPrincipal.setText(ingresos + " €");

        campoGastosCardPrincipal.setText(gastos + " €");



        // CARGAR GRAFICA

        cargarGrafica(true);



        // CARGAR ULTIMOS MOVIMIENTOS

        cargarUltimosMovimientos();



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

                finish();

                return true;
            }

            if (id == R.id.nav_stats) {

                startActivity(new Intent(this, Graficas.class));

                overridePendingTransition(0, 0);

                finish();

                return true;
            }

            return false;
        });

        bottomNavigation.setOnItemReselectedListener(item -> {

            // no hacer nada
        });


        // PULSAR DOS VECES PARA SALIR DE LA APLICACION

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {

                if (salir) {

                    finishAffinity();

                } else {

                    salir = true;

                    Toast toast = Toast.makeText(
                            Principal.this,
                            "Pulse otra vez para salir",
                            Toast.LENGTH_SHORT
                    );

                    toast.setGravity(Gravity.CENTER, 0, 0);

                    toast.show();

                    new android.os.Handler().postDelayed(() -> salir = false, 2000);
                }
            }
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

        if (ingresos <= 0) {

            porcentajeBalance = 0;

        } else {

            porcentajeBalance = ((ingresos - gastos) / ingresos) * 100;
        }

        String textoPorcentaje;

        if (porcentajeBalance > 0) {

            textoPorcentaje = "+" + (int) porcentajeBalance + "%";

        } else {

            textoPorcentaje = (int) porcentajeBalance + "%";
        }

        campoPorcentajeBalancePrincipal.setText(textoPorcentaje);

        if (porcentajeBalance >= 0) {

            campoPorcentajeBalancePrincipal.setTextColor(getColor(R.color.ingreso));

        } else {

            campoPorcentajeBalancePrincipal.setTextColor(getColor(R.color.gasto));
        }



        // GRAFICA VACIA

        if (ingresos == 0 && gastos == 0) {

            graficaResumen.clear();

            return;
        }



        ArrayList<PieEntry> entradas = new ArrayList<>();

        entradas.add(new PieEntry((float) ingresos, "Ingresos"));

        entradas.add(new PieEntry((float) gastos, "Gastos"));



        PieDataSet dataSet =
                new PieDataSet(entradas, "");



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



    private void cargarUltimosMovimientos() {

        layoutMovimiento2.setVisibility(android.view.View.GONE);

        campoTituloMovimiento.setText("-");
        campoFechaMovimiento.setText("-");
        campoImporteMovimiento.setText("-");

        campoTituloMovimiento2.setText("-");
        campoFechaMovimiento2.setText("-");
        campoImporteMovimiento2.setText("-");

        Cursor cursor = Consultas.obtenerUltimoMovimiento(this, CorreoActual);


        int contador = 0;


        while (cursor.moveToNext() && contador < 2) {

            String tipo = cursor.getString(0);

            String descripcion = cursor.getString(1);

            double cantidad = cursor.getDouble(2);

            String fecha = cursor.getString(3);

            int idCategoria = cursor.getInt(4);

            String categoria = cursor.getString(5);


            ImageView icono;

            TextView titulo;

            TextView fechaTexto;

            TextView importe;


            if (contador == 0) {

                icono = iconoMovimiento;

                titulo = campoTituloMovimiento;

                fechaTexto = campoFechaMovimiento;

                importe = campoImporteMovimiento;

            } else {

                layoutMovimiento2.setVisibility(android.view.View.VISIBLE);

                layoutMovimiento2.setVisibility(View.VISIBLE);

                icono = iconoMovimiento2;

                titulo = campoTituloMovimiento2;

                fechaTexto = campoFechaMovimiento2;

                importe = campoImporteMovimiento2;
            }



            // ICONOS

            switch (idCategoria) {

                case 1:
                    icono.setImageResource(R.drawable.ic_comida);
                    break;

                case 2:
                    icono.setImageResource(R.drawable.ic_transport);
                    break;

                case 3:
                    icono.setImageResource(R.drawable.ic_ocio);
                    break;

                case 4:
                    icono.setImageResource(R.drawable.ic_vivienda);
                    break;

                case 5:
                    icono.setImageResource(R.drawable.ic_salud);
                    break;

                case 6:
                    icono.setImageResource(R.drawable.ic_educacion);
                    break;

                case 7:
                    icono.setImageResource(R.drawable.ic_compras);
                    break;

                case 8:
                    icono.setImageResource(R.drawable.ic_otro_gasto);
                    break;

                case 9:
                    icono.setImageResource(R.drawable.ic_nomina);
                    break;

                case 10:
                    icono.setImageResource(R.drawable.ic_beca);
                    break;

                case 11:
                    icono.setImageResource(R.drawable.ic_regalo);
                    break;

                case 12:
                    icono.setImageResource(R.drawable.ic_venta);
                    break;

                case 13:
                    icono.setImageResource(R.drawable.ic_otros_ingresos);
                    break;

                case 14:
                    icono.setImageResource(R.drawable.ic_saldo_inicial_ingreso);
                    break;

                case 15:
                    icono.setImageResource(R.drawable.ic_saldo_incial_gasto);
                    break;

                default:
                    icono.setImageResource(R.drawable.ic_gasto);
                    break;
            }



            // TITULO

            titulo.setText(categoria);



            // FECHA

            String extra = "";

            if (descripcion != null && !descripcion.trim().isEmpty()) {

                extra = " · " + descripcion;
            }

            fechaTexto.setText(fecha + extra);



            // IMPORTE

            if (tipo.equals("gasto")) {

                importe.setText("-" + cantidad + " €");

                importe.setTextColor(getColor(R.color.gasto));

                icono.setBackgroundResource(R.drawable.bg_gasto);

            } else {

                importe.setText("+" + cantidad + " €");

                importe.setTextColor(getColor(R.color.ingreso));

                icono.setBackgroundResource(R.drawable.bg_ingreso);
            }


            contador++;
        }


        // SI SOLO HAY 1 MOVIMIENTO

        if (contador < 2) {

            layoutMovimiento2.setVisibility(View.GONE);
        }


        cursor.close();
    }

    // REFRESCAR LA VENTANA
    @Override
    protected void onResume() {

        super.onResume();


        double saldo = Consultas.obtenerSaldoActual(this, CorreoActual);

        TextView campoSaldoActualPrincipal = findViewById(R.id.campoSaldoActualPrincipal);

        campoSaldoActualPrincipal.setText(saldo + " €");


        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);

        double gastos = Consultas.obtenerGastos(this, CorreoActual);

        TextView campoIngresosCardPrincipal = findViewById(R.id.campoIngresosCardPrincipal);

        TextView campoGastosCardPrincipal = findViewById(R.id.campoGastosCardPrincipal);

        campoIngresosCardPrincipal.setText(ingresos + " €");

        campoGastosCardPrincipal.setText(gastos + " €");


        cargarGrafica(botonEsteMes.getCurrentTextColor() == getColor(R.color.tarjetas_cards));


        cargarUltimosMovimientos();
    }
}