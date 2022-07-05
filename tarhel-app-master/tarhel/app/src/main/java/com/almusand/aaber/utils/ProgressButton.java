package com.almusand.aaber.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.almusand.aaber.R;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class ProgressButton {

    private CardView cardView;
    private ConstraintLayout constraintLayout;
    private ProgressBar progressBar;
    private TextView tvButton;

    Animation fad_in;
    private String btTittle;

    public ProgressButton(Context context, View view) {
        fad_in = AnimationUtils.loadAnimation(context, R.anim.fad_in);

        cardView = view.findViewById(R.id.card_view);
        constraintLayout = view.findViewById(R.id.constraint_layout);
        progressBar = view.findViewById(R.id.progressBar);
        tvButton = view.findViewById(R.id.tv_button);
    }

    public void setButtonTittle(String s) {
        btTittle = s;
        tvButton.setText(s);
    }

    public void buttonActivated() {
        progressBar.setAnimation(fad_in);
        progressBar.setVisibility(View.VISIBLE);
        tvButton.setAnimation(fad_in);
        tvButton.setText(cardView.getResources().getText(R.string.please_wait));
    }

    public void buttonDactivated() {
        progressBar.setAnimation(fad_in);
        progressBar.setVisibility(View.GONE);
        tvButton.setAnimation(fad_in);
        tvButton.setText(btTittle);
    }

    public void buttonFinished() {
        tvButton.setText(cardView.getResources().getText(R.string.done));
        constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
        progressBar.setVisibility(View.GONE);
    }


}
