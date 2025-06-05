package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securitychat.R;
import com.example.securitychat.model.Room;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RoomsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Room, RoomVH> adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        RecyclerView rv = view.findViewById(R.id.recyclerRooms);
        FloatingActionButton fab = view.findViewById(R.id.fab_newRoom);

        Query q = db.collection("rooms")
                .whereArrayContains("participants", auth.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Room> opts =
                new FirestoreRecyclerOptions.Builder<Room>()
                        .setQuery(q, Room.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Room, RoomVH>(opts) {
            @NonNull @Override
            public RoomVH onCreateViewHolder(@NonNull ViewGroup p, int t) {
                View row = LayoutInflater.from(p.getContext())
                        .inflate(R.layout.item_room, p, false);
                return new RoomVH(row);
            }
            @Override
            protected void onBindViewHolder(
                    @NonNull RoomVH h, int pos, @NonNull Room r) {
                String id = getSnapshots().getSnapshot(pos).getId();
                h.bind(r, id);
            }
        };

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> createRoom());
    }

    /** Создание новой комнаты (только текущий пользователь). */
    private void createRoom() {
        Map<String, Object> data = new HashMap<>();
        data.put("participants", Collections.singletonList(auth.getUid()));
        data.put("createdAt",    System.currentTimeMillis());

        db.collection("rooms").add(data)
                .addOnSuccessListener(doc -> {
                    Bundle args = new Bundle();
                    args.putString("roomId", doc.getId());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.privateChatFragment, args);
                });
    }

    @Override public void onStart() { super.onStart(); adapter.startListening(); }
    @Override public void onStop()  { super.onStop();  adapter.stopListening(); }

    /* ------------ View-holder ------------- */
    class RoomVH extends RecyclerView.ViewHolder {
        final android.widget.TextView tv;
        RoomVH(@NonNull View item) {
            super(item);
            tv = item.findViewById(R.id.textRoom);
        }
        void bind(Room room, String roomId) {
            List<String> parts = room.getParticipants();
            String label = "Room " + roomId.substring(0, 6) +
                    " (" + parts.size() + "/2)";
            tv.setText(label);

            itemView.setOnClickListener(v -> {
                Bundle a = new Bundle();
                a.putString("roomId", roomId);
                Navigation.findNavController(v)
                        .navigate(R.id.privateChatFragment, a);
            });
        }
    }
}
