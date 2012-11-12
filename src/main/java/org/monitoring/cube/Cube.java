package org.monitoring.cube;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;
import java.net.UnknownHostException;
import java.util.Date;
import org.json.simple.JSONObject;

/**
 * Hello world!
 *
 */
public class Cube {
    private static CubeEvent cube;
            
    public static void main(String[] args) {
        cube = new CubeEvent();
        JSONObject js = new JSONObject();
        js.put("metric", 987);
        cube.addType("testevent");
        cube.addData(js);
        cube.addTime(new Date());

        try {
            Mongo m = new Mongo("192.168.219.129", 27017);
            
            DB db = m.getDB("cube_development");
            
            DBCollection coll = db.getCollection("cube_development");
            DBObject object = (DBObject) JSON.parse("{\"test\":\"b\"}");
            //coll.insert(object);
            
            //System.out.println(cube);
            
            //cube.sendViaUDP();


        } catch (UnknownHostException e) {
            System.err.println(e);
        }
    }

    
}
