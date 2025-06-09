package com.example.parkx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

public class RequestsFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //    RecyclerView.Adapter<RecyclerAdapter.ViewHolder> adapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        viewPager = view.findViewById(R.id.viewP);
//        tabLayout = view.findViewById(R.id.tab);
//
//        pageAdapter adapter = new pageAdapter(requireActivity()); // FragmentActivity required
//        viewPager.setAdapter(adapter);
//
//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> tab.setText(position == 0 ? "Sent" : "Received")
//        ).attach();
    }
}