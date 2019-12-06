package com.python.pythonator.ui.templates.adapter.viewholder;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.python.pythonator.ui.templates.adapter.InternalClickListener;

/**
 * Viewholder type to define necessary abstract calls
 * @param <T> Type you want to define a viewholder for
 */
public abstract class ViewHolder<T> extends RecyclerView.ViewHolder {
    // Internal listener for clicks
    protected InternalClickListener internalClickListener;

    public ViewHolder(@NonNull View itemView, @NonNull InternalClickListener internalClickListener) {
        super(itemView);
        this.internalClickListener = internalClickListener;
        findViews();
        setupClicks();
    }

    /**
     * finds all views of the viewholder
     */
    public abstract void findViews();

    /**
     * Setup clicks
     */
    public abstract void setupClicks();

    /**
     * Set item to display
     * @param t Item to display
     */
    public abstract void set(T t);
}
