// app/src/main/java/com/example/securitychat/ui/SplashAuthFragment.java
package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.securitychat.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashAuthFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_splash_auth, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            navigateToChatList(view);
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(getActivity(), task -> {
                        // Если вход успешен или нет, переходим дальше
                        navigateToChatList(view);
                    });
        }
    }

    private void navigateToChatList(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_splash_to_chatList);
    }
}
