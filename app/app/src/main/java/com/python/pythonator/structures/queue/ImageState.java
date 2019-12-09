package com.python.pythonator.structures.queue;

/**
 * Possible states for an {@link com.python.pythonator.structures.Image}.
 * Other than the trivial <code>NOT_SENT</code>, <code>SENDING</code>, <code>SENT</code> states,
 * there is also the <code>DRAWN</code> state, which is set when an image is drawn
 */
public enum ImageState {
    NOT_SENT,
    SENDING,
    SENT,
    DRAWN
}
