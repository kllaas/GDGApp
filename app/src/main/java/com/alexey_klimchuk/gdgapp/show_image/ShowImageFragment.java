package com.alexey_klimchuk.gdgapp.show_image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.ImageShowPagerAdapter;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexey on 13.11.2016.
 */
public class ShowImageFragment extends Fragment {

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;
    private String noteId;

    public static ShowImageFragment newInstance(@Nullable String noteId) {
        Bundle arguments = new Bundle();
        arguments.putString(Constants.EXTRA_NOTE_ID, noteId);
        ShowImageFragment fragment = new ShowImageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_image, container, false);

        ButterKnife.bind(this, v);
        noteId = getArguments().getString(Constants.EXTRA_NOTE_ID);
        String[] data = {NotesRepository.getCachedNotesMap().get(noteId).getLocalImage(),
                NotesRepository.getCachedNotesMap().get(noteId).getLocalImage(),
                NotesRepository.getCachedNotesMap().get(noteId).getLocalImage(),
                NotesRepository.getCachedNotesMap().get(noteId).getLocalImage()};
        ImageShowPagerAdapter adapter = new ImageShowPagerAdapter(mViewPager, data);
        mViewPager.setAdapter(adapter);
        mViewPager.setPageMargin(20);
        return v;
    }

    public interface OnBackPressedListener {
        void doBack();
    }
}
