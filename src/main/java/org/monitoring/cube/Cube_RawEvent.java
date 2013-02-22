package org.monitoring.cube;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.monitoring.szt.db.MongoDatabaseMapper;
import org.monitoring.szt.db.MongoDatabaseMapperManual;
import org.monitoring.szt.model.RawEvent;

/**
 *
 * @author Michal Dubravcik
 */
public class Cube_RawEvent {

    RawEvent event;

    public Cube_RawEvent(RawEvent event) {
        this.event = event;
    }

    public void setEvent(RawEvent event) {
        this.event = event;
    }

    public void sendViaRest() {

        DBObject json = new BasicDBObject();

        json.put("type", "szt");

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        json.put("time", df.format(event.getOccurrenceTimestamp()));

        DBObject eventm = new BasicDBObject();
        eventm.put("id", event.getId());
        eventm.put("measurementType", event.getMeasurementType().toString());
        eventm.put("simulationId", event.getSimulationId());
        eventm.put("simulationTimestamp", event.getSimulationTimestamp());
        eventm.put("source", event.getSource());
        eventm.put("sourceType", event.getSourceType().toString());
        eventm.put("version", event.getVersion());
        eventm.put("values", event.getValues());
        json.put("data", eventm);



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
            //System.out.println("Output from Server .... \n");
            //while ((output = br.readLine()) != null) {
            //    System.out.println(output);
            //}
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + conn.getResponseMessage());
            }
            os.close();
            conn.disconnect();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}