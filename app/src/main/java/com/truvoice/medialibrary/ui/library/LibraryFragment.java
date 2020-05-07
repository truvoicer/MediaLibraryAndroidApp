package com.truvoice.medialibrary.ui.library;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;

public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";

    ViewPager2 viewPager2;
    LibraryPageAdapter libraryPageAdapter;
    private MediaLibraryManager mediaLibraryManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mediaLibraryManager = new MediaLibraryManager();
        libraryPageAdapter = new LibraryPageAdapter(getActivity(), mediaLibraryManager.getTabSections().size());
        viewPager2 = view.findViewById(R.id.library_view_pager);
        viewPager2.setAdapter(libraryPageAdapter);

        TabLayout tabLayout = view.findViewById(R.id.library_tabs);
        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(mediaLibraryManager.getTabSectionObject(position).getTitle());
                    }
                }).attach();
    }

}
