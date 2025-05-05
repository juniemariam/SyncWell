package com.example.syncwell_android;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {

    private EditText chatInput;
    private Button sendButton;
    private TextView chatResponse;

    private final OkHttpClient client = new OkHttpClient();
    //private final String backendUrl = "http://10.0.2.2:8000/query";  // Emulator IP
    private final String backendUrl = "https://syncwell.onrender.com/query";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatInput = view.findViewById(R.id.chatInput);
        sendButton = view.findViewById(R.id.sendButton);
        chatResponse = view.findViewById(R.id.chatResponse);

        sendButton.setOnClickListener(v -> {
            String question = chatInput.getText().toString().trim();
            if (question.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a question", Toast.LENGTH_SHORT).show();
            } else {
                queryRagModel(question);
            }
        });

        return view;
    }

    private void queryRagModel(String question) {
        MediaType mediaType = MediaType.parse("application/json");
        String json = "{\"question\": \"" + question + "\"}";

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(backendUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to connect to RAG", Toast.LENGTH_SHORT).show());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String answer = jsonObject.getString("answer");

                        requireActivity().runOnUiThread(() ->
                                chatResponse.setText(answer));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Failed to parse response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}


