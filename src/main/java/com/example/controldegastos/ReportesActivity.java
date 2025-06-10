package com.example.gastosapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.controldegastos.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportesActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_reportes);

        initializeViews();
        setupToolbar();
        initializeDAO();
        loadReportes();

        decimalFormat = new DecimalFormat("#,##0.00");
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        cardMensual = findViewById(R.id.card_mensual);
        cardAnual = findViewById(R.id.card_anual);

        // Reporte mensual
        tvMesActual = findViewById(R.id.tv_mes_actual);
        tvTotalIngresosMes = findViewById(R.id.tv_total_ingresos_mes);
        tvTotalGastosMes = findViewById(R.id.tv_total_gastos_mes);
        tvBalanceMes = findViewById(R.id.tv_balance_mes);

        // Reporte anual
        tvAnioActual = findViewById(R.id.tv_anio_actual);
        tvTotalIngresosAnio = findViewById(R.id.tv_total_ingresos_anio);
        tvTotalGastosAnio = findViewById(R.id.tv_total_gastos_anio);
        tvBalanceAnio = findViewById(R.id.tv_balance_anio);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reportes");
        }
    }

    private void initializeDAO() {
        gastoDAO = new GastosDAO(this);
        ingresoDAO = new IngresosDAO(this);
    }

    private void loadReportes() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // REPORTE MENSUAL
        // Primer día del mes actual
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String fechaInicioMes = dateFormat.format(calendar.getTime());

        // Último día del mes actual
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String fechaFinMes = dateFormat.format(calendar.getTime());

        tvMesActual.setText("Reporte de " + monthFormat.format(calendar.getTime()));

        double totalIngresosMes = ingresoDAO.getTotalIngresosPorPeriodo(fechaInicioMes, fechaFinMes);
        double totalGastosMes = gastoDAO.getTotalGastosPorPeriodo(fechaInicioMes, fechaFinMes);
        double balanceMes = totalIngresosMes - totalGastosMes;

        tvTotalIngresosMes.setText("S/ " + decimalFormat.format(totalIngresosMes));
        tvTotalGastosMes.setText("S/ " + decimalFormat.format(totalGastosMes));
        tvBalanceMes.setText("S/ " + decimalFormat.format(balanceMes));

        // Color del balance mensual
        if (balanceMes >= 0) {
            tvBalanceMes.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvBalanceMes.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        // REPORTE ANUAL
        // Primer día del año actual
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String fechaInicioAnio = dateFormat.format(calendar.getTime());

        // Último día del año actual
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        String fechaFinAnio = dateFormat.format(calendar.getTime());

        tvAnioActual.setText("Reporte Anual " + yearFormat.format(calendar.getTime()));

        double totalIngresosAnio = ingresoDAO.getTotalIngresosPorPeriodo(fechaInicioAnio, fechaFinAnio);
        double totalGastosAnio = gastoDAO.getTotalGastosPorPeriodo(fechaInicioAnio, fechaFinAnio);
        double balanceAnio = totalIngresosAnio - totalGastosAnio;

        tvTotalIngresosAnio.setText("S/ " + decimalFormat.format(totalIngresosAnio));
        tvTotalGastosAnio.setText("S/ " + decimalFormat.format(totalGastosAnio));
        tvBalanceAnio.setText("S/ " + decimalFormat.format(balanceAnio));

        // Color del balance anual
        if (balanceAnio >= 0) {
            tvBalanceAnio.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvBalanceAnio.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
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

