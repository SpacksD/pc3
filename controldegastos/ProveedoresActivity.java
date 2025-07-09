package com.example.controldegastos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresActivity extends AppCompatActivity {

    private static final String TAG = "ProveedoresActivity";

    // Views
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout tvEmptyView;
    private FloatingActionButton fabAgregar;
    private EditText etSearch;
    private ImageButton btnClearSearch;
    private TextView tvTotalProveedores, tvResultadosBusqueda;
    private TextView tvEmptyTitle, tvEmptyMessage;

    // Data & Adapter
    private ProveedoresAdapter adapter;
    private ProveedoresDAO proveedoresDAO;
    private List<Proveedor> listaProveedores;
    private List<Proveedor> listaProveedoresFiltrada;

    // State
    private String currentSearchQuery = "";

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
            setupSearchFunctionality();
            loadData();

        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate: " + e.getMessage(), e);
            showErrorAndFinish("Error al inicializar la actividad");
        }
    }

    private void initializeViews() {
        try {
            toolbar = findViewById(R.id.toolbar);
            recyclerView = findViewById(R.id.recycler_view);
            tvEmptyView = findViewById(R.id.tv_empty_view);
            fabAgregar = findViewById(R.id.fab_agregar);
            etSearch = findViewById(R.id.et_search);
            btnClearSearch = findViewById(R.id.btn_clear_search);
            tvTotalProveedores = findViewById(R.id.tv_total_proveedores);
            tvResultadosBusqueda = findViewById(R.id.tv_resultados_busqueda);
            tvEmptyTitle = findViewById(R.id.tv_empty_title);
            tvEmptyMessage = findViewById(R.id.tv_empty_message);

            Log.d(TAG, "Todas las vistas inicializadas correctamente");

        } catch (Exception e) {
            Log.e(TAG, "Error en initializeViews: " + e.getMessage(), e);
            throw e;
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
            // Usar el DAO especializado directamente
            proveedoresDAO = new ProveedoresDAO(this);
            listaProveedores = new ArrayList<>();
            listaProveedoresFiltrada = new ArrayList<>();
            Log.d(TAG, "ProveedoresDAO inicializado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando DAO: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupRecyclerView() {
        try {
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ProveedoresAdapter(this, new ProveedoresAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Proveedor proveedor) {
                        showProveedorDetails(proveedor);
                    }

                    @Override
                    public void onEditClick(Proveedor proveedor) {
                        mostrarDialogoEditarProveedor(proveedor);
                    }

                    @Override
                    public void onDeleteClick(Proveedor proveedor) {
                        confirmarEliminarProveedor(proveedor);
                    }
                });
                recyclerView.setAdapter(adapter);
                Log.d(TAG, "RecyclerView configurado correctamente");
            } else {
                Log.w(TAG, "RecyclerView es null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando RecyclerView: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupListeners() {
        try {
            if (fabAgregar != null) {
                fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarProveedor());
            }

            if (btnClearSearch != null) {
                btnClearSearch.setOnClickListener(v -> limpiarBusqueda());
            }

            Log.d(TAG, "Listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error configurando listeners: " + e.getMessage(), e);
        }
    }

    private void setupSearchFunctionality() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString();
                    filtrarProveedores(currentSearchQuery);

                    // Mostrar/ocultar botón de limpiar
                    if (btnClearSearch != null) {
                        btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void filtrarProveedores(String query) {
        listaProveedoresFiltrada.clear();

        if (query.isEmpty()) {
            listaProveedoresFiltrada.addAll(listaProveedores);
            updateEmptyViewForSearch(false, "");
        } else {
            for (Proveedor proveedor : listaProveedores) {
                if (proveedor.getNombreProveedor().toLowerCase()
                        .contains(query.toLowerCase())) {
                    listaProveedoresFiltrada.add(proveedor);
                }
            }
            updateEmptyViewForSearch(listaProveedoresFiltrada.isEmpty(), query);
        }

        updateUI();
    }

    private void limpiarBusqueda() {
        if (etSearch != null) {
            etSearch.setText("");
            etSearch.clearFocus();
        }
        currentSearchQuery = "";
    }

    private void updateEmptyViewForSearch(boolean isSearchEmpty, String query) {
        if (isSearchEmpty && !query.isEmpty()) {
            if (tvEmptyTitle != null) {
                tvEmptyTitle.setText("No se encontraron resultados");
            }
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setText("No hay proveedores que coincidan con \"" + query + "\"");
            }
        } else if (listaProveedores.isEmpty()) {
            if (tvEmptyTitle != null) {
                tvEmptyTitle.setText("No hay proveedores registrados");
            }
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setText("Toca el botón + para agregar tu primer proveedor");
            }
        }
    }

    private void loadData() {
        try {
            if (proveedoresDAO == null) {
                Log.e(TAG, "proveedoresDAO es null");
                return;
            }

            listaProveedores = proveedoresDAO.getAllProveedores();
            listaProveedoresFiltrada.clear();
            listaProveedoresFiltrada.addAll(listaProveedores);

            Log.d(TAG, "Proveedores cargados: " + (listaProveedores != null ? listaProveedores.size() : "null"));

            updateUI();

        } catch (Exception e) {
            Log.e(TAG, "Error cargando datos: " + e.getMessage(), e);
            showError("Error al cargar los proveedores");
        }
    }

    private void updateUI() {
        // Actualizar estadísticas
        if (tvTotalProveedores != null) {
            tvTotalProveedores.setText(String.valueOf(listaProveedores.size()));
        }
        if (tvResultadosBusqueda != null) {
            tvResultadosBusqueda.setText(String.valueOf(listaProveedoresFiltrada.size()));
        }

        // Mostrar/ocultar vistas según datos
        if (listaProveedoresFiltrada == null || listaProveedoresFiltrada.isEmpty()) {
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            if (tvEmptyView != null) tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            if (tvEmptyView != null) tvEmptyView.setVisibility(View.GONE);
            if (adapter != null) adapter.updateData(listaProveedoresFiltrada);
        }
    }

    private void mostrarDialogoAgregarProveedor() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Agregar Proveedor");

            // Crear vista del diálogo
            LinearLayout dialogView = new LinearLayout(this);
            dialogView.setOrientation(LinearLayout.VERTICAL);
            dialogView.setPadding(50, 20, 50, 20);

            EditText etNombreProveedor = new EditText(this);
            etNombreProveedor.setHint("Nombre del proveedor");
            etNombreProveedor.setSingleLine(true);
            dialogView.addView(etNombreProveedor);

            builder.setView(dialogView);
            builder.setPositiveButton("Agregar", (dialog, which) -> {
                String nombre = etNombreProveedor.getText().toString().trim();
                if (!nombre.isEmpty()) {
                    try {
                        Proveedor proveedor = new Proveedor(0, nombre);
                        long id = proveedoresDAO.insertProveedor(proveedor);
                        if (id > 0) {
                            Toast.makeText(this, "Proveedor agregado exitosamente", Toast.LENGTH_SHORT).show();
                            loadData();
                            limpiarBusqueda(); // Limpiar búsqueda para mostrar el nuevo proveedor
                        } else {
                            Toast.makeText(this, "Error al agregar proveedor", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error al agregar proveedor: " + e.getMessage(), e);
                        Toast.makeText(this, "Error inesperado al agregar proveedor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Ingrese el nombre del proveedor", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // Enfocar el campo de texto
            etNombreProveedor.requestFocus();

        } catch (Exception e) {
            Log.e(TAG, "Error mostrando diálogo agregar: " + e.getMessage(), e);
            Toast.makeText(this, "Error al mostrar diálogo", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEditarProveedor(Proveedor proveedor) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Editar Proveedor");

            // Crear vista del diálogo
            LinearLayout dialogView = new LinearLayout(this);
            dialogView.setOrientation(LinearLayout.VERTICAL);
            dialogView.setPadding(50, 20, 50, 20);

            EditText etNombreProveedor = new EditText(this);
            etNombreProveedor.setText(proveedor.getNombreProveedor());
            etNombreProveedor.setHint("Nombre del proveedor");
            etNombreProveedor.setSingleLine(true);
            etNombreProveedor.selectAll();
            dialogView.addView(etNombreProveedor);

            builder.setView(dialogView);
            builder.setPositiveButton("Actualizar", (dialog, which) -> {
                String nombre = etNombreProveedor.getText().toString().trim();
                if (!nombre.isEmpty()) {
                    try {
                        // Crear una copia del proveedor con el nuevo nombre
                        Proveedor proveedorActualizado = new Proveedor(proveedor.getIdProveedor(), nombre);
                        int rowsAffected = proveedoresDAO.updateProveedor(proveedorActualizado);

                        if (rowsAffected > 0) {
                            Toast.makeText(this, "Proveedor actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Toast.makeText(this, "No se pudo actualizar el proveedor", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error al actualizar proveedor: " + e.getMessage(), e);
                        Toast.makeText(this, "Error inesperado al actualizar proveedor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Ingrese el nombre del proveedor", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancelar", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // Enfocar el campo de texto
            etNombreProveedor.requestFocus();

        } catch (Exception e) {
            Log.e(TAG, "Error mostrando diálogo editar: " + e.getMessage(), e);
            Toast.makeText(this, "Error al mostrar diálogo", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarProveedor(Proveedor proveedor) {
        try {
            // Primero verificar si está en uso
            boolean inUse = proveedoresDAO.isProveedorInUse(proveedor.getIdProveedor());

            if (inUse) {
                // Mostrar información detallada del uso
                ProveedoresDAO.ProveedorStats stats = proveedoresDAO.getProveedorStats(proveedor.getIdProveedor());

                String mensaje = "No se puede eliminar \"" + proveedor.getNombreProveedor() +
                        "\" porque tiene registros asociados:\n\n" +
                        "• " + stats.totalComprobantes + " comprobantes de gastos\n" +
                        "• " + stats.totalIngresos + " registros de ingresos\n\n" +
                        "Para eliminarlo, primero debe eliminar todos los registros asociados.";

                new AlertDialog.Builder(this)
                        .setTitle("No se puede eliminar")
                        .setMessage(mensaje)
                        .setPositiveButton("Entendido", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                // Confirmar eliminación
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar Proveedor")
                        .setMessage("¿Está seguro que desea eliminar \"" + proveedor.getNombreProveedor() + "\"?\n\n" +
                                "Esta acción no se puede deshacer.")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            try {
                                boolean deleted = proveedoresDAO.deleteProveedor(proveedor.getIdProveedor());
                                if (deleted) {
                                    Toast.makeText(this, "Proveedor eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                    loadData();
                                } else {
                                    Toast.makeText(this, "No se pudo eliminar el proveedor", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error al eliminar proveedor: " + e.getMessage(), e);
                                Toast.makeText(this, "Error al eliminar proveedor", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en confirmar eliminar: " + e.getMessage(), e);
            Toast.makeText(this, "Error al verificar el proveedor", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProveedorDetails(Proveedor proveedor) {
        try {
            // Obtener estadísticas del proveedor
            ProveedoresDAO.ProveedorStats stats = proveedoresDAO.getProveedorStats(proveedor.getIdProveedor());

            String mensaje = "Información del proveedor:\n\n" +
                    "ID: " + proveedor.getIdProveedor() + "\n" +
                    "Nombre: " + proveedor.getNombreProveedor() + "\n\n" +
                    "Estadísticas:\n" +
                    "• Comprobantes: " + stats.totalComprobantes + "\n" +
                    "• Total en gastos: S/ " + String.format("%.2f", stats.totalGastos) + "\n" +
                    "• Ingresos: " + stats.totalIngresos + "\n" +
                    "• Total en ingresos: S/ " + String.format("%.2f", stats.totalMontoIngresos);

            new AlertDialog.Builder(this)
                    .setTitle("Detalles del Proveedor")
                    .setMessage(mensaje)
                    .setPositiveButton("Cerrar", null)
                    .setNeutralButton("Editar", (dialog, which) -> mostrarDialogoEditarProveedor(proveedor))
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error mostrando detalles: " + e.getMessage(), e);
            Toast.makeText(this, "Error al mostrar detalles", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private void showErrorAndFinish(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos al volver a la actividad
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar referencias para evitar memory leaks
        listaProveedores = null;
        listaProveedoresFiltrada = null;
        adapter = null;
        proveedoresDAO = null;
    }
}