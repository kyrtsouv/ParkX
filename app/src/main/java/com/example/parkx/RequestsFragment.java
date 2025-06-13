package com.example.parkx;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.parkx.PageAdapter;
import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

public class RequestsFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //    RecyclerView.Adapter<RecyclerAdapter.ViewHolder> adapter;
    private ViewPager2 viewPager;

    private TabLayout tabLayout;

    private Button btn_signOut;
    private TextView topText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    public void log_out() {

    }

    /**
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewP);
        tabLayout = view.findViewById(R.id.tab);
        topText = view.findViewById(R.id.topText);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(String.valueOf((SupabaseManager.getMetadata())));
        } catch (JSONException ignored) {

        }
        String greet = null;
        try {
            greet = "Hello " + jsonObject.getString("name") + " " + jsonObject.getString("surname") + "!";
        } catch (JSONException e) {
            greet = "Hello!";

        }

        topText.setText(greet);

        btn_signOut = view.findViewById(R.id.btn_signOut);
        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupabaseManager.signOut(new JavaResultCallback<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        // αποτρεπω το χρηστη να παει 'πισω' οταν κανει Log out
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NotNull Throwable exception) {

                    }
                });
            }
        });
        PageAdapter adapter = new PageAdapter(requireActivity()); // FragmentActivity required
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Sent");
                            break;
                        case 1:
                            tab.setText("Received");
                            break;
                        case 2:
                            tab.setText("Parking spots"); // Change this label as needed
                            break;
                    }
                }
        ).attach();

        // Add a PageChangeCallback to enable/disable swiping
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Enable swipe on other pages excepts 2 -> Map
                viewPager.setUserInputEnabled(position != 2);
            }
        });

    }
}