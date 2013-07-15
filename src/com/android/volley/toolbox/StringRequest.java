/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<String> {
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final Listener<String> mListener;
    private Map<String, String> mHeaders = new HashMap<String, String>();
    private Map<String, String> mParams = new HashMap<String, String>();
    private String mRequestBody;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Listener<String> listener,
                         ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param headers headers of the request
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Map<String, String> headers, Listener<String> listener,
                         ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param headers Headers of the request
     * @param params Parameters of the request
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Map<String, String> headers, Map<String, String> params, JSONObject requestBody, Listener<String> listener, ErrorListener errorListener) {
        this(method, url, listener, errorListener);
        mHeaders = headers;
        mParams = params;
        mRequestBody = (requestBody == null) ? null : requestBody.toString();

        String paramsString = "";
        String headersString = "";
        for (Map.Entry<String, String> entry: params.entrySet()){
            paramsString += entry.getKey() + ":" + entry.getValue() + "\n";
        }
        for (Map.Entry<String, String> entry: headers.entrySet()){
            headersString += entry.getKey() + ":" + entry.getValue() + "\n";
        }

        VolleyLog.d("Request String : URL: " + url + "\nHeaders: " + headersString + "\nParams: " + paramsString);
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getParams(){
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders(){
        return mHeaders;
    }

    public String getCacheKey() {
        String cacheKey = getUrl();
        for (Map.Entry<String, String> entry: mHeaders.entrySet()){
            cacheKey += entry.getKey() + entry.getValue();
        }
        for (Map.Entry<String, String> entry: mHeaders.entrySet()){
            cacheKey += entry.getKey() + entry.getValue();
        }
        return cacheKey;
    }

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() {
        return getBody();
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
