package com.example.parkx;
import com.example.parkx.supabase.SupabaseManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.parkx.RecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkx.R;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.Request;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Requests_sent extends Fragment {
    private List<String> strings = new ArrayList<>(); // Moved outside
    List<String> details = Arrays.asList("Details X");
    List<Integer> images = Arrays.asList(
            R.drawable.android_image_4

    );






    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_sent, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SupabaseManager.getRequestsSent(new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<Request> value) {
                System.out.println("Total requests: " + value.size());

                // Ensure `strings` is updated globally
                strings.clear();
                for (Request request : value) {
                    if (request.getRequesterId() != null) {
                        strings.add(request.getRequesterId().toString());
                    } else {
                        System.out.println("Null requester ID found!");
                    }
                }
                System.out.println("Strings " + strings.size());

                // Set adapter AFTER data is loaded
                RecyclerAdapter adapter = new RecyclerAdapter(strings, details, images);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                strings.clear();
                strings.add("Error encountered");
                System.out.println("Error fetching requests: " + exception.getMessage());
            }
        });

        return view;
    }




}
