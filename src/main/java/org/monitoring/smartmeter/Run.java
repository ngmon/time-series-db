/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.smartmeter;

import org.monitoring.smartmeter.db.Database;
import org.monitoring.smartmeter.db.MongoDatabase;
import org.monitoring.smartmeter.db.PostgreSQLDatabase;

/**
 *
 * @author Michal
 */
public class Run {
    
    static Database postgre = new PostgreSQLDatabase();
    static Database mongo = new MongoDatabase("postgres", "meterevent");
    
    public static void main(String[] args) {
        
    }    
}
