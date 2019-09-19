package com.python.pythonator.ui.queue.view;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.python.pythonator.R;
import com.python.pythonator.structures.Image;
import com.python.pythonator.ui.templates.ClickListener;
import com.python.pythonator.ui.templates.ViewHolder;

public class QueueViewHolder extends ViewHolder<Image> {
    public static @LayoutRes final int layout_resource = R.layout.item_image;
    private ImageView thumbnail_view;
    private TextView name_view, date_view;
    private Image image;
    private ConstraintLayout layout;

    public QueueViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public QueueViewHolder(@NonNull View itemView, @NonNull ClickListener clickListener) {
        super(itemView, clickListener);
    }

    @Override
    public void findViews() {
        thumbnail_view = itemView.findViewById(R.id.item_image_thumbnail);
        name_view = itemView.findViewById(R.id.item_image_title);
        date_view = itemView.findViewById(R.id.item_image_date);
        layout = itemView.findViewById(R.id.item_image_layout);
    }

    @Override
    public void setupClicks() {
        thumbnail_view.setOnClickListener(v -> {
            //TODO: inflate to dialog size
        });
        layout.setOnClickListener(v -> clickListener.onClick(v, getAdapterPosition()));
        layout.setOnLongClickListener(v -> clickListener.onLongClick(v, getAdapterPosition()));
    }

    @Override
    public void set(Image image) {
        this.image = image;

        thumbnail_view.setImageBitmap(image.getThumbnail(thumbnail_view.getWidth(), thumbnail_view.getHeight()));
        name_view.setText(image.getName());
        date_view.setText("Created " + image.getDate());
    }
}
