package com.example.balancea.Ventanas;

import static com.example.balancea.Ventanas.MainActivity.CorreoActual;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.balancea.Consultas;
import com.example.balancea.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;

public class AgregarMovimiento extends AppCompatActivity {

    TextView botonIngreso;
    TextView botonGasto;

    EditText campoCantidad;
    EditText campoDescripcion;
    EditText campoFecha;

    Spinner spinnerCategoria;

    MaterialButton botonGuardarMovimiento;

    ImageView botonVolver;

    TextView campoError;

    String tipoActual = "ingreso";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_movimiento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // COMPONENTES

        botonIngreso = findViewById(R.id.botonIngreso);

        botonGasto = findViewById(R.id.botonGasto);

        campoCantidad = findViewById(R.id.campoCantidad);

        campoDescripcion = findViewById(R.id.campoDescripcion);

        campoFecha = findViewById(R.id.campoFecha);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        botonGuardarMovimiento =
                findViewById(R.id.botonGuardarMovimiento);

        botonVolver = findViewById(R.id.botonVolver);

        campoError =
                findViewById(R.id.campoErrorAgregarMovimiento);


        campoError.setVisibility(View.GONE);


        // CARGAR CATEGORIAS

        cargarCategorias();


        // BOTON INGRESO

        botonIngreso.setOnClickListener(v -> {

            tipoActual = "ingreso";

            actualizarTabs();

            cargarCategorias();
        });


        // BOTON GASTO

        botonGasto.setOnClickListener(v -> {

            tipoActual = "gasto";

            actualizarTabs();

            cargarCategorias();
        });


        // FECHA

        campoFecha.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            int year = calendario.get(Calendar.YEAR);

            int month = calendario.get(Calendar.MONTH);

            int day = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(

                    this,

                    (view, year1, month1, dayOfMonth) -> {

                        String fecha =
                                year1 + "-" +
                                        String.format("%02d", month1 + 1) + "-" +
                                        String.format("%02d", dayOfMonth);

                        campoFecha.setText(fecha);

                    },

                    year,
                    month,
                    day
            );

            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            dialog.show();
        });


        // GUARDAR MOVIMIENTO

        botonGuardarMovimiento.setOnClickListener(v -> {

            campoError.setVisibility(View.GONE);


            if(campoCantidad.getText().toString().isEmpty()
                    || campoFecha.getText().toString().isEmpty()
                    || spinnerCategoria.getSelectedItem() == null){

                campoError.setText(R.string.CamposObligatorios);

                campoError.setVisibility(View.VISIBLE);

                return;
            }


            double cantidad = Double.parseDouble(
                    campoCantidad.getText().toString()
            );

            String descripcion =
                    campoDescripcion.getText().toString();

            String fecha =
                    campoFecha.getText().toString();

            String categoria =
                    spinnerCategoria.getSelectedItem().toString();


            int idCategoria =
                    Consultas.obtenerIdCategoria(
                            this,
                            categoria
                    );


            boolean resultado =
                    Consultas.insertarMovimiento(

                            this,

                            CorreoActual,

                            idCategoria,

                            tipoActual,

                            cantidad,

                            descripcion,

                            fecha
                    );


            if(resultado){

                finish();

            }else{

                campoError.setText("Error al guardar movimiento");

                campoError.setVisibility(View.VISIBLE);
            }
        });


        // VOLVER

        botonVolver.setOnClickListener(v -> finish());
    }


    private void cargarCategorias() {

        ArrayList<String> lista =
                Consultas.obtenerCategorias(
                        this,
                        tipoActual
                );


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_spinner_dropdown_item,

                        lista
                );

        spinnerCategoria.setAdapter(adapter);
    }


    private void actualizarTabs() {

        botonIngreso.setBackgroundResource(R.drawable.tab_inactivo);

        botonGasto.setBackgroundResource(R.drawable.tab_inactivo);


        botonIngreso.setTextColor(getColor(R.color.texto_principal));

        botonGasto.setTextColor(getColor(R.color.texto_principal));


        if(tipoActual.equals("ingreso")){

            botonIngreso.setBackgroundResource(R.drawable.tab_activo);

            botonIngreso.setTextColor(getColor(R.color.tarjetas_cards));

        }else{

            botonGasto.setBackgroundResource(R.drawable.tab_activo);

            botonGasto.setTextColor(getColor(R.color.tarjetas_cards));
        }
    }
}