package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TiposComprobanteDAO {
    private static final String TAG = "TiposComprobanteDAO";
    private DatabaseHelper dbHelper;

    public TiposComprobanteDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Obtiene todos los tipos de comprobante activos
     */
    public List<TipoComprobante> getAllTiposComprobante() {
        List<TipoComprobante> tipos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_ESTADO_COMPROBANTE + " = ?";
            String[] selectionArgs = {"1"};

            cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE,
                    null, selection, selectionArgs, null, null,
                    DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    TipoComprobante tipo = new TipoComprobante();
                    tipo.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
                    tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE)));
                    tipo.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE)) == 1);
                    tipos.add(tipo);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Tipos de comprobante activos obtenidos: " + tipos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipos de comprobante: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipos;
    }

    /**
     * Obtiene todos los tipos de comprobante (incluyendo inactivos)
     */
    public List<TipoComprobante> getAllTiposComprobanteIncludingInactive() {
        List<TipoComprobante> tipos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    TipoComprobante tipo = new TipoComprobante();
                    tipo.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
                    tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE)));
                    tipo.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE)) == 1);
                    tipos.add(tipo);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Todos los tipos de comprobante obtenidos: " + tipos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener todos los tipos de comprobante: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipos;
    }

    /**
     * Obtiene un tipo de comprobante por ID
     */
    public TipoComprobante getTipoComprobanteById(int idTipoComprobante) {
        TipoComprobante tipo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " = ?";
            String[] selectionArgs = {String.valueOf(idTipoComprobante)};

            cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE,
                    null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                tipo = new TipoComprobante();
                tipo.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
                tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE)));
                tipo.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE)) == 1);

                Log.d(TAG, "Tipo de comprobante encontrado: " + tipo.getDescripcion());
            } else {
                Log.w(TAG, "No se encontró tipo de comprobante con ID: " + idTipoComprobante);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipo de comprobante por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipo;
    }

    /**
     * Inserta un nuevo tipo de comprobante
     */
    public long insertTipoComprobante(TipoComprobante tipoComprobante) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            // Validaciones
            if (tipoComprobante == null || tipoComprobante.getDescripcion() == null ||
                    tipoComprobante.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del tipo de comprobante no puede estar vacía");
            }

            // Verificar si ya existe
            if (existsTipoComprobanteWithDescription(tipoComprobante.getDescripcion().trim())) {
                throw new IllegalArgumentException("Ya existe un tipo de comprobante con esa descripción");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE, tipoComprobante.getDescripcion().trim().toUpperCase());
            values.put(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE, tipoComprobante.isEstado() ? 1 : 0);

            id = db.insert(DatabaseHelper.TABLE_TIPO_COMPROBANTE, null, values);

            if (id > 0) {
                Log.d(TAG, "Tipo de comprobante insertado exitosamente con ID: " + id);
            } else {
                Log.e(TAG, "Error al insertar tipo de comprobante");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar tipo de comprobante: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    /**
     * Actualiza un tipo de comprobante
     */
    public int updateTipoComprobante(TipoComprobante tipoComprobante) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Validaciones
            if (tipoComprobante == null || tipoComprobante.getDescripcion() == null ||
                    tipoComprobante.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del tipo de comprobante no puede estar vacía");
            }

            if (tipoComprobante.getIdTipoComprobante() <= 0) {
                throw new IllegalArgumentException("ID de tipo de comprobante inválido");
            }

            // Verificar duplicados (excluyendo el actual)
            if (existsTipoComprobanteWithDescriptionExcluding(tipoComprobante.getDescripcion().trim(),
                    tipoComprobante.getIdTipoComprobante())) {
                throw new IllegalArgumentException("Ya existe otro tipo de comprobante con esa descripción");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE, tipoComprobante.getDescripcion().trim().toUpperCase());
            values.put(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE, tipoComprobante.isEstado() ? 1 : 0);

            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " = ?";
            String[] whereArgs = {String.valueOf(tipoComprobante.getIdTipoComprobante())};

            rowsAffected = db.update(DatabaseHelper.TABLE_TIPO_COMPROBANTE, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Tipo de comprobante actualizado exitosamente. Filas afectadas: " + rowsAffected);
            } else {
                Log.w(TAG, "No se actualizó ningún tipo de comprobante. Posiblemente no existe el ID: " + tipoComprobante.getIdTipoComprobante());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar tipo de comprobante: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Activa o desactiva un tipo de comprobante (soft delete/enable)
     */
    public int toggleTipoComprobanteEstado(int idTipoComprobante) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Obtener estado actual
            TipoComprobante tipo = getTipoComprobanteById(idTipoComprobante);
            if (tipo == null) {
                throw new IllegalArgumentException("No existe el tipo de comprobante con ID: " + idTipoComprobante);
            }

            // Si se va a desactivar, verificar que no esté en uso
            if (tipo.isEstado() && isTipoComprobanteInUse(idTipoComprobante)) {
                throw new IllegalStateException("No se puede desactivar el tipo de comprobante porque está en uso");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            boolean nuevoEstado = !tipo.isEstado();
            values.put(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE, nuevoEstado ? 1 : 0);

            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " = ?";
            String[] whereArgs = {String.valueOf(idTipoComprobante)};

            rowsAffected = db.update(DatabaseHelper.TABLE_TIPO_COMPROBANTE, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                String estadoTexto = nuevoEstado ? "activado" : "desactivado";
                Log.d(TAG, "Tipo de comprobante " + estadoTexto + " exitosamente. ID: " + idTipoComprobante);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cambiar estado del tipo de comprobante: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Verifica si un tipo de comprobante está en uso
     */
    public boolean isTipoComprobanteInUse(int idTipoComprobante) {
        SQLiteDatabase db = null;
        boolean inUse = false;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE " + DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoComprobante)});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                inUse = count > 0;
                Log.d(TAG, "Tipo de comprobante ID " + idTipoComprobante + " tiene " + count + " comprobantes asociados");
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar uso del tipo de comprobante: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return inUse;
    }

    /**
     * Elimina permanentemente un tipo de comprobante (solo si no está en uso)
     */
    public boolean deleteTipoComprobante(int idTipoComprobante) {
        // Primero verificar si está en uso
        if (isTipoComprobanteInUse(idTipoComprobante)) {
            Log.w(TAG, "No se puede eliminar el tipo de comprobante ID " + idTipoComprobante + " porque está en uso");
            return false;
        }

        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " = ?";
            String[] whereArgs = {String.valueOf(idTipoComprobante)};

            rowsAffected = db.delete(DatabaseHelper.TABLE_TIPO_COMPROBANTE, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Tipo de comprobante eliminado exitosamente. ID: " + idTipoComprobante);
                return true;
            } else {
                Log.w(TAG, "No se encontró el tipo de comprobante para eliminar. ID: " + idTipoComprobante);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar tipo de comprobante: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Obtiene estadísticas de uso de un tipo de comprobante
     */
    public TipoComprobanteStats getTipoComprobanteStats(int idTipoComprobante) {
        SQLiteDatabase db = null;
        TipoComprobanteStats stats = new TipoComprobanteStats();

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*), COALESCE(SUM(" + DatabaseHelper.COLUMN_MONTO + "), 0) FROM " +
                    DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE " + DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoComprobante)});
            if (cursor.moveToFirst()) {
                stats.totalComprobantes = cursor.getInt(0);
                stats.totalMonto = cursor.getDouble(1);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estadísticas del tipo de comprobante: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return stats;
    }

    /**
     * Busca tipos de comprobante por descripción
     */
    public List<TipoComprobante> searchTiposComprobanteByDescription(String searchQuery) {
        List<TipoComprobante> tipos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE + " LIKE ? AND " +
                    DatabaseHelper.COLUMN_ESTADO_COMPROBANTE + " = ?";
            String[] selectionArgs = {"%" + searchQuery.trim() + "%", "1"};

            cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE,
                    null, selection, selectionArgs, null, null,
                    DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    TipoComprobante tipo = new TipoComprobante();
                    tipo.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
                    tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE)));
                    tipo.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE)) == 1);
                    tipos.add(tipo);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Búsqueda '" + searchQuery + "' - Resultados: " + tipos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error en búsqueda de tipos de comprobante: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipos;
    }

    /**
     * Obtiene el conteo total de tipos de comprobante activos
     */
    public int getTotalTiposComprobanteActivosCount() {
        SQLiteDatabase db = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TIPO_COMPROBANTE +
                    " WHERE " + DatabaseHelper.COLUMN_ESTADO_COMPROBANTE + " = 1";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener conteo de tipos de comprobante activos: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return count;
    }

    /**
     * Obtiene el conteo total de tipos de comprobante (incluyendo inactivos)
     */
    public int getTotalTiposComprobanteCount() {
        SQLiteDatabase db = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TIPO_COMPROBANTE;

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener conteo total de tipos de comprobante: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return count;
    }

    // ================== MÉTODOS PRIVADOS AUXILIARES ==================

    /**
     * Verifica si existe un tipo de comprobante con la descripción dada
     */
    private boolean existsTipoComprobanteWithDescription(String descripcion) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE + ") = ?";
            String[] selectionArgs = {descripcion.trim().toUpperCase()};

            Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE,
                    new String[]{DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de tipo de comprobante: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    /**
     * Verifica si existe un tipo de comprobante con la descripción dada, excluyendo un ID específico
     */
    private boolean existsTipoComprobanteWithDescriptionExcluding(String descripcion, int excludeId) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE + ") = ? AND " +
                    DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE + " != ?";
            String[] selectionArgs = {descripcion.trim().toUpperCase(), String.valueOf(excludeId)};

            Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE,
                    new String[]{DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de tipo de comprobante excluyendo ID: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    // ================== CLASE INTERNA PARA ESTADÍSTICAS ==================

    /**
     * Clase para almacenar estadísticas de un tipo de comprobante
     */
    public static class TipoComprobanteStats {
        public int totalComprobantes = 0;
        public double totalMonto = 0.0;

        public boolean hasTransactions() {
            return totalComprobantes > 0;
        }

        public double getPromedioMonto() {
            return totalComprobantes > 0 ? totalMonto / totalComprobantes : 0.0;
        }

        @Override
        public String toString() {
            return String.format("TipoComprobanteStats{comprobantes=%d, monto=%.2f, promedio=%.2f}",
                    totalComprobantes, totalMonto, getPromedioMonto());
        }
    }
}