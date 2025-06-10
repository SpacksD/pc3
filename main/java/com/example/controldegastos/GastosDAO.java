package com.example.controldegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.controldegastos.*;
import java.util.ArrayList;
import java.util.List;

public class GastosDAO {
    private DatabaseHelper dbHelper;

    public GastosDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // MÉTODOS PARA TIPO_GASTO
    public List<TipoGasto> getAllTiposGasto() {
        List<TipoGasto> tipos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_GASTO, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TipoGasto tipo = new TipoGasto();
                tipo.setIdTipoGasto(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_GASTO)));
                tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_GASTO)));
                tipos.add(tipo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tipos;
    }

    public long insertTipoGasto(TipoGasto tipoGasto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DESCRIPCION_GASTO, tipoGasto.getDescripcion());
        long id = db.insert(DatabaseHelper.TABLE_TIPO_GASTO, null, values);
        db.close();
        return id;
    }

    // MÉTODOS PARA TIPO_COMPROBANTE
    public List<TipoComprobante> getAllTiposComprobante() {
        List<TipoComprobante> tipos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_ESTADO_COMPROBANTE + " = ?";
        String[] selectionArgs = {"1"};

        Cursor cursor = db.query(DatabaseHelper.TABLE_TIPO_COMPROBANTE, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TipoComprobante tipo = new TipoComprobante();
                tipo.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
                tipo.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION_COMPROBANTE)));
                tipo.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO_COMPROBANTE)) == 1);
                tipos.add(tipo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tipos;
    }

    // MÉTODOS PARA PROVEEDORES
    public List<Proveedor> getAllProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_PROVEEDORES, null, null, null, null, null,
                DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Proveedor proveedor = new Proveedor();
                proveedor.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
                proveedor.setNombreProveedor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR)));
                proveedores.add(proveedor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return proveedores;
    }

    public long insertProveedor(Proveedor proveedor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOMBRE_PROVEEDOR, proveedor.getNombreProveedor());
        long id = db.insert(DatabaseHelper.TABLE_PROVEEDORES, null, values);
        db.close();
        return id;
    }

    // MÉTODOS PARA COMPROBANTES
    public List<Comprobante> getAllComprobantes() {
        List<Comprobante> comprobantes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Comprobante comprobante = new Comprobante();
                comprobante.setIdComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_COMPROBANTE)));
                comprobante.setIdTipoComprobante(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE)));
                comprobante.setIdTipoGasto(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_TIPO_GASTO)));
                comprobante.setIdProveedor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID_PROVEEDOR)));
                comprobante.setSerieNumero(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERIE_NUMERO)));
                comprobante.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FECHA)));
                comprobante.setMonto(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MONTO)));
                comprobante.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)) == 1);

                // Datos para mostrar
                comprobante.setTipoComprobanteDesc(cursor.getString(cursor.getColumnIndexOrThrow("tipo_comprobante_desc")));
                comprobante.setTipoGastoDesc(cursor.getString(cursor.getColumnIndexOrThrow("tipo_gasto_desc")));
                comprobante.setProveedorNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre_proveedor")));

                comprobantes.add(comprobante);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return comprobantes;
    }

    public long insertComprobante(Comprobante comprobante) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE, comprobante.getIdTipoComprobante());
        values.put(DatabaseHelper.COLUMN_ID_TIPO_GASTO, comprobante.getIdTipoGasto());
        values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, comprobante.getIdProveedor());
        values.put(DatabaseHelper.COLUMN_SERIE_NUMERO, comprobante.getSerieNumero());
        values.put(DatabaseHelper.COLUMN_FECHA, comprobante.getFecha());
        values.put(DatabaseHelper.COLUMN_MONTO, comprobante.getMonto());
        values.put(DatabaseHelper.COLUMN_ESTADO, comprobante.isEstado() ? 1 : 0);

        long id = db.insert(DatabaseHelper.TABLE_COMPROBANTES, null, values);
        db.close();
        return id;
    }

    public int updateComprobante(Comprobante comprobante) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID_TIPO_COMPROBANTE, comprobante.getIdTipoComprobante());
        values.put(DatabaseHelper.COLUMN_ID_TIPO_GASTO, comprobante.getIdTipoGasto());
        values.put(DatabaseHelper.COLUMN_ID_PROVEEDOR, comprobante.getIdProveedor());
        values.put(DatabaseHelper.COLUMN_SERIE_NUMERO, comprobante.getSerieNumero());
        values.put(DatabaseHelper.COLUMN_FECHA, comprobante.getFecha());
        values.put(DatabaseHelper.COLUMN_MONTO, comprobante.getMonto());
        values.put(DatabaseHelper.COLUMN_ESTADO, comprobante.isEstado() ? 1 : 0);

        String whereClause = DatabaseHelper.COLUMN_ID_COMPROBANTE + " = ?";
        String[] whereArgs = {String.valueOf(comprobante.getIdComprobante())};

        int rowsAffected = db.update(DatabaseHelper.TABLE_COMPROBANTES, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public int deleteComprobante(int idComprobante) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ESTADO, 0); // Soft delete

        String whereClause = DatabaseHelper.COLUMN_ID_COMPROBANTE + " = ?";
        String[] whereArgs = {String.valueOf(idComprobante)};

        int rowsAffected = db.update(DatabaseHelper.TABLE_COMPROBANTES, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    // Obtener total de gastos por período
    public double getTotalGastosPorPeriodo(String fechaInicio, String fechaFin) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0.0;

        String query = "SELECT SUM(monto) as total FROM " + DatabaseHelper.TABLE_COMPROBANTES +
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