package com.python.pythonator.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;

import com.python.pythonator.structures.ImageQueueItem;
import com.python.pythonator.ui.templates.adapter.Adapter;
import com.python.pythonator.ui.templates.adapter.Comperator;
import com.python.pythonator.ui.templates.adapter.listener.AdapterListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

public class QueueAdapter extends Adapter<ImageQueueItem> {

    private QueueImageClickListener queueImageClickListener;

    public QueueAdapter(@NonNull AdapterListener adapterListener, QueueImageClickListener queueImageClickListener) {
        super(adapterListener);
        this.queueImageClickListener = queueImageClickListener;
    }

    @NonNull
    @Override
    protected SortedList<ImageQueueItem> getSortedList(Comperator<ImageQueueItem> comperator) {
        return new SortedList<>(ImageQueueItem.class, comperator);
    }

    @NonNull
    @Override
    protected Comperator<ImageQueueItem> getComperator() {
        return new ImageComperator(this);
    }

    @NonNull
    @Override
    public ViewHolder<ImageQueueItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(QueueViewHolder.layout_resource, parent,false);
        return new QueueViewHolder(view, this, queueImageClickListener);
    }
}
