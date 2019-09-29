package com.python.pythonator.ui.templates.adapter.touch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.python.pythonator.ui.templates.adapter.listener.DragListener;

public class TouchCallback extends ItemTouchHelper.Callback {
    private final DragListener adapter;
    private boolean allow_drag, allow_swipe;

    public TouchCallback(DragListener adapter) {
        this.adapter = adapter;
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

    public void setAllowDrag(boolean allow) {
        allow_drag = allow;
    }

    public void setAllowSwipe(boolean allow) {
        allow_swipe = allow;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
