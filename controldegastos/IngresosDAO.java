package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * IngresosDAO - Clase actualizada que combina operaciones de ingresos
 * con delegación a DAOs especializados para tipos y proveedores
 */
public class IngresosDAO {
    private static final String TAG = "IngresosDAO";
    private DatabaseHelper dbHelper;

    // DAOs especializados
    private TiposIngresoDAO tiposIngresoDAO;
    private ProveedoresDAO proveedoresDAO;

    public IngresosDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        tiposIngresoDAO = new TiposIngresoDAO(context);
        proveedoresDAO = new ProveedoresDAO(context);
    }

    // ================== MÉTODOS DELEGADOS PARA TIPOS DE INGRESO ==================

    public List<TipoIngreso> getAllTiposIngreso() {
        return tiposIngresoDAO.getAllTiposIngreso();
    }

    public long insertTipoIngreso(TipoIngreso tipoIngreso) {
        return tiposIngresoDAO.insertTipoIngreso(tipoIngreso);
    }

    // ================== MÉTODOS PARA INGRESOS ==================

    /**
     * Obtiene todos los ingresos activos con información completa
     */
    public List<Ingreso> getAllIngresos() {
        List<Ingreso> ingresos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT i.*, " +
                    "ti.descripcion as tipo_ingreso_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_INGRESOS + " i " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_INGRESO + " ti ON i.id_tipo_ingreso = ti.id_tipo_ingreso " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON i.id_proveedor = p.id_proveedor " +
                    "WHERE i.estado = 1 " +
                    "ORDER BY i.fecha DESC";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Ingreso ingreso = createIngresoFromCursor(cursor);
                    ingresos.add(ingreso);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Ingresos obtenidos: " + ingresos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener ingresos: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return ingresos;
    }

    /**
     * Obtiene ingresos por rango de fechas
     */
    public List<Ingreso> getIngresosByDateRange(String fechaInicio, String fechaFin) {
        List<Ingreso> ingresos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT i.*, " +
                    "ti.descripcion as tipo_ingreso_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_INGRESOS + " i " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_INGRESO + " ti ON i.id_tipo_ingreso = ti.id_tipo_ingreso " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON i.id_proveedor = p.id_proveedor " +
                    "WHERE i.estado = 1 AND i.fecha BETWEEN ? AND ? " +
                    "ORDER BY i.fecha DESC";

            cursor = db.rawQuery(query, new String[]{fechaInicio, fechaFin});

            if (cursor.moveToFirst()) {
                do {
                    Ingreso ingreso = createIngresoFromCursor(cursor);
                    ingresos.add(ingreso);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Ingresos obtenidos por fecha: " + ingresos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener ingresos por fecha: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return ingresos;
    }

    /**
     * Obtiene un ingreso por ID
     */
    public Ingreso getIngresoById(int idIngreso) {
        Ingreso ingreso = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT i.*, " +
                    "ti.descripcion as tipo_ingreso_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_INGRESOS + " i " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_INGRESO + " ti ON i.id_tipo_ingreso = ti.id_tipo_ingreso " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON i.id_proveedor = p.id_proveedor " +
                    "WHERE i.id_ingreso = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(idIngreso)});

            if (cursor.moveToFirst()) {
                ingreso = createIngresoFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener ingreso por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return ingreso;
    }

    /**
     * Inserta un nuevo ingreso
     */
    public long insertIngreso(Ingreso ingreso) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            // Validaciones
            validateIngreso(ingreso);

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID_TIPO_INGRESO, ingreso.getIdTipoIngreso());
            values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, ingreso.getIdProveedor());
            values.put(DatabaseHelper.COLUMN_FECHA, ingreso.getFecha());
            values.put(DatabaseHelper.COLUMN_MONTO, ingreso.getMonto());
            values.put(DatabaseHelper.COLUMN_ESTADO, ingreso.isEstado() ? 1 : 0);

            id = db.insert(DatabaseHelper.TABLE_INGRESOS, null, values);

            if (id > 0) {
                Log.d(TAG, "Ingreso insertado con ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar ingreso: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    /**
     * Actualiza un ingreso existente
     */
    public int updateIngreso(Ingreso ingreso) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Validaciones
            validateIngreso(ingreso);

            if (ingreso.getIdIngreso() <= 0) {
                throw new IllegalArgumentException("ID de ingreso inválido");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID_TIPO_INGRESO, ingreso.getIdTipoIngreso());
            values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, ingreso.getIdProveedor());
            values.put(DatabaseHelper.COLUMN_FECHA, ingreso.getFecha());
            values.put(DatabaseHelper.COLUMN_MONTO, ingreso.getMonto());
            values.put(DatabaseHelper.COLUMN_ESTADO, ingreso.isEstado() ? 1 : 0);

            String whereClause = DatabaseHelper.COLUMN_ID_INGRESO + " = ?";
            String[] whereArgs = {String.valueOf(ingreso.getIdIngreso())};

            rowsAffected = db.update(DatabaseHelper.TABLE_INGRESOS, values, whereClause, whereArgs);

            Log.d(TAG, "Ingreso actualizado. Filas afectadas: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar ingreso: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Elimina un ingreso (soft delete)
     */
    public int deleteIngreso(int idIngreso) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ESTADO, 0); // Soft delete

            String whereClause = DatabaseHelper.COLUMN_ID_INGRESO + " = ?";
            String[] whereArgs = {String.valueOf(idIngreso)};

            rowsAffected = db.update(DatabaseHelper.TABLE_INGRESOS, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Ingreso eliminado (soft delete). ID: " + idIngreso);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar ingreso: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Obtiene el total de ingresos por período
     */
    public double getTotalIngresosPorPeriodo(String fechaInicio, String fechaFin) {
        SQLiteDatabase db = null;
        double total = 0.0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT SUM(monto) as total FROM " + DatabaseHelper.TABLE_INGRESOS +
                    " WHERE estado = 1 AND fecha BETWEEN ? AND ?";

            Cursor cursor = db.rawQuery(query, new String[]{fechaInicio, fechaFin});

            if (cursor.moveToFirst()) {
                total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener total de ingresos por período: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return total;
    }

    /**
     * Busca ingresos por concepto o descripción
     */
    public List<Ingreso> searchIngresos(String searchQuery) {
        List<Ingreso> ingresos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT i.*, " +
                    "ti.descripcion as tipo_ingreso_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_INGRESOS + " i " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_INGRESO + " ti ON i.id_tipo_ingreso = ti.id_tipo_ingreso " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON i.id_proveedor = p.id_proveedor " +
                    "WHERE i.estado = 1 AND (ti.descripcion LIKE ? OR p.nombre_proveedor LIKE ?) " +
                    "ORDER BY i.fecha DESC";

            String searchPattern = "%" + searchQuery.trim() + "%";
            cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern});

            if (cursor.moveToFirst()) {
                do {
                    Ingreso ingreso = createIngresoFromCursor(cursor);
                    ingresos.add(ingreso);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Búsqueda '" + searchQuery + "' - Resultados: " + ingresos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error en búsqueda de ingresos: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return ingresos;
    }

    // ================== MÉTODOS PARA ACCEDER A DAOs ESPECIALIZADOS ==================

    /**
     * Obtiene el DAO especializado para tipos de ingreso
     */
    public TiposIngresoDAO getTiposIngresoDAO() {
        return tiposIngresoDAO;
    }

    /**
     * Obtiene el DAO especializado para proveedores
     */
    public ProveedoresDAO getProveedoresDAO() {
        return proveedoresDAO;
    }

    // ================== MÉTODOS PRIVADOS ==================

    private Ingreso createIngresoFromCursor(Cursor cursor) {
        Ingreso ingreso = new Ingreso();
        ingreso.setIdIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_INGRESO)));
        ingreso.setIdTipoIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_INGRESO)));
        ingreso.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
        ingreso.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)));
        ingreso.setMonto(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MONTO)));
        ingreso.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)) == 1);

        // Datos para mostrar (si existen en el cursor)
        try {
            ingreso.setTipoIngresoDesc(cursor.getString(cursor.getColumnIndexOrThrow("tipo_ingreso_desc")));
            ingreso.setProveedorNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre_proveedor")));
        } catch (IllegalArgumentException e) {
            // Columnas no encontradas, ignorar
        }

        return ingreso;
    }

    private void validateIngreso(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser null");
        }

        if (ingreso.getIdTipoIngreso() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de ingreso válido");
        }

        if (ingreso.getIdProveedor() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor/cliente válido");
        }

        if (ingreso.getFecha() == null || ingreso.getFecha().trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha del ingreso no puede estar vacía");
        }

        if (ingreso.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    // ================== CLASE PARA ESTADÍSTICAS ==================

    public static class IngresoStats {
        public int totalIngresos = 0;
        public double totalMonto = 0.0;
        public double promedioMonto = 0.0;
        public String fechaUltimoIngreso = null;

        @Override
        public String toString() {
            return String.format("Stats{total=%d, monto=%.2f, promedio=%.2f, ultimaFecha=%s}",
                    totalIngresos, totalMonto, promedioMonto, fechaUltimoIngreso);
        }
    }
}