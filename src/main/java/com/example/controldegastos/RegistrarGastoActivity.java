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

public class RegistrarGastoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinnerTipoGasto, spinnerTipoComprobante, spinnerProveedor;
    private EditText etSerieNumero, etFecha, etMonto;
    private Button btnGuardar, btnCancelar;

    private GastosDAO gastoDAO;
    private List<TipoGasto> tiposGasto;
    private List<TipoComprobante> tiposComprobante;
    private List<Proveedor> proveedores;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_gasto);

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
        spinnerTipoGasto = findViewById(R.id.spinner_tipo_gasto);
        spinnerTipoComprobante = findViewById(R.id.spinner_tipo_comprobante);
        spinnerProveedor = findViewById(R.id.spinner_proveedor);
        etSerieNumero = findViewById(R.id.et_serie_numero);
        etFecha = findViewById(R.id.et_fecha);
        etMonto = findViewById(R.id.et_monto);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnCancelar = findViewById(R.id.btn_cancelar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Gasto");
        }
    }

    private void initializeDAO() {
        gastoDAO = new GastosDAO(this);
    }

    private void loadSpinnerData() {
        // Cargar tipos de gasto
        tiposGasto = gastoDAO.getAllTiposGasto();
        ArrayAdapter<TipoGasto> adapterTipoGasto = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposGasto);
        adapterTipoGasto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoGasto.setAdapter(adapterTipoGasto);

        // Cargar tipos de comprobante
        tiposComprobante = gastoDAO.getAllTiposComprobante();
        ArrayAdapter<TipoComprobante> adapterTipoComprobante = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposComprobante);
        adapterTipoComprobante.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoComprobante.setAdapter(adapterTipoComprobante);

        // Cargar proveedores
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
                guardarGasto();
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

    private void guardarGasto() {
        if (!validateFields()) {
            return;
        }

        try {
            // Obtener valores seleccionados
            TipoGasto tipoGastoSeleccionado = (TipoGasto) spinnerTipoGasto.getSelectedItem();
            TipoComprobante tipoComprobanteSeleccionado = (TipoComprobante) spinnerTipoComprobante.getSelectedItem();
            Proveedor proveedorSeleccionado = (Proveedor) spinnerProveedor.getSelectedItem();

            String serieNumero = etSerieNumero.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            double monto = Double.parseDouble(etMonto.getText().toString().trim());

            // Crear objeto Comprobante
            Comprobante comprobante = new Comprobante(
                    tipoComprobanteSeleccionado.getIdTipoComprobante(),
                    tipoGastoSeleccionado.getIdTipoGasto(),
                    proveedorSeleccionado.getIdProveedor(),
                    serieNumero,
                    fecha,
                    monto
            );

            // Guardar en base de datos
            long id = gastoDAO.insertComprobante(comprobante);

            if (id > 0) {
                Toast.makeText(this, "Gasto registrado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al registrar el gasto", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingrese un monto válido", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        if (spinnerTipoGasto.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione un tipo de gasto", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerTipoComprobante.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione un tipo de comprobante", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerProveedor.getSelectedItem() == null) {
            Toast.makeText(this, "Seleccione un proveedor", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etSerieNumero.getText().toString().trim().isEmpty()) {
            etSerieNumero.setError("Ingrese la serie/número");
            etSerieNumero.requestFocus();
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