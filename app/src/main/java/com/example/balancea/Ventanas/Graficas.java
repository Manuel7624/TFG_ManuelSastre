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

/**
 * Actividad que gestiona la visualización de estadísticas mediante gráficos.
 * Muestra el balance total, la distribución por categorías y la evolución mensual.
 */
public class Graficas extends AppCompatActivity {

    /** TextViews para mostrar el balance monetario y un resumen textual. */
    TextView campoBalance, campoResumenBalance;

    /** Botones de filtro para alternar la gráfica de categorías entre gastos e ingresos. */
    TextView botonGastos, botonIngresos;

    /** Gráfico circular para la distribución por categorías. */
    PieChart graficaCategorias;
    /** Gráfico de barras para la evolución mensual comparativa. */
    BarChart graficaMensual;

    /** Contenedor dinámico para la leyenda personalizada de la gráfica de categorías. */
    LinearLayout layoutLeyenda;

    /** TextViews que muestran los valores máximos históricos detectados. */
    TextView textValorMayorGasto, textValorMayorIngreso;

    /** Estado del filtro de la gráfica de categorías. */
    boolean mostrandoGastos = true;

    /**
     * Inicializa la interfaz, configura las gráficas y carga los datos desde la BD.
     * @param savedInstanceState Estado guardado de la instancia.
     */
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

        // Componentes
        campoBalance = findViewById(R.id.campoBalance);
        campoResumenBalance = findViewById(R.id.textResumenBalance);
        botonGastos = findViewById(R.id.botonGastos);
        botonIngresos = findViewById(R.id.botonIngresos);
        graficaCategorias = findViewById(R.id.graficaCategorias);
        graficaMensual = findViewById(R.id.graficaMensual);
        layoutLeyenda = findViewById(R.id.layoutLeyenda);
        textValorMayorGasto = findViewById(R.id.textValorMayorGasto);
        textValorMayorIngreso = findViewById(R.id.textValorMayorIngreso);

        configurarEsteticaGraficas();

        int añoActual = Calendar.getInstance().get(Calendar.YEAR);
        ((TextView)findViewById(R.id.textEvolucion)).setText("Evolucion mensual " + añoActual);

        // Carga inicial
        cargarBalance();
        cargarGraficaCategorias(true);
        cargarGraficaMensual();
        cargarMayoresMovimientos();

        // Control de botones de filtro
        botonGastos.setOnClickListener(v -> {
            mostrandoGastos = true;
            cambiarEstiloFiltro(botonGastos, botonIngresos);
            cargarGraficaCategorias(true);
        });

        botonIngresos.setOnClickListener(v -> {
            mostrandoGastos = false;
            cambiarEstiloFiltro(botonIngresos, botonGastos);
            cargarGraficaCategorias(false);
        });

