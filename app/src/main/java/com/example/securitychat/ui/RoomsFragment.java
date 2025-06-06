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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фрагмент «Комнаты»:
 * 1) Показывает список приватных комнат, в которых участвует текущий пользователь;
 * 2) Позволяет создать новую комнату (с одним участником) — при создании сразу переходим в PrivateChatFragment.
 */
public class RoomsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Room, RoomVH> adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

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

        // Запрос: показать только те комнаты, где есть текущий UID
        Query query = db.collection("rooms")
                .whereArrayContains("participants", auth.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Room> options =
                new FirestoreRecyclerOptions.Builder<Room>()
                        .setQuery(query, Room.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Room, RoomVH>(options) {
            @NonNull
            @Override
            public RoomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View row = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_room, parent, false);
                return new RoomVH(row);
            }

            @Override
            protected void onBindViewHolder(
                    @NonNull RoomVH holder, int position, @NonNull Room model) {
                String roomId = getSnapshots().getSnapshot(position).getId();
                holder.bind(model, roomId);
            }
        };

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        // При нажатии на «плюс» → создаём новую комнату
        fab.setOnClickListener(v -> createRoom());
    }

    /** Метод создаёт новый документ комнаты с одним участником (текущий UID). */
    private void createRoom() {
        Map<String, Object> data = new HashMap<>();
        data.put("participants", Collections.singletonList(auth.getUid()));
        data.put("createdAt", System.currentTimeMillis());

        db.collection("rooms").add(data)
                .addOnSuccessListener(doc -> {
                    // Перейти в PrivateChatFragment сразу после создания комнаты:
                    Bundle args = new Bundle();
                    args.putString("roomId", doc.getId());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.privateChatFragment, args);
                })
                .addOnFailureListener(e -> {
                    // При желании можно вывести Toast с ошибкой
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

    /* -------------- ViewHolder для отображения «комнаты» -------------- */
    class RoomVH extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvRoom;

        RoomVH(@NonNull View itemView) {
            super(itemView);
            tvRoom = itemView.findViewById(R.id.textRoom);
        }

        void bind(Room room, String roomId) {
            List<String> parts = room.getParticipants();
            String label = "Room " + roomId.substring(0, 6)
                    + " (" + parts.size() + "/2)";
            tvRoom.setText(label);

            itemView.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("roomId", roomId);
                Navigation.findNavController(v)
                        .navigate(R.id.privateChatFragment, args);
            });
        }
    }
}
