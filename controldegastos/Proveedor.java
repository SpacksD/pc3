package com.example.controldegastos;

import androidx.annotation.NonNull;

public class Proveedor {
    private int idProveedor;
    private String nombreProveedor;

    public Proveedor() {}

    public Proveedor(int idProveedor, String nombreProveedor) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    @NonNull
    @Override
    public String toString() {
        return nombreProveedor;
    }
}