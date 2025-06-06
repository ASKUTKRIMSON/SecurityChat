package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.securitychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Фрагмент «Создать комнату».
 * Одноразовая кнопка «Создать» тут создаёт новую комнату в Firestore
 * и сразу переходит в PrivateChatFragment.
 */
public class CreateRoomFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_room, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnCreate = view.findViewById(R.id.buttonCreate);
        btnCreate.setOnClickListener(v -> {
            // Собираем данные новой комнаты
            Map<String, Object> data = new HashMap<>();
            data.put("participants", Collections.singletonList(
                    FirebaseAuth.getInstance().getUid()
            ));
            data.put("createdAt", System.currentTimeMillis());

            // Добавляем документ в коллекцию "rooms"
            FirebaseFirestore.getInstance()
                    .collection("rooms")
                    .add(data)
                    .addOnSuccessListener(doc -> {
                        // Как только комната создана, переходим в PrivateChatFragment
                        Bundle args = new Bundle();
                        args.putString("roomId", doc.getId());

                        // ВНИМАНИЕ: используем R.id.nav_host_container
                        Navigation.findNavController(
                                requireActivity(),
                                R.id.nav_host_container
                        ).navigate(R.id.privateChatFragment, args);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),
                                "Ошибка создания комнаты",
                                Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
