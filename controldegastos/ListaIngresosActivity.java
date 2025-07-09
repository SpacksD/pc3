package com.example.controldegastos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
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

public class ListaIngresosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvTotalIngresos;
    private LinearLayout tvEmptyView;
    private FloatingActionButton fabAgregar;

    private IngresosAdapter adapter;
    private IngresosDAO ingresoDAO;
    private List<Ingreso> listaIngresos;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ingresos);

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
        tvTotalIngresos = findViewById(R.id.tv_total_ingresos);
        tvEmptyView = findViewById(R.id.tv_empty_view);
        fabAgregar = findViewById(R.id.fab_agregar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lista de Ingresos");
        }
    }

    private void initializeDAO() {
        ingresoDAO = new IngresosDAO(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngresosAdapter(this, new IngresosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ingreso ingreso) {
                // Implementar edición si es necesario
            }

            @Override
            public void onDeleteClick(Ingreso ingreso) {
                deleteIngreso(ingreso);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaIngresosActivity.this, RegistrarIngresosActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        listaIngresos = ingresoDAO.getAllIngresos();

        if (listaIngresos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
            tvTotalIngresos.setText("Total: S/ 0.00");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);

            adapter.updateData(listaIngresos);

            // Calcular total
            double total = 0.0;
            for (Ingreso ingreso : listaIngresos) {
                total += ingreso.getMonto();
            }
            tvTotalIngresos.setText("Total: S/ " + decimalFormat.format(total));
        }
    }

    private void deleteIngreso(Ingreso ingreso) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Ingreso")
                .setMessage("¿Está seguro que desea eliminar este ingreso?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    int result = ingresoDAO.deleteIngreso(ingreso.getIdIngreso());
                    if (result > 0) {
                        loadData(); // Recargar datos
                        android.widget.Toast.makeText(this, "Ingreso eliminado", android.widget.Toast.LENGTH_SHORT).show();
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