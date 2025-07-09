package com.example.controldegastos;

public class Comprobante {
    private int idComprobante;
    private int idTipoComprobante;
    private int idTipoGasto;
    private int idProveedor;
    private String serieNumero;
    private String fecha;
    private double monto;
    private boolean estado;

    // Para mostrar en listas
    private String tipoComprobanteDesc;
    private String tipoGastoDesc;
    private String proveedorNombre;

    public Comprobante() {}

    public Comprobante(int idTipoComprobante, int idTipoGasto, int idProveedor,
                       String serieNumero, String fecha, double monto) {
        this.idTipoComprobante = idTipoComprobante;
        this.idTipoGasto = idTipoGasto;
        this.idProveedor = idProveedor;
        this.serieNumero = serieNumero;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = true;
    }

    // Getters y Setters
    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }
    public int getIdTipoComprobante() { return idTipoComprobante; }
    public void setIdTipoComprobante(int idTipoComprobante) { this.idTipoComprobante = idTipoComprobante; }
    public int getIdTipoGasto() { return idTipoGasto; }
    public void setIdTipoGasto(int idTipoGasto) { this.idTipoGasto = idTipoGasto; }
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    public String getSerieNumero() { return serieNumero; }
    public void setSerieNumero(String serieNumero) { this.serieNumero = serieNumero; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getTipoComprobanteDesc() { return tipoComprobanteDesc; }
    public void setTipoComprobanteDesc(String tipoComprobanteDesc) { this.tipoComprobanteDesc = tipoComprobanteDesc; }
    public String getTipoGastoDesc() { return tipoGastoDesc; }
    public void setTipoGastoDesc(String tipoGastoDesc) { this.tipoGastoDesc = tipoGastoDesc; }
    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }
}