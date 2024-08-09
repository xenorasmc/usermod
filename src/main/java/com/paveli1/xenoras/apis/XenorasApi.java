package com.paveli1.xenoras.apis;

import com.paveli1.xenoras.Xenoras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class XenorasApi {
    public static XSocket xsocket;

    public static class HostAddress {
        public String host;
        public int port;
        public HostAddress (String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    public static class Response {
        public boolean status;
        public String error;
        public String response;
        public Response(boolean status, String error, String response) {
            this.error = error;
            this.status = status;
            this.response = response;
        }
    }

    public static class XSocket {
        public String HOST = "93.158.194.211";
        public int PORT = 25566;
        private InetAddress host;
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        public XSocket (HostAddress address) throws IOException {
            this.HOST = address.host;
            this.PORT = address.port;

            this.host = InetAddress.getByName(this.HOST);
            this.socket = new Socket(this.HOST, this.PORT);
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
        }

        public void send(JSONObject jmsg) throws IOException {
            System.out.println("sending string: "+jmsg.toString());
            this.out.write(jmsg.toString().getBytes());
        }

        public void sendFile(byte[] file) throws IOException {
            this.out.write(file);
        }

        public JSONObject recv() throws IOException {
            byte[] message = new byte[1024];
            try {
                System.out.println("recv...");
                in.readFully(message, 0, 1024);
                System.out.println("recv");
            } catch (EOFException err) {
                return new JSONObject(new String(message, StandardCharsets.UTF_8));
            }
            return new JSONObject(new String(message, StandardCharsets.UTF_8));
        }

        public void close() throws IOException {
            this.out.close();
            this.in.close();
        }

        public void update() throws IOException {
            this.close();
            this.socket = new Socket(this.host, this.PORT);
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
        }
    }

    public static JSONObject requestConstructor(JSONObject request, ClientPlayerEntity player) {
        JSONObject jmsg = new JSONObject();
        jmsg.put("from", "usermod");
        jmsg.put("version", Xenoras.VERSION);
        jmsg.put("username", player.getName().getString());
        jmsg.put("request", request);
        return jmsg;
    }

    public static HostAddress genAddress() {
        if (Xenoras.CONFIG.Bridge().equals(ConfigModel.BridgeChoices.OFFICIAL)) {
            String napiresp = NgrokApi.getDiscServerAddress();
            if (napiresp != null) {
                String[] address = NgrokApi.getDiscServerAddress().split(":");
                return new HostAddress(address[0], Integer.parseInt(address[1]));
            }
            else {
                return new HostAddress("93.158.194.211", 25566);
            }
        }
        else {
            return new HostAddress("93.158.194.211", 25566);
        }
    }

    public static Response getdics(String name) {
        try {
            System.out.println("connecting to disc server with command getdisc and q="+name);
            XSocket xsocket = new XSocket(genAddress());

            JSONObject call = new JSONObject();
            call.put("type", "getdisc");
            call.put("name", name);
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return new Response(false, "player = null?", null);
            JSONObject jmsg = requestConstructor(call, player);

            xsocket.send(jmsg);
            JSONObject resp = xsocket.recv();
            if (resp.getString("status").equals("ok")) {
                xsocket.close();
                return new Response(true, null, resp.getString("response"));
            }
            else {
                xsocket.close();
                return new Response(false, resp.getString("error"), null);
            }
        } catch (Exception err) {
            System.out.println(err.toString());
            return new Response(false, "java (usermod) error", null);
        }
    }

    public static Response diskbylink(String link) {
        try {
            XSocket xsocket = new XSocket(genAddress());

            JSONObject call = new JSONObject();
            call.put("type", "disc");
            call.put("link", link);
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return new Response(false, "player = null?", null);
            JSONObject jmsg = requestConstructor(call, player);

            xsocket.send(jmsg);
            JSONObject resp = xsocket.recv();
            if (resp.getString("status").equals("ok")) {
                xsocket.close();
                return new Response(true, null, resp.getString("response"));
            }
            else {
                xsocket.close();
                return new Response(false, resp.getString("error"), null);
            }
        } catch (Exception err) {
            System.out.println(err.toString());
            return new Response(false, "java (usermod) error", null);
        }
    }

    public static Response getlastq() {
        try {
            XSocket xsocket = new XSocket(genAddress());

            JSONObject call = new JSONObject();
            call.put("type", "disc");
            call.put("link", "mylast");
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return new Response(false, "player = null?", null);
            JSONObject jmsg = requestConstructor(call, player);

            xsocket.send(jmsg);
            JSONObject resp = xsocket.recv();
            if (resp.getString("status").equals("ok")) {
                xsocket.close();
                return new Response(true, null, resp.getString("response"));
            }
            else {
                xsocket.close();
                return new Response(false, resp.getString("error"), null);
            }
        } catch (Exception err) {
            System.out.println(err.toString());
            return new Response(false, "java (usermod) error", null);
        }
    }

    public static Response diskbyfile(String filepath) {
        try {
            File file = new File(filepath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            if (fileContent.length > 50000000) return new Response(false, "file size limit: 50MB", null);
            if (!FilenameUtils.getExtension(filepath).equals("mp3") && !FilenameUtils.getExtension(filepath).equals("wav")) return new Response(false, "only WAV or MP3 are supported", null);
            XSocket xsocket = new XSocket(genAddress());
            JSONObject call = new JSONObject();
            call.put("type", "disc");
            call.put("link", "file");
            call.put("filesize", fileContent.length);
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return new Response(false, "player = null?", null);
            JSONObject jmsg = requestConstructor(call, player);

            xsocket.send(jmsg);
            xsocket.sendFile(fileContent);
            xsocket.close();
            Response resp = getlastq();
            if (resp.status) {
                Response r = new Response(false, "java (usermod) error", null);
                for (int i = 0; i < 5; i++) {
                    r = getdics(resp.response);
                    System.out.println(r.response);
                    if (r.status) break;
                    Thread.sleep(1000);
                }
                return r;

            }
            else {
                return new Response(false, resp.error, null);
            }
        } catch (Exception err) {
            System.out.println(err.toString());
            return new Response(false, "java (usermod) error", null);
        }
    }
}
