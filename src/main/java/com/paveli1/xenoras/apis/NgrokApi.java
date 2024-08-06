package com.paveli1.xenoras.apis;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NgrokApi {
    final static String API_URL = "https://api.ngrok.com/";
    final static String API_KEY = "2kGOxTlm3AUMLECJuqmcFQK8LS9_4YFXJ96ovhGM6YVZ6wNNN";

    public static String getEndpoint() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(API_URL + "endpoints");
            get.addHeader("authorization", "Bearer " + API_KEY);
            get.addHeader("ngrok-version", "2");
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                try {
                    return new JSONObject(EntityUtils.toString(resEntityGet)).getJSONArray("endpoints").getJSONObject(0).getString("hostport");
                } catch (JSONException | IOException err) {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
