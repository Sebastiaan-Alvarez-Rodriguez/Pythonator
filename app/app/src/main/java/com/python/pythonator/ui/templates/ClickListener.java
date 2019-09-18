package com.python.pythonator.ui.templates;

import android.view.View;

public interface ClickListener {
    void onClick(View view, int pos);
    boolean onLongClick(View view, int pos);
}
