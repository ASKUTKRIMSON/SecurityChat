package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
 * Фрагмент «Общий чат» (только чтение):
 * показывает все сообщения из коллекции "messages".
 * Здесь нет возможности отправить — для этого отдельный PrivateChatFragment.
 */
public class ChatListFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private FirestoreRecyclerAdapter<Message, MessageVH> adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        prefs = requireActivity()
                .getSharedPreferences("settings_prefs", 0);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        setupRecyclerView();
    }

    /** Настраиваем RecyclerView на чтение всех сообщений из коллекции "messages". */
    private void setupRecyclerView() {
        Query query = db.collection("messages")
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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

    /* -------------- ViewHolder для отдельного сообщения -------------- */
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
            // Показываем первые 6 символов UID отправителя
            tvSender.setText("User: " + m.getSenderUid().substring(0, 6));

            // Дешифруем текст, если есть ключ в SharedPreferences
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
