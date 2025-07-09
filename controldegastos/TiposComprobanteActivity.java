package com.example.controldegastos;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TiposComprobanteActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Por ahora usamos un layout simple hasta que se implemente completamente
        setContentView(R.layout.activity_item_ingreso); // Layout temporal

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();

        // Mensaje temporal
        Toast.makeText(this, "Tipos de Comprobante - En desarrollo", Toast.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tipos de Comprobante");
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