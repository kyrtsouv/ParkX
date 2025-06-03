package com.example.parkx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pageAdapter#newInstance} factory method to
 * create an instance of this fragment.
 */
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
                return new Map_Fragment();
            default:
                return new Requests_sent(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Total number of pages/tabs
    }
}
