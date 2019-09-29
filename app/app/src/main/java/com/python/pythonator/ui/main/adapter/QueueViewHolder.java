package com.python.pythonator.ui.main.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.python.pythonator.R;
import com.python.pythonator.structures.Image;
import com.python.pythonator.ui.templates.adapter.InternalClickListener;
import com.python.pythonator.ui.templates.adapter.viewholder.ViewHolder;

public class QueueViewHolder extends ViewHolder<Image> {
    public static @LayoutRes final int layout_resource = R.layout.item_image;
    private ImageView thumbnail_view;
    private ConstraintLayout layout;

    public QueueViewHolder(@NonNull View itemView, @NonNull InternalClickListener internalClickListener) {
        super(itemView, internalClickListener);
    }

    @Override
    public void findViews() {
        thumbnail_view = itemView.findViewById(R.id.item_image_thumbnail);
        layout = itemView.findViewById(R.id.item_image_layout);
    }

    @Override
    public void setupClicks() {
        layout.setOnClickListener(v -> internalClickListener.onClick(v, getAdapterPosition()));
        layout.setOnLongClickListener(v -> internalClickListener.onLongClick(v, getAdapterPosition()));
    }

    @Override
    public void set(Image image) {
        image.getThumbnail(thumbnail_view.getWidth(), thumbnail_view.getHeight(), bitmap -> {
            thumbnail_view.post(() -> thumbnail_view.setImageBitmap(bitmap));
        });
    }
}
