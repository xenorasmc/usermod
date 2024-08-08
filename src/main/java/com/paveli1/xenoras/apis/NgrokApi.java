package com.paveli1.xenoras.apis;

import com.paveli1.xenoras.Xenoras;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class NgrokApi {
    final static String API_URL = "https://api.ngrok.com/";
    final static String API_KEY = "2kGOxTlm3AUMLECJuqmcFQK8LS9_4YFXJ96ovhGM6YVZ6wNNN";

    public static String getEndpoint() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(API_URL + "endpoints");
            get.addHeader("authorization", "Bearer " + API_KEY);
            get.addHeader("ngrok-version", "2");
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                String resp = EntityUtils.toString(resEntityGet);
                try {
                    return getHostport(resp);
                } catch (Exception err) {
                    System.out.println(err.toString());
                    return null;
                }
            }
        } catch (IOException err) {
            System.out.println(err.toString());
            return null;
        }
        return null;
    }

    private static String getHostport(String input) {
        return new JSONObject(input).getJSONArray("endpoints").getJSONObject(0).getString("hostport");
    }

    private static String findHosport(String input) {
        input = input.replace("\"", "'");
        String a = "";
        String result = "";
        boolean sr = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            a += Character.toString(c);
            if (sr) {
                result += Character.toString(c);
            }
            else if (a.equals("rt':'")) {
                sr = true;
            }
            else if (a.length() > 4) {
                a = a.substring(1);
            }
            //System.out.println(a);
        }
        input = reverse(result);
        result = "";
        a = "";
        sr = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            a += Character.toString(c);
            if (sr) {
                result += Character.toString(c);
            }
            else if (a.equals("yt','")) {
                sr = true;
            }
            else if (a.length() > 4) {
                a = a.substring(1);
            }
            //System.out.println(a);
        }
        return reverse(result);
    }

    public static String reverse(String input){
        char[] in = input.toCharArray();
        int begin=0;
        int end=in.length-1;
        char temp;
        while(end>begin){
            temp = in[begin];
            in[begin]=in[end];
            in[end] = temp;
            end--;
            begin++;
        }
        return new String(in);
    }
}
