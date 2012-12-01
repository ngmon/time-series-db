package org.monitoring.cube;

import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michal
 */
public class RequestCube {

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("expression", "sum(testevent)");
        json.put("limit", 3);
        json.put("step","36e5");
        //System.out.println(json);
        //JSONObject output = (JSONObject) JSON.parse(getHttp(json));
        //System.out.println("parsed");
        //System.out.println(output);
    }

    public static String getHttp(JSONObject json) {
        String output = "";
        try {
            String urlString = "http://192.168.219.129:1081/1.0/metric/get";
            if(json.containsKey("expression")){
                urlString += "?expression="+json.get("expression");
                if(json.containsKey("start"))
                    urlString += "&start="+json.get("start");
                if(json.containsKey("stop"))
                    urlString += "&stop="+json.get("stop");
                if(json.containsKey("limit"))
                    urlString += "&limit="+json.get("limit");               
                if(json.containsKey("step"))
                    urlString += "&step="+json.get("step");               
                
            }
                
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "plain/text");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return output;
    }
}
