package com.example.controldegastos;

public class Ingreso {
    private int idIngreso;
    private int idTipoIngreso;
    private int idProveedor;
    private String fecha;
    private double monto;
    private boolean estado;

    // Para mostrar en listas
    private String tipoIngresoDesc;
    private String proveedorNombre;

    public Ingreso() {}

    public Ingreso(int idTipoIngreso, int idProveedor, String fecha, double monto) {
        this.idTipoIngreso = idTipoIngreso;
        this.idProveedor = idProveedor;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = true;
    }

    // Getters y Setters
    public int getIdIngreso() { return idIngreso; }
    public void setIdIngreso(int idIngreso) { this.idIngreso = idIngreso; }
    public int getIdTipoIngreso() { return idTipoIngreso; }
    public void setIdTipoIngreso(int idTipoIngreso) { this.idTipoIngreso = idTipoIngreso; }
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getTipoIngresoDesc() { return tipoIngresoDesc; }
    public void setTipoIngresoDesc(String tipoIngresoDesc) { this.tipoIngresoDesc = tipoIngresoDesc; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
}