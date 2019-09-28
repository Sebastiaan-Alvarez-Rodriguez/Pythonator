package com.python.pythonator.ui.templates.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.python.pythonator.R;
import com.python.pythonator.ui.templates.adapter.listener.ClickListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see Adapter
 * Template specialization to allow items to be checked
 * @param <T> the type of items in the list
 */
public abstract class AdapterCheckable<T> extends Adapter<T> implements ClickListener {
    protected Set<T> selected_items;

    /**
     * Constructor to set initially selected items and provide a listener to send click callbacks to
     * @param initialSelected List of initially selected items
     * @param onClickListener Listener to send callbacks in case of item clicks
     */
    public AdapterCheckable(List<T> initialSelected, ClickListener onClickListener) {
        super(onClickListener);
        selected_items = initialSelected == null ? new HashSet<>() : new HashSet<>(initialSelected);
    }

    /**
     * @see #AdapterCheckable(List, ClickListener)
     * Same function, only takes away the need to call with second null argument
     */
    public AdapterCheckable(List<T> initialSelected) { this(initialSelected, null);}

    /**
     * @see #AdapterCheckable(List, ClickListener)
     * Same function, only takes away the need to call with 2 null arguments
     */
    public AdapterCheckable() { this(null, null); }


    /**
     * @see Adapter#onClick(View, int)
     * Here, selecting and background changing is handled
     */
    @Override
    public void onClick(View view, int pos) {
        T item = list.get(pos);
        if (selected_items.add(item)) {
            view.setBackgroundResource(R.color.colorAccent);
        } else {
            selected_items.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        }
        super.onClick(view, pos);
    }

    /**
     * @see Adapter#onLongClick(View, int)
     * Here, selecting and background changing is handled
     */
    @Override
    public boolean onLongClick(View view, int pos) {
        T item = list.get(pos);
        if (selected_items.add(item)) {
            view.setBackgroundResource(R.color.colorAccent);
        } else {
            selected_items.remove(item);
            view.setBackgroundResource(android.R.color.transparent);
        }
        return super.onLongClick(view, pos);
    }

    /**
     * @see Adapter#onBindViewHolder(ViewHolder, int)
     * Same as linked function, but colors background if selected
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<T> viewHolder, int position) {
        if (selected_items.contains(list.get(position)))
            viewHolder.itemView.setBackgroundResource(R.color.colorAccent);
        super.onBindViewHolder(viewHolder, position);
    }

    /**
     * When a view is sent to recycle view pool, it may have a colored background.
     * Such colors are removed to ensure the view does not appear always selected when rebound
     * @param holder The holder the view belonged to
     */
    @Override
    public void onViewRecycled(@NonNull ViewHolder<T> holder) {
        holder.itemView.setBackgroundResource(android.R.color.transparent);
        super.onViewRecycled(holder);
    }

    /**
     * @return the amount of selected items
     */
    public int getSelectedCount() {
        return selected_items.size();
    }

    /**
     * @return whether any items are selected at all
     */
    public boolean hasSelected() {
        return !selected_items.isEmpty();
    }

    /**
     * @return all selected items, by value
     */
    public Set<T> getSelected() {
        return new HashSet<>(selected_items);
    }

    /**
     * @see Adapter#onChanged(List)
     * Clears selected items before a change happens
     */
    @Override
    public void onChanged(@Nullable List<T> newList) {
        if (newList == null)
            selected_items = new HashSet<>();
        else
            for (T t : selected_items)
                if (!newList.contains(t))
                    selected_items.remove(t);
        super.onChanged(newList);

    }
}
