package com.python.pythonator.ui.templates.adapter.listener;

import android.view.View;

/**
 * Interface for UI to respond to clicks and swipes
 */
public interface AdapterListener {

    /**
     * Gets called when an adapter item is clicked
     * @param view View of clicked item
     * @param pos Adapter position for clicked item
     */
    void onClick(View view, int pos);
    /**
     * Gets called when an adapter item is long clicked
     * @param view View of clicked item
     * @param pos Adapter position for clicked item
     */
    boolean onLongClick(View view, int pos);

    /**
     * Gets called when an adapter item is swiped
     * @param pos Position of item which was swiped
     */
    void onSwiped(int pos);
    boolean allowSwipe(int pos);
}
