package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Adaptadores.AdaptadorMovimientos;
import com.example.balancea.Consultas;
import com.example.balancea.Modelos.Movimiento;
import com.example.balancea.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Movimientos extends AppCompatActivity {

    ListView listViewMovimientos;
    TextView botonTodos;
    TextView botonIngresos;
    TextView botonGastos;
    TextView botonOrdenFecha;
    EditText campoBuscar;
    FloatingActionButton botonAgregarMovimiento;
    ArrayList<Movimiento> lista;
    AdaptadorMovimientos adaptador;
    String filtroActual = "todos";
    String ordenActual = "fecha";
    TextView botonOrdenRecientes;

    private boolean salir = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movimientos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // COMPONENTES

        listViewMovimientos = findViewById(R.id.listaMovimientos);

        botonTodos = findViewById(R.id.filtroTodos);

        botonIngresos = findViewById(R.id.filtroIngresos);

        botonGastos = findViewById(R.id.filtroGastos);

        botonOrdenFecha = findViewById(R.id.filtroFecha);

        botonOrdenRecientes = findViewById(R.id.filtroRecientes);

        campoBuscar = findViewById(R.id.campoBuscarMovimiento);

        botonAgregarMovimiento = findViewById(R.id.botonAgregarMovimientoPrincipal);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);


        // CARGAR MOVIMIENTOS

        cargarMovimientos("", filtroActual, ordenActual);


        // FILTRO TODOS

        botonTodos.setOnClickListener(v -> {

            filtroActual = "todos";

            actualizarFiltros();

            cargarMovimientos(campoBuscar.getText().toString(), filtroActual, ordenActual);
        });


        // FILTRO INGRESOS

        botonIngresos.setOnClickListener(v -> {

            filtroActual = "ingreso";

            actualizarFiltros();

            cargarMovimientos(campoBuscar.getText().toString(), filtroActual, ordenActual);
        });


        // FILTRO GASTOS

        botonGastos.setOnClickListener(v -> {

            filtroActual = "gasto";

            actualizarFiltros();

            cargarMovimientos(campoBuscar.getText().toString(), filtroActual, ordenActual);
        });


        // ORDEN FECHA

        botonOrdenFecha.setOnClickListener(v -> {

            ordenActual = "fecha";

            actualizarOrden();

            cargarMovimientos(campoBuscar.getText().toString(), filtroActual, ordenActual);
        });

        botonOrdenRecientes.setOnClickListener(v -> {

            ordenActual = "recientes";

            actualizarOrden();

            cargarMovimientos(campoBuscar.getText().toString(), filtroActual, ordenActual);
        });


        // BUSCADOR

        campoBuscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                cargarMovimientos(s.toString(), filtroActual, ordenActual);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // BOTON AGREGAR MOVIMIENTO

        botonAgregarMovimiento.setOnClickListener(v -> {

            Intent intent = new Intent(this, AgregarMovimiento.class);

            startActivity(intent);
        });


        // NAVEGACION

        bottomNavigation.setSelectedItemId(R.id.nav_movimientos);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, Principal.class));

                overridePendingTransition(0, 0);

                finish();

                return true;
            }

            if (id == R.id.nav_movimientos) {

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
            // no hacer nada si ya estas en esta ventana
        });

        actualizarFiltros();

        actualizarOrden();

        // PULSAR DOS VECES PARA SALIR DE LA APLICACION

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {

                if (salir) {

                    finishAffinity();

                } else {

                    salir = true;

                    Toast toast = Toast.makeText(
                            Movimientos.this,
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


    private void cargarMovimientos(String busqueda, String filtro, String orden) {

        lista = new ArrayList<>();

        Cursor cursor = Consultas.obtenerMovimientos(
                this,
                CorreoActual,
                filtro,
                busqueda,
                orden
        );

        while(cursor.moveToNext()){

            lista.add(new Movimiento(

                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5)
            ));
        }

        cursor.close();

        adaptador = new AdaptadorMovimientos(this, lista);

        listViewMovimientos.setAdapter(adaptador);
    }


    private void actualizarFiltros() {

        botonTodos.setBackgroundResource(R.drawable.tab_inactivo);

        botonIngresos.setBackgroundResource(R.drawable.tab_inactivo);

        botonGastos.setBackgroundResource(R.drawable.tab_inactivo);


        botonTodos.setTextColor(getColor(R.color.texto_principal));

        botonIngresos.setTextColor(getColor(R.color.texto_principal));

        botonGastos.setTextColor(getColor(R.color.texto_principal));


        if(filtroActual.equals("todos")){

            botonTodos.setBackgroundResource(R.drawable.tab_activo);

            botonTodos.setTextColor(getColor(R.color.tarjetas_cards));

        }else if(filtroActual.equals("ingreso")){

            botonIngresos.setBackgroundResource(R.drawable.tab_activo);

            botonIngresos.setTextColor(getColor(R.color.tarjetas_cards));

        }else{

            botonGastos.setBackgroundResource(R.drawable.tab_activo);

            botonGastos.setTextColor(getColor(R.color.tarjetas_cards));
        }
    }


    private void actualizarOrden() {

        botonOrdenFecha.setBackgroundResource(R.drawable.tab_inactivo);

        botonOrdenRecientes.setBackgroundResource(R.drawable.tab_inactivo);

        botonOrdenFecha.setTextColor(getColor(R.color.texto_principal));

        botonOrdenRecientes.setTextColor(getColor(R.color.texto_principal));


        if(ordenActual.equals("fecha")){

            botonOrdenFecha.setBackgroundResource(R.drawable.tab_activo);

            botonOrdenFecha.setTextColor(getColor(R.color.tarjetas_cards));

        }else{

            botonOrdenRecientes.setBackgroundResource(R.drawable.tab_activo);

            botonOrdenRecientes.setTextColor(getColor(R.color.tarjetas_cards));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarMovimientos(campoBuscar.getText().toString(), filtroActual, ordenActual);
    }
}