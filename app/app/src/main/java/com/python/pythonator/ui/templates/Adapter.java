package com.python.pythonator.ui.templates;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.List;

/**
 * Template to create an Adapter, which works with architecture LiveData
 * @param <T> The type of items of the list to be displayed
 */
public abstract class Adapter<T> extends RecyclerView.Adapter<ViewHolder<T>> implements Observer<List<T>>, ClickListener {

    protected List<T> list;
    protected ClickListener clickListener;

    /**
     * Constructor which sets given clickListener to send callbacks to, if the listener is not null
     * @param clickListener Listener to send callbacks in case of item clicks
     */
    public Adapter(@Nullable ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * @see #Adapter(ClickListener)
     * Same function, but sets no listener
     */
    public Adapter() {
        this(null);
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
    public void add(T item) {
        list.add(item);
    }

    /**
     * @see #add(Object)
     * Function to add a collection of items to the list
     */
    public void add(Collection<T> items) {
        list.addAll(items);
    }

    /**
     * Removes a single item from the list, if it is in the list. Does nothing otherwise
     * @param item item to be removed
     */
    public void remove(T item) {
        list.remove(item);
    }

    /**
     * @see #remove(Object)
     * Function to remove a collection of items from the list
     */
    public void remove(Collection<T> items) {
        for (T item : items)
            list.remove(item);
    }

    public void replaceAll(@NonNull Collection<T> items) {
        for (int i = list.size() -1; i >= 0; i--) {
            final T item = list.get(i);
            if (!items.contains(item))
                list.remove(item);
        }
        list.addAll(items);
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
        if (clickListener != null)
            clickListener.onClick(view, pos);
    }

    @Override
    public boolean onLongClick(View view, int pos) {
        if (clickListener != null)
            return clickListener.onLongClick(view, pos);
        return false;
    }

    /**
     * Callback receiver for list changes.
     * @param newList the new List
     */
    @Override
    public void onChanged(@Nullable List<T> newList) {
        if (newList != null)
            replaceAll(newList);
        else
            list.clear();
//        List<T> removed = ListUtil.getRemoved(list, newList);
//        List<T> added = ListUtil.getAdded(list, newList);
//        remove(removed);
//        add(added);
    }
}
