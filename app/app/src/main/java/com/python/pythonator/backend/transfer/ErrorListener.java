package com.python.pythonator.backend.transfer;

/**
 * Listener to receive server errors
 */
public interface ErrorListener {
    /**
     * Gets called when there was a server error
     * @param error Error type from server
     */
    void onError(ErrorType error);
}
