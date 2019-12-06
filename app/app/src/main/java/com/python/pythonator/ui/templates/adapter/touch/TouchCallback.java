package com.python.pythonator.ui.templates.adapter.touch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.python.pythonator.ui.templates.adapter.listener.DragListener;

/**
 * Touchcallback to handle item touches
 */
public class TouchCallback extends ItemTouchHelper.Callback {
    // Listener for item drags
    private final DragListener listener;
    // Booleans to indicate if drags and swipes are allowed right now
    private boolean allow_drag, allow_swipe;

    public TouchCallback(DragListener listener) {
        this.listener = listener;
        allow_drag = false;
        allow_swipe = false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return allow_drag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return allow_swipe;
    }

    /**
     * Sets value for allowing dragging
     * @param allow <code>true</code> for allowing dragging, <code>false</code> for not allowing this
     */
    public void setAllowDrag(boolean allow) {
        allow_drag = allow;
    }

    /**
     * Sets value for allowing swipes
     * @param allow <code>true</code> for allowing swiping, <code>false</code> for not allowing this
     */
    public void setAllowSwipe(boolean allow) {
        allow_swipe = allow;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = listener.allowItemDismiss(viewHolder.getAdapterPosition()) ? ItemTouchHelper.START | ItemTouchHelper.END : 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
