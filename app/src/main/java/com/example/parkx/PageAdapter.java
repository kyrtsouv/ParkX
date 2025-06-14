package com.example.parkx;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class PageAdapter extends FragmentStateAdapter {


    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return different fragment for each tab
        switch (position) {
            case 0:
                return new RequestsSent();
            case 1:
                return new RequestsReceived();
            case 2:
                return new ProfileParkingSpots();
            default:
                return new RequestsSent(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Total number of pages/tabs
    }
}
