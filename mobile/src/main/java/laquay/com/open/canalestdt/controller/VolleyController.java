package laquay.com.open.canalestdt.controller;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyController {
    private static VolleyController instance;
    private RequestQueue fRequestQueue;
    private VolleyCore volley;

    private VolleyController(Context ctx) {
        volley = new VolleyCore(ctx);
        fRequestQueue = volley.getRequestQueue();
    }

    public static VolleyController getInstance(Context ctx) {
        if (instance == null) {
            createInstance(ctx);
        }
        return instance;
    }

    private synchronized static void createInstance(Context ctx) {
        if (instance == null) {
            instance = new VolleyController(ctx);
        }
    }

    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            fRequestQueue.add(request);
        }
    }

    public void onConnectionFailed(String error) {
        //Toast.makeText(ctx, error, Toast.LENGTH_SHORT).show();
    }

    public void removeAllDataInQueue() {
        if (fRequestQueue != null) {
            fRequestQueue.cancelAll(this);
        }
    }

    private class VolleyCore {
        private RequestQueue mRequestQueue;

        private VolleyCore(Context context) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        public RequestQueue getRequestQueue() {
            return mRequestQueue;
        }
    }
}