package com.huji.apps.haji.petugas.Utils;

/**
 * Created by Dell_Cleva on 13/02/2017.
 */

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoopjHttpClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(Constant.Username, Constant.Password);
        client.get(url, responseHandler);
    }

    public static void post(Context xt, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(Constant.Username, Constant.Password);
        client.post(xt, url, entity, "application/x-www-form-urlencoded", responseHandler);
    }

    public static void post(Context xt, String url, ByteArrayEntity entity, String type, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(Constant.Username, Constant.Password);
        client.post(xt, url, entity, type, responseHandler);
    }

    public static void debugLoopJ(String TAG, String methodName, String url, RequestParams requestParams, byte[] response, cz.msebera.android.httpclient.Header[] headers, int statusCode, Throwable t) {

        Log.d(TAG, client.getUrlWithQueryString(false, url, requestParams));

        if (headers != null) {
            Log.e(TAG, methodName);
            Log.d(TAG, "Return Headers:");
            /*
            for (Header h : headers) {
                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                Log.d(TAG, _h);
            }
            */

            if (t != null) {
                Log.d(TAG, "Throwable:" + t);
            }

            Log.e(TAG, "StatusCode: " + statusCode);

            if (response != null) {
                Log.d(TAG, "Response: " + new String(response));
            }

        }
    }
}
