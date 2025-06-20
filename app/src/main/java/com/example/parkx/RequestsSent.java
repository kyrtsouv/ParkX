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
import com.example.parkx.utils.RequestStatus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RequestsSent extends Fragment {
    List<String> titles = new ArrayList<>();
    List<String> details = new ArrayList<>();
    List<Integer> images = new ArrayList<>();
    List<Integer> ids = new ArrayList<>();
    List<RequestStatus> statuses = new ArrayList<>();

    // This method is called when the fragment's view is created.
    // It inflates the layout for the fragment and sets up a RecyclerView to display requests sent by the user.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_sent, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //After the view is created the SupabaseManager method getRequestsSent is called
        SupabaseManager.getRequestsSent(new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<Request> value) {

                // If the database retrieved the spots, a string is created with the
                // name of the owner to whom the current user sent a request and is added to the titles list
                for (Request request : value) {
                    String title = "You have requested " + request.getOwnerName() + " " + request.getOwnerSurname() + "'s spot at coordinates :";

                    titles.add(title);
                    //The coordinates are provided to details list
                    details.add(String.format("(%.5f %.5f)", request.getLatitude(), request.getLongitude()) + " at \n" + request.getExchangeTime().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

                    ids.add(request.getId());
                    statuses.add(request.getStatus());
                    //According to the status a suitable icon is added to the images list
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
                RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images, ids, statuses, 0);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                //If the database method fails it displays an error

                titles.clear();
                details.clear();
                images.clear();

                titles.add("Error");
                details.add("Error");
                images.add(R.drawable.crossmark_svgrepo_com);


                RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images, ids, statuses, 0);
                recyclerView.setAdapter(adapter);

            }
        });

        return view;
    }
}







