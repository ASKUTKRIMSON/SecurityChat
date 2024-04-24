package com.example.testrv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testrv.R;
import com.example.testrv.databinding.ProductItemBinding;
import com.example.testrv.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private ProductItemBinding productItemBinding;
        public ProductViewHolder(@NonNull View itemView){
            super(itemView);
            productItemBinding = ProductItemBinding.bind(itemView);
        }
        public void bind(Product product){
            productItemBinding.name.setText(product.name);
            productItemBinding.price.setText(String.valueOf(product.price));
            productItemBinding.description.setText(product.description);
        }
    }
    public void Add(Product product){
        products.add(product);
        notifyDataSetChanged();
    }
}

