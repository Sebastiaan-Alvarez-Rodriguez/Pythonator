package com.python.pythonator.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.python.pythonator.structures.Image;
import com.python.pythonator.ui.templates.adapter.Adapter;
import com.python.pythonator.ui.templates.adapter.listener.AdapterListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

public class QueueAdapter extends Adapter<Image> {

    public QueueAdapter(@NonNull AdapterListener adapterListener) {
        super(adapterListener);
    }

    @NonNull
    @Override
    public ViewHolder<Image> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(QueueViewHolder.layout_resource, parent,false);
        return new QueueViewHolder(view, this);
    }
}
