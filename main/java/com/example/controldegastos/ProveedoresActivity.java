package com.example.controldegastos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

public class ProveedoresActivity extends AppCompatActivity {

    private static final String TAG = "ProveedoresActivity";

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

        try {
            setContentView(R.layout.activity_proveedores);
            Log.d(TAG, "Layout cargado correctamente");

            initializeViews();
            setupToolbar();
            initializeDAO();
            setupRecyclerView();
            setupListeners();
            loadData();

        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate: " + e.getMessage(), e);
        }
    }

    private void initializeViews() {
        try {
            toolbar = findViewById(R.id.toolbar);
            recyclerView = findViewById(R.id.recycler_view);
            tvEmptyView = findViewById(R.id.tv_empty_view);
            fabAgregar = findViewById(R.id.fab_agregar);

            Log.d(TAG, "toolbar: " + (toolbar != null ? "OK" : "NULL"));
            Log.d(TAG, "recyclerView: " + (recyclerView != null ? "OK" : "NULL"));
            Log.d(TAG, "fabAgregar: " + (fabAgregar != null ? "OK" : "NULL"));

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
                    getSupportActionBar().setTitle("Gestión de Proveedores");
                }
                Log.d(TAG, "Toolbar configurado correctamente");
            } else {
                Log.w(TAG, "Toolbar es null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando toolbar: " + e.getMessage(), e);
        }
    }

    private void initializeDAO() {
        try {
            gastoDAO = new GastosDAO(this);
            Log.d(TAG, "DAO inicializado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando DAO: " + e.getMessage(), e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ProveedoresAdapter(this, new ProveedoresAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Proveedor proveedor) {
                        mostrarDialogoEditarProveedor(proveedor);
                    }

                    @Override
                    public void onDeleteClick(Proveedor proveedor) {
                        Toast.makeText(ProveedoresActivity.this,
                                "No se puede eliminar un proveedor que tiene registros asociados",
                                Toast.LENGTH_LONG).show();
                    }
                });
                recyclerView.setAdapter(adapter);
                Log.d(TAG, "RecyclerView configurado correctamente");
            } else {
                Log.w(TAG, "RecyclerView es null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando RecyclerView: " + e.getMessage(), e);
        }
    }

    private void setupListeners() {
        try {
            if (fabAgregar != null) {
                fabAgregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarDialogoAgregarProveedor();
                    }
                });
                Log.d(TAG, "Listeners configurados correctamente");
            } else {
                Log.w(TAG, "FAB es null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando listeners: " + e.getMessage(), e);
        }
    }

    private void loadData() {
        try {
            if (gastoDAO == null) {
                Log.e(TAG, "gastoDAO es null");
                return;
            }

            listaProveedores = gastoDAO.getAllProveedores();
            Log.d(TAG, "Proveedores cargados: " + (listaProveedores != null ? listaProveedores.size() : "null"));

            if (listaProveedores == null || listaProveedores.isEmpty()) {
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                if (tvEmptyView != null) tvEmptyView.setVisibility(View.VISIBLE);
            } else {
                if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                if (tvEmptyView != null) tvEmptyView.setVisibility(View.GONE);
                if (adapter != null) adapter.updateData(listaProveedores);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error cargando datos: " + e.getMessage(), e);
        }
    }

    private void mostrarDialogoAgregarProveedor() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Agregar Proveedor");

            // Crear vista del diálogo programáticamente si no existe el layout
            LinearLayout dialogView = new LinearLayout(this);
            dialogView.setOrientation(LinearLayout.VERTICAL);
            dialogView.setPadding(50, 20, 50, 20);

            EditText etNombreProveedor = new EditText(this);
            etNombreProveedor.setHint("Nombre del proveedor");
            dialogView.addView(etNombreProveedor);

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

        } catch (Exception e) {
            Log.e(TAG, "Error mostrando diálogo: " + e.getMessage(), e);
            Toast.makeText(this, "Error al mostrar diálogo", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEditarProveedor(Proveedor proveedor) {
        Toast.makeText(this, "Funcionalidad de edición en desarrollo", Toast.LENGTH_SHORT).show();
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