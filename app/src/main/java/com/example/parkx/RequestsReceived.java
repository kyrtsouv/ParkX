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


public class RequestsReceived extends Fragment {

    List<String> titles = new ArrayList<>();
    List<String> details = new ArrayList<>();
    List<Integer> images = new ArrayList<>();
    List<Integer> ids = new ArrayList<>();
    List<RequestStatus> statuses = new ArrayList<>();

    /**
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests_received, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        SupabaseManager.getRequestsReceived(new JavaResultCallback<>() {
            @Override
            public void onSuccess(List<Request> value) {

                for (Request request : value) {
                    String title = "User " + request.getRequesterName() + " " + request.getRequesterSurname() +
                            " has requested your spot at coordinates : ";

                    titles.add(title);
                    details.add(String.format("(%.5f %.5f)", request.getLatitude(), request.getLongitude()) + " at \n" + request.getExchangeTime().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                    ids.add(request.getId());
                    statuses.add(request.getStatus());

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
                RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images, ids, statuses, 1);
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

                RecyclerAdapter adapter = new RecyclerAdapter(titles, details, images, new ArrayList<>(), new ArrayList<>(), 1);
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }
}