package com.example.controldegastos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controldegastos.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ProveedoresActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout tvEmptyView;
    private FloatingActionButton fabAgregar;

    private ProveedoresAdapter adapter;
    private GastosDAO gastoDAO;
    private List<Proveedor> listaProveedores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedores);

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
            getSupportActionBar().setTitle("Gestión de Proveedores");
        }
    }

    private void initializeDAO() {
        gastoDAO = new GastosDAO(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProveedoresAdapter(this, new ProveedoresAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Proveedor proveedor) {
                // Implementar edición si es necesario
                mostrarDialogoEditarProveedor(proveedor);
            }

            @Override
            public void onDeleteClick(Proveedor proveedor) {
                // Por simplicidad, no implementamos eliminación física
                // ya que puede tener referencias en comprobantes
                Toast.makeText(ProveedoresActivity.this,
                        "No se puede eliminar un proveedor que tiene registros asociados",
                        Toast.LENGTH_LONG).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregarProveedor();
            }
        });
    }

    private void loadData() {
        listaProveedores = gastoDAO.getAllProveedores();

        if (listaProveedores.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
            adapter.updateData(listaProveedores);
        }
    }

    private void mostrarDialogoAgregarProveedor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Proveedor");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_proveedor, null);
        EditText etNombreProveedor = dialogView.findViewById(R.id.et_nombre_proveedor);

        builder.setView(dialogView);
        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String nombre = etNombreProveedor.getText().toString().trim();
            if (!nombre.isEmpty()) {
                Proveedor proveedor = new Proveedor(0, nombre.toUpperCase());
                long id = gastoDAO.insertProveedor(proveedor);
                if (id > 0) {
                    Toast.makeText(this, "Proveedor agregado exitosamente", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Error al agregar proveedor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Ingrese el nombre del proveedor", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoEditarProveedor(Proveedor proveedor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Proveedor");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_proveedor, null);
        EditText etNombreProveedor = dialogView.findViewById(R.id.et_nombre_proveedor);
        etNombreProveedor.setText(proveedor.getNombreProveedor());

        builder.setView(dialogView);
        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String nombre = etNombreProveedor.getText().toString().trim();
            if (!nombre.isEmpty()) {
                // Aquí implementarías el método updateProveedor en GastoDAO
                Toast.makeText(this, "Funcionalidad de edición pendiente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ingrese el nombre del proveedor", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
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

