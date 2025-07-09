package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresDAO {
    private static final String TAG = "ProveedoresDAO";
    private DatabaseHelper dbHelper;

    public ProveedoresDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Obtiene todos los proveedores ordenados alfabéticamente
     */
    public List<Proveedor> getAllProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_PROVEEDORES,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    Proveedor proveedor = new Proveedor();
                    proveedor.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
                    proveedor.setNombreProveedor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR)));
                    proveedores.add(proveedor);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Proveedores obtenidos: " + proveedores.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener proveedores: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return proveedores;
    }

    /**
     * Obtiene un proveedor por su ID
     */
    public Proveedor getProveedorById(int idProveedor) {
        Proveedor proveedor = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ?";
            String[] selectionArgs = {String.valueOf(idProveedor)};

            cursor = db.query(DatabaseHelper.TABLE_PROVEEDORES,
                    null, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                proveedor = new Proveedor();
                proveedor.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
                proveedor.setNombreProveedor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR)));

                Log.d(TAG, "Proveedor encontrado: " + proveedor.getNombreProveedor());
            } else {
                Log.w(TAG, "No se encontró proveedor con ID: " + idProveedor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener proveedor por ID: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return proveedor;
    }

    /**
     * Busca proveedores por nombre (para funcionalidad de búsqueda)
     */
    public List<Proveedor> searchProveedoresByName(String searchQuery) {
        List<Proveedor> proveedores = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR + " LIKE ?";
            String[] selectionArgs = {"%" + searchQuery.trim() + "%"};

            cursor = db.query(DatabaseHelper.TABLE_PROVEEDORES,
                    null, selection, selectionArgs, null, null,
                    DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    Proveedor proveedor = new Proveedor();
                    proveedor.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
                    proveedor.setNombreProveedor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR)));
                    proveedores.add(proveedor);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Búsqueda '" + searchQuery + "' - Resultados: " + proveedores.size());
        } catch (Exception e) {
            Log.e(TAG, "Error en búsqueda de proveedores: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return proveedores;
    }

    /**
     * Inserta un nuevo proveedor
     */
    public long insertProveedor(Proveedor proveedor) {
        SQLiteDatabase db = null;
        long id = -1;

        try {
            // Validar datos antes de insertar
            if (proveedor == null || proveedor.getNombreProveedor() == null ||
                    proveedor.getNombreProveedor().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del proveedor no puede estar vacío");
            }

            // Verificar si ya existe un proveedor con el mismo nombre
            if (existsProveedorWithName(proveedor.getNombreProveedor().trim())) {
                throw new IllegalArgumentException("Ya existe un proveedor con ese nombre");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR, proveedor.getNombreProveedor().trim().toUpperCase());

            id = db.insert(DatabaseHelper.TABLE_PROVEEDORES, null, values);

            if (id > 0) {
                Log.d(TAG, "Proveedor insertado exitosamente con ID: " + id);
            } else {
                Log.e(TAG, "Error al insertar proveedor");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al insertar proveedor: " + e.getMessage(), e);
            throw e; // Re-lanzar para que el Activity pueda manejarlo
        } finally {
            if (db != null) db.close();
        }

        return id;
    }

    /**
     * Actualiza un proveedor existente
     */
    public int updateProveedor(Proveedor proveedor) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            // Validar datos antes de actualizar
            if (proveedor == null || proveedor.getNombreProveedor() == null ||
                    proveedor.getNombreProveedor().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del proveedor no puede estar vacío");
            }

            if (proveedor.getIdProveedor() <= 0) {
                throw new IllegalArgumentException("ID de proveedor inválido");
            }

            // Verificar si ya existe otro proveedor con el mismo nombre (excluyendo el actual)
            if (existsProveedorWithNameExcluding(proveedor.getNombreProveedor().trim(), proveedor.getIdProveedor())) {
                throw new IllegalArgumentException("Ya existe otro proveedor con ese nombre");
            }

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR, proveedor.getNombreProveedor().trim().toUpperCase());

            String whereClause = DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ?";
            String[] whereArgs = {String.valueOf(proveedor.getIdProveedor())};

            rowsAffected = db.update(DatabaseHelper.TABLE_PROVEEDORES, values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Proveedor actualizado exitosamente. Filas afectadas: " + rowsAffected);
            } else {
                Log.w(TAG, "No se actualizó ningún proveedor. Posiblemente no existe el ID: " + proveedor.getIdProveedor());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar proveedor: " + e.getMessage(), e);
            throw e; // Re-lanzar para que el Activity pueda manejarlo
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Verifica si un proveedor está siendo utilizado en comprobantes o ingresos
     */
    public boolean isProveedorInUse(int idProveedor) {
        SQLiteDatabase db = null;
        boolean inUse = false;

        try {
            db = dbHelper.getReadableDatabase();

            // Verificar en comprobantes activos
            String queryComprobantes = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE " + DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursorComprobantes = db.rawQuery(queryComprobantes, new String[]{String.valueOf(idProveedor)});
            if (cursorComprobantes.moveToFirst()) {
                int countComprobantes = cursorComprobantes.getInt(0);
                if (countComprobantes > 0) {
                    inUse = true;
                    Log.d(TAG, "Proveedor ID " + idProveedor + " tiene " + countComprobantes + " comprobantes asociados");
                }
            }
            cursorComprobantes.close();

            // Si no está en uso en comprobantes, verificar en ingresos
            if (!inUse) {
                String queryIngresos = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_INGRESOS +
                        " WHERE " + DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ? AND " +
                        DatabaseHelper.COLUMN_ESTADO + " = 1";

                Cursor cursorIngresos = db.rawQuery(queryIngresos, new String[]{String.valueOf(idProveedor)});
                if (cursorIngresos.moveToFirst()) {
                    int countIngresos = cursorIngresos.getInt(0);
                    if (countIngresos > 0) {
                        inUse = true;
                        Log.d(TAG, "Proveedor ID " + idProveedor + " tiene " + countIngresos + " ingresos asociados");
                    }
                }
                cursorIngresos.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al verificar uso del proveedor: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return inUse;
    }

    /**
     * Elimina un proveedor (solo si no está en uso)
     */
    public boolean deleteProveedor(int idProveedor) {
        // Primero verificar si está en uso
        if (isProveedorInUse(idProveedor)) {
            Log.w(TAG, "No se puede eliminar el proveedor ID " + idProveedor + " porque está en uso");
            return false;
        }

        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ?";
            String[] whereArgs = {String.valueOf(idProveedor)};

            rowsAffected = db.delete(DatabaseHelper.TABLE_PROVEEDORES, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d(TAG, "Proveedor eliminado exitosamente. ID: " + idProveedor);
                return true;
            } else {
                Log.w(TAG, "No se encontró el proveedor para eliminar. ID: " + idProveedor);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar proveedor: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Obtiene el conteo total de proveedores
     */
    public int getTotalProveedoresCount() {
        SQLiteDatabase db = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_PROVEEDORES;

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener conteo de proveedores: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return count;
    }

    /**
     * Obtiene estadísticas de uso del proveedor
     */
    public ProveedorStats getProveedorStats(int idProveedor) {
        SQLiteDatabase db = null;
        ProveedorStats stats = new ProveedorStats();

        try {
            db = dbHelper.getReadableDatabase();

            // Contar comprobantes
            String queryComprobantes = "SELECT COUNT(*), COALESCE(SUM(" + DatabaseHelper.COLUMN_MONTO + "), 0) FROM " +
                    DatabaseHelper.TABLE_COMPROBANTES +
                    " WHERE " + DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursorComprobantes = db.rawQuery(queryComprobantes, new String[]{String.valueOf(idProveedor)});
            if (cursorComprobantes.moveToFirst()) {
                stats.totalComprobantes = cursorComprobantes.getInt(0);
                stats.totalGastos = cursorComprobantes.getDouble(1);
            }
            cursorComprobantes.close();

            // Contar ingresos
            String queryIngresos = "SELECT COUNT(*), COALESCE(SUM(" + DatabaseHelper.COLUMN_MONTO + "), 0) FROM " +
                    DatabaseHelper.TABLE_INGRESOS +
                    " WHERE " + DatabaseHelper.COLUMN_ID_PROVEEDOR + " = ? AND " +
                    DatabaseHelper.COLUMN_ESTADO + " = 1";

            Cursor cursorIngresos = db.rawQuery(queryIngresos, new String[]{String.valueOf(idProveedor)});
            if (cursorIngresos.moveToFirst()) {
                stats.totalIngresos = cursorIngresos.getInt(0);
                stats.totalMontoIngresos = cursorIngresos.getDouble(1);
            }
            cursorIngresos.close();

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener estadísticas del proveedor: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return stats;
    }

    // ================== MÉTODOS PRIVADOS AUXILIARES ==================

    /**
     * Verifica si existe un proveedor con el nombre dado
     */
    private boolean existsProveedorWithName(String nombre) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR + ") = ?";
            String[] selectionArgs = {nombre.trim().toUpperCase()};

            Cursor cursor = db.query(DatabaseHelper.TABLE_PROVEEDORES,
                    new String[]{DatabaseHelper.COLUMN_ID_PROVEEDOR},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de proveedor: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    /**
     * Verifica si existe un proveedor con el nombre dado, excluyendo un ID específico
     */
    private boolean existsProveedorWithNameExcluding(String nombre, int excludeId) {
        SQLiteDatabase db = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = "UPPER(" + DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR + ") = ? AND " +
                    DatabaseHelper.COLUMN_ID_PROVEEDOR + " != ?";
            String[] selectionArgs = {nombre.trim().toUpperCase(), String.valueOf(excludeId)};

            Cursor cursor = db.query(DatabaseHelper.TABLE_PROVEEDORES,
                    new String[]{DatabaseHelper.COLUMN_ID_PROVEEDOR},
                    selection, selectionArgs, null, null, null);
            exists = cursor.moveToFirst();
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar existencia de proveedor excluyendo ID: " + e.getMessage(), e);
        } finally {
            if (db != null) db.close();
        }

        return exists;
    }

    // ================== CLASE INTERNA PARA ESTADÍSTICAS ==================

    /**
     * Clase para almacenar estadísticas de un proveedor
     */
    public static class ProveedorStats {
        public int totalComprobantes = 0;
        public int totalIngresos = 0;
        public double totalGastos = 0.0;
        public double totalMontoIngresos = 0.0;

        public boolean hasTransactions() {
            return totalComprobantes > 0 || totalIngresos > 0;
        }

        @Override
        public String toString() {
            return String.format("Stats{comprobantes=%d, ingresos=%d, gastos=%.2f, montoIngresos=%.2f}",
                    totalComprobantes, totalIngresos, totalGastos, totalMontoIngresos);
        }
    }
}