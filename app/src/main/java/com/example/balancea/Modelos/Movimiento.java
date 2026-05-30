package com.example.balancea.Modelos;

/**
 * Modelo que representa un movimiento financiero (ingreso o gasto).
 * Contiene los detalles de la transacción y su categorización.
 */
public class Movimiento {

    private String tipo;
    private String descripcion;
    private double cantidad;
    private String fecha;
    private int idCategoria;
    private String categoria;

    /**
     * Constructor para crear un objeto Movimiento.
     * @param tipo Tipo de movimiento ('ingreso' o 'gasto').
     * @param descripcion Detalle textual de la operación.
     * @param cantidad Importe de la transacción.
     * @param fecha Fecha en formato String (yyyy-MM-dd).
     * @param idCategoria ID numérico de la categoría asociada.
     * @param categoria Nombre textual de la categoría.
     */
    public Movimiento(String tipo, String descripcion, double cantidad, String fecha, int idCategoria, String categoria) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
    }

    /** @return Tipo de movimiento. */
    public String getTipo() {
        return tipo;
    }

    /** @return Descripción del movimiento. */
    public String getDescripcion() {
        return descripcion;
    }

    /** @return Cantidad o importe. */
    public double getCantidad() {
        return cantidad;
    }

    /** @return Fecha del movimiento. */
    public String getFecha() {
        return fecha;
    }

    /** @return ID de la categoría. */
    public int getIdCategoria() {
        return idCategoria;
    }

    /** @return Nombre de la categoría. */
    public String getCategoria() {
        return categoria;
    }
}