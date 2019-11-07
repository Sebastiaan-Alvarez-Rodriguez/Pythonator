package com.python.pythonator.ui.templates.adapter.listener;

import android.view.View;

public interface AdapterListener {
    void onClick(View view, int pos);
    boolean onLongClick(View view, int pos);
    void onSwiped(int pos);
}
