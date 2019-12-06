package com.python.pythonator.ui.templates.adapter;

import android.view.View;

/**
 * Internal listener for templates, do not use this except for inter-template communication
 */
public interface InternalClickListener {
    /**
     * Handles clicks on viewholders
     * @param view Clicked view
     * @param pos Clicked pos
     */
    void onClick(View view, int pos);

    /**
     * Handles long clicks on viewholders
     * @param view Clicked view
     * @param pos Clicked pos
     * @return true if the click was consumed, false otherwise
     */
    boolean onLongClick(View view, int pos);
}
