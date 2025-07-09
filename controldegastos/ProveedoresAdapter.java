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

public class ProveedoresAdapter extends RecyclerView.Adapter<ProveedoresAdapter.ViewHolder> {

    private Context context;
    private List<Proveedor> proveedores;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Proveedor proveedor);
        void onEditClick(Proveedor proveedor);
        void onDeleteClick(Proveedor proveedor);
    }

    public ProveedoresAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.proveedores = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_proveedor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Proveedor proveedor = proveedores.get(position);

        // Establecer información del proveedor
        holder.tvNombreProveedor.setText(proveedor.getNombreProveedor());
        holder.tvIdProveedor.setText(String.format("ID: %03d", proveedor.getIdProveedor()));

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(proveedor);
            }
        });

        // Verificar si existe el botón de editar
        if (holder.btnEdit != null) {
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(proveedor);
                }
            });
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(proveedor);
            }
        });

        // Animación sutil al hacer bind
        holder.itemView.setAlpha(0.8f);
        holder.itemView.animate()
                .alpha(1.0f)
                .setDuration(300)
                .start();
    }

    @Override
    public int getItemCount() {
        return proveedores.size();
    }

    public void updateData(List<Proveedor> newProveedores) {
        this.proveedores.clear();
        if (newProveedores != null) {
            this.proveedores.addAll(newProveedores);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProveedor, tvIdProveedor;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProveedor = itemView.findViewById(R.id.tv_nombre_proveedor);
            tvIdProveedor = itemView.findViewById(R.id.tv_id_proveedor);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}