// app/src/main/java/com/example/securitychat/ui/SettingsFragment.java
package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.securitychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Фрагмент «Настройки». Показывает UID, отписку/подписку на пуш-уведомления и Logout.
 */
public class SettingsFragment extends Fragment {
    private TextView textViewYourUid;
    private Button buttonLogout;
    private Switch switchPush;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        prefs = requireActivity()
                .getSharedPreferences("settings_prefs", getContext().MODE_PRIVATE);

        textViewYourUid = view.findViewById(R.id.textViewYourUid);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        switchPush = view.findViewById(R.id.switchPush);

        if (mAuth.getCurrentUser() != null) {
            String shortUid = mAuth.getCurrentUser().getUid().substring(0, 6);
            textViewYourUid.setText("Ваш UID: " + shortUid);
        }

        boolean pushEnabled = prefs.getBoolean("push_enabled", true);
        switchPush.setChecked(pushEnabled);
        updatePushState(pushEnabled);

        switchPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("push_enabled", isChecked).apply();
            updatePushState(isChecked);
        });

        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack(R.id.splashAuthFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Navigation.findNavController(view).navigate(R.id.splashAuthFragment);
        });
    }

    private void updatePushState(boolean enabled) {
        if (enabled) {
            FirebaseMessaging.getInstance().subscribeToTopic("all_users");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all_users");
        }
    }
}
