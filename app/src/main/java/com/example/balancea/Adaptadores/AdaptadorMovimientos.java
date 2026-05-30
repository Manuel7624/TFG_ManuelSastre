package com.example.balancea.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.balancea.Modelos.Movimiento;
import com.example.balancea.R;

import java.util.ArrayList;

/**
 * Adaptador personalizado para mostrar objetos de tipo Movimiento en un ListView.
 * Gestiona la vinculación de datos entre la lista de movimientos y los elementos visuales del item.
 */
public class AdaptadorMovimientos extends ArrayAdapter<Movimiento> {

    /** Contexto de la aplicación o actividad. */
    Context context;
    /** Lista de datos a mostrar. */
    ArrayList<Movimiento> lista;

    /**
     * Constructor del adaptador.
     * @param context Contexto de la actividad.
     * @param lista Colección de movimientos a representar.
     */
    public AdaptadorMovimientos(Context context, ArrayList<Movimiento> lista) {
        super(context, 0, lista);
        this.context = context;
        this.lista = lista;
    }

    /**
     * Proporciona una vista para un elemento de la lista en una posición específica.
     * @param position Posición del elemento en la lista.
     * @param convertView Vista reciclada si existe.
     * @param parent Contenedor padre.
     * @return Vista configurada con los datos del movimiento.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_movimiento, parent, false);
        }

        Movimiento movimiento = lista.get(position);

        ImageView icono = convertView.findViewById(R.id.iconoMovimientoItem);
        TextView categoria = convertView.findViewById(R.id.campoCategoriaItem);
        TextView descripcion = convertView.findViewById(R.id.campoDescripcionItem);
        TextView fecha = convertView.findViewById(R.id.campoFechaItem);
        TextView cantidad = convertView.findViewById(R.id.campoCantidadItem);

        categoria.setText(movimiento.getCategoria());

        if(movimiento.getDescripcion() == null || movimiento.getDescripcion().isEmpty()){
            descripcion.setText("Sin descripción");
        }else{
            descripcion.setText(movimiento.getDescripcion());
        }

        fecha.setText(movimiento.getFecha());

        // Configuración visual según tipo de movimiento
        if(movimiento.getTipo().equals("gasto")){
            cantidad.setText("-" + movimiento.getCantidad() + " €");
            cantidad.setTextColor(context.getColor(R.color.gasto));
            icono.setBackgroundResource(R.drawable.bg_gasto);
        } else {
            cantidad.setText("+" + movimiento.getCantidad() + " €");
            cantidad.setTextColor(context.getColor(R.color.ingreso));
            icono.setBackgroundResource(R.drawable.bg_ingreso);
        }

        // Asignación del icono correspondiente a la categoría
        switch (movimiento.getIdCategoria()) {
            case 1: icono.setImageResource(R.drawable.ic_comida); break;
            case 2: icono.setImageResource(R.drawable.ic_transport); break;
            case 3: icono.setImageResource(R.drawable.ic_ocio); break;
            case 4: icono.setImageResource(R.drawable.ic_vivienda); break;
            case 5: icono.setImageResource(R.drawable.ic_salud); break;
            case 6: icono.setImageResource(R.drawable.ic_educacion); break;
            case 7: icono.setImageResource(R.drawable.ic_compras); break;
            case 8: icono.setImageResource(R.drawable.ic_otro_gasto); break;
            case 9: icono.setImageResource(R.drawable.ic_nomina); break;
            case 10: icono.setImageResource(R.drawable.ic_beca); break;
            case 11: icono.setImageResource(R.drawable.ic_regalo); break;
            case 12: icono.setImageResource(R.drawable.ic_venta); break;
            case 13: icono.setImageResource(R.drawable.ic_otros_ingresos); break;
            case 14: icono.setImageResource(R.drawable.ic_saldo_inicial_ingreso); break;
            case 15: icono.setImageResource(R.drawable.ic_saldo_incial_gasto); break;
        }

        return convertView;
    }
}
