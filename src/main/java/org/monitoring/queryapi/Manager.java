package org.monitoring.queryapi;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michal Dubravcik
 */
public class Manager {
    
    Mongo m ;
    DB db ;
    DBCollection col;
    
    /**
     * @param host ip address or host name of server
     * @param port number representing port on server
     */
    public Manager(String host, int port, String dba){
        try {
            m = new Mongo(host, port); 
            db = m.getDB(dba);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, "Unknown host Mongo DB", ex);
        } catch (IOException ex){
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, "Could not connect Mongo DB", ex);
        }
        
    }
    
    /**
     * Manager connected on ip:port localhost:27017 and Database test
     * @see Manager#Manager(java.lang.String, int) 
     */
    public Manager(){
        this("localhost",27017, "test");
    }
    
    public Query createQueryOnCollection(String col){
        setCollection(col);
        return new QueryMapReduce(this.col);
    }
    
    public DB getDb(){
        return db;
    }
    
    public void setCollection(String col){
        this.col = db.getCollection(col);
    }
        
    public DBCollection getCollection(){
        return col;
    }
    
    public DBCollection getCollection(String collectionName){
        return db.getCollection(collectionName);
    }
            
}
