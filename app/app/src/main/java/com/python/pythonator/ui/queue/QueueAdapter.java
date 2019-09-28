package com.python.pythonator.ui.queue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.python.pythonator.structures.Image;
import com.python.pythonator.ui.templates.adapter.listener.ActionListener;
import com.python.pythonator.ui.templates.adapter.AdapterAction;
import com.python.pythonator.ui.templates.adapter.listener.ClickListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

public class QueueAdapter extends AdapterAction<Image> implements ClickListener {

    public QueueAdapter(@Nullable ActionListener actionListener) {
        super(actionListener);
    }

    @NonNull
    @Override
    public ViewHolder<Image> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(QueueViewHolder.layout_resource, parent,false);
        return clickListener != null ? new QueueViewHolder(view, this) : new QueueViewHolder(view);
    }
}
