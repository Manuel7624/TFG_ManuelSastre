package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class Graficas extends AppCompatActivity {

    TextView campoBalance;
    TextView campoResumenBalance;

    TextView botonGastos;
    TextView botonIngresos;

    PieChart graficaCategorias;
    BarChart graficaMensual;

    LinearLayout layoutLeyenda;

    TextView textValorMayorGasto;
    TextView textValorMayorIngreso;

    boolean mostrandoGastos = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graficas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // COMPONENTES

        campoBalance = findViewById(R.id.campoBalance);

        campoResumenBalance = findViewById(R.id.textResumenBalance);

        botonGastos = findViewById(R.id.botonGastos);

        botonIngresos = findViewById(R.id.botonIngresos);

        graficaCategorias = findViewById(R.id.graficaCategorias);

        graficaMensual = findViewById(R.id.graficaMensual);

        layoutLeyenda = findViewById(R.id.layoutLeyenda);

        textValorMayorGasto = findViewById(R.id.textValorMayorGasto);

        textValorMayorIngreso = findViewById(R.id.textValorMayorIngreso);


        // CONFIG GRAFICA CATEGORIAS

        graficaCategorias.getDescription().setEnabled(false);

        graficaCategorias.setDrawHoleEnabled(true);

        graficaCategorias.setHoleRadius(72f);

        graficaCategorias.setTransparentCircleRadius(0f);

        graficaCategorias.getLegend().setEnabled(false);

        graficaCategorias.setRotationEnabled(false);

        graficaCategorias.setDrawEntryLabels(false);

        graficaCategorias.setHighlightPerTapEnabled(false);


        // CONFIG GRAFICA MENSUAL

        Description descripcion = new Description();

        descripcion.setText("");

        graficaMensual.setDescription(descripcion);

        graficaMensual.getLegend().setEnabled(false);

        graficaMensual.setFitBars(true);

        graficaMensual.setDrawGridBackground(false);

        graficaMensual.setScaleEnabled(false);

        graficaMensual.setPinchZoom(false);

        graficaMensual.setDoubleTapToZoomEnabled(false);

        graficaMensual.getAxisRight().setEnabled(false);

        graficaMensual.getAxisLeft().setTextColor(getColor(R.color.texto_secundario));

        graficaMensual.getAxisLeft().setDrawGridLines(false);

        graficaMensual.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        graficaMensual.getXAxis().setDrawGridLines(false);

        graficaMensual.getXAxis().setTextColor(getColor(R.color.texto_secundario));

        graficaMensual.getXAxis().setGranularity(1f);

        TextView textEvol = findViewById(R.id.textEvolucion);

        int añoActual = Calendar.getInstance().get(Calendar.YEAR);

        textEvol.setText("Evolucion mensual " + añoActual);

        // CARGAR DATOS

        cargarBalance();

        cargarGraficaCategorias(true);

        cargarGraficaMensual();

        cargarMayoresMovimientos();


        // BOTONES

        botonGastos.setOnClickListener(v -> {

            mostrandoGastos = true;

            botonGastos.setBackgroundResource(R.drawable.tab_activo);

            botonIngresos.setBackgroundResource(R.drawable.tab_inactivo);

            botonGastos.setTextColor(getColor(android.R.color.white));

            botonIngresos.setTextColor(getColor(R.color.texto_principal));

            cargarGraficaCategorias(true);
        });


        botonIngresos.setOnClickListener(v -> {

            mostrandoGastos = false;

            botonIngresos.setBackgroundResource(R.drawable.tab_activo);

            botonGastos.setBackgroundResource(R.drawable.tab_inactivo);

            botonIngresos.setTextColor(getColor(android.R.color.white));

            botonGastos.setTextColor(getColor(R.color.texto_principal));

            cargarGraficaCategorias(false);
        });


        // BOTTOM NAVIGATION

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.nav_stats);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, Principal.class));

                overridePendingTransition(0, 0);

                finish();

                return true;
            }

            if (id == R.id.nav_movimientos) {

                startActivity(new Intent(this, Movimientos.class));

                overridePendingTransition(0, 0);

                finish();

                return true;
            }

            return id == R.id.nav_stats;
        });

        bottomNavigation.setOnItemReselectedListener(item -> {

            // NO HACER NADA
        });
    }



    private void cargarBalance() {

        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);

        double gastos = Consultas.obtenerGastos(this, CorreoActual);

        double balance = ingresos - gastos;


        campoBalance.setText(String.format("%.2f €", balance));


        if (balance >= 0) {

            campoBalance.setTextColor(getColor(R.color.ingreso));

            campoResumenBalance.setText("Tus ingresos superan tus gastos");

        } else {

            campoBalance.setTextColor(getColor(R.color.gasto));

            campoResumenBalance.setText("Tus gastos superan tus ingresos");
        }
    }



    private void cargarGraficaCategorias(boolean gastos) {

        ArrayList<PieEntry> entradas = new ArrayList<>();

        layoutLeyenda.removeAllViews();

        Cursor cursor;


        if (gastos) {

            cursor = Consultas.obtenerGastosPorCategoria(this, CorreoActual);

        } else {

            cursor =
                    Consultas.obtenerIngresosPorCategoria(this, CorreoActual);
        }


        int[] colores = {

                Color.parseColor("#4A90E2"),
                Color.parseColor("#E74C3C"),
                Color.parseColor("#F39C12"),
                Color.parseColor("#27AE60"),
                Color.parseColor("#9B59B6"),
                Color.parseColor("#1ABC9C"),
                Color.parseColor("#E67E22")
        };


        int contador = 0;


        while (cursor.moveToNext()) {

            String categoria = cursor.getString(0);

            double total = cursor.getDouble(1);


            entradas.add(new PieEntry((float) total, categoria));


            TextView texto = new TextView(this);

            texto.setText("• " + categoria + "   " + String.format("%.2f €", total));

            texto.setTextSize(13);

            texto.setPadding(0, 6, 0, 0);

            texto.setTextColor(colores[contador % colores.length]);

            layoutLeyenda.addView(texto);

            contador++;
        }

        cursor.close();


        if (entradas.isEmpty()) {

            graficaCategorias.clear();

            graficaCategorias.setData(null);

            graficaCategorias.setNoDataText("Sin movimientos");

            graficaCategorias.invalidate();

            return;
        }


        PieDataSet dataSet = new PieDataSet(entradas, "");

        ArrayList<Integer> listaColores = new ArrayList<>();


        for (int i = 0; i < entradas.size(); i++) {

            listaColores.add(colores[i % colores.length]);
        }


        dataSet.setColors(listaColores);

        dataSet.setDrawValues(false);

        dataSet.setSliceSpace(4f);

        dataSet.setSelectionShift(0f);


        PieData pieData = new PieData(dataSet);

        graficaCategorias.setData(pieData);

        graficaCategorias.animateY(500);

        graficaCategorias.invalidate();
    }



    private void cargarGraficaMensual() {

        ArrayList<BarEntry> entradasIngresos = new ArrayList<>();

        ArrayList<BarEntry> entradasGastos = new ArrayList<>();

        ArrayList<String> etiquetasMeses = new ArrayList<>();

        boolean hayDatos = false;


        Cursor cursor = Consultas.obtenerEvolucionMensual(this, CorreoActual);


        int posicion = 0;


        while (cursor.moveToNext()) {

            hayDatos = true;

            String mesNumero = cursor.getString(0);

            float ingresos = (float) cursor.getDouble(1);

            float gastos = (float) cursor.getDouble(2);


            entradasIngresos.add(new BarEntry(posicion, ingresos));

            entradasGastos.add(new BarEntry(posicion, gastos));


            etiquetasMeses.add(convertirMes(mesNumero));

            posicion++;
        }

        cursor.close();


        if (!hayDatos) {

            graficaMensual.clear();

            graficaMensual.setNoDataText("Sin movimientos");

            graficaMensual.invalidate();

            return;
        }


        BarDataSet dataSetIngresos = new BarDataSet(entradasIngresos, "Ingresos");

        dataSetIngresos.setColor(Color.parseColor("#4A90E2"));

        dataSetIngresos.setDrawValues(false);


        BarDataSet dataSetGastos = new BarDataSet(entradasGastos, "Gastos");

        dataSetGastos.setColor(Color.parseColor("#E74C3C"));

        dataSetGastos.setDrawValues(false);


        BarData barData = new BarData(dataSetIngresos, dataSetGastos);

        float groupSpace = 0.4f;
        float barSpace = 0.02f;
        float barWidth = 0.28f;


        barData.setBarWidth(barWidth);


        graficaMensual.setData(barData);


        graficaMensual.getXAxis().setAxisMinimum(0f);

        graficaMensual.getXAxis().setAxisMaximum(0f + graficaMensual.getBarData().getGroupWidth(groupSpace, barSpace) * etiquetasMeses.size());


        graficaMensual.groupBars(0f, groupSpace, barSpace);



        graficaMensual.getXAxis().setValueFormatter(
                new ValueFormatter() {

                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {

                        int index = (int) value;

                        if (index >= 0 &&
                                index < etiquetasMeses.size()) {

                            return etiquetasMeses.get(index);
                        }

                        return "";
                    }
                });


        graficaMensual.getXAxis().setCenterAxisLabels(true);

        graficaMensual.getXAxis().setGranularity(1f);

        graficaMensual.getXAxis().setLabelCount(etiquetasMeses.size());

        graficaMensual.animateY(600);

        graficaMensual.invalidate();
    }

    private String convertirMes(String numeroMes) {

        switch (numeroMes) {

            case "01": return "Ene";
            case "02": return "Feb";
            case "03": return "Mar";
            case "04": return "Abr";
            case "05": return "May";
            case "06": return "Jun";
            case "07": return "Jul";
            case "08": return "Ago";
            case "09": return "Sep";
            case "10": return "Oct";
            case "11": return "Nov";
            case "12": return "Dic";
        }

        return "";
    }



    private void cargarMayoresMovimientos() {

        String mayorGasto = Consultas.obtenerMayorGasto(this, CorreoActual);

        String mayorIngreso = Consultas.obtenerMayorIngreso(this, CorreoActual);

        textValorMayorGasto.setText(mayorGasto);

        textValorMayorIngreso.setText(mayorIngreso);
    }



    @Override
    protected void onResume() {

        super.onResume();

        cargarBalance();

        cargarGraficaCategorias(mostrandoGastos);

        cargarGraficaMensual();

        cargarMayoresMovimientos();
    }
}