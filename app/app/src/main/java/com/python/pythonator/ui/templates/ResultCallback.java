package com.python.pythonator.ui.templates;

/**
 * Generic interface to send generic objects
 * @param <T> Type of object to send
 */
public interface ResultCallback<T> {
    void onResult(T t);
}
