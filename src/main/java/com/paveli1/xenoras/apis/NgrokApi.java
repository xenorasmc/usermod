package com.paveli1.xenoras.apis;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
                String resp = EntityUtils.toString(resEntityGet);
                try {
                    return findHosport(resp);
                } catch (Exception err) {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    public static String findHosport(String input) {
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
