package com.almusand.aaber.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.almusand.aaber.R;
import com.almusand.aaber.utils.Image;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

public class DialogImage extends Dialog implements View.OnClickListener {

    private RoundedImageView imageView;
    private RoundedImageView imgbtSave;
    private String imageUrl;


    public DialogImage(@NonNull Context context, String imageUrl) {
        super(context);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        this.setContentView(R.layout.dialog_image);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        imageView = findViewById(R.id.Card_profile);
        imgbtSave = findViewById(R.id.imgbt_save);

        Glide.with(getContext()).load(imageUrl).apply(new RequestOptions().error(R.drawable.aaber_logo))
                .into(imageView);

        imgbtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgbt_save:
                Image.downloadImage(imageView);
                break;


        }
    }


}
