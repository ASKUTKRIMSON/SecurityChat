package com.example.testrv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SecondFragment extends Fragment {

    private String mParam1;
    private NotificationCompat.Builder builder;
    private String mParam2;

    public SecondFragment() {

    }
//    public static SecondFragment newInstance(String param1, String param2) {
//        SecondFragment fragment = new SecondFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (builder==null){
            builder = new NotificationCompat.Builder(requireContext(),MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Уведомление")
                    .setContentText("Бла бла бла")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        View root = inflater.inflate(R.layout.fragment_second, container, false);

        Button button = root.findViewById(R.id.button_notify);
        button.setOnClickListener(v -> {
            NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
        });
        return root;
    }
}