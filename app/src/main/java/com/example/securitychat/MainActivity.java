package com.example.securitychat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom=findViewById(R.id.bottom_nav);
        NavHostFragment host=(NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);
        NavController nav=host.getNavController();
        NavigationUI.setupWithNavController(bottom,nav);
    }
}
