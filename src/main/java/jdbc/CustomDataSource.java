package jdbc;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private final String username;
    private final String password;

    private CustomDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    Properties properties = new Properties();
                    try (FileInputStream fis = new FileInputStream("src/main/resources/app.properties")) {
                        properties.load(fis);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String driver = properties.getProperty("postgres.driver");
                    String url = properties.getProperty("postgres.url");
                    String username = properties.getProperty("postgres.name");
                    String password = properties.getProperty("postgres.password");
                    instance = new CustomDataSource(driver,url,username,password);
                }
            }
        }
        return instance;
    }
    @Override
    public Connection getConnection() throws SQLException {
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLException();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        throw new SQLException();

    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        throw new SQLException();

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        throw new SQLException();
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        throw new SQLException();
    }
}