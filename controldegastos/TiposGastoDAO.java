package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TiposGastoDAO {
    private static final String TAG = "TiposGastoDAO";
    private DatabaseHelper dbHelper;

    public TiposGastoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Obtiene todos los tipos de gasto
     */
    public List<TipoGasto> getAllTiposGasto() {
        List<TipoGasto> tipos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_TIPO_GASTO,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_DESCRIPCION_GASTO + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    TipoGasto tipo = new TipoGasto();
                    tipo.setIdTipoGasto(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_GASTO)));
                    tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_GASTO)));
                    tipos.add(tipo);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Tipos de gasto obtenidos: " + tipos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipos de gasto: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipos;
    }

    /**
     * Obtiene un tipo de gasto por ID
     */
    public TipoGasto getTipoGastoById(int idTipoGasto) {
        TipoGasto tipo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_ID_TIPO_GASTO + " = ?";
            String[] selectionArgs = {String.valueOf(idTipoGasto)};

            cursor = db.query(DatabaseHelper.TABLE_TIPO_GASTO,
                    null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                tipo = new TipoGasto();
                tipo.setIdTipoGasto(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_GASTO)));
                tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_GASTO)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipo de gasto por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipo;
    }

    /**
     * Inserta un nuevo tipo de gasto
     */
    public long insertTipoGasto(TipoGasto tipoGasto) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            // Validaciones
            if (tipoGasto == null || tipoGasto.getDescripcion() == null ||
                    tipoGasto.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del tipo de gasto no puede estar vacía");
            }

            // Verificar si ya existe
            if (existsTipoGastoWithDescription(tipoGasto.getDescripcion().trim())) {
                throw new IllegalArgumentException("Ya existe un tipo de gasto con esa descripción");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPCION_GASTO, tipoGasto.getDescripcion().trim().toUpperCase());

            id = db.insert(DatabaseHelper.TABLE_TIPO_GASTO, null, values);

            if (id > 0) {
                Log.d(TAG, "Tipo de gasto insertado con ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar tipo de gasto: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    /**
     * Actualiza un tipo de gasto
     */
    public int updateTipoGasto(TipoGasto tipoGasto) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Validaciones
            if (tipoGasto == null || tipoGasto.getDescripcion() == null ||
                    tipoGasto.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del tipo de gasto no puede estar vacía");
            }

            if (tipoGasto.getIdTipoGasto() <= 0) {
                throw new IllegalArgumentException("ID de tipo de gasto inválido");
            }

            // Verificar duplicados
            if (existsTipoGastoWithDescriptionExcluding(tipoGasto.getDescripcion().trim(), tipoGasto.getIdTipoGasto())) {
                throw new IllegalArgumentException("Ya existe otro tipo de gasto con esa descripción");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPCION_GASTO, tipoGasto.getDescripcion().trim().toUpperCase());

            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_GASTO + " = ?";
            String[] whereArgs = {String.valueOf(tipoGasto.getIdTipoGasto())};

            rowsAffected = db.update(DatabaseHelper.TABLE_TIPO_GASTO, values, whereClause, whereArgs);

            Log.d(TAG, "Tipo de gasto actualizado. Filas afectadas: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar tipo de gasto: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Verifica si un tipo de gasto está en uso
     */
    public boolean isTipoGastoInUse(int idTipoGasto) {
        SQLiteDatabase db = null;
        boolean inUse = false;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE " + DatabaseHelper.COLUMN_ID_TIPO_GASTO + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoGasto)});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                inUse = count > 0;
                Log.d(TAG, "Tipo de gasto ID " + idTipoGasto + " tiene " + count + " comprobantes asociados");
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar uso del tipo de gasto: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return inUse;
    }

    /**
     * Elimina un tipo de gasto (solo si no está en uso)
     */
    public boolean deleteTipoGasto(int idTipoGasto) {
        if (isTipoGastoInUse(idTipoGasto)) {
            Log.w(TAG, "No se puede eliminar el tipo de gasto ID " + idTipoGasto + " porque está en uso");
            return false;
        }

        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_GASTO + " = ?";
            String[] whereArgs = {String.valueOf(idTipoGasto)};

            rowsAffected = db.delete(DatabaseHelper.TABLE_TIPO_GASTO, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Tipo de gasto eliminado. ID: " + idTipoGasto);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar tipo de gasto: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return false;
    }

    /**
     * Obtiene estadísticas de uso de un tipo de gasto
     */
    public TipoGastoStats getTipoGastoStats(int idTipoGasto) {
        SQLiteDatabase db = null;
        TipoGastoStats stats = new TipoGastoStats();

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*), COALESCE(SUM(" + DatabaseHelper.COLUMN_MONTO + "), 0) FROM " +
                    DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE " + DatabaseHelper.COLUMN_ID_TIPO_GASTO + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoGasto)});
            if (cursor.moveToFirst()) {
                stats.totalComprobantes = cursor.getInt(0);
                stats.totalMonto = cursor.getDouble(1);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estadísticas del tipo de gasto: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return stats;
    }

    // ================== MÉTODOS PRIVADOS ==================

    private boolean existsTipoGastoWithDescription(String descripcion) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_DESCRIPCION_GASTO + ") = ?";
            String[] selectionArgs = {descripcion.trim().toUpperCase()};

            Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_GASTO,
                    new String[]{DatabaseHelper.COLUMN_ID_TIPO_GASTO},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de tipo de gasto: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    private boolean existsTipoGastoWithDescriptionExcluding(String descripcion, int excludeId) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_DESCRIPCION_GASTO + ") = ? AND " +
                    DatabaseHelper.COLUMN_ID_TIPO_GASTO + " != ?";
            String[] selectionArgs = {descripcion.trim().toUpperCase(), String.valueOf(excludeId)};

            Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_GASTO,
                    new String[]{DatabaseHelper.COLUMN_ID_TIPO_GASTO},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de tipo de gasto excluyendo ID: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    // ================== CLASE PARA ESTADÍSTICAS ==================

    public static class TipoGastoStats {
        public int totalComprobantes = 0;
        public double totalMonto = 0.0;

        public boolean hasTransactions() {
            return totalComprobantes > 0;
        }

        @Override
        public String toString() {
            return String.format("Stats{comprobantes=%d, monto=%.2f}", totalComprobantes, totalMonto);
        }
    }
}