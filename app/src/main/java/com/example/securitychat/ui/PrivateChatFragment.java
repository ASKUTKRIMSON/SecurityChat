package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securitychat.R;
import com.example.securitychat.crypto.CryptoUtil;
import com.example.securitychat.model.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Фрагмент «Приватный чат» (одно-на-одно):
 * 1) Получаем roomId через аргументы;
 * 2) Слушаем коллекцию rooms/{roomId}/messages;
 * 3) Шифруем каждое отправляемое сообщение;
 * 4) Дешифруем отображённые сообщения.
 */
public class PrivateChatFragment extends Fragment {

    private String roomId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences prefs;

    private FirestoreRecyclerAdapter<Message, MessageVH> adapter;
    private RecyclerView recyclerPrivate;
    private EditText editPrivate;
    private ImageButton btnSendPrivate;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_private_chat, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();
        prefs = requireActivity()
                .getSharedPreferences("settings_prefs", 0);

        // Получаем roomId из аргументов NavController
        if (getArguments() != null) {
            roomId = getArguments().getString("roomId");
        }

        recyclerPrivate = view.findViewById(R.id.recyclerPrivate);
        editPrivate     = view.findViewById(R.id.editPrivate);
        btnSendPrivate  = view.findViewById(R.id.btnSendPrivate);

        setupRecycler();
        btnSendPrivate.setOnClickListener(v -> sendMessage());
    }

    /** Настраиваем RecyclerView для прослушивания rooms/{roomId}/messages. */
    private void setupRecycler() {
        Query query = db.collection("rooms")
                .document(roomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Message, MessageVH>(options) {
            @NonNull
            @Override
            public MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View row = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message, parent, false);
                return new MessageVH(row);
            }

            @Override
            protected void onBindViewHolder(
                    @NonNull MessageVH holder, int position, @NonNull Message model) {
                holder.bind(model);
            }
        };

        recyclerPrivate.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPrivate.setAdapter(adapter);
    }

    /** Шифрует и отправляет текст в Firestore (в подколлекцию). */
    private void sendMessage() {
        String text = editPrivate.getText().toString().trim();
        if (TextUtils.isEmpty(text) || auth.getCurrentUser() == null) {
            return;
        }
        String senderUid = auth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();
        String key = prefs.getString("secure_shared_key", null);
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(getContext(), "Нет общего ключа", Toast.LENGTH_SHORT).show();
            return;
        }
        String cipher = CryptoUtil.encrypt(text, key);
        Message msg = new Message(cipher, senderUid, timestamp);

        db.collection("rooms")
                .document(roomId)
                .collection("messages")
                .add(msg)
                .addOnSuccessListener(doc -> {
                    editPrivate.setText("");
                    recyclerPrivate.scrollToPosition(adapter.getItemCount() - 1);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Ошибка отправки", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    /* -------------- ViewHolder для сообщения (одно-на-одно) -------------- */
    private class MessageVH extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvSender;
        private final android.widget.TextView tvText;
        private final android.widget.TextView tvTime;

        MessageVH(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.textViewSender);
            tvText   = itemView.findViewById(R.id.textViewMessage);
            tvTime   = itemView.findViewById(R.id.textViewTime);
        }

        void bind(Message m) {
            tvSender.setText("User: " + m.getSenderUid().substring(0, 6));

            String key = prefs.getString("secure_shared_key", null);
            String plain;
            try {
                if (!TextUtils.isEmpty(key)) {
                    plain = CryptoUtil.decrypt(m.getText(), key);
                } else {
                    plain = "🔒 Нет ключа";
                }
            } catch (Exception e) {
                plain = "⛔ decrypt error";
            }
            tvText.setText(plain);

            String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(new Date(m.getTimestamp()));
            tvTime.setText(time);
        }
    }
}
