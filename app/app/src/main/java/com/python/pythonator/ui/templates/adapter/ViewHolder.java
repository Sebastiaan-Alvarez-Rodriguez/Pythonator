package com.python.pythonator.ui.templates.adapter;



import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ViewHolder<T> extends RecyclerView.ViewHolder {
    protected ClickListener clickListener;

    public ViewHolder(@NonNull View itemView) {
        this(itemView, null);
    }

    public ViewHolder(@NonNull View itemView, @NonNull ClickListener clickListener) {
        super(itemView);
        this.clickListener = clickListener;
        findViews();
        setupClicks();
    }

    public abstract void findViews();
    public abstract void setupClicks();

    public abstract void set(T t);
}
