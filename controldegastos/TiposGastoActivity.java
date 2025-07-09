package com.example.controldegastos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class TiposGastoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout tvEmptyView;
    private FloatingActionButton fabAgregar;

    private TiposGastoAdapter adapter;
    private GastosDAO gastoDAO;
    private List<TipoGasto> listaTiposGasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedores); // Usamos layout similar

        initializeViews();
        setupToolbar();
        initializeDAO();
        setupRecyclerView();
        setupListeners();
        loadData();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        tvEmptyView = findViewById(R.id.tv_empty_view);
        fabAgregar = findViewById(R.id.fab_agregar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tipos de Gasto");
        }
    }

    private void initializeDAO() {
        gastoDAO = new GastosDAO(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TiposGastoAdapter(this, new TiposGastoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TipoGasto tipoGasto) {
                mostrarDialogoEditarTipoGasto(tipoGasto);
            }

            @Override
            public void onDeleteClick(TipoGasto tipoGasto) {
                confirmarEliminarTipoGasto(tipoGasto);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregarTipoGasto();
            }
        });
    }

    private void loadData() {
        listaTiposGasto = gastoDAO.getAllTiposGasto();

        if (listaTiposGasto == null || listaTiposGasto.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
            adapter.updateData(listaTiposGasto);
        }
    }

    private void mostrarDialogoAgregarTipoGasto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Tipo de Gasto");

        LinearLayout dialogView = new LinearLayout(this);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setPadding(50, 20, 50, 20);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción del tipo de gasto");
        etDescripcion.setSingleLine(true);
        dialogView.addView(etDescripcion);

        builder.setView(dialogView);
        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String descripcion = etDescripcion.getText().toString().trim();
            if (!descripcion.isEmpty()) {
                TipoGasto tipoGasto = new TipoGasto(0, descripcion.toUpperCase());
                long id = gastoDAO.insertTipoGasto(tipoGasto);
                if (id > 0) {
                    Toast.makeText(this, "Tipo de gasto agregado exitosamente", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Error al agregar tipo de gasto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Ingrese la descripción del tipo de gasto", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoEditarTipoGasto(TipoGasto tipoGasto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Tipo de Gasto");

        LinearLayout dialogView = new LinearLayout(this);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setPadding(50, 20, 50, 20);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setText(tipoGasto.getDescripcion());
        etDescripcion.setHint("Descripción del tipo de gasto");
        etDescripcion.setSingleLine(true);
        etDescripcion.selectAll();
        dialogView.addView(etDescripcion);

        builder.setView(dialogView);
        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String descripcion = etDescripcion.getText().toString().trim();
            if (!descripcion.isEmpty()) {
                // Por ahora solo mostramos mensaje, implementar update después
                Toast.makeText(this, "Funcionalidad de edición en desarrollo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ingrese la descripción del tipo de gasto", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmarEliminarTipoGasto(TipoGasto tipoGasto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Tipo de Gasto")
                .setMessage("¿Está seguro que desea eliminar \"" + tipoGasto.getDescripcion() + "\"?\n\nNota: Solo se puede eliminar si no tiene registros asociados.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Por ahora solo mostramos mensaje de advertencia
                    Toast.makeText(this,
                            "No se puede eliminar un tipo de gasto que tiene registros asociados",
                            Toast.LENGTH_LONG).show();
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