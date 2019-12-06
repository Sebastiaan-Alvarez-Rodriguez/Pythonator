package com.python.pythonator.ui.templates.adapter.listener;

/**
 * Listener to signal UI of item drags and swipes
 */
public interface DragListener {
    /**
     * Called when an item has been moved one place
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     */
    void onItemMove(int fromPosition, int toPosition);


    /**
     * Allow item at position to be dismissed
     * @param position Position of item to be dismissed
     * @return <code>true</code> if you allow the item to be dismissed, otherwise <code>false</code>
     */
    boolean allowItemDismiss(int position);
    /**
     * Called when an item has been dismissed by a swipe.
     * @param position The position of the item dismissed.
     */
    void onItemDismiss(int position);
}
