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

public class UpdatesGithub {
    public static class GithubApi{
        public static String api = "https://api.github.com/";
        public static String repo = api+"repos/xenorasmc/usermod";
        public static class releases {
            public static String list = api+"repos/xenorasmc/usermod/releases";
            public static String latest = list+"/latest";
        }

        public static String pageReleases = "https://github.com/xenorasmc/usermod/releases";
        public static String pageRepo = "https://github.com/xenorasmc/usermod";
    }

    public static class Update {
        public String version;
        public boolean need;

        public Update(String v, boolean n) {
            this.version = v;
            this.need = n;
        }
        // Add constructor, get, set, as needed.
    }

    public static Update getLastUpdate() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(GithubApi.releases.latest);
            get.addHeader("Accept", "application/vnd.github.v3+json");
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            JSONObject resp = new JSONObject(EntityUtils.toString(resEntityGet));
            String version = resp.getString("tag_name");
            return new Update(version, checkNeed(version));
        } catch (IOException e) {
            return new Update("0.0.0", false);
        }
    }

    private static boolean checkNeed(String version) {
        Xenoras.LOGGER.info(version+" "+Xenoras.VERSION);
        return Integer.parseInt(version.replace(".", "")) > Integer.parseInt(Xenoras.VERSION.replace(".", ""));
    }
}
