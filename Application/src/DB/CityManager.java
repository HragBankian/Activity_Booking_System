package DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CityManager {

    public static void fetchCities(Connection conn) {
        try {
            String query = "SELECT * FROM \"City\"";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("City: " + rs.getString("name"));
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            fetchCities(conn);
        }
    }
}
