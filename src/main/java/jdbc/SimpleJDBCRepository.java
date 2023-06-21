package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {
    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = """
            INSERT INTO myusers(
            firstname, lastname, age)
            VALUES (?, ?, ?)
            RETURNING id;
            """;
    private static final String updateUserSQL = """
            UPDATE myusers
            SET firstname=?, lastname=?, age=?
            WHERE id = ?;
            """;
    private static final String deleteUser = """
            DELETE FROM myusers
            WHERE id = ?;
            """;
    private static final String findUserByIdSQL = """
            SELECT id, firstname, lastname, age FROM myusers
            WHERE id = ?;
            """;
    private static final String findUserByNameSQL = """
            SELECT id, firstname, lastname, age FROM myusers
            WHERE firstname ILIKE '%' || ? || '%';
            """;
    private static final String findAllUserSQL = """
            SELECT id, firstname, lastname, age FROM myusers;
            """;


    public Long createUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.execute();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Failed to create user. No generated ID obtained.");
            }
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }

    }

    public User findUserById(Long userId) {
        if (userId == null) {
            return null;
        }

        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            User user = new User();
            while (rs.next()) {
                user.setId(rs.getLong("id"));
                user.setAge(rs.getInt("age"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
            }
            return user;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources();
        }

    }

    public User findUserByName(String userName) {
        if (userName == null) {
            return null;
        }
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            User user = new User();
            while (rs.next()) {
                user.setId(rs.getLong("id"));
                user.setAge(rs.getInt("age"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
            }
            return user;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources();
        }


    }

    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(findAllUserSQL);
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setAge(rs.getInt("age"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }finally {
            closeResources();
        }


    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            return null;
        }

        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
            User changedUser = findUserById(user.getId());

            return changedUser;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }
    }

    public void deleteUser(Long userId) {
        if (userId == null) {
            return;
        }

        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeUpdate();

        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources();
        }

    }

    private void closeResources() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (st != null) {
                st.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}