/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.caliperBenchmark;

import com.mongodb.BasicDBObject;
import org.monitoring.db.MongoDatabase;

/**
 *
 * @author Michal
 */
public class Run {
    
    static Documents documents = new Documents();
    
    static MongoDatabase mongo = new MongoDatabase("postgres", "rawevent");

    public static void main(String[] args) {
    }
}
