package com.example.tracemaster.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tracemaster.R;
import com.example.tracemaster.data.viewModel.GalleryViewModel;

public class GalleryFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        findID(root);


        return root;
    }


    private void findID(View root) {


    }


}