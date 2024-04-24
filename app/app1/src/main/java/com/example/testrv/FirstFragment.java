package com.example.testrv;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testrv.adapter.ProductAdapter;
import com.example.testrv.model.Product;

public class FirstFragment extends Fragment {
    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_first, container, false);
        RecyclerView rv = root.findViewById(R.id.rv_test);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        ProductAdapter productAdpter = new ProductAdapter();
        productAdpter.Add(new Product("Tomato", 777, "Ochen vkusno1"));
        productAdpter.Add(new Product("Potato", 666, "Ochen vkusno2"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Tomato", 777, "Ochen vkusno1"));
        productAdpter.Add(new Product("Potato", 666, "Ochen vkusno2"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Tomato", 777, "Ochen vkusno1"));
        productAdpter.Add(new Product("Potato", 666, "Ochen vkusno2"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Tomato", 777, "Ochen vkusno1"));
        productAdpter.Add(new Product("Potato", 666, "Ochen vkusno2"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Tomato", 777, "Ochen vkusno1"));
        productAdpter.Add(new Product("Potato", 666, "Ochen vkusno2"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Bread", 555, "Ochen vkusno3"));
        productAdpter.Add(new Product("Tomato", 777, "Ochen vkusno1"));
        productAdpter.Add(new Product("Папа", 666, "Казбек Кутаев "));
        productAdpter.Add(new Product("Мама", 555, "Зарема Кутаева"));
        rv.setAdapter(productAdpter);
        return root;
    }

}