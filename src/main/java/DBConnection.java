import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public DBConnection getConnection() {
        try {
            String jdbcURl = System.getenv("JDBC_URl");
            String user = System.getenv("USER");
            String password = System.getenv("PASS");
            return (DBConnection) DriverManager.getConnection("jdbc:postgresql://localhost:5432/mini_dish_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
