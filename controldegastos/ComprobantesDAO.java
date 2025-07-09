package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ComprobantesDAO {
    private static final String TAG = "ComprobantesDAO";
    private DatabaseHelper dbHelper;

    public ComprobantesDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Obtiene todos los comprobantes activos con información completa
     */
    public List<Comprobante> getAllComprobantes() {
        List<Comprobante> comprobantes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, " +
                    "tc.descripcion as tipo_comprobante_desc, " +
                    "tg.descripcion as tipo_gasto_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_COMPROBANTES + " c " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_COMPROBANTE + " tc ON c.id_tipo_comprobante = tc.id_tipo_comprobante " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_GASTO + " tg ON c.id_tipo_gasto = tg.id_tipo_gasto " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON c.id_proveedor = p.id_proveedor " +
                    "WHERE c.estado = 1 " +
                    "ORDER BY c.fecha DESC";

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Comprobante comprobante = createComprobanteFromCursor(cursor);
                    comprobantes.add(comprobante);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Comprobantes obtenidos: " + comprobantes.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener comprobantes: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return comprobantes;
    }

    /**
     * Obtiene comprobantes por rango de fechas
     */
    public List<Comprobante> getComprobantesByDateRange(String fechaInicio, String fechaFin) {
        List<Comprobante> comprobantes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, " +
                    "tc.descripcion as tipo_comprobante_desc, " +
                    "tg.descripcion as tipo_gasto_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_COMPROBANTES + " c " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_COMPROBANTE + " tc ON c.id_tipo_comprobante = tc.id_tipo_comprobante " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_GASTO + " tg ON c.id_tipo_gasto = tg.id_tipo_gasto " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON c.id_proveedor = p.id_proveedor " +
                    "WHERE c.estado = 1 AND c.fecha BETWEEN ? AND ? " +
                    "ORDER BY c.fecha DESC";

            cursor = db.rawQuery(query, new String[]{fechaInicio, fechaFin});

            if (cursor.moveToFirst()) {
                do {
                    Comprobante comprobante = createComprobanteFromCursor(cursor);
                    comprobantes.add(comprobante);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Comprobantes obtenidos por fecha: " + comprobantes.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener comprobantes por fecha: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return comprobantes;
    }

    /**
     * Obtiene comprobantes por proveedor
     */
    public List<Comprobante> getComprobantesByProveedor(int idProveedor) {
        List<Comprobante> comprobantes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, " +
                    "tc.descripcion as tipo_comprobante_desc, " +
                    "tg.descripcion as tipo_gasto_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_COMPROBANTES + " c " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_COMPROBANTE + " tc ON c.id_tipo_comprobante = tc.id_tipo_comprobante " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_GASTO + " tg ON c.id_tipo_gasto = tg.id_tipo_gasto " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON c.id_proveedor = p.id_proveedor " +
                    "WHERE c.estado = 1 AND c.id_proveedor = ? " +
                    "ORDER BY c.fecha DESC";

            cursor = db.rawQuery(query, new String[]{String.valueOf(idProveedor)});

            if (cursor.moveToFirst()) {
                do {
                    Comprobante comprobante = createComprobanteFromCursor(cursor);
                    comprobantes.add(comprobante);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Comprobantes obtenidos por proveedor " + idProveedor + ": " + comprobantes.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener comprobantes por proveedor: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return comprobantes;
    }

    /**
     * Obtiene comprobantes por tipo de gasto
     */
    public List<Comprobante> getComprobantesByTipoGasto(int idTipoGasto) {
        List<Comprobante> comprobantes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, " +
                    "tc.descripcion as tipo_comprobante_desc, " +
                    "tg.descripcion as tipo_gasto_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_COMPROBANTES + " c " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_COMPROBANTE + " tc ON c.id_tipo_comprobante = tc.id_tipo_comprobante " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_GASTO + " tg ON c.id_tipo_gasto = tg.id_tipo_gasto " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON c.id_proveedor = p.id_proveedor " +
                    "WHERE c.estado = 1 AND c.id_tipo_gasto = ? " +
                    "ORDER BY c.fecha DESC";

            cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoGasto)});

            if (cursor.moveToFirst()) {
                do {
                    Comprobante comprobante = createComprobanteFromCursor(cursor);
                    comprobantes.add(comprobante);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Comprobantes obtenidos por tipo gasto " + idTipoGasto + ": " + comprobantes.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener comprobantes por tipo de gasto: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return comprobantes;
    }

    /**
     * Obtiene un comprobante por ID
     */
    public Comprobante getComprobanteById(int idComprobante) {
        Comprobante comprobante = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, " +
                    "tc.descripcion as tipo_comprobante_desc, " +
                    "tg.descripcion as tipo_gasto_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_COMPROBANTES + " c " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_COMPROBANTE + " tc ON c.id_tipo_comprobante = tc.id_tipo_comprobante " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_GASTO + " tg ON c.id_tipo_gasto = tg.id_tipo_gasto " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON c.id_proveedor = p.id_proveedor " +
                    "WHERE c.id_comprobante = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(idComprobante)});

            if (cursor.moveToFirst()) {
                comprobante = createComprobanteFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener comprobante por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return comprobante;
    }

    /**
     * Inserta un nuevo comprobante
     */
    public long insertComprobante(Comprobante comprobante) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            // Validaciones
            validateComprobante(comprobante);

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE, comprobante.getIdTipoComprobante());
            values.put(DatabaseHelper.COLUMN_ID_TIPO_GASTO, comprobante.getIdTipoGasto());
            values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, comprobante.getIdProveedor());
            values.put(DatabaseHelper.COLUMN_SERIE_NUMERO, comprobante.getSerieNumero().trim());
            values.put(DatabaseHelper.COLUMN_FECHA, comprobante.getFecha());
            values.put(DatabaseHelper.COLUMN_MONTO, comprobante.getMonto());
            values.put(DatabaseHelper.COLUMN_ESTADO, comprobante.isEstado() ? 1 : 0);

            id = db.insert(DatabaseHelper.TABLE_COMPROBANTES, null, values);

            if (id > 0) {
                Log.d(TAG, "Comprobante insertado con ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar comprobante: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    /**
     * Actualiza un comprobante existente
     */
    public int updateComprobante(Comprobante comprobante) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Validaciones
            validateComprobante(comprobante);

            if (comprobante.getIdComprobante() <= 0) {
                throw new IllegalArgumentException("ID de comprobante inválido");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE, comprobante.getIdTipoComprobante());
            values.put(DatabaseHelper.COLUMN_ID_TIPO_GASTO, comprobante.getIdTipoGasto());
            values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, comprobante.getIdProveedor());
            values.put(DatabaseHelper.COLUMN_SERIE_NUMERO, comprobante.getSerieNumero().trim());
            values.put(DatabaseHelper.COLUMN_FECHA, comprobante.getFecha());
            values.put(DatabaseHelper.COLUMN_MONTO, comprobante.getMonto());
            values.put(DatabaseHelper.COLUMN_ESTADO, comprobante.isEstado() ? 1 : 0);

            String whereClause = DatabaseHelper.COLUMN_ID_COMPROBANTE + " = ?";
            String[] whereArgs = {String.valueOf(comprobante.getIdComprobante())};

            rowsAffected = db.update(DatabaseHelper.TABLE_COMPROBANTES, values, whereClause, whereArgs);

            Log.d(TAG, "Comprobante actualizado. Filas afectadas: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar comprobante: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Elimina un comprobante (soft delete)
     */
    public int deleteComprobante(int idComprobante) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ESTADO, 0); // Soft delete

            String whereClause = DatabaseHelper.COLUMN_ID_COMPROBANTE + " = ?";
            String[] whereArgs = {String.valueOf(idComprobante)};

            rowsAffected = db.update(DatabaseHelper.TABLE_COMPROBANTES, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Comprobante eliminado (soft delete). ID: " + idComprobante);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar comprobante: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Elimina permanentemente un comprobante
     */
    public boolean hardDeleteComprobante(int idComprobante) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_ID_COMPROBANTE + " = ?";
            String[] whereArgs = {String.valueOf(idComprobante)};

            rowsAffected = db.delete(DatabaseHelper.TABLE_COMPROBANTES, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Comprobante eliminado permanentemente. ID: " + idComprobante);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar permanentemente comprobante: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return false;
    }

    /**
     * Obtiene el total de gastos por período
     */
    public double getTotalGastosPorPeriodo(String fechaInicio, String fechaFin) {
        SQLiteDatabase db = null;
        double total = 0.0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT SUM(monto) as total FROM " + DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE estado = 1 AND fecha BETWEEN ? AND ?";

            Cursor cursor = db.rawQuery(query, new String[]{fechaInicio, fechaFin});

            if (cursor.moveToFirst()) {
                total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener total de gastos por período: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return total;
    }

    /**
     * Obtiene estadísticas generales de comprobantes
     */
    public ComprobanteStats getComprobanteStats() {
        SQLiteDatabase db = null;
        ComprobanteStats stats = new ComprobanteStats();

        try {
            db = dbHelper.getReadableDatabase();

            // Total de comprobantes activos
            String queryTotal = "SELECT COUNT(*), COALESCE(SUM(" + DatabaseHelper.COLUMN_MONTO + "), 0) FROM " +
                    DatabaseHelper.TABLE_COMPROBANTES + " WHERE " + DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(queryTotal, null);
            if (cursor.moveToFirst()) {
                stats.totalComprobantes = cursor.getInt(0);
                stats.totalMonto = cursor.getDouble(1);
            }
            cursor.close();

            // Comprobante más reciente
            String queryReciente = "SELECT MAX(" + DatabaseHelper.COLUMN_FECHA + ") FROM " +
                    DatabaseHelper.TABLE_COMPROBANTES + " WHERE " + DatabaseHelper.COLUMN_ESTADO + " = 1";

            cursor = db.rawQuery(queryReciente, null);
            if (cursor.moveToFirst()) {
                stats.fechaUltimoComprobante = cursor.getString(0);
            }
            cursor.close();

            // Promedio por comprobante
            if (stats.totalComprobantes > 0) {
                stats.promedioMonto = stats.totalMonto / stats.totalComprobantes;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estadísticas de comprobantes: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return stats;
    }

    /**
     * Busca comprobantes por serie/número
     */
    public List<Comprobante> searchComprobantesBySerieNumero(String searchQuery) {
        List<Comprobante> comprobantes = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT c.*, " +
                    "tc.descripcion as tipo_comprobante_desc, " +
                    "tg.descripcion as tipo_gasto_desc, " +
                    "p.nombre_proveedor " +
                    "FROM " + DatabaseHelper.TABLE_COMPROBANTES + " c " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_COMPROBANTE + " tc ON c.id_tipo_comprobante = tc.id_tipo_comprobante " +
                    "INNER JOIN " + DatabaseHelper.TABLE_TIPO_GASTO + " tg ON c.id_tipo_gasto = tg.id_tipo_gasto " +
                    "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON c.id_proveedor = p.id_proveedor " +
                    "WHERE c.estado = 1 AND c.serie_numero LIKE ? " +
                    "ORDER BY c.fecha DESC";

            cursor = db.rawQuery(query, new String[]{"%" + searchQuery.trim() + "%"});

            if (cursor.moveToFirst()) {
                do {
                    Comprobante comprobante = createComprobanteFromCursor(cursor);
                    comprobantes.add(comprobante);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Búsqueda serie/número '" + searchQuery + "' - Resultados: " + comprobantes.size());
        } catch (Exception e) {
            Log.e(TAG, "Error en búsqueda de comprobantes: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return comprobantes;
    }

    // ================== MÉTODOS PRIVADOS ==================

    private Comprobante createComprobanteFromCursor(Cursor cursor) {
        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_COMPROBANTE)));
        comprobante.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
        comprobante.setIdTipoGasto(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_GASTO)));
        comprobante.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
        comprobante.setSerieNumero(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERIE_NUMERO)));
        comprobante.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)));
        comprobante.setMonto(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MONTO)));
        comprobante.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)) == 1);

        // Datos para mostrar (si existen en el cursor)
        try {
            comprobante.setTipoComprobanteDesc(cursor.getString(cursor.getColumnIndexOrThrow("tipo_comprobante_desc")));
            comprobante.setTipoGastoDesc(cursor.getString(cursor.getColumnIndexOrThrow("tipo_gasto_desc")));
            comprobante.setProveedorNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre_proveedor")));
        } catch (IllegalArgumentException e) {
            // Columnas no encontradas, ignorar
        }

        return comprobante;
    }

    private void validateComprobante(Comprobante comprobante) {
        if (comprobante == null) {
            throw new IllegalArgumentException("El comprobante no puede ser null");
        }

        if (comprobante.getIdTipoComprobante() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de comprobante válido");
        }

        if (comprobante.getIdTipoGasto() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de gasto válido");
        }

        if (comprobante.getIdProveedor() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor válido");
        }

        if (comprobante.getSerieNumero() == null || comprobante.getSerieNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("La serie/número del comprobante no puede estar vacía");
        }

        if (comprobante.getFecha() == null || comprobante.getFecha().trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha del comprobante no puede estar vacía");
        }

        if (comprobante.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    // ================== CLASE PARA ESTADÍSTICAS ==================

    public static class ComprobanteStats {
        public int totalComprobantes = 0;
        public double totalMonto = 0.0;
        public double promedioMonto = 0.0;
        public String fechaUltimoComprobante = null;

        @Override
        public String toString() {
            return String.format("Stats{total=%d, monto=%.2f, promedio=%.2f, ultimaFecha=%s}",
                    totalComprobantes, totalMonto, promedioMonto, fechaUltimoComprobante);
        }
    }
}