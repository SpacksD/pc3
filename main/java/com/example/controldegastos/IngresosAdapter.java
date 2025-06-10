package com.example.controldegastos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.controldegastos.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngresosAdapter extends RecyclerView.Adapter<IngresosAdapter.ViewHolder> {

    private Context context;
    private List<Ingreso> ingresos;
    private OnItemClickListener listener;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat inputDateFormat;
    private SimpleDateFormat outputDateFormat;

    public interface OnItemClickListener {
        void onItemClick(Ingreso ingreso);
        void onDeleteClick(Ingreso ingreso);
    }

    public IngresosAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.ingresos = new ArrayList<>();
        this.decimalFormat = new DecimalFormat("#,##0.00");
        this.inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingreso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingreso ingreso = ingresos.get(position);

        holder.tvTipoIngreso.setText(ingreso.getTipoIngresoDesc());
        holder.tvProveedor.setText(ingreso.getProveedorNombre());
        holder.tvMonto.setText("S/ " + decimalFormat.format(ingreso.getMonto()));
        holder.tvIdIngreso.setText("#" + String.format("%03d", ingreso.getIdIngreso()));

        // Formatear fecha
        try {
            Date date = inputDateFormat.parse(ingreso.getFecha());
            holder.tvFecha.setText(outputDateFormat.format(date));
        } catch (ParseException e) {
            holder.tvFecha.setText(ingreso.getFecha());
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(ingreso);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(ingreso);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingresos.size();
    }

    public void updateData(List<Ingreso> newIngresos) {
        this.ingresos.clear();
        this.ingresos.addAll(newIngresos);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipoIngreso, tvProveedor, tvFecha, tvMonto, tvIdIngreso;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoIngreso = itemView.findViewById(R.id.tv_tipo_ingreso);
            tvProveedor = itemView.findViewById(R.id.tv_proveedor);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvMonto = itemView.findViewById(R.id.tv_monto);
            tvIdIngreso = itemView.findViewById(R.id.tv_id_ingreso);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}