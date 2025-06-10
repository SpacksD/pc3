package com.example.controldegastos;

import androidx.annotation.NonNull;

public class TipoGasto {
    private int idTipoGasto;
    private String descripcion;

    public TipoGasto() {}

    public TipoGasto(int idTipoGasto, String descripcion) {
        this.idTipoGasto = idTipoGasto;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdTipoGasto() { return idTipoGasto; }
    public void setIdTipoGasto(int idTipoGasto) { this.idTipoGasto = idTipoGasto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @NonNull
    @Override
    public String toString() {
        return descripcion;
    }
}