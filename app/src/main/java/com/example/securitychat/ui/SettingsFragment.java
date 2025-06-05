// app/src/main/java/com/example/securitychat/ui/SettingsFragment.java
package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.securitychat.R;
import com.example.securitychat.crypto.CryptoUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsFragment extends Fragment {
    private TextView textViewYourUid;
    private EditText editTextKey;
    private Button buttonSaveKey, buttonGenKey, buttonLogout;
    private Switch switchPush;
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity()
                .getSharedPreferences("settings_prefs", getContext().MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        textViewYourUid = view.findViewById(R.id.textViewYourUid);
        editTextKey     = view.findViewById(R.id.editTextKey);
        buttonSaveKey   = view.findViewById(R.id.buttonSaveKey);
        buttonGenKey    = view.findViewById(R.id.buttonGenKey);
        buttonLogout    = view.findViewById(R.id.buttonLogout);
        switchPush      = view.findViewById(R.id.switchPush);

        if (mAuth.getCurrentUser() != null) {
            String shortUid = mAuth.getCurrentUser().getUid().substring(0, 6);
            textViewYourUid.setText("UID: " + shortUid);
        }

        // показать сохранённый ключ (если есть)
        editTextKey.setText(prefs.getString("secure_shared_key", ""));

        buttonSaveKey.setOnClickListener(v -> {
            String key = editTextKey.getText().toString().trim();
            if (key.length() < 40) {
                Toast.makeText(getContext(), "Ключ слишком короткий", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("secure_shared_key", key).apply();
            Toast.makeText(getContext(), "Ключ сохранён", Toast.LENGTH_SHORT).show();
        });

        buttonGenKey.setOnClickListener(v -> {
            String newKey = CryptoUtil.generateKey();
            editTextKey.setText(newKey);
        });

        // Push-switch
        boolean pushEnabled = prefs.getBoolean("push_enabled", true);
        switchPush.setChecked(pushEnabled);
        updatePushState(pushEnabled);

        switchPush.setOnCheckedChangeListener((b, enabled) -> {
            prefs.edit().putBoolean("push_enabled", enabled).apply();
            updatePushState(enabled);
        });

        // Logout
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
