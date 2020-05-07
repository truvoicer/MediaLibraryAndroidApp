package com.truvoice.medialibrary.ui.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LibraryPageAdapter extends FragmentStateAdapter {

    private int tabSections;

    public LibraryPageAdapter(@NonNull FragmentActivity fragmentActivity, int tabSections) {
        super(fragmentActivity);
        this.tabSections = tabSections;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new LibraryPageFragment();
        Bundle args = new Bundle();
        args.putInt(LibraryPageFragment.PAGE_ADAPTER_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabSections;
    }
}
