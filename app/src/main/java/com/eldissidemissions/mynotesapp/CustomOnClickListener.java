package com.eldissidemissions.mynotesapp;

import android.view.View;

//class ini untuk membuat item di cardview clickable di dalam adapter
public class CustomOnClickListener implements View.OnClickListener{
    private int position;
    private OnItemClickCallBack onItemClickCallBack;

    public CustomOnClickListener(int position, OnItemClickCallBack onItemClickCallBack) {
        this.position = position;
        this.onItemClickCallBack = onItemClickCallBack;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallBack.clickable(view, position);
    }

    public interface OnItemClickCallBack{
        void clickable(View view, int position);
    }
}
