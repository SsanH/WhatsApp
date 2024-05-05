package com.example.ap2_ex3.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ap2_ex3.AppDatabase;
import com.example.ap2_ex3.R;
import com.example.ap2_ex3.activities.ChatListActivity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddChatFragment extends DialogFragment {
    private String currentUsername;
    private EditText usernameEditText;
    private Button confirmButton;
    private AppDatabase appDatabase;
    private ExecutorService executorService;
    private List<String> chat;

    public AddChatFragment(List<String> chats, String currentUsername) {
        this.chat = chats;
        this.currentUsername = currentUsername;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_chat, container, false);

        usernameEditText = view.findViewById(R.id.chat_input_field);
        confirmButton = view.findViewById(R.id.confirm_button);

        appDatabase = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        // Set click listener for the confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAddingChat();
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        // Increase the size of the dialog window
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }
    private void tryAddingChat() {
        final String username = usernameEditText.getText().toString().trim();
        for (String user: chat) {
            if (Objects.equals(user, username)) {
                showToast("A chat with this user already exists");
                return;
            }
        }
        if (Objects.equals(username, currentUsername)) {
            showToast("Cannot chat with yourself");
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ((ChatListActivity) getActivity()).addChatWithUser(username);
                dismiss();
            }
        });

    }
    private void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}