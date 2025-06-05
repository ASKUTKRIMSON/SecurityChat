// app/src/main/java/com/example/securitychat/ui/ChatFragment.java
package com.example.securitychat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.securitychat.R;

/**
 * Пустой фрагмент «Chat» (формально 3-й экран).
 * В минимальном варианте содержит лишь информационный TextView.
 */
public class ChatFragment extends Fragment {
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_chat_empty, container, false);
    }
}
