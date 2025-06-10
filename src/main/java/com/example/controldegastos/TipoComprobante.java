package com.example.controldegastos;

import androidx.annotation.NonNull;

public class TipoComprobante {
    private int idTipoComprobante;
    private String descripcion;
    private boolean estado;

    public TipoComprobante() {}

    public TipoComprobante(int idTipoComprobante, String descripcion, boolean estado) {
        this.idTipoComprobante = idTipoComprobante;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdTipoComprobante() { return idTipoComprobante; }
    public void setIdTipoComprobante(int idTipoComprobante) { this.idTipoComprobante = idTipoComprobante; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    @NonNull
    @Override
    public String toString() {
        return descripcion;
    }
}