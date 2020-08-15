package com.example.liverpooltest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;

public class AdaptadorProduct extends RecyclerView.Adapter<AdaptadorProduct.ViewHolder> {

    private Context context;
    private ArrayList<ItemProduct> products;

    public AdaptadorProduct(Context context, ArrayList<ItemProduct> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_product, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        ItemProduct product = products.get(position);
        holder.titulo.setText(product.getTitulo());
        holder.ubicacion.setText(product.getUbicacion());
        holder.precio.setText(product.getPrecio());
        Glide.with(context).load(product.getImagen())
                .signature(new ObjectKey(product.getTitulo()))
                .placeholder(R.drawable.img_holder)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo,ubicacion,precio;
        public ImageView imagen;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.view_product_titulo);
            ubicacion = (TextView) itemView.findViewById(R.id.view_product_ubicacion);
            precio = (TextView) itemView.findViewById(R.id.view_product_precio);
            imagen = (ImageView) itemView.findViewById(R.id.view_product_image);
        }
    }

}