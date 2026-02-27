package DataAcessLayerDAL;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/arabic_prose_db?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=UTC&useSSL=false";
        String user = "ap_user";
        String password = "ap_pass_123";

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected successfully!");

            // Close connection
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
