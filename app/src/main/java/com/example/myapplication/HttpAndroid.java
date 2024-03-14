package com.example.myapplication;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpAndroid {
    private static final String TAG = "HttpAndroid";

    // Corresponding Java enum for the C++ 'Status' enum
    public enum Status {
        Invalid(0),
        Ok(200),
        NotFound(404);

        private final int code;
        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    // Corresponding Java enum for the C++ 'Method' enum
    public enum Method {
        GET,
        POST,
        PUT
    }

    // Java class equivalent to the C++ 'Response' struct
    public static class Response {
        Status status;
        String contentType;
        String body;
        Map<String, List<String>> headers;
        String error;

        // Getters and setters for each field
        // ...
    }

    // Java class equivalent to the C++ 'Request' struct
    public static class Request {
        String url;
        Map<String, String> headers;
        Method method;
        String body;
        boolean forceIPv4;

        public Request() {
            this.method = Method.GET; // Default method
            this.headers = new HashMap<>();
            this.headers.put("User-Agent", "OpenRCT2 Android");
        }
    }

    public static Response request(Request request) {
        Response response = new Response();
        response.status = Status.Invalid;
        response.error = "Request failed";
        try {
            InputStream inputStream = null;
            try {
                URL url = new URL(request.url);
                Log.d(TAG, "Requesting " + request.url + " with method " + request.method + " and body " + request.body + " and headers " + request.headers + " and forceIPv4 " + request.forceIPv4);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(request.method.toString());
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(true);
                if (request.headers != null) {
                    for (Map.Entry<String, String> entry : request.headers.entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                        Log.d(TAG, "Request header: " + entry.getKey() + " Value: " + entry.getValue());
                    }
                }
                connection.connect();
                if (request.body!= null) {
                    OutputStream os = connection.getOutputStream();
                    os.write(request.body.getBytes());
                    os.flush();
                    os.close();
                }
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                if (responseCode == 200) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }
                response.status = Status.Ok;
                response.contentType = connection.getContentType();
                response.headers = connection.getHeaderFields();
                // iterate through the headers and log them, using iterator
                java.util.Set<Map.Entry<String, List<String>>> entries = response.headers.entrySet();
                // iterator
                java.util.Iterator<Map.Entry<String, List<String>>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<String>> entry = iterator.next();
                    Log.d(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
                }

                response.error = null;
                return response;
            } catch (IOException e) {
                Log.e(TAG, "Error while requesting " + request.url + ", error: " + e.getMessage(), e);
                response.error = e.getMessage();
                return response;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while requesting " + request.url, e);
            response.error = e.getMessage();
            return response;
        }
    }
}
