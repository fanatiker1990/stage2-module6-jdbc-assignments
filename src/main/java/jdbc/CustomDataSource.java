
package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public static CustomDataSource getInstance() {
        synchronized (CustomDataSource.class) {
            if (instance == null) {
                Properties prop = new Properties();
                try (InputStream input = CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties")) {
                    prop.load(input);

                } catch (IOException e) {
                    System.out.println(e);
                }
                String driver = prop.getProperty("postgres.driver");
                String url = prop.getProperty("postgres.url");
                String password = prop.getProperty("postgres.password");
                String name = prop.getProperty("postgres.name");
                instance = new CustomDataSource(driver, url, password, name);
            }
        }

        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        CustomConnector connector = new CustomConnector();
        return connector.getConnection(url);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        CustomConnector connector = new CustomConnector();
        return connector.getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}