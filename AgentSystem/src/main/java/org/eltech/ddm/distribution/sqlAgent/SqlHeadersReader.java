package org.eltech.ddm.distribution.sqlAgent;

import org.apache.commons.collections4.CollectionUtils;
import org.eltech.ddm.distribution.common.HeadersMessage;
import org.eltech.ddm.distribution.common.IHeadersReader;
import org.eltech.ddm.distribution.settings.ConnectionSettings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlHeadersReader implements IHeadersReader {
    private static final String GET_ATTRIBUTES_NAMES = "SELECT column_name "
            + "FROM information_schema.columns "
            + "WHERE table_schema = '%s' AND table_name in(%s) "
            + "ORDER BY column_name";

    private ConnectionSettings connectionSettings;

    public SqlHeadersReader(ConnectionSettings connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    @Override
    public HeadersMessage readHeaders() {
        Connection connection = new Connector(connectionSettings).getConnection();
        Statement statement = createStatement(connection);
        List<String> columnNames = connectionSettings.getColumnNames();
        String query = String.format(GET_ATTRIBUTES_NAMES, connectionSettings.getSchemaName(), formatTableName(columnNames));
        return executeQuery(statement, query);
    }

    private String formatTableName(List<String> columnNames) {
        if (CollectionUtils.isNotEmpty(columnNames)) {
            String tables = "'" + columnNames.get(0) + "'";

            for (int i = 1; i < columnNames.size(); i++) {
                tables = tables.concat(",'").concat(columnNames.get(i)).concat("'");
            }
            return tables;
        }
        throw new RuntimeException("Нужно передать хотя бы одно название таблицы");
    }

    private HeadersMessage executeQuery(Statement statement, String query) {
        List<String> headerNames = new ArrayList<>();

        try {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                headerNames.add(resultSet.getString("column_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HeadersMessage(headerNames);
    }

    private Statement createStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
