package com.example.controldegastos;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportesActivity extends AppCompatActivity {

    private static final String TAG = "ReportesActivity";

    private Toolbar toolbar;
    private TextView tvMesActual, tvTotalIngresosMes, tvTotalGastosMes, tvBalanceMes;
    private TextView tvAnioActual, tvTotalIngresosAnio, tvTotalGastosAnio, tvBalanceAnio;
    private CardView cardMensual, cardAnual;

    private GastosDAO gastoDAO;
    private IngresosDAO ingresoDAO;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_reportes);
            Log.d(TAG, "Layout cargado correctamente");

            decimalFormat = new DecimalFormat("#,##0.00");

            initializeViews();
            setupToolbar();
            initializeDAO();
            loadReportes();

        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate: " + e.getMessage(), e);
            // Si hay error, mostrar una actividad básica
            setContentView(android.R.layout.simple_list_item_1);
        }
    }

    private void initializeViews() {
        try {
            toolbar = findViewById(R.id.toolbar);

            // Verificar si existen las cards
            cardMensual = findViewById(R.id.card_mensual);
            cardAnual = findViewById(R.id.card_anual);

            // Reporte mensual - verificar uno por uno
            tvMesActual = findViewById(R.id.tv_mes_actual);
            tvTotalIngresosMes = findViewById(R.id.tv_total_ingresos_mes);
            tvTotalGastosMes = findViewById(R.id.tv_total_gastos_mes);
            tvBalanceMes = findViewById(R.id.tv_balance_mes);

            // Reporte anual - verificar uno por uno
            tvAnioActual = findViewById(R.id.tv_anio_actual);
            tvTotalIngresosAnio = findViewById(R.id.tv_total_ingresos_anio);
            tvTotalGastosAnio = findViewById(R.id.tv_total_gastos_anio);
            tvBalanceAnio = findViewById(R.id.tv_balance_anio);

            // Log para debugging
            Log.d(TAG, "toolbar: " + (toolbar != null ? "OK" : "NULL"));
            Log.d(TAG, "tvMesActual: " + (tvMesActual != null ? "OK" : "NULL"));
            Log.d(TAG, "tvAnioActual: " + (tvAnioActual != null ? "OK" : "NULL"));

        } catch (Exception e) {
            Log.e(TAG, "Error en initializeViews: " + e.getMessage(), e);
        }
    }

    private void setupToolbar() {
        try {
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("Reportes");
                }
                Log.d(TAG, "Toolbar configurado correctamente");
            } else {
                Log.w(TAG, "Toolbar es null, saltando configuración");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando toolbar: " + e.getMessage(), e);
        }
    }

    private void initializeDAO() {
        try {
            gastoDAO = new GastosDAO(this);
            ingresoDAO = new IngresosDAO(this);
            Log.d(TAG, "DAOs inicializados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando DAOs: " + e.getMessage(), e);
        }
    }

    private void loadReportes() {
        try {
            if (gastoDAO == null || ingresoDAO == null) {
                Log.e(TAG, "DAOs no están inicializados");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // REPORTE MENSUAL
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String fechaInicioMes = dateFormat.format(calendar.getTime());

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String fechaFinMes = dateFormat.format(calendar.getTime());

            // Verificar antes de asignar
            if (tvMesActual != null) {
                tvMesActual.setText("Reporte de " + monthFormat.format(calendar.getTime()));
            }

            double totalIngresosMes = ingresoDAO.getTotalIngresosPorPeriodo(fechaInicioMes, fechaFinMes);
            double totalGastosMes = gastoDAO.getTotalGastosPorPeriodo(fechaInicioMes, fechaFinMes);
            double balanceMes = totalIngresosMes - totalGastosMes;

            // Asignar solo si los TextViews existen
            if (tvTotalIngresosMes != null) {
                tvTotalIngresosMes.setText("S/ " + decimalFormat.format(totalIngresosMes));
            }
            if (tvTotalGastosMes != null) {
                tvTotalGastosMes.setText("S/ " + decimalFormat.format(totalGastosMes));
            }
            if (tvBalanceMes != null) {
                tvBalanceMes.setText("S/ " + decimalFormat.format(balanceMes));

                // Color del balance mensual
                if (balanceMes >= 0) {
                    tvBalanceMes.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    tvBalanceMes.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            // REPORTE ANUAL
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            String fechaInicioAnio = dateFormat.format(calendar.getTime());

            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 31);
            String fechaFinAnio = dateFormat.format(calendar.getTime());

            if (tvAnioActual != null) {
                tvAnioActual.setText("Reporte Anual " + yearFormat.format(calendar.getTime()));
            }

            double totalIngresosAnio = ingresoDAO.getTotalIngresosPorPeriodo(fechaInicioAnio, fechaFinAnio);
            double totalGastosAnio = gastoDAO.getTotalGastosPorPeriodo(fechaInicioAnio, fechaFinAnio);
            double balanceAnio = totalIngresosAnio - totalGastosAnio;

            if (tvTotalIngresosAnio != null) {
                tvTotalIngresosAnio.setText("S/ " + decimalFormat.format(totalIngresosAnio));
            }
            if (tvTotalGastosAnio != null) {
                tvTotalGastosAnio.setText("S/ " + decimalFormat.format(totalGastosAnio));
            }
            if (tvBalanceAnio != null) {
                tvBalanceAnio.setText("S/ " + decimalFormat.format(balanceAnio));

                // Color del balance anual
                if (balanceAnio >= 0) {
                    tvBalanceAnio.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    tvBalanceAnio.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            Log.d(TAG, "Reportes cargados correctamente");

        } catch (Exception e) {
            Log.e(TAG, "Error cargando reportes: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}