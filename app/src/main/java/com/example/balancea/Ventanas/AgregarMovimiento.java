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

/**
 * Actividad que permite al usuario registrar un nuevo movimiento (Ingreso o Gasto).
 * Gestiona la selección de categorías dinámicas, selección de fecha mediante calendario
 * y validación de los datos introducidos.
 */
public class AgregarMovimiento extends AppCompatActivity {

    /** Botones para alternar entre tipo Ingreso y Gasto. */
    TextView botonIngreso, botonGasto;

    /** Campos de entrada para cantidad, descripción y fecha del movimiento. */
    EditText campoCantidad, campoDescripcion, campoFecha;

    /** Selector desplegable para las categorías disponibles según el tipo de movimiento. */
    Spinner spinnerCategoria;

    /** Botón para procesar y guardar el nuevo movimiento en la base de datos. */
    MaterialButton botonGuardarMovimiento;

    /** Botón para regresar a la pantalla anterior sin guardar cambios. */
    ImageView botonVolver;

    /** TextView para mostrar avisos de error en la validación del formulario. */
    TextView campoError;

    /** Estado que rastrea si se está registrando un 'ingreso' o un 'gasto'. */
    String tipoActual = "ingreso";

    /**
     * Inicializa la actividad, vincula los componentes y configura los eventos de usuario.
     * @param savedInstanceState Estado guardado de la instancia.
     */
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

        // Inicialización de componentes
        botonIngreso = findViewById(R.id.botonIngreso);
        botonGasto = findViewById(R.id.botonGasto);
        campoCantidad = findViewById(R.id.campoCantidad);
        campoDescripcion = findViewById(R.id.campoDescripcion);
        campoFecha = findViewById(R.id.campoFecha);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        botonGuardarMovimiento = findViewById(R.id.botonGuardarMovimiento);
        botonVolver = findViewById(R.id.botonVolver);
        campoError = findViewById(R.id.campoErrorAgregarMovimiento);

        campoError.setVisibility(View.GONE);

        // Carga inicial de categorías por defecto (ingresos)
        cargarCategorias();

        // Control de selección de tipo Ingreso
        botonIngreso.setOnClickListener(v -> {
            tipoActual = "ingreso";
            actualizarTabs();
            cargarCategorias();
        });

        // Control de selección de tipo Gasto
        botonGasto.setOnClickListener(v -> {
            tipoActual = "gasto";
            actualizarTabs();
            cargarCategorias();
        });

        // Diálogo de selección de fecha (DatePicker)
        campoFecha.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            int year = calendario.get(Calendar.YEAR);
            int month = calendario.get(Calendar.MONTH);
            int day = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Formatea la fecha seleccionada a YYYY-MM-DD
                        String fecha = year1 + "-" + String.format("%02d", month1 + 1) + "-" + String.format("%02d", dayOfMonth);
                        campoFecha.setText(fecha);
                    },
                    year, month, day
            );

            // Evita seleccionar fechas futuras
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });

        // Lógica para guardar el movimiento
        botonGuardarMovimiento.setOnClickListener(v -> {
            campoError.setVisibility(View.GONE);

            // Validación de campos obligatorios
            if(campoCantidad.getText().toString().isEmpty() || campoFecha.getText().toString().isEmpty() || spinnerCategoria.getSelectedItem() == null){
                campoError.setText(R.string.CamposObligatorios);
                campoError.setVisibility(View.VISIBLE);
                return;
            }

            double cantidad = Double.parseDouble(campoCantidad.getText().toString());
            String descripcion = campoDescripcion.getText().toString();
            String fecha = campoFecha.getText().toString();
            String categoria = spinnerCategoria.getSelectedItem().toString();

            // Obtiene el ID correspondiente al nombre de categoría seleccionado
            int idCategoria = Consultas.obtenerIdCategoria(this, categoria);

            // Inserción en base de datos
            boolean resultado = Consultas.insertarMovimiento(this, CorreoActual, idCategoria, tipoActual, cantidad, descripcion, fecha);

            if(resultado){
                finish(); // Cierra la ventana tras el éxito
            } else {
                campoError.setText("Error al guardar movimiento");
                campoError.setVisibility(View.VISIBLE);
            }
        });

        botonVolver.setOnClickListener(v -> finish());
    }

    /**
     * Recupera las categorías de la base de datos según el tipo actual (ingreso/gasto)
     * y las carga en el Spinner.
     */
    private void cargarCategorias() {
        ArrayList<String> lista = Consultas.obtenerCategorias(this, tipoActual);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista);
        spinnerCategoria.setAdapter(adapter);
    }

    /**
     * Actualiza la apariencia visual de los selectores de tipo (Ingreso/Gasto)
     * para reflejar cuál está seleccionado.
     */
    private void actualizarTabs() {
        botonIngreso.setBackgroundResource(R.drawable.tab_inactivo);
        botonGasto.setBackgroundResource(R.drawable.tab_inactivo);
        botonIngreso.setTextColor(getColor(R.color.texto_principal));
        botonGasto.setTextColor(getColor(R.color.texto_principal));

        if(tipoActual.equals("ingreso")){
            botonIngreso.setBackgroundResource(R.drawable.tab_activo);
            botonIngreso.setTextColor(getColor(R.color.tarjetas_cards));
        } else {
            botonGasto.setBackgroundResource(R.drawable.tab_activo);
            botonGasto.setTextColor(getColor(R.color.tarjetas_cards));
        }
    }
}
