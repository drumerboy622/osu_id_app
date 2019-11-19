package com.e.osu_id_app;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhotoPreviewAdapter extends RecyclerView.Adapter<PhotoPreviewHolder> {

    Context c;
    ArrayList<ColorSpace.Model> models;

    public PhotoPreviewAdapter(Context c, ArrayList<ColorSpace.Model> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public PhotoPreviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, null);

        return new PhotoPreviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoPreviewHolder holder, int position) {

        //set upload statistics here

        //set image paths here

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
