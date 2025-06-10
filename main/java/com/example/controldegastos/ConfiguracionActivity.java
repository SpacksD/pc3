package com.example.controldegastos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class ConfiguracionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView cardProveedores, cardTiposGasto, cardTiposIngreso, cardTiposComprobante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        initializeViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        cardProveedores = findViewById(R.id.card_proveedores);
        cardTiposGasto = findViewById(R.id.card_tipos_gasto);
        cardTiposIngreso = findViewById(R.id.card_tipos_ingreso);
        cardTiposComprobante = findViewById(R.id.card_tipos_comprobante);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Configuración");
        }
    }

    private void setupClickListeners() {
        cardProveedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracionActivity.this, ProveedoresActivity.class);
                startActivity(intent);
            }
        });

        cardTiposGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracionActivity.this, TiposGastoActivity.class);
                startActivity(intent);
            }
        });

        cardTiposIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracionActivity.this, TiposIngresoActivity.class);
                startActivity(intent);
            }
        });

        cardTiposComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracionActivity.this, TiposComprobanteActivity.class);
                startActivity(intent);
            }
        });
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