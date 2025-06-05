package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatListFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Message, MessageViewHolder> adapter;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private FloatingActionButton fabMore;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        prefs = requireActivity()
                .getSharedPreferences("settings_prefs", getContext().MODE_PRIVATE);

        recyclerView    = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend      = view.findViewById(R.id.buttonSend);
        fabMore         = view.findViewById(R.id.fab_more);

        setupRecyclerView();

        buttonSend.setOnClickListener(v -> sendMessage());
        fabMore.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_chatList_to_settings));
    }

    /** Отправка сообщения */
    private void sendMessage() {
        String plain = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(plain) || mAuth.getCurrentUser() == null) return;

        String key = prefs.getString("secure_shared_key", null);
        if (key == null) {
            Toast.makeText(getContext(), "Нет общего ключа", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String cipher = CryptoUtil.encrypt(plain, key);
            String senderUid = mAuth.getCurrentUser().getUid();
            long timestamp   = System.currentTimeMillis();

            Message msg = new Message(cipher, senderUid, timestamp);

            db.collection("messages")
                    .add(msg)
                    .addOnSuccessListener(dRef -> {
                        editTextMessage.setText("");
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка шифрования", Toast.LENGTH_SHORT).show();
        }
    }

    /** Настройка адаптера Firestore */
    private void setupRecyclerView() {
        Query q = db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> opts =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(q, Message.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(opts) {
            @NonNull @Override
            public MessageViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(v);
            }
            @Override
            protected void onBindViewHolder(
                    @NonNull MessageViewHolder h, int position, @NonNull Message m) {
                h.bind(m);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override public void onStart() { super.onStart(); adapter.startListening(); }
    @Override public void onStop()  { super.onStop();  adapter.stopListening(); }

    /** ViewHolder */
    private class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvText, tvTime;
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.textViewSender);
            tvText   = itemView.findViewById(R.id.textViewMessage);
            tvTime   = itemView.findViewById(R.id.textViewTime);
        }
        void bind(Message m) {
            String shortUid = m.getSenderUid().substring(0, 6);
            tvSender.setText("User: " + shortUid);

            String key = prefs.getString("secure_shared_key", null);
            String plain;
            try { plain = (key != null) ? CryptoUtil.decrypt(m.getText(), key) : "🔒";
            } catch (Exception e) { plain = "⛔ decrypt error"; }
            tvText.setText(plain);

            String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(new Date(m.getTimestamp()));
            tvTime.setText(time);
        }
    }


}
