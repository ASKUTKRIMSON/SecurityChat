package com.example.securitychat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.securitychat.*;
import com.example.securitychat.R;
import com.example.securitychat.crypto.CryptoUtil;
import com.example.securitychat.model.Message;
import com.firebase.ui.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class PrivateChatFragment extends Fragment {

    private String roomId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences prefs;

    private FirestoreRecyclerAdapter<Message, MsgVH> adapter;
    private EditText input;
    private RecyclerView rv;

    @Override public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s){
        return i.inflate(R.layout.fragment_private_chat, c,false);
    }

    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
        super.onViewCreated(v,s);
        roomId   = requireArguments().getString("roomId");
        db       = FirebaseFirestore.getInstance();
        auth     = FirebaseAuth.getInstance();
        prefs    = requireActivity().getSharedPreferences("settings_prefs",0);

        rv    = v.findViewById(R.id.recyclerPrivate);
        input = v.findViewById(R.id.editPrivate);
        ImageButton send = v.findViewById(R.id.btnSendPrivate);

        Query q = db.collection("rooms").document(roomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> opt =
                new FirestoreRecyclerOptions.Builder<Message>().setQuery(q, Message.class).build();

        adapter = new FirestoreRecyclerAdapter<Message, MsgVH>(opt){
            @NonNull @Override public MsgVH onCreateViewHolder(@NonNull ViewGroup p,int t){
                View row=LayoutInflater.from(p.getContext())
                        .inflate(R.layout.item_message, p,false);
                return new MsgVH(row);
            }
            @Override protected void onBindViewHolder(@NonNull MsgVH h,int p,@NonNull Message m){
                h.bind(m);
            }
        };
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        send.setOnClickListener(vv -> send());
    }

    private void send(){
        String plain = input.getText().toString().trim();
        if(TextUtils.isEmpty(plain)) return;

        String key = prefs.getString("secure_shared_key", null);
        if(key==null){ Toast.makeText(getContext(),"Нет ключа",Toast.LENGTH_SHORT).show(); return;}

        try{
            String cipher = CryptoUtil.encrypt(plain, key);
            Message m = new Message(cipher, auth.getUid(), System.currentTimeMillis());

            db.collection("rooms").document(roomId)
                    .collection("messages").add(m)
                    .addOnSuccessListener(r -> { input.setText(""); });
        }catch(Exception e){
            Toast.makeText(getContext(),"encrypt err",Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onStart(){ super.onStart(); adapter.startListening(); }
    @Override public void onStop() { super.onStop();  adapter.stopListening(); }

    /* ---------- vh ---------- */
    class MsgVH extends RecyclerView.ViewHolder{
        TextView tvSender,tvText,tvTime;
        MsgVH(@NonNull View v){ super(v);
            tvSender=v.findViewById(R.id.textViewSender);
            tvText  =v.findViewById(R.id.textViewMessage);
            tvTime  =v.findViewById(R.id.textViewTime);
        }
        void bind(Message m){
            tvSender.setText(m.getSenderUid().substring(0,6));
            String key=prefs.getString("secure_shared_key",null);
            String plain; try{ plain=CryptoUtil.decrypt(m.getText(),key);}
            catch(Exception e){ plain="err"; }
            tvText.setText(plain);
            String t=new SimpleDateFormat("HH:mm",Locale.getDefault())
                    .format(new Date(m.getTimestamp()));
            tvTime.setText(t);
        }
    }
}
