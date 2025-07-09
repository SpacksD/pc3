package com.example.controldegastos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TiposGastoAdapter extends RecyclerView.Adapter<TiposGastoAdapter.ViewHolder> {

    private Context context;
    private List<TipoGasto> tiposGasto;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TipoGasto tipoGasto);
        void onDeleteClick(TipoGasto tipoGasto);
    }

    public TiposGastoAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.tiposGasto = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos el layout de proveedor como base, es muy similar
        View view = LayoutInflater.from(context).inflate(R.layout.item_proveedor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TipoGasto tipoGasto = tiposGasto.get(position);

        holder.tvNombre.setText(tipoGasto.getDescripcion());
        holder.tvId.setText("ID: " + tipoGasto.getIdTipoGasto());

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(tipoGasto);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(tipoGasto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tiposGasto.size();
    }

    public void updateData(List<TipoGasto> newTiposGasto) {
        this.tiposGasto.clear();
        if (newTiposGasto != null) {
            this.tiposGasto.addAll(newTiposGasto);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvId;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Usamos los IDs del layout de proveedor
            tvNombre = itemView.findViewById(R.id.tv_nombre_proveedor);
            tvId = itemView.findViewById(R.id.tv_id_proveedor);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}