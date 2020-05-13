package org.eltech.ddm.distribution.sqlAgent;

import org.eltech.ddm.distribution.settings.ConnectionSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private ConnectionSettings settings;

    public Connector(ConnectionSettings settings) {
        loadDriver();
        this.settings = settings;
    }

    public Connection getConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(settings.getUrl(), settings.getUser(), settings.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver"); // todo рассмотреть вариант передачи в ConnectionSettings.
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path");
            e.printStackTrace();
        }
    }
}
