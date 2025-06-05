// app/src/main/java/com/example/securitychat/ui/ChatListFragment.java
package com.example.securitychat.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securitychat.R;
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
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Message, MessageViewHolder> adapter;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private FloatingActionButton fabMore;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        fabMore = view.findViewById(R.id.fab_more);

        setupRecyclerView();

        buttonSend.setOnClickListener(v -> {
            String text = editTextMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(text) && mAuth.getCurrentUser() != null) {
                String senderUid = mAuth.getCurrentUser().getUid();
                long timestamp = System.currentTimeMillis();

                Message msg = new Message(text, senderUid, timestamp);
                db.collection("messages")
                        .add(msg)
                        .addOnSuccessListener(documentReference -> {
                            editTextMessage.setText("");
                            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                        });
            }
        });

        fabMore.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_chatList_to_settings)
        );
    }

    private void setupRecyclerView() {
        Query query = db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Message, MessageViewHolder>(options) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent,
                    int viewType
            ) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(
                    @NonNull MessageViewHolder holder,
                    int position,
                    @NonNull Message model
            ) {
                holder.bind(model);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSender, textViewMessage, textViewTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSender = itemView.findViewById(R.id.textViewSender);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        public void bind(Message msg) {
            String shortUid = msg.getSenderUid().substring(0, 6);
            textViewSender.setText("User: " + shortUid);
            textViewMessage.setText(msg.getText());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeStr = sdf.format(new Date(msg.getTimestamp()));
            textViewTime.setText(timeStr);
        }
    }
}
