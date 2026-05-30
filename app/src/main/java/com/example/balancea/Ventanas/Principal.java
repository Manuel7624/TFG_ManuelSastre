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

/**
 * Actividad principal de la aplicación que muestra el resumen financiero del usuario.
 * Incluye el saldo actual, un gráfico de balance de ingresos/gastos y los últimos movimientos.
 */
public class Principal extends AppCompatActivity {

    /** TextView para mostrar el saludo personalizado al usuario. */
    TextView campoSaludo;

    /** Botones de filtro para la gráfica de resumen (Este mes / General). */
    TextView botonEsteMes, botonGeneral;

    /** TextViews para mostrar los importes de ingresos y gastos en el resumen. */
    TextView campoIngresosResumen, campoGastosResumen;

    /** Gráfico circular que representa la proporción de ingresos y gastos. */
    PieChart graficaResumen;

    /** TextView que muestra el porcentaje de ahorro o déficit. */
    TextView campoPorcentajeBalancePrincipal;

    // MOVIMIENTO 1
    /** Componentes visuales para el primer movimiento de la lista rápida. */
    ImageView iconoMovimiento;
    TextView campoTituloMovimiento, campoFechaMovimiento, campoImporteMovimiento;

    // MOVIMIENTO 2
    /** Componentes visuales para el segundo movimiento de la lista rápida. */
    ImageView iconoMovimiento2;
    TextView campoTituloMovimiento2, campoFechaMovimiento2, campoImporteMovimiento2;
    /** Layout contenedor del segundo movimiento, se oculta si no existe. */
    ConstraintLayout layoutMovimiento2;

    /** Variable para controlar el doble toque al salir de la aplicación. */
    private boolean salir = false;

    /** Botón flotante para abrir la ventana de agregar nuevo movimiento. */
    FloatingActionButton botonAgregarMovimientoPrincipal;

    /**
     * Inicializa la actividad, configura la interfaz y carga los datos iniciales.
     * @param savedInstanceState Estado guardado de la instancia.
     */
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

        // Inicialización de componentes
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

        // Movimiento 1
        iconoMovimiento = findViewById(R.id.iconoMovimiento);
        campoTituloMovimiento = findViewById(R.id.campoTituloMovimiento);
        campoFechaMovimiento = findViewById(R.id.campoFechaMovimiento);
        campoImporteMovimiento = findViewById(R.id.campoImporteMovimiento);

        // Movimiento 2
        iconoMovimiento2 = findViewById(R.id.iconoMovimiento2);
        campoTituloMovimiento2 = findViewById(R.id.campoTituloMovimiento2);
        campoFechaMovimiento2 = findViewById(R.id.campoFechaMovimiento2);
        campoImporteMovimiento2 = findViewById(R.id.campoImporteMovimiento2);
        layoutMovimiento2 = findViewById(R.id.layoutMovimiento2);

        botonAgregarMovimientoPrincipal = findViewById(R.id.botonAgregarMovimientoPrincipal);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // Evento para agregar movimiento
        botonAgregarMovimientoPrincipal.setOnClickListener(v -> {
            startActivity(new Intent(this, AgregarMovimiento.class));
        });

        // Configuración estética de la gráfica
        configurarEsteticaGrafica();

        // Carga de datos iniciales
        String nombreUsuario = Consultas.obtenerNombreUsuario(this, CorreoActual);
        campoSaludo.setText("Hola, " + nombreUsuario + " 👋");

        perfilUsuario.setOnClickListener(v -> {
            startActivity(new Intent(Principal.this, PerfilUsuario.class));
        });

