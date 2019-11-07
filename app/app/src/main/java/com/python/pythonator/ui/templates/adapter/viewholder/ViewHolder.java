package com.python.pythonator.ui.templates.adapter.viewholder;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.python.pythonator.ui.templates.adapter.InternalClickListener;

public abstract class ViewHolder<T> extends RecyclerView.ViewHolder {
    protected InternalClickListener internalClickListener;

    public ViewHolder(@NonNull View itemView, @NonNull InternalClickListener internalClickListener) {
        super(itemView);
        this.internalClickListener = internalClickListener;
        findViews();
        setupClicks();
    }

    public abstract void findViews();
    public abstract void setupClicks();

    public abstract void set(T t);
}