        // Configuración de navegación
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
    }

    /**
     * Establece los parámetros visuales para los gráficos de la pantalla.
     */
    private void configurarEsteticaGraficas() {
        // PieChart
        graficaCategorias.getDescription().setEnabled(false);
        graficaCategorias.setDrawHoleEnabled(true);
        graficaCategorias.setHoleRadius(72f);
        graficaCategorias.setTransparentCircleRadius(0f);
        graficaCategorias.getLegend().setEnabled(false);
        graficaCategorias.setRotationEnabled(false);
        graficaCategorias.setDrawEntryLabels(false);
        graficaCategorias.setHighlightPerTapEnabled(false);

        // BarChart
        graficaMensual.getDescription().setText("");
        graficaMensual.getLegend().setEnabled(false);
        graficaMensual.setFitBars(true);
        graficaMensual.setDrawGridBackground(false);
        graficaMensual.setScaleEnabled(false);
        graficaMensual.setPinchZoom(false);
        graficaMensual.getAxisRight().setEnabled(false);
        graficaMensual.getAxisLeft().setTextColor(getColor(R.color.texto_secundario));
        graficaMensual.getAxisLeft().setDrawGridLines(false);
        graficaMensual.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        graficaMensual.getXAxis().setDrawGridLines(false);
        graficaMensual.getXAxis().setTextColor(getColor(R.color.texto_secundario));
        graficaMensual.getXAxis().setGranularity(1f);
    }

    /**
     * Cambia el estilo de los botones para indicar cuál está activo.
     * @param activo Botón seleccionado.
     * @param inactivo Botón no seleccionado.
     */
    private void cambiarEstiloFiltro(TextView activo, TextView inactivo) {
        activo.setBackgroundResource(R.drawable.tab_activo);
        inactivo.setBackgroundResource(R.drawable.tab_inactivo);
        activo.setTextColor(Color.WHITE);
        inactivo.setTextColor(getColor(R.color.texto_principal));
    }

    /**
     * Calcula y muestra el balance total y el mensaje de resumen correspondiente.
     */
    private void cargarBalance() {
        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);
        double gastos = Consultas.obtenerGastos(this, CorreoActual);
        double balance = ingresos - gastos;
        campoBalance.setText(String.format("%.2f €", balance));
        campoBalance.setTextColor(getColor(balance >= 0 ? R.color.ingreso : R.color.gasto));
        campoResumenBalance.setText(balance >= 0 ? "Tus ingresos superan tus gastos" : "Tus gastos superan tus ingresos");
    }

    /**
     * Genera la gráfica circular y su leyenda personalizada basada en las categorías.
     * @param gastos true para mostrar distribución de gastos, false para ingresos.
     */
    private void cargarGraficaCategorias(boolean gastos) {
        ArrayList<PieEntry> entradas = new ArrayList<>();
        layoutLeyenda.removeAllViews();
        Cursor cursor = gastos ? Consultas.obtenerGastosPorCategoria(this, CorreoActual) : Consultas.obtenerIngresosPorCategoria(this, CorreoActual);

        int[] colores = {Color.parseColor("#4A90E2"), Color.parseColor("#E74C3C"), Color.parseColor("#F39C12"),
                         Color.parseColor("#27AE60"), Color.parseColor("#9B59B6"), Color.parseColor("#1ABC9C"), Color.parseColor("#E67E22")};

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
            graficaCategorias.setNoDataText("Sin movimientos");
            graficaCategorias.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entradas, "");
        ArrayList<Integer> listaColores = new ArrayList<>();
        for (int i = 0; i < entradas.size(); i++) listaColores.add(colores[i % colores.length]);

        dataSet.setColors(listaColores);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(4f);
        graficaCategorias.setData(new PieData(dataSet));
        graficaCategorias.animateY(500);
        graficaCategorias.invalidate();
    }

    /**
     * Genera la gráfica de barras con la comparativa mensual de ingresos y gastos.
     */
    private void cargarGraficaMensual() {
        ArrayList<BarEntry> eIngresos = new ArrayList<>(), eGastos = new ArrayList<>();
        ArrayList<String> etiquetas = new ArrayList<>();
        Cursor cursor = Consultas.obtenerEvolucionMensual(this, CorreoActual);
        int pos = 0;

        while (cursor.moveToNext()) {
            eIngresos.add(new BarEntry(pos, (float) cursor.getDouble(1)));
            eGastos.add(new BarEntry(pos, (float) cursor.getDouble(2)));
            etiquetas.add(convertirMes(cursor.getString(0)));
            pos++;
        }
        cursor.close();

        if (etiquetas.isEmpty()) {
            graficaMensual.clear();
            graficaMensual.setNoDataText("Sin movimientos");
            graficaMensual.invalidate();
            return;
        }

        BarDataSet dsIngresos = new BarDataSet(eIngresos, "Ingresos");
        dsIngresos.setColor(Color.parseColor("#4A90E2"));
        dsIngresos.setDrawValues(false);

        BarDataSet dsGastos = new BarDataSet(eGastos, "Gastos");
        dsGastos.setColor(Color.parseColor("#E74C3C"));
        dsGastos.setDrawValues(false);

        BarData barData = new BarData(dsIngresos, dsGastos);
        float groupSpace = 0.4f, barSpace = 0.02f, barWidth = 0.28f;
        barData.setBarWidth(barWidth);

        graficaMensual.setData(barData);
        graficaMensual.getXAxis().setAxisMinimum(0f);
        graficaMensual.getXAxis().setAxisMaximum(0f + barData.getGroupWidth(groupSpace, barSpace) * etiquetas.size());
        graficaMensual.groupBars(0f, groupSpace, barSpace);

        graficaMensual.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                return (index >= 0 && index < etiquetas.size()) ? etiquetas.get(index) : "";
            }
        });
        graficaMensual.getXAxis().setCenterAxisLabels(true);
        graficaMensual.getXAxis().setGranularity(1f);
        graficaMensual.getXAxis().setLabelCount(etiquetas.size());
        graficaMensual.animateY(600);
        graficaMensual.invalidate();
    }

    /**
     * Convierte el número de mes (01-12) en su abreviatura textual.
     * @param num Mes en formato numérico.
     * @return Nombre abreviado del mes.
     */
    private String convertirMes(String num) {
        switch (num) {
            case "01": return "Ene"; case "02": return "Feb"; case "03": return "Mar";
            case "04": return "Abr"; case "05": return "May"; case "06": return "Jun";
            case "07": return "Jul"; case "08": return "Ago"; case "09": return "Sep";
            case "10": return "Oct"; case "11": return "Nov"; case "12": return "Dic";
            default: return "";
        }
    }

    /**
     * Carga y muestra los hitos de mayor gasto e ingreso histórico.
     */
    private void cargarMayoresMovimientos() {
        textValorMayorGasto.setText(Consultas.obtenerMayorGasto(this, CorreoActual));
        textValorMayorIngreso.setText(Consultas.obtenerMayorIngreso(this, CorreoActual));
    }

    /**
     * Refresca todos los datos de las gráficas al reanudar la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarBalance();
        cargarGraficaCategorias(mostrandoGastos);
        cargarGraficaMensual();
        cargarMayoresMovimientos();
    }
}
