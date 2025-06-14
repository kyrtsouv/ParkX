package com.example.parkx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestsFragment extends Fragment {
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    // This method is called after the view has been created. It initializes the ViewPager2, TabLayout, and sets up the greeting text based on user metadata.
    // It also sets up a sign-out button that clears the user session and redirects to the MainActivity.

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewP);
        TabLayout tabLayout = view.findViewById(R.id.tab);
        TextView topText = view.findViewById(R.id.topText);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(String.valueOf((SupabaseManager.getMetadata())));
        } catch (JSONException ignored) {

        }
        String greet;
        try {
            greet = "Hello " + jsonObject.getString("name") + " " + jsonObject.getString("surname") + "!";
        } catch (JSONException e) {
            greet = "Hello!";
        }

        topText.setText(greet);

        Button btn_signOut = view.findViewById(R.id.btn_signOut);
        btn_signOut.setOnClickListener(v -> SupabaseManager.signOut(new JavaResultCallback<>() {
            @Override
            public void onSuccess(String value) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                //The user is not allowed to go back when they log out
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(@NotNull Throwable exception) {

            }
        }));
        PageAdapter adapter = new PageAdapter(requireActivity());
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
                            tab.setText("Parking spots");
                            break;
                    }
                }
        ).attach();

        //Adds a PageChangeCallback to enable/disable swiping
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Enables swipe on other pages except 2 -> Map
                viewPager.setUserInputEnabled(position != 2);
            }
        });

    }
}