package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TiposIngresoDAO {
    private static final String TAG = "TiposIngresoDAO";
    private DatabaseHelper dbHelper;

    public TiposIngresoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Obtiene todos los tipos de ingreso
     */
    public List<TipoIngreso> getAllTiposIngreso() {
        List<TipoIngreso> tipos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_TIPO_INGRESO,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_DESCRIPCION_INGRESO + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    TipoIngreso tipo = new TipoIngreso();
                    tipo.setIdTipoIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_INGRESO)));
                    tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO)));
                    tipos.add(tipo);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Tipos de ingreso obtenidos: " + tipos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipos de ingreso: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipos;
    }

    /**
     * Obtiene un tipo de ingreso por ID
     */
    public TipoIngreso getTipoIngresoById(int idTipoIngreso) {
        TipoIngreso tipo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_ID_TIPO_INGRESO + " = ?";
            String[] selectionArgs = {String.valueOf(idTipoIngreso)};

            cursor = db.query(DatabaseHelper.TABLE_TIPO_INGRESO,
                    null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                tipo = new TipoIngreso();
                tipo.setIdTipoIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_INGRESO)));
                tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener tipo de ingreso por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipo;
    }

    /**
     * Inserta un nuevo tipo de ingreso
     */
    public long insertTipoIngreso(TipoIngreso tipoIngreso) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            // Validaciones
            if (tipoIngreso == null || tipoIngreso.getDescripcion() == null ||
                    tipoIngreso.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del tipo de ingreso no puede estar vacía");
            }

            // Verificar si ya existe
            if (existsTipoIngresoWithDescription(tipoIngreso.getDescripcion().trim())) {
                throw new IllegalArgumentException("Ya existe un tipo de ingreso con esa descripción");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO, tipoIngreso.getDescripcion().trim().toUpperCase());

            id = db.insert(DatabaseHelper.TABLE_TIPO_INGRESO, null, values);

            if (id > 0) {
                Log.d(TAG, "Tipo de ingreso insertado con ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar tipo de ingreso: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    /**
     * Actualiza un tipo de ingreso
     */
    public int updateTipoIngreso(TipoIngreso tipoIngreso) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Validaciones
            if (tipoIngreso == null || tipoIngreso.getDescripcion() == null ||
                    tipoIngreso.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del tipo de ingreso no puede estar vacía");
            }

            if (tipoIngreso.getIdTipoIngreso() <= 0) {
                throw new IllegalArgumentException("ID de tipo de ingreso inválido");
            }

            // Verificar duplicados
            if (existsTipoIngresoWithDescriptionExcluding(tipoIngreso.getDescripcion().trim(), tipoIngreso.getIdTipoIngreso())) {
                throw new IllegalArgumentException("Ya existe otro tipo de ingreso con esa descripción");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO, tipoIngreso.getDescripcion().trim().toUpperCase());

            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_INGRESO + " = ?";
            String[] whereArgs = {String.valueOf(tipoIngreso.getIdTipoIngreso())};

            rowsAffected = db.update(DatabaseHelper.TABLE_TIPO_INGRESO, values, whereClause, whereArgs);

            Log.d(TAG, "Tipo de ingreso actualizado. Filas afectadas: " + rowsAffected);
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar tipo de ingreso: " + e.getMessage(), e);
            throw e;
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Verifica si un tipo de ingreso está en uso
     */
    public boolean isTipoIngresoInUse(int idTipoIngreso) {
        SQLiteDatabase db = null;
        boolean inUse = false;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_INGRESOS +
                    " WHERE " + DatabaseHelper.COLUMN_ID_TIPO_INGRESO + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoIngreso)});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                inUse = count > 0;
                Log.d(TAG, "Tipo de ingreso ID " + idTipoIngreso + " tiene " + count + " ingresos asociados");
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar uso del tipo de ingreso: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return inUse;
    }

    /**
     * Elimina un tipo de ingreso (solo si no está en uso)
     */
    public boolean deleteTipoIngreso(int idTipoIngreso) {
        if (isTipoIngresoInUse(idTipoIngreso)) {
            Log.w(TAG, "No se puede eliminar el tipo de ingreso ID " + idTipoIngreso + " porque está en uso");
            return false;
        }

        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_ID_TIPO_INGRESO + " = ?";
            String[] whereArgs = {String.valueOf(idTipoIngreso)};

            rowsAffected = db.delete(DatabaseHelper.TABLE_TIPO_INGRESO, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Tipo de ingreso eliminado. ID: " + idTipoIngreso);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar tipo de ingreso: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return false;
    }

    /**
     * Obtiene estadísticas de uso de un tipo de ingreso
     */
    public TipoIngresoStats getTipoIngresoStats(int idTipoIngreso) {
        SQLiteDatabase db = null;
        TipoIngresoStats stats = new TipoIngresoStats();

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*), COALESCE(SUM(" + DatabaseHelper.COLUMN_MONTO + "), 0) FROM " +
                    DatabaseHelper.TABLE_INGRESOS +
                    " WHERE " + DatabaseHelper.COLUMN_ID_TIPO_INGRESO + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idTipoIngreso)});
            if (cursor.moveToFirst()) {
                stats.totalIngresos = cursor.getInt(0);
                stats.totalMonto = cursor.getDouble(1);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estadísticas del tipo de ingreso: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return stats;
    }

    /**
     * Busca tipos de ingreso por descripción
     */
    public List<TipoIngreso> searchTiposIngresoByDescription(String searchQuery) {
        List<TipoIngreso> tipos = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_DESCRIPCION_INGRESO + " LIKE ?";
            String[] selectionArgs = {"%" + searchQuery.trim() + "%"};

            cursor = db.query(DatabaseHelper.TABLE_TIPO_INGRESO,
                    null, selection, selectionArgs, null, null,
                    DatabaseHelper.COLUMN_DESCRIPCION_INGRESO + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    TipoIngreso tipo = new TipoIngreso();
                    tipo.setIdTipoIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_INGRESO)));
                    tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO)));
                    tipos.add(tipo);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Búsqueda '" + searchQuery + "' - Resultados: " + tipos.size());
        } catch (Exception e) {
            Log.e(TAG, "Error en búsqueda de tipos de ingreso: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return tipos;
    }

    /**
     * Obtiene el conteo total de tipos de ingreso
     */
    public int getTotalTiposIngresoCount() {
        SQLiteDatabase db = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TIPO_INGRESO;

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener conteo de tipos de ingreso: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return count;
    }

    // ================== MÉTODOS PRIVADOS ==================

    private boolean existsTipoIngresoWithDescription(String descripcion) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_DESCRIPCION_INGRESO + ") = ?";
            String[] selectionArgs = {descripcion.trim().toUpperCase()};

            Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_INGRESO,
                    new String[]{DatabaseHelper.COLUMN_ID_TIPO_INGRESO},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de tipo de ingreso: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    private boolean existsTipoIngresoWithDescriptionExcluding(String descripcion, int excludeId) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_DESCRIPCION_INGRESO + ") = ? AND " +
                    DatabaseHelper.COLUMN_ID_TIPO_INGRESO + " != ?";
            String[] selectionArgs = {descripcion.trim().toUpperCase(), String.valueOf(excludeId)};

            Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_INGRESO,
                    new String[]{DatabaseHelper.COLUMN_ID_TIPO_INGRESO},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de tipo de ingreso excluyendo ID: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    // ================== CLASE PARA ESTADÍSTICAS ==================

    public static class TipoIngresoStats {
        public int totalIngresos = 0;
        public double totalMonto = 0.0;

        public boolean hasTransactions() {
            return totalIngresos > 0;
        }

        @Override
        public String toString() {
            return String.format("Stats{ingresos=%d, monto=%.2f}", totalIngresos, totalMonto);
        }
    }
}