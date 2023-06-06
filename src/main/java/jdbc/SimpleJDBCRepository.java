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

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?);";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";


    public Long createUser(User user){
        if (user.getId() == null) {
            return null;
        }

        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            long val = ps.executeUpdate();
            connection.close();
            return val;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId){
        if (userId == null) {
            return null;
        }

        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ResultSet rs = ps.executeQuery();
            User user = new User();
            while (rs.next()) {
                user.setAge(rs.getInt("age"));
                user.setFirstName(rs.getString("firstname") );
                user.setLastName(rs.getString("lastname"));
            }
            connection.close();
            return user;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }

    }

    public User findUserByName(String userName) {
        if (userName == null) {
            return null;
        }
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ResultSet rs = ps.executeQuery();
            User user = new User();
            while (rs.next()) {
                user.setAge(rs.getInt("age"));
                user.setFirstName(rs.getString("firstname") );
                user.setLastName(rs.getString("lastname"));
            }
            connection.close();
            return user;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
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
                user.setFirstName(rs.getString("firstname") );
                user.setLastName(rs.getString("lastname"));
                userList.add(user);
            }
            connection.close();
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }
        return userList;
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
            connection.close();
            return changedUser;
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
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
            connection.close();
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }

    }
}