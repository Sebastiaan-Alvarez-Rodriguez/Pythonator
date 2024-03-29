package com.python.pythonator.ui.templates.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.python.pythonator.ui.templates.adapter.listener.AdapterListener;
import com.python.pythonator.ui.templates.adapter.listener.DragListener;
import com.python.pythonator.ui.templates.adapter.touch.TouchCallback;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;
import com.python.pythonator.util.ListUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Template to create an Adapter, which works with architecture LiveData
 * @param <T> The type of items of the list to be displayed
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Adapter<T> extends RecyclerView.Adapter<ViewHolder<T>> implements Observer<List<T>>, InternalClickListener, DragListener {

    protected List<T> list;
    protected AdapterListener adapter_listener;

    protected TouchCallback touch_callback;
    protected boolean user_sorting_enabled;

    /**
     * Constructor which sets given adapter_listener to send callbacks to, if the listener is not null
     * @param adapterListener Listener to send callbacks in case of item clicks
     */
    public Adapter(@Nullable AdapterListener adapterListener) {
        adapter_listener = adapterListener;
        touch_callback = new TouchCallback(this);
        user_sorting_enabled = false;
        list = new ArrayList<>();
        toggleSort();
    }

    /**
     * Simple function to return an item for given position
     * @throws IndexOutOfBoundsException when an illegal position is passed
     * @return the specified item
     */
    public T get(int index) {
        return list.get(index);
    }

    /**
     * Adds a single item to the list, and displays it to the user
     * @param item The item to be added
     */
    private void add(T item) {
        list.add(item);
    }

    /**
     * @see #add(Object)
     * Function to add a collection of items to the list
     */
    private void add(Collection<T> items) {
        for (T item : items) {
            list.add(item);
            notifyItemInserted(list.size()-1);
        }
    }

    /**
     * Removes a single item from the list, if it is in the list. Does nothing otherwise
     * @param item item to be removed
     */
    private void remove(T item) {
        list.remove(item);
    }

    /**
     * @see #remove(Object)
     * Function to remove a collection of items from the list
     */
    private void remove(Collection<T> items) {
        for (T item : items) {
            int index = list.indexOf(item);
            list.remove(index);
            notifyItemRemoved(index);
        }
    }

    /**
     * Assigns a viewholder an item from the list, depending on the position of the viewholder in the list
     * @param holder The viewholder to receive an item from the UI list
     * @param position The position of the viewholder in the UI list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position) {
        T item = list.get(position);
        holder.set(item);
    }

    /**
     * @return the amount of items in the list
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view, int pos) {
        if (adapter_listener != null)
            adapter_listener.onClick(view, pos);
    }

    @Override
    public boolean onLongClick(View view, int pos) {
        if (adapter_listener != null)
            return adapter_listener.onLongClick(view, pos);
        return false;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(list, i, i + 1);

        } else {
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(list, i, i - 1);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ItemTouchHelper helper = new ItemTouchHelper(touch_callback);
        helper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onItemDismiss(int position) {
        adapter_listener.onSwiped(position);
    }

    public void toggleSort() {
        user_sorting_enabled = !user_sorting_enabled;
        touch_callback.setAllowDrag(user_sorting_enabled);
        touch_callback.setAllowSwipe(user_sorting_enabled);
    }

    /**
     * Callback receiver for list changes.
     * @param newList the new List
     */
    @Override
    public void onChanged(@Nullable List<T> newList) {
        List<T> removed = ListUtil.getRemoved(list, newList);
        List<T> added = ListUtil.getAdded(list, newList);
        remove(removed);
        add(added);
    }


    @Override
    public boolean allowItemDismiss(int position) {
        return adapter_listener.allowSwipe(position);
    }
}
