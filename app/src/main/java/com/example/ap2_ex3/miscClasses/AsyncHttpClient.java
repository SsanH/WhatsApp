package com.example.ap2_ex3.miscClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AsyncHttpClient {
    private static final int CONN_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    private Map<String, String> headers;
    private String baseUrl;

    public AsyncHttpClient(String baseUrl) {
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        this.baseUrl = baseUrl;
    }

    public void setToken(String token) {
        headers.put("Authorization", "Bearer " + token);
    }

    public void setHeader(String key, String val) {
        headers.put(key, val);
    }

    public void deleteHeader(String key) {
        headers.remove(key);
    }

    public void get(String urlPath, AsyncHttpRequestCallback callback) {
        req("GET", urlPath, null, callback);
    }

    public void post(String urlPath, String body, AsyncHttpRequestCallback callback) {
        req("POST", urlPath, body, callback);
    }

    public void delete(String urlPath, AsyncHttpRequestCallback callback) {
        req("DELETE", urlPath, null, callback);
    }

    private void req(String method, String urlPath, String body, AsyncHttpRequestCallback callback) {
        Thread requestThread = new Thread(() -> {
            try {
                URL url = new URL(this.baseUrl + urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method, timeouts, or any other necessary properties
                connection.setRequestMethod(method);
                connection.setConnectTimeout(CONN_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }

                if (body != null) {
                    connection.setDoOutput(true); // Enable request body
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(body.getBytes());
                    outputStream.flush();
                    outputStream.close();
                }

                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } catch (Exception e) {
                    System.out.println("Failed to read response body");
                    System.out.println(e);
                }
                callback.onSuccess(response.toString(), responseCode);


                connection.disconnect();
            } catch (Exception e) {
                callback.onFailure(e);
            }
        });

        requestThread.start();
    }

    public interface AsyncHttpRequestCallback {
        void onSuccess(String response, int responseCode);

        void onFailure(Exception e);
    }
}

