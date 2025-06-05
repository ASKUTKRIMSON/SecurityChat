package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ui/ChatListFragment.java  (упрощённая версия)
public class ChatListFragment extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Message, VH> adapter;

    @Override public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s){
        return i.inflate(R.layout.fragment_chat_list, c, false);
    }

    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
        db = FirebaseFirestore.getInstance();

        RecyclerView rv = v.findViewById(R.id.recyclerViewMessages);
        Query q = db.collection("messages").orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> opt = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(q, Message.class).build();

        adapter = new FirestoreRecyclerAdapter<Message, VH>(opt) {
            @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int t){
                View row = LayoutInflater.from(p.getContext())
                        .inflate(R.layout.item_message, p, false);
                return new VH(row);
            }
            @Override protected void onBindViewHolder(@NonNull VH h,int p,@NonNull Message m){
                h.bind(m);
            }
        };
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }
    @Override public void onStart(){ super.onStart(); adapter.startListening();}
    @Override public void onStop() { super.onStop();  adapter.stopListening();}

    /* view-holder */
    class VH extends RecyclerView.ViewHolder{
        TextView t; VH(@NonNull View v){super(v);t=v.findViewById(R.id.textViewMessage);}
        void bind(Message m){ t.setText("⛔ Только чтение"); }
    }
}
