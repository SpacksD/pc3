package com.example.controldegastos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_gastos_ingresos.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla TIPO_GASTO
    public static final String TABLE_TIPO_GASTO = "tipo_gasto";
    public static final String COLUMN_ID_TIPO_GASTO = "id_tipo_gasto";
    public static final String COLUMN_DESCRIPCION_GASTO = "descripcion";

    // Tabla TIPO_COMPROBANTE
    public static final String TABLE_TIPO_COMPROBANTE = "tipo_comprobante";
    public static final String COLUMN_ID_TIPO_COMPROBANTE = "id_tipo_comprobante";
    public static final String COLUMN_DESCRIPCION_COMPROBANTE = "descripcion";
    public static final String COLUMN_ESTADO_COMPROBANTE = "estado";

    // Tabla PROVEEDORES
    public static final String TABLE_PROVEEDORES = "proveedores";
    public static final String COLUMN_ID_PROVEEDOR = "id_proveedor";
    public static final String COLUMN_NOMBRE_PROVEEDOR = "nombre_proveedor";

    // Tabla COMPROBANTES
    public static final String TABLE_COMPROBANTES = "comprobantes";
    public static final String COLUMN_ID_COMPROBANTE = "id_comprobante";
    public static final String COLUMN_SERIE_NUMERO = "serie_numero";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_MONTO = "monto";
    public static final String COLUMN_ESTADO = "estado";

    // Tabla TIPO_INGRESO
    public static final String TABLE_TIPO_INGRESO = "tipo_ingreso";
    public static final String COLUMN_ID_TIPO_INGRESO = "id_tipo_ingreso";
    public static final String COLUMN_DESCRIPCION_INGRESO = "descripcion";

    // Tabla INGRESOS
    public static final String TABLE_INGRESOS = "ingresos";
    public static final String COLUMN_ID_INGRESO = "id_ingreso";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla TIPO_GASTO
        String createTipoGastoTable = "CREATE TABLE " + TABLE_TIPO_GASTO + " (" +
                COLUMN_ID_TIPO_GASTO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPCION_GASTO + " TEXT NOT NULL)";
        db.execSQL(createTipoGastoTable);

        // Crear tabla TIPO_COMPROBANTE
        String createTipoComprobanteTable = "CREATE TABLE " + TABLE_TIPO_COMPROBANTE + " (" +
                COLUMN_ID_TIPO_COMPROBANTE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPCION_COMPROBANTE + " TEXT NOT NULL, " +
                COLUMN_ESTADO_COMPROBANTE + " INTEGER NOT NULL DEFAULT 1)";
        db.execSQL(createTipoComprobanteTable);

        // Crear tabla PROVEEDORES
        String createProveedoresTable = "CREATE TABLE " + TABLE_PROVEEDORES + " (" +
                COLUMN_ID_PROVEEDOR + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE_PROVEEDOR + " TEXT NOT NULL)";
        db.execSQL(createProveedoresTable);

        // Crear tabla COMPROBANTES
        String createComprobantesTable = "CREATE TABLE " + TABLE_COMPROBANTES + " (" +
                COLUMN_ID_COMPROBANTE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID_TIPO_COMPROBANTE + " INTEGER NOT NULL, " +
                COLUMN_ID_TIPO_GASTO + " INTEGER NOT NULL, " +
                COLUMN_ID_PROVEEDOR + " INTEGER NOT NULL, " +
                COLUMN_SERIE_NUMERO + " TEXT NOT NULL, " +
                COLUMN_FECHA + " TEXT NOT NULL, " +
                COLUMN_MONTO + " REAL NOT NULL, " +
                COLUMN_ESTADO + " INTEGER NOT NULL DEFAULT 1, " +
                "FOREIGN KEY(" + COLUMN_ID_TIPO_COMPROBANTE + ") REFERENCES " + TABLE_TIPO_COMPROBANTE + "(" + COLUMN_ID_TIPO_COMPROBANTE + "), " +
                "FOREIGN KEY(" + COLUMN_ID_TIPO_GASTO + ") REFERENCES " + TABLE_TIPO_GASTO + "(" + COLUMN_ID_TIPO_GASTO + "), " +
                "FOREIGN KEY(" + COLUMN_ID_PROVEEDOR + ") REFERENCES " + TABLE_PROVEEDORES + "(" + COLUMN_ID_PROVEEDOR + "))";
        db.execSQL(createComprobantesTable);

        // Crear tabla TIPO_INGRESO
        String createTipoIngresoTable = "CREATE TABLE " + TABLE_TIPO_INGRESO + " (" +
                COLUMN_ID_TIPO_INGRESO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPCION_INGRESO + " TEXT NOT NULL)";
        db.execSQL(createTipoIngresoTable);

        // Crear tabla INGRESOS
        String createIngresosTable = "CREATE TABLE " + TABLE_INGRESOS + " (" +
                COLUMN_ID_INGRESO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID_TIPO_INGRESO + " INTEGER NOT NULL, " +
                COLUMN_ID_PROVEEDOR + " INTEGER NOT NULL, " +
                COLUMN_FECHA + " TEXT NOT NULL, " +
                COLUMN_MONTO + " REAL NOT NULL, " +
                COLUMN_ESTADO + " INTEGER NOT NULL DEFAULT 1, " +
                "FOREIGN KEY(" + COLUMN_ID_TIPO_INGRESO + ") REFERENCES " + TABLE_TIPO_INGRESO + "(" + COLUMN_ID_TIPO_INGRESO + "), " +
                "FOREIGN KEY(" + COLUMN_ID_PROVEEDOR + ") REFERENCES " + TABLE_PROVEEDORES + "(" + COLUMN_ID_PROVEEDOR + "))";
        db.execSQL(createIngresosTable);

        // Insertar datos de prueba
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Tipos de gasto
        db.execSQL("INSERT INTO " + TABLE_TIPO_GASTO + " (" + COLUMN_DESCRIPCION_GASTO + ") VALUES ('ALIMENTOS')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_GASTO + " (" + COLUMN_DESCRIPCION_GASTO + ") VALUES ('TRANSPORTE')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_GASTO + " (" + COLUMN_DESCRIPCION_GASTO + ") VALUES ('COMBUSTIBLE')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_GASTO + " (" + COLUMN_DESCRIPCION_GASTO + ") VALUES ('PAGO DE SERVICIOS')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_GASTO + " (" + COLUMN_DESCRIPCION_GASTO + ") VALUES ('OTROS')");

        // Tipos de comprobante
        db.execSQL("INSERT INTO " + TABLE_TIPO_COMPROBANTE + " (" + COLUMN_DESCRIPCION_COMPROBANTE + ") VALUES ('BOLETA')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_COMPROBANTE + " (" + COLUMN_DESCRIPCION_COMPROBANTE + ") VALUES ('FACTURA')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_COMPROBANTE + " (" + COLUMN_DESCRIPCION_COMPROBANTE + ") VALUES ('TICKET')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_COMPROBANTE + " (" + COLUMN_DESCRIPCION_COMPROBANTE + ") VALUES ('OTRO')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_COMPROBANTE + " (" + COLUMN_DESCRIPCION_COMPROBANTE + ") VALUES ('NINGUNO')");

        // Tipos de ingreso
        db.execSQL("INSERT INTO " + TABLE_TIPO_INGRESO + " (" + COLUMN_DESCRIPCION_INGRESO + ") VALUES ('SALARIO')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_INGRESO + " (" + COLUMN_DESCRIPCION_INGRESO + ") VALUES ('FREELANCE')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_INGRESO + " (" + COLUMN_DESCRIPCION_INGRESO + ") VALUES ('VENTA')");
        db.execSQL("INSERT INTO " + TABLE_TIPO_INGRESO + " (" + COLUMN_DESCRIPCION_INGRESO + ") VALUES ('OTROS')");

        // Proveedores de ejemplo
        db.execSQL("INSERT INTO " + TABLE_PROVEEDORES + " (" + COLUMN_NOMBRE_PROVEEDOR + ") VALUES ('SUPERMERCADO XYZ')");
        db.execSQL("INSERT INTO " + TABLE_PROVEEDORES + " (" + COLUMN_NOMBRE_PROVEEDOR + ") VALUES ('ESTACION DE SERVICIO ABC')");
        db.execSQL("INSERT INTO " + TABLE_PROVEEDORES + " (" + COLUMN_NOMBRE_PROVEEDOR + ") VALUES ('EMPRESA DE SERVICIOS')");
        db.execSQL("INSERT INTO " + TABLE_PROVEEDORES + " (" + COLUMN_NOMBRE_PROVEEDOR + ") VALUES ('CLIENTE GENERAL')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGRESOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO_INGRESO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPROBANTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROVEEDORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO_COMPROBANTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO_GASTO);
        onCreate(db);
    }
}