        double saldo = Consultas.obtenerSaldoActual(this, CorreoActual);
        campoSaldoActualPrincipal.setText(saldo + " €");

        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);
        double gastos = Consultas.obtenerGastos(this, CorreoActual);
        campoIngresosCardPrincipal.setText(ingresos + " €");
        campoGastosCardPrincipal.setText(gastos + " €");

        cargarGrafica(true);
        cargarUltimosMovimientos();

        // Control de filtros de la gráfica
        botonEsteMes.setOnClickListener(v -> {
            marcarBotonActivo(botonEsteMes, botonGeneral);
            cargarGrafica(true);
        });

        botonGeneral.setOnClickListener(v -> {
            marcarBotonActivo(botonGeneral, botonEsteMes);
            cargarGrafica(false);
        });

        // Navegación inferior
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
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

        // Control de salida de la app
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (salir) {
                    finishAffinity();
                } else {
                    salir = true;
                    Toast toast = Toast.makeText(Principal.this, "Pulse otra vez para salir", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    new android.os.Handler().postDelayed(() -> salir = false, 2000);
                }
            }
        });
    }

    /**
     * Configura los parámetros visuales iniciales de la gráfica circular.
     */
    private void configurarEsteticaGrafica() {
        graficaResumen.getDescription().setEnabled(false);
        graficaResumen.setDrawHoleEnabled(true);
        graficaResumen.setHoleRadius(82f);
        graficaResumen.setTransparentCircleRadius(0f);
        graficaResumen.getLegend().setEnabled(false);
        graficaResumen.setRotationEnabled(false);
        graficaResumen.setHighlightPerTapEnabled(false);
    }

    /**
     * Cambia el estilo visual de los botones de filtro para indicar cuál está activo.
     * @param activo Botón pulsado.
     * @param inactivo Botón que queda en segundo plano.
     */
    private void marcarBotonActivo(TextView activo, TextView inactivo) {
        activo.setBackgroundResource(R.drawable.tab_activo);
        inactivo.setBackgroundResource(R.drawable.tab_inactivo);
        activo.setTextColor(getColor(R.color.tarjetas_cards));
        inactivo.setTextColor(getColor(R.color.texto_principal));
    }

    /**
     * Carga y procesa los datos de la gráfica de resumen.
     * @param soloEsteMes true para filtrar por el mes actual, false para datos totales.
     */
    private void cargarGrafica(boolean soloEsteMes) {
        double ingresos = soloEsteMes ? Consultas.obtenerIngresosMesActual(this, CorreoActual) : Consultas.obtenerIngresos(this, CorreoActual);
        double gastos = soloEsteMes ? Consultas.obtenerGastosMesActual(this, CorreoActual) : Consultas.obtenerGastos(this, CorreoActual);

        campoIngresosResumen.setText(ingresos + " €");
        campoGastosResumen.setText(gastos + " €");

        double porcentajeBalance = (ingresos <= 0) ? 0 : ((ingresos - gastos) / ingresos) * 100;
        String textoPorcentaje = (porcentajeBalance > 0 ? "+" : "") + (int) porcentajeBalance + "%";
        campoPorcentajeBalancePrincipal.setText(textoPorcentaje);
        campoPorcentajeBalancePrincipal.setTextColor(getColor(porcentajeBalance >= 0 ? R.color.ingreso : R.color.gasto));

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
        dataSet.setDrawValues(false);
        graficaResumen.setDrawEntryLabels(false);
        graficaResumen.setData(new PieData(dataSet));
        graficaResumen.animateY(500);
        graficaResumen.invalidate();
    }

    /**
     * Recupera y muestra en la interfaz los dos últimos movimientos del usuario.
     */
    private void cargarUltimosMovimientos() {
        layoutMovimiento2.setVisibility(View.GONE);
        limpiarCamposMovimientos();

        Cursor cursor = Consultas.obtenerUltimoMovimiento(this, CorreoActual);
        int contador = 0;

        while (cursor.moveToNext() && contador < 2) {
            asignarDatosMovimiento(cursor, contador);
            contador++;
        }
        cursor.close();
    }

    /**
     * Limpia el texto de los campos de movimientos previos a la carga.
     */
    private void limpiarCamposMovimientos() {
        campoTituloMovimiento.setText("-"); campoFechaMovimiento.setText("-"); campoImporteMovimiento.setText("-");
        campoTituloMovimiento2.setText("-"); campoFechaMovimiento2.setText("-"); campoImporteMovimiento2.setText("-");
    }

    /**
     * Asigna los datos de un movimiento desde un cursor a la interfaz gráfica.
     * @param cursor Cursor con los datos de la base de datos.
     * @param index Índice del movimiento (0 para el primero, 1 para el segundo).
     */
    private void asignarDatosMovimiento(Cursor cursor, int index) {
        String tipo = cursor.getString(0);
        String descripcion = cursor.getString(1);
        double cantidad = cursor.getDouble(2);
        String fecha = cursor.getString(3);
        int idCat = cursor.getInt(4);
        String catNom = cursor.getString(5);

        ImageView icono = (index == 0) ? iconoMovimiento : iconoMovimiento2;
        TextView titulo = (index == 0) ? campoTituloMovimiento : campoTituloMovimiento2;
        TextView fTxt = (index == 0) ? campoFechaMovimiento : campoFechaMovimiento2;
        TextView imp = (index == 0) ? campoImporteMovimiento : campoImporteMovimiento2;

        if (index == 1) layoutMovimiento2.setVisibility(View.VISIBLE);

        establecerIconoCategoria(icono, idCat);
        titulo.setText(catNom);
        fTxt.setText(fecha + (descripcion != null && !descripcion.trim().isEmpty() ? " · " + descripcion : ""));
        
        if (tipo.equals("gasto")) {
            imp.setText("-" + cantidad + " €");
            imp.setTextColor(getColor(R.color.gasto));
            icono.setBackgroundResource(R.drawable.bg_gasto);
        } else {
            imp.setText("+" + cantidad + " €");
            imp.setTextColor(getColor(R.color.ingreso));
            icono.setBackgroundResource(R.drawable.bg_ingreso);
        }
    }

    /**
     * Asigna el recurso de imagen correspondiente según el ID de la categoría.
     * @param iv ImageView donde se cargará el icono.
     * @param id ID de la categoría.
     */
    private void establecerIconoCategoria(ImageView iv, int id) {
        switch (id) {
            case 1: iv.setImageResource(R.drawable.ic_comida); break;
            case 2: iv.setImageResource(R.drawable.ic_transport); break;
            case 3: iv.setImageResource(R.drawable.ic_ocio); break;
            case 4: iv.setImageResource(R.drawable.ic_vivienda); break;
            case 5: iv.setImageResource(R.drawable.ic_salud); break;
            case 6: iv.setImageResource(R.drawable.ic_educacion); break;
            case 7: iv.setImageResource(R.drawable.ic_compras); break;
            case 8: iv.setImageResource(R.drawable.ic_otro_gasto); break;
            case 9: iv.setImageResource(R.drawable.ic_nomina); break;
            case 10: iv.setImageResource(R.drawable.ic_beca); break;
            case 11: iv.setImageResource(R.drawable.ic_regalo); break;
            case 12: iv.setImageResource(R.drawable.ic_venta); break;
            case 13: iv.setImageResource(R.drawable.ic_otros_ingresos); break;
            case 14: iv.setImageResource(R.drawable.ic_saldo_inicial_ingreso); break;
            case 15: iv.setImageResource(R.drawable.ic_saldo_incial_gasto); break;
            default: iv.setImageResource(R.drawable.ic_gasto); break;
        }
    }

    /**
     * Refresca los datos de la actividad cuando el usuario vuelve a ella.
     */
    @Override
    protected void onResume() {
        super.onResume();
        actualizarInterfazCompleta();
    }

    /**
     * Método auxiliar para refrescar todos los campos de datos de la pantalla.
     */
    private void actualizarInterfazCompleta() {
        double saldo = Consultas.obtenerSaldoActual(this, CorreoActual);
        ((TextView)findViewById(R.id.campoSaldoActualPrincipal)).setText(saldo + " €");

        double ingresos = Consultas.obtenerIngresos(this, CorreoActual);
        double gastos = Consultas.obtenerGastos(this, CorreoActual);
        ((TextView)findViewById(R.id.campoIngresosCardPrincipal)).setText(ingresos + " €");
        ((TextView)findViewById(R.id.campoGastosCardPrincipal)).setText(gastos + " €");

        cargarGrafica(botonEsteMes.getCurrentTextColor() == getColor(R.color.tarjetas_cards));
        cargarUltimosMovimientos();
    }
}
