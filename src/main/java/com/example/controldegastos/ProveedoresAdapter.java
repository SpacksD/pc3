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
import java.util.ArrayList;
import java.util.List;

public class ProveedoresAdapter extends RecyclerView.Adapter<ProveedoresAdapter.ViewHolder> {

    private Context context;
    private List<Proveedor> proveedores;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Proveedor proveedor);
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

        holder.tvNombreProveedor.setText(proveedor.getNombreProveedor());
        holder.tvIdProveedor.setText("ID: " + proveedor.getIdProveedor());

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(proveedor);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(proveedor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return proveedores.size();
    }

    public void updateData(List<Proveedor> newProveedores) {
        this.proveedores.clear();
        this.proveedores.addAll(newProveedores);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProveedor, tvIdProveedor;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProveedor = itemView.findViewById(R.id.tv_nombre_proveedor);
            tvIdProveedor = itemView.findViewById(R.id.tv_id_proveedor);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}