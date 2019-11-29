package com.python.pythonator.ui.main.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.python.pythonator.R;
import com.python.pythonator.structures.Image;
import com.python.pythonator.structures.ImageQueueItem;
import com.python.pythonator.structures.QueueItem;
import com.python.pythonator.ui.templates.adapter.InternalClickListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

public class QueueViewHolder extends ViewHolder<ImageQueueItem> {
    public static @LayoutRes final int layout_resource = R.layout.item_image;
    private ImageView thumbnail_view;
    private TextView size_view;
    private ImageButton send_button;
    private ConstraintLayout layout;

    private QueueImageClickListener queueImageClickListener;

    public QueueViewHolder(@NonNull View itemView, @NonNull InternalClickListener internalClickListener, @NonNull QueueImageClickListener queueImageClickListener) {
        super(itemView, internalClickListener);
        this.queueImageClickListener = queueImageClickListener;
    }

    @Override
    public void findViews() {
        thumbnail_view = itemView.findViewById(R.id.item_image_thumbnail);
        size_view = itemView.findViewById(R.id.item_image_size);
        send_button = itemView.findViewById(R.id.item_image_send);
        layout = itemView.findViewById(R.id.item_image_layout);
    }

    @Override
    public void setupClicks() {
        layout.setOnLongClickListener(v -> internalClickListener.onLongClick(v, getAdapterPosition()));
        layout.setOnClickListener(v -> internalClickListener.onClick(v, getAdapterPosition()));
        thumbnail_view.setOnClickListener(v -> queueImageClickListener.onThumbnailClick(getAdapterPosition()));
        send_button.setOnClickListener(v -> queueImageClickListener.onSendClicked(getAdapterPosition()));
    }

    @Override
    public void set(ImageQueueItem image) {
        image.get().getThumbnail(thumbnail_view.getWidth(), thumbnail_view.getHeight(), bitmap -> {
            thumbnail_view.post(() -> thumbnail_view.setImageBitmap(bitmap));
            size_view.setText(image.get().getSize());
        });
    }
}
