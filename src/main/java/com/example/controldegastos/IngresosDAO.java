package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.controldegastos.*;
import java.util.ArrayList;
import java.util.List;

public class IngresosDAO {
    private DatabaseHelper dbHelper;

    public IngresosDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // MÉTODOS PARA TIPO_INGRESO
    public List<TipoIngreso> getAllTiposIngreso() {
        List<TipoIngreso> tipos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_INGRESO, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TipoIngreso tipo = new TipoIngreso();
                tipo.setIdTipoIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_INGRESO)));
                tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO)));
                tipos.add(tipo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tipos;
    }

    public long insertTipoIngreso(TipoIngreso tipoIngreso) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DESCRIPCION_INGRESO, tipoIngreso.getDescripcion());
        long id = db.insert(DatabaseHelper.TABLE_TIPO_INGRESO, null, values);
        db.close();
        return id;
    }

    // MÉTODOS PARA INGRESOS
    public List<Ingreso> getAllIngresos() {
        List<Ingreso> ingresos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT i.*, " +
                "ti.descripcion as tipo_ingreso_desc, " +
                "p.nombre_proveedor " +
                "FROM " + DatabaseHelper.TABLE_INGRESOS + " i " +
                "INNER JOIN " + DatabaseHelper.TABLE_TIPO_INGRESO + " ti ON i.id_tipo_ingreso = ti.id_tipo_ingreso " +
                "INNER JOIN " + DatabaseHelper.TABLE_PROVEEDORES + " p ON i.id_proveedor = p.id_proveedor " +
                "WHERE i.estado = 1 " +
                "ORDER BY i.fecha DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Ingreso ingreso = new Ingreso();
                ingreso.setIdIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_INGRESO)));
                ingreso.setIdTipoIngreso(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_INGRESO)));
                ingreso.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
                ingreso.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)));
                ingreso.setMonto(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MONTO)));
                ingreso.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)) == 1);

                // Datos para mostrar
                ingreso.setTipoIngresoDesc(cursor.getString(cursor.getColumnIndexOrThrow("tipo_ingreso_desc")));
                ingreso.setProveedorNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre_proveedor")));

                ingresos.add(ingreso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingresos;
    }

    public long insertIngreso(Ingreso ingreso) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID_TIPO_INGRESO, ingreso.getIdTipoIngreso());
        values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, ingreso.getIdProveedor());
        values.put(DatabaseHelper.COLUMN_FECHA, ingreso.getFecha());
        values.put(DatabaseHelper.COLUMN_MONTO, ingreso.getMonto());
        values.put(DatabaseHelper.COLUMN_ESTADO, ingreso.isEstado() ? 1 : 0);

        long id = db.insert(DatabaseHelper.TABLE_INGRESOS, null, values);
        db.close();
        return id;
    }

    public int updateIngreso(Ingreso ingreso) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID_TIPO_INGRESO, ingreso.getIdTipoIngreso());
        values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, ingreso.getIdProveedor());
        values.put(DatabaseHelper.COLUMN_FECHA, ingreso.getFecha());
        values.put(DatabaseHelper.COLUMN_MONTO, ingreso.getMonto());
        values.put(DatabaseHelper.COLUMN_ESTADO, ingreso.isEstado() ? 1 : 0);

        String whereClause = DatabaseHelper.COLUMN_ID_INGRESO + " = ?";
        String[] whereArgs = {String.valueOf(ingreso.getIdIngreso())};

        int rowsAffected = db.update(DatabaseHelper.TABLE_INGRESOS, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public int deleteIngreso(int idIngreso) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ESTADO, 0); // Soft delete

        String whereClause = DatabaseHelper.COLUMN_ID_INGRESO + " = ?";
        String[] whereArgs = {String.valueOf(idIngreso)};

        int rowsAffected = db.update(DatabaseHelper.TABLE_INGRESOS, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    // Obtener total de ingresos por período
    public double getTotalIngresosPorPeriodo(String fechaInicio, String fechaFin) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0.0;

        String query = "SELECT SUM(monto) as total FROM " + DatabaseHelper.TABLE_INGRESOS +
                " WHERE estado = 1 AND fecha BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{fechaInicio, fechaFin});

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        db.close();
        return total;
    }
}