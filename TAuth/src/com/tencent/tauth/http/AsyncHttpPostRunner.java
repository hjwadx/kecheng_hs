package com.tencent.tauth.http;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Bundle;

public class AsyncHttpPostRunner {
	
    public void request(final String url,Bundle parameters, IRequestListener listener) {
        request(url, parameters, "POST", listener, /* state */ null);
    }
	
    /**
     * Make a request to the Facebook Graph API with the given HTTP method and
     * string parameters. Note that binary data parameters (e.g. pictures) are
     * not yet supported by this helper function.
     *
     * See http://developers.facebook.com/docs/api
     *
     * Note that this method is asynchronous and the callback will be invoked
     * in a background thread; operations that affect the UI will need to be
     * posted to the UI thread or an appropriate handler.
     *
     * @param graphPath
     *            Path to resource in the Facebook graph, e.g., to fetch data
     *            about the currently logged authenticated user, provide "me",
     *            which will fetch http://graph.facebook.com/me
     * @param parameters
     *            key-value string parameters, e.g. the path "search" with
     *            parameters {"q" : "facebook"} would produce a query for the
     *            following graph resource:
     *            https://graph.facebook.com/search?q=facebook
     * @param httpMethod
     *            http verb, e.g. "POST", "DELETE"
     * @param listener
     *            Callback interface to notify the application when the request
     *            has completed.
     * @param state
     *            An arbitrary object used to identify the request when it
     *            returns to the callback. This has no effect on the request
     *            itself.
     */
    public void request(final String url,
                        final Bundle parameters,
                        final String httpMethod,
                        final IRequestListener listener,
                        final Object state) {
        new Thread() {
            @Override public void run() {
                try {
                	
                    String resp = ClientHttpRequest.openUrl(url, httpMethod, parameters);
                    listener.onComplete(resp, state);
                } catch (FileNotFoundException e) {
                    listener.onFileNotFoundException(e, state);
                } catch (IOException e) {
                    listener.onIOException(e, state);
                }
            }
        }.start();
    }

}
