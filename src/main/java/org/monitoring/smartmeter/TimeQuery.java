package org.monitoring.smartmeter;

import com.mongodb.DBObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.monitoring.queryapi.Manager;

/**
 *
 * @author Michal Dubravcik
 */
public class TimeQuery {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "root");

        String query = "SELECT date_trunc('second',edate) as date, sum(evalue) as sum"
                + " FROM meterevent GROUP BY date LIMIT 1000;";
        PreparedStatement st = conn.prepareStatement(query);

        System.out.println(query);
        double mean = 0;
        int i;
        for (i = 0; i < 10; i++) {
            long start = System.nanoTime();
            ResultSet result = st.executeQuery();
            long end = System.nanoTime();
            double elapsed = (end - start) / 1e6;
            System.out.println("Time elapsed for query: " + elapsed + " ms");
            mean += elapsed;
        }
        mean = mean / i;
        System.out.println("Mean: " + mean);

        mean = 0;
        Manager m = new Manager();
        m.setMode(Manager.Mode.AggregationFramework);
        for (i = 0; i < 10; i++) {
            long start = System.nanoTime();
            DBObject out = m.createQueryOnCollection("meterevent").setStep(1000).sum("evalue");
            long end = System.nanoTime();
            double elapsed = (end - start) / 1e6;
            System.out.println("Time elapsed for query: " + elapsed + " ms");
            mean += elapsed;
        }
        mean = mean / i;
        System.out.println("Mean: " + mean);
    }
}
