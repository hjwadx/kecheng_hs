package com.tencent.tauth.http;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IRequestListener {
    /**
     * Called when a request completes with the given response.
     *
     * Executed by a background thread: do not update the UI in this method.
     */
    public void onComplete(String response, Object state);

    /**
     * Called when a request has a network or request error.
     *
     * Executed by a background thread: do not update the UI in this method.
     */
    public void onIOException(IOException e, Object state);

    /**
     * Called when a request fails because the requested resource is
     * invalid or does not exist.
     *
     * Executed by a background thread: do not update the UI in this method.
     */
    public void onFileNotFoundException(FileNotFoundException e,
                                        Object state);

}
