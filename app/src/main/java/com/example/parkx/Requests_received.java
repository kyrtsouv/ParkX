package com.example.parkx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkx.R;
import com.example.parkx.RecyclerAdapter;
import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.Request;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Requests_received extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_received, container, false);

        List<String> titles = Arrays.asList("Received X", "Received Y", "Received Z");
        List<String> details = Arrays.asList("Details X");
        List<Integer> images = Arrays.asList(
                R.drawable.android_image_4
        );
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        SupabaseManager.getRequestsReceived(new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<Request> value) {

                List<String> strings=new ArrayList<>();
                for(Request request:value)
                    strings.add(request.getStatus().toString());
                RecyclerAdapter adapter = new RecyclerAdapter(strings, details, images);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onError(@NotNull Throwable exception) {
                List<String> strings=new ArrayList<>();
                strings.add("Error encountered");
                RecyclerAdapter adapter = new RecyclerAdapter(strings, null, null);
                recyclerView.setAdapter(adapter);

            }
        });





        return view;
    }
}