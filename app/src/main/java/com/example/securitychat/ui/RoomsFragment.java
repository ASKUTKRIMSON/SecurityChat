package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;
import com.example.securitychat.R;
import com.example.securitychat.model.Room;
import com.firebase.ui.firestore.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class RoomsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Room, RoomVH> adapter;

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        return inf.inflate(R.layout.fragment_rooms, c, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v,s);
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        RecyclerView rv = v.findViewById(R.id.recyclerRooms);
        FloatingActionButton fab = v.findViewById(R.id.fab_newRoom);

        Query q = db.collection("rooms")
                .whereArrayContains("participants", auth.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Room> opts =
                new FirestoreRecyclerOptions.Builder<Room>().setQuery(q, Room.class).build();

        adapter = new FirestoreRecyclerAdapter<Room, RoomVH>(opts) {
            @NonNull @Override public RoomVH onCreateViewHolder(@NonNull ViewGroup p,int t){
                View item = LayoutInflater.from(p.getContext())
                        .inflate(R.layout.item_room, p,false);
                return new RoomVH(item);
            }
            @Override protected void onBindViewHolder(@NonNull RoomVH h,int p,@NonNull Room r){
                h.bind(r, getSnapshots().getSnapshot(p).getId());
            }
        };
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        fab.setOnClickListener(vv -> createRoom());
    }

    private void createRoom() {
        // создаём комнату с одним участником, второй пригласится по roomId
        Map<String,Object> data=new HashMap<>();
        data.put("participants", Collections.singletonList(auth.getUid()));
        data.put("createdAt", System.currentTimeMillis());

        db.collection("rooms").add(data)
                .addOnSuccessListener(doc -> {
                    Bundle b=new Bundle(); b.putString("roomId", doc.getId());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_rooms_to_privateChat,b);
                });
    }

    @Override public void onStart(){ super.onStart(); adapter.startListening(); }
    @Override public void onStop() { super.onStop();  adapter.stopListening(); }

    /* ---------- View-holder ---------- */
    class RoomVH extends RecyclerView.ViewHolder {
        TextView tv;
        RoomVH(@NonNull View item){ super(item); tv=item.findViewById(R.id.textRoom); }
        void bind(Room r, String roomId){
            List<String> parts=r.getParticipants();
            String label = "Room: "+roomId.substring(0,6)
                    +" ("+parts.size()+"/2)";
            tv.setText(label);
            itemView.setOnClickListener(v -> {
                Bundle b=new Bundle(); b.putString("roomId", roomId);
                Navigation.findNavController(v)
                        .navigate(R.id.action_rooms_to_privateChat,b);
            });
        }
    }
}
