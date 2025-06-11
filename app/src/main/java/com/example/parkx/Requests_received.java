package com.example.parkx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.Request;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Requests_received extends Fragment {

    List<String> titles = new ArrayList<>();
    List<String> details = new ArrayList<>();
    List<Integer> images = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_received, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        SupabaseManager.getRequestsReceived(new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<Request> value) {

                for (Request request : value) {
                    String title = "User " + request.getRequesterName() + " " + request.getRequesterSurname() + " " +
                            " has requested your spot at coordinates : ";

                    titles.add(title);

                    String detail = String.format("%.5f %.5f", request.getLatitude(), request.getLongitude());
                    details.add(detail);


                    switch (request.getStatus()) {
                        case ACCEPTED:
                            images.add(R.drawable.checkmark_svgrepo_com);
                            break;
                        case REJECTED:
                            images.add(R.drawable.crossmark_svgrepo_com);
                            break;
                        case PENDING:
                            images.add(R.drawable.pending_svgrepo_com);
                            break;
                    }
                }
                RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                titles.clear();
                details.clear();
                images.clear();
                titles.add("Error");
                details.add("Error");
                images.add(R.drawable.crossmark_svgrepo_com);

                RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images);
                recyclerView.setAdapter(adapter);
            }
        });


        return view;
    }
}