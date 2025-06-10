package com.example.controldegastos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.controldegastos.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalIngresos, tvTotalGastos, tvBalance, tvMes;
    private CardView cardRegistrarIngresos, cardRegistrarGastos;

    // Bottom Navigation Items
    private LinearLayout navVerIngresos, navVerGastos, navReportes, navConfiguracion;

    private GastosDAO gastoDAO;
    private IngresosDAO ingresoDAO;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeDAO();
        setupClickListeners();
        setupBottomNavigation();

        decimalFormat = new DecimalFormat("#,##0.00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    private void initializeViews() {
        tvTotalIngresos = findViewById(R.id.tv_total_ingresos);
        tvTotalGastos = findViewById(R.id.tv_total_gastos);
        tvBalance = findViewById(R.id.tv_balance);
        tvMes = findViewById(R.id.tv_mes);

        // Cards de registro rápido
        cardRegistrarIngresos = findViewById(R.id.card_registrar_ingresos);
        cardRegistrarGastos = findViewById(R.id.card_registrar_gastos);

        // Bottom Navigation
        navVerIngresos = findViewById(R.id.nav_ver_ingresos);
        navVerGastos = findViewById(R.id.nav_ver_gastos);
        navReportes = findViewById(R.id.nav_reportes);
        navConfiguracion = findViewById(R.id.nav_configuracion);
    }

    private void initializeDAO() {
        gastoDAO = new GastosDAO(this);
        ingresoDAO = new IngresosDAO(this);
    }

    private void setupClickListeners() {
        // Cards de registro rápido
        cardRegistrarIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarIngresosActivity.class);
                startActivity(intent);
            }
        });

        cardRegistrarGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarGastoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigation() {
        navVerIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListaIngresosActivity.class);
                startActivity(intent);
            }
        });

        navVerGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListaGastosActivity.class);
                startActivity(intent);
            }
        });

        navReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReportesActivity.class);
                startActivity(intent);
            }
        });

        navConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateDashboard() {
        // Obtener fechas del mes actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Primer día del mes
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String fechaInicio = dateFormat.format(calendar.getTime());

        // Último día del mes
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String fechaFin = dateFormat.format(calendar.getTime());

        // Mostrar mes actual
        tvMes.setText(monthFormat.format(calendar.getTime()));

        // Obtener totales
        double totalIngresos = ingresoDAO.getTotalIngresosPorPeriodo(fechaInicio, fechaFin);
        double totalGastos = gastoDAO.getTotalGastosPorPeriodo(fechaInicio, fechaFin);
        double balance = totalIngresos - totalGastos;

        // Actualizar UI
        tvTotalIngresos.setText("S/ " + decimalFormat.format(totalIngresos));
        tvTotalGastos.setText("S/ " + decimalFormat.format(totalGastos));
        tvBalance.setText("S/ " + decimalFormat.format(balance));

        // Cambiar color del balance según sea positivo o negativo
        if (balance >= 0) {
            tvBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}