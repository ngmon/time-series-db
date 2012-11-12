/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monitoring.db;

import com.mongodb.DBObject;

/**
 *
 * @author Michal
 */
public interface Database {
    
    void saveDocument(DBObject object);
    
    void tearDown();
    
}
