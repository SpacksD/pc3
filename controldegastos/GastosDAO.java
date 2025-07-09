package com.example.controldegastos;

import android.content.Context;
import java.util.List;

/**
 * GastosDAO - Clase de compatibilidad que delega a los DAOs especializados
 * Esta clase mantiene compatibilidad con código existente mientras redirige
 * las operaciones a los DAOs especializados correspondientes.
 */
public class GastosDAO {

    private TiposGastoDAO tiposGastoDAO;
    private TiposComprobanteDAO tiposComprobanteDAO;
    private ProveedoresDAO proveedoresDAO;
    private ComprobantesDAO comprobantesDAO;

    public GastosDAO(Context context) {
        // Inicializar DAOs especializados
        tiposGastoDAO = new TiposGastoDAO(context);
        tiposComprobanteDAO = new TiposComprobanteDAO(context);
        proveedoresDAO = new ProveedoresDAO(context);
        comprobantesDAO = new ComprobantesDAO(context);
    }

    // ================== MÉTODOS DELEGADOS PARA TIPOS DE GASTO ==================

    public List<TipoGasto> getAllTiposGasto() {
        return tiposGastoDAO.getAllTiposGasto();
    }

    public long insertTipoGasto(TipoGasto tipoGasto) {
        return tiposGastoDAO.insertTipoGasto(tipoGasto);
    }

    // ================== MÉTODOS DELEGADOS PARA TIPOS DE COMPROBANTE ==================

    public List<TipoComprobante> getAllTiposComprobante() {
        return tiposComprobanteDAO.getAllTiposComprobante();
    }

    // ================== MÉTODOS DELEGADOS PARA PROVEEDORES ==================

    public List<Proveedor> getAllProveedores() {
        return proveedoresDAO.getAllProveedores();
    }

    public long insertProveedor(Proveedor proveedor) {
        return proveedoresDAO.insertProveedor(proveedor);
    }

    // ================== MÉTODOS DELEGADOS PARA COMPROBANTES ==================

    public List<Comprobante> getAllComprobantes() {
        return comprobantesDAO.getAllComprobantes();
    }

    public long insertComprobante(Comprobante comprobante) {
        return comprobantesDAO.insertComprobante(comprobante);
    }

    public int updateComprobante(Comprobante comprobante) {
        return comprobantesDAO.updateComprobante(comprobante);
    }

    public int deleteComprobante(int idComprobante) {
        return comprobantesDAO.deleteComprobante(idComprobante);
    }

    public double getTotalGastosPorPeriodo(String fechaInicio, String fechaFin) {
        return comprobantesDAO.getTotalGastosPorPeriodo(fechaInicio, fechaFin);
    }

    // ================== MÉTODOS PARA ACCEDER A DAOs ESPECIALIZADOS ==================

    /**
     * Obtiene el DAO especializado para tipos de gasto
     */
    public TiposGastoDAO getTiposGastoDAO() {
        return tiposGastoDAO;
    }

    /**
     * Obtiene el DAO especializado para tipos de comprobante
     */
    public TiposComprobanteDAO getTiposComprobanteDAO() {
        return tiposComprobanteDAO;
    }

    /**
     * Obtiene el DAO especializado para proveedores
     */
    public ProveedoresDAO getProveedoresDAO() {
        return proveedoresDAO;
    }

    /**
     * Obtiene el DAO especializado para comprobantes
     */
    public ComprobantesDAO getComprobantesDAO() {
        return comprobantesDAO;
    }
}