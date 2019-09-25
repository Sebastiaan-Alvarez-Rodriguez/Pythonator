package com.python.pythonator.backend.local;

import androidx.annotation.Nullable;

import com.python.pythonator.structures.Image;

public interface LocalResultInterface {
    void onResult(@Nullable Image image);
}
