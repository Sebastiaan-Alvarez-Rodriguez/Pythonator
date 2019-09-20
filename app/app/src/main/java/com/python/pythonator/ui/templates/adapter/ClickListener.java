package com.python.pythonator.ui.templates.adapter;

import android.view.View;

public interface ClickListener {
    void onClick(View view, int pos);
    boolean onLongClick(View view, int pos);
}
