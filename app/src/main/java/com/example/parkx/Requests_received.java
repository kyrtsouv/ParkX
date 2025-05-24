package com.example.parkx;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;


public class Requests_received extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_received, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> titles = Arrays.asList("Received X", "Received Y", "Received Z");
        List<String> details = Arrays.asList("Details X", "Details Y", "Details Z");
        List<Integer> images = Arrays.asList(
                R.drawable.android_image_4,
                R.drawable.android_image_5,
                R.drawable.android_image_6
        );

        RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images);
        recyclerView.setAdapter(adapter);

        return view;
    }
}