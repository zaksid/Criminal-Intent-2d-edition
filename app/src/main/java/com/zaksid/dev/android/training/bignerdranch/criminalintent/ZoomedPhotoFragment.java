package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Dialog to show zoomed-in version of photo
 */
public class ZoomedPhotoFragment extends DialogFragment {
    private final static String ARG_PHOTO = "photo path";

    public static ZoomedPhotoFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, file);

        ZoomedPhotoFragment fragment = new ZoomedPhotoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File photoFile = (File) getArguments().getSerializable(ARG_PHOTO);

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.photo_dialog_fragment, null);

        final ImageView imageView = (ImageView) view.findViewById(R.id.zoomed_photo_view);
        imageView.setImageBitmap(PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity()));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZoomedPhotoFragment.this.dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
            .setView(view)
            .create();
    }
}
