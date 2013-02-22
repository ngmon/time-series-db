package org.monitoring.cube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michal
 */
class CubeEvent {
    JSONObject json;

    public CubeEvent() {
        json = new JSONObject();
        json.put("type", "default");
    }
    
    public void addData(JSONObject data) {
        json.put("data", data);
    }

    public void addTime(Date time) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        json.put("time", df.format(time));
    }

    public void addType(String type) {
        json.put("type", type);
    }

    public void sendViaRest() {
        try {
            URL url = new URL("http://192.168.219.129:1080/1.0/event/put");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            JSONArray cubeArray = new JSONArray();
            cubeArray.add(json);
            os.write(cubeArray.toString().getBytes());
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
            }
            os.close();
            conn.disconnect();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void sendViaUDP() {
        try {
            String host = "192.168.219.129";
            int port = 1180;
            byte[] message = json.toString().getBytes();
            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);
            // Initialize a datagram packet with data and address
            DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
            // Create a datagram socket, send the packet through it, close it.
            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(packet);
            dsocket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public String toString() {
        return json.toString();
    }

}
