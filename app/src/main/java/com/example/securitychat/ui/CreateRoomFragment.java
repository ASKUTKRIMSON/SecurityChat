package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.securitychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

// ui/CreateRoomFragment.java  (обновлён)
public class CreateRoomFragment extends Fragment {
    @Override public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s){
        return i.inflate(R.layout.fragment_create_room, c,false);
    }
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){
        v.findViewById(R.id.buttonCreate).setOnClickListener(view -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Map<String,Object> data = new HashMap<>();
            data.put("participants", Collections.singletonList(auth.getUid()));
            data.put("createdAt", System.currentTimeMillis());

            FirebaseFirestore.getInstance().collection("rooms").add(data)
                    .addOnSuccessListener(doc -> {
                        Bundle a = new Bundle();
                        a.putString("roomId", doc.getId());   // аргумент
                        Navigation.findNavController(requireActivity(), R.id.nav_host)
                                .navigate(R.id.privateChatFragment, a);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),"Ошибка: "+e.getMessage(),Toast.LENGTH_SHORT).show());
        });
    }
}
