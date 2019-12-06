package com.python.pythonator.ui.templates.adapter.listener;

/**
 * Listener for UI actionmode changes
 */
public interface AdapterActionListener extends AdapterListener {

    /**
     * Gets called when there is a change in actionmode
     * @param isActionMode Current actionmode state
     */
    void onActionModeChange(boolean isActionMode);
}
