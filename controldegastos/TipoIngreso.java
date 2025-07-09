package com.example.controldegastos;

public class TipoIngreso {
    private int idTipoIngreso;
    private String descripcion;

    public TipoIngreso() {}

    public TipoIngreso(int idTipoIngreso, String descripcion) {
        this.idTipoIngreso = idTipoIngreso;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdTipoIngreso() { return idTipoIngreso; }
    public void setIdTipoIngreso(int idTipoIngreso) { this.idTipoIngreso = idTipoIngreso; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return descripcion;
    }
}