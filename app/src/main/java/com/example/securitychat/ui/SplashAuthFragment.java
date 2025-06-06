package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.securitychat.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Сплэш-фрагмент: выполняет анонимную авторизацию и
 * после успеха переходит в основной граф (ChatListFragment).
 */
public class SplashAuthFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        // attachToRoot = false
        return inflater.inflate(R.layout.fragment_splash_auth, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            proceedToMain();
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> proceedToMain());
        }
    }

    /** Перейти к экрану ChatListFragment. */
    private void proceedToMain() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_splash_to_chat);
    }
}
