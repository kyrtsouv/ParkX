package com.example.parkx.tests;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.parkx.MapFragment;

public class pageAdapter extends FragmentStateAdapter {

    public pageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return different fragment for each tab
        switch (position) {
            case 0:
                return new Requests_sent();
            case 1:
                return new Requests_received();
            case 2:
                return new MapFragment();
            default:
                return new Requests_sent(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Total number of pages/tabs
    }
}
