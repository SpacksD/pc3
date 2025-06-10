package com.example.controldegastos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controldegastos.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.List;

public class ListaGastosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvTotalGastos;
    LinearLayout tvEmptyView;
    private FloatingActionButton fabAgregar;

    private ComprobantesAdapter adapter;
    private GastosDAO gastoDAO;
    private List<Comprobante> listaComprobantes;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gastos);

        initializeViews();
        setupToolbar();
        initializeDAO();
        setupRecyclerView();
        setupListeners();

        decimalFormat = new DecimalFormat("#,##0.00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        tvTotalGastos = findViewById(R.id.tv_total_gastos);
        tvEmptyView = findViewById(R.id.tv_empty_view);
        fabAgregar = findViewById(R.id.fab_agregar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lista de Gastos");
        }
    }

    private void initializeDAO() {
        gastoDAO = new GastosDAO(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComprobantesAdapter(this, new ComprobantesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comprobante comprobante) {
                // Implementar edición si es necesario
            }

            @Override
            public void onDeleteClick(Comprobante comprobante) {
                deleteComprobante(comprobante);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaGastosActivity.this, RegistrarGastoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        listaComprobantes = gastoDAO.getAllComprobantes();

        if (listaComprobantes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
            tvTotalGastos.setText("Total: S/ 0.00");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);

            adapter.updateData(listaComprobantes);

            // Calcular total
            double total = 0.0;
            for (Comprobante comprobante : listaComprobantes) {
                total += comprobante.getMonto();
            }
            tvTotalGastos.setText("Total: S/ " + decimalFormat.format(total));
        }
    }

    private void deleteComprobante(Comprobante comprobante) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Gasto")
                .setMessage("¿Está seguro que desea eliminar este gasto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    int result = gastoDAO.deleteComprobante(comprobante.getIdComprobante());
                    if (result > 0) {
                        loadData(); // Recargar datos
                        android.widget.Toast.makeText(this, "Gasto eliminado", android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        android.widget.Toast.makeText(this, "Error al eliminar", android.widget.Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
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

