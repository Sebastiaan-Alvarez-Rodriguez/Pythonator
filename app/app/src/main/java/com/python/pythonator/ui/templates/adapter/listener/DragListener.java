package com.python.pythonator.ui.templates.adapter.listener;

public interface DragListener {
    /**
     * Called when an item has been moved one place
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     */
    void onItemMove(int fromPosition, int toPosition);


    /**
     * Called when an item has been dismissed by a swipe.
     * @param position The position of the item dismissed.
     */
    void onItemDismiss(int position);
}
