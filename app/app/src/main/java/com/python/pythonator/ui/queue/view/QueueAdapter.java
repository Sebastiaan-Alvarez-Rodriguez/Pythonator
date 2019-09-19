package com.python.pythonator.ui.queue.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.python.pythonator.structures.Image;
import com.python.pythonator.ui.templates.ActionListener;
import com.python.pythonator.ui.templates.AdapterAction;
import com.python.pythonator.ui.templates.ViewHolder;

public class QueueAdapter extends AdapterAction<Image> {

    public QueueAdapter(@Nullable ActionListener actionListener) {
        super(actionListener);
    }

    @NonNull
    @Override
    public ViewHolder<Image> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(QueueViewHolder.layout_resource, parent,false);
        return clickListener != null ? new QueueViewHolder(view, clickListener) : new QueueViewHolder(view);
    }
}
