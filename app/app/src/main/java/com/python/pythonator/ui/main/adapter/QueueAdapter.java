package com.python.pythonator.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.python.pythonator.structures.queue.ImageQueueItem;
import com.python.pythonator.ui.templates.adapter.Adapter;
import com.python.pythonator.ui.templates.adapter.listener.AdapterListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

/**
 * Adapter to display a list of {@link ImageQueueItem}s
 */
public class QueueAdapter extends Adapter<ImageQueueItem> {

    // Listener for clicks on specific parts on an ImageQueueItem
    private QueueImageClickListener queueImageClickListener;

    public QueueAdapter(@NonNull AdapterListener adapterListener, QueueImageClickListener queueImageClickListener) {
        super(adapterListener);
        this.queueImageClickListener = queueImageClickListener;
    }

    @NonNull
    @Override
    public ViewHolder<ImageQueueItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(QueueViewHolder.layout_resource, parent,false);
        return new QueueViewHolder(view, this, queueImageClickListener);
    }
}
