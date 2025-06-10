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

public class ComprobantesAdapter extends RecyclerView.Adapter<ComprobantesAdapter.ViewHolder> {

    private Context context;
    private List<Comprobante> comprobantes;
    private OnItemClickListener listener;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat inputDateFormat;
    private SimpleDateFormat outputDateFormat;

    public interface OnItemClickListener {
        void onItemClick(Comprobante comprobante);
        void onDeleteClick(Comprobante comprobante);
    }

    public ComprobantesAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.comprobantes = new ArrayList<>();
        this.decimalFormat = new DecimalFormat("#,##0.00");
        this.inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comprobante, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comprobante comprobante = comprobantes.get(position);

        holder.tvTipoGasto.setText(comprobante.getTipoGastoDesc());
        holder.tvProveedor.setText(comprobante.getProveedorNombre());
        holder.tvSerieNumero.setText(comprobante.getSerieNumero());
        holder.tvMonto.setText("S/ " + decimalFormat.format(comprobante.getMonto()));

        // Formatear fecha
        try {
            Date date = inputDateFormat.parse(comprobante.getFecha());
            holder.tvFecha.setText(outputDateFormat.format(date));
        } catch (ParseException e) {
            holder.tvFecha.setText(comprobante.getFecha());
        }

        holder.tvTipoComprobante.setText(comprobante.getTipoComprobanteDesc());

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(comprobante);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(comprobante);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comprobantes.size();
    }

    public void updateData(List<Comprobante> newComprobantes) {
        this.comprobantes.clear();
        this.comprobantes.addAll(newComprobantes);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipoGasto, tvProveedor, tvSerieNumero, tvFecha, tvMonto, tvTipoComprobante;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoGasto = itemView.findViewById(R.id.tv_tipo_gasto);
            tvProveedor = itemView.findViewById(R.id.tv_proveedor);
            tvSerieNumero = itemView.findViewById(R.id.tv_serie_numero);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvMonto = itemView.findViewById(R.id.tv_monto);
            tvTipoComprobante = itemView.findViewById(R.id.tv_tipo_comprobante);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}