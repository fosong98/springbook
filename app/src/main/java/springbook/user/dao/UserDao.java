package springbook.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.domain.User;

import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class UserDao {
    public DataSource dataSource;

    public UserDao() {}

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setConnectionMaker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws SQLException {
        StatementStrategy st = new AddStatement(user);
        jdbcContextWithStatementStrategy(st);
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM users WHERE id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;

        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException(1);
        return user;
    }

    public void deleteAll() throws SQLException {
        StatementStrategy st = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(st);
    }

    public int getCount() throws SQLException {
        try (
                Connection c = dataSource.getConnection();
                PreparedStatement ps = c.prepareStatement("select count(*) from users");
                ResultSet rs = ps.executeQuery();
        ) {
            rs.next();
            int count = rs.getInt(1);
            return count;
        }
    }

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        try (
                Connection c = dataSource.getConnection();
                PreparedStatement ps = stmt.makePreparedStatement(c);
        ) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
