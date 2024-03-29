package com.python.pythonator.ui.templates.adapter;

import android.view.View;

import androidx.annotation.Nullable;

import com.python.pythonator.ui.templates.adapter.listener.AdapterActionListener;

import java.util.List;

/**
 * @see AdapterCheckable
 * Template specialization to allow for action modes, without Android's monstrosities
 * @param <T> The type of items in the list
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AdapterAction<T> extends AdapterCheckable<T> {
    private boolean actionMode = false;

    /**
     * Constructor to set an actionlistener
     * @param adapterActionListener the listener to send callbacks to in case of clicks or action mode changes
     */
    public AdapterAction(AdapterActionListener adapterActionListener) {
        super(null, adapterActionListener);
    }

    /**
     * @see #AdapterAction(AdapterActionListener)
     * Same function, without having to call with null as argument
     */
    public AdapterAction() {
        this(null);
    }

    /**
     * @see AdapterCheckable#onClick(View, int)
     * Selects item in case of action mode. Sends click event otherwise.
     */
    @Override
    public void onClick(View view, int pos) {
        if (actionMode) {
            super.onClick(view, pos);

            if (actionMode && !hasSelected()) {
                actionMode = false;
                ((AdapterActionListener) adapter_listener).onActionModeChange(false);
            }
        } else {
            adapter_listener.onClick(view, pos);
        }
    }

    /**
     * @see AdapterCheckable#onLongClick(View, int)
     * Activates actionmode and selects item
     * @return true if the click is consumed, false otherwise
     */
    @Override
    public boolean onLongClick(View view, int pos) {
        if (!actionMode) {
            actionMode = true;
            ((AdapterActionListener) adapter_listener).onActionModeChange(true);
        }
        boolean consumed =  super.onLongClick(view, pos);

        if (actionMode && !hasSelected()) {
            actionMode = false;
            ((AdapterActionListener) adapter_listener).onActionModeChange(false);
        }
        return consumed;
    }

    /**
     * @return whether actionmode is active or not
     */
    public boolean isActionMode() {
        return actionMode;
    }

    public void deactivateActionMode() {
        actionMode = false;
        ((AdapterActionListener) adapter_listener).onActionModeChange(false);
        selected_items.clear();
    }
    /**
     * @see AdapterCheckable#onChanged(List)
     * Stops action mode
     */
    @Override
    public void onChanged(@Nullable List<T> newList) {
        super.onChanged(newList);
    }
}
