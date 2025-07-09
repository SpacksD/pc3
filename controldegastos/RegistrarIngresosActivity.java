package com.example.controldegastos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.controldegastos.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistrarIngresosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinnerTipoIngreso, spinnerProveedor;
    private EditText etFecha, etMonto, etDescripcion;
    private Button btnGuardar, btnCancelar;

    private IngresosDAO ingresoDAO;
    private GastosDAO gastoDAO; // Para obtener proveedores
    private List<TipoIngreso> tiposIngreso;
    private List<Proveedor> proveedores;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_ingresos);

        initializeViews();
        setupToolbar();
        initializeDAO();
        loadSpinnerData();
        setupListeners();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Establecer fecha actual por defecto
        etFecha.setText(dateFormat.format(calendar.getTime()));
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        spinnerTipoIngreso = findViewById(R.id.spinner_tipo_ingreso);
        spinnerProveedor = findViewById(R.id.spinner_proveedor);
        etFecha = findViewById(R.id.et_fecha);
        etMonto = findViewById(R.id.et_monto);
        etDescripcion = findViewById(R.id.et_descripcion);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnCancelar = findViewById(R.id.btn_cancelar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Ingreso");
        }
    }
    private void initializeDAO() {
        ingresoDAO = new IngresosDAO(this);
        gastoDAO = new GastosDAO(this);
    }

    private void loadSpinnerData() {
        // Cargar tipos de ingreso
        tiposIngreso = ingresoDAO.getAllTiposIngreso();
        ArrayAdapter<TipoIngreso> adapterTipoIngreso = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposIngreso);
        adapterTipoIngreso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoIngreso.setAdapter(adapterTipoIngreso);

        // Cargar proveedores (reutilizamos la tabla de proveedores)
        proveedores = gastoDAO.getAllProveedores();
        ArrayAdapter<Proveedor> adapterProveedor = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, proveedores);
        adapterProveedor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProveedor.setAdapter(adapterProveedor);
    }

    private void setupListeners() {
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarIngreso();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        etFecha.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void guardarIngreso() {
        if (!validateFields()) {
            return;
        }

        try {
            // Obtener valores seleccionados
            TipoIngreso tipoIngresoSeleccionado = (TipoIngreso) spinnerTipoIngreso.getSelectedItem();
            Proveedor proveedorSeleccionado = (Proveedor) spinnerProveedor.getSelectedItem();

            String fecha = etFecha.getText().toString().trim();
            double monto = Double.parseDouble(etMonto.getText().toString().trim());

            // Crear objeto Ingreso
            Ingreso ingreso = new Ingreso(
                    tipoIngresoSeleccionado.getIdTipoIngreso(),
                    proveedorSeleccionado.getIdProveedor(),
                    fecha,
                    monto
            );

            // Guardar en base de datos
            long id = ingresoDAO.insertIngreso(ingreso);

            if (id > 0) {
                Toast.makeText(this, "Ingreso registrado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al registrar el ingreso", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingrese un monto válido", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        if (spinnerTipoIngreso.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione un tipo de ingreso", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerProveedor.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione un proveedor/cliente", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etFecha.getText().toString().trim().isEmpty()) {
            etFecha.setError("Seleccione una fecha");
            etFecha.requestFocus();
            return false;
        }

        if (etMonto.getText().toString().trim().isEmpty()) {
            etMonto.setError("Ingrese el monto");
            etMonto.requestFocus();
            return false;
        }

        try {
            double monto = Double.parseDouble(etMonto.getText().toString().trim());
            if (monto <= 0) {
                etMonto.setError("El monto debe ser mayor a 0");
                etMonto.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etMonto.setError("Ingrese un monto válido");
            etMonto.requestFocus();
            return false;
        }

        return true;
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