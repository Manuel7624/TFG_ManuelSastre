package com.example.balancea.Modelos;

public class Movimiento {

    String tipo;
    String descripcion;
    double cantidad;
    String fecha;
    int idCategoria;
    String categoria;

    public Movimiento(String tipo, String descripcion, double cantidad, String fecha, int idCategoria, String categoria) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getCantidad() {
        return cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }
}