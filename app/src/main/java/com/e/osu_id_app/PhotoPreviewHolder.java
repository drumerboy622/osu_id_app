package com.e.osu_id_app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoPreviewHolder extends RecyclerView.ViewHolder {

    ImageView imageView1, imageView2, imageView3, imageView4, imageView5;


    public PhotoPreviewHolder(@NonNull View itemView) {
        super(itemView);

        this.imageView1 = itemView.findViewById(R.id.photoPreview1);
        this.imageView2 = itemView.findViewById(R.id.photoPreview2);
        this.imageView3 = itemView.findViewById(R.id.photoPreview3);
        this.imageView4 = itemView.findViewById(R.id.photoPreview4);
        this.imageView5 = itemView.findViewById(R.id.photoPreview5);


    }
}